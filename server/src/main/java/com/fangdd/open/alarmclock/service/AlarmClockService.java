package com.fangdd.open.alarmclock.service;

import com.fangdd.open.alarmclock.config.Constants;
import com.fangdd.open.alarmclock.data.access.ClockDataAccess;
import com.fangdd.open.alarmclock.data.access.TargetDataAccess;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;

import com.fangdd.open.alarmclock.task.TaskFactory;
import com.fangdd.open.alarmclock.utils.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 闹钟服务
 */
@Service
public class AlarmClockService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlarmClockService.class);

  private static final HashedWheelTimer SECOND_TIMER = new HashedWheelTimer(1L, TimeUnit.SECONDS, 131072);
  private static final HashedWheelTimer MINUTE_TIMER = new HashedWheelTimer(1L, TimeUnit.MINUTES, 2048);

  private static final Map<String, Timeout> TASKS = new ConcurrentHashMap<>();
  private static final Map<String, Boolean> LOADING = new ConcurrentHashMap<>();

  private static final ExecutorService SECOND_EXECUTOR = Executors.newFixedThreadPool(30);
  private static final ExecutorService MINUTE_EXECUTOR = Executors.newFixedThreadPool(30);
  private static final ExecutorService CAL_CHAIN_LENGTH_EXECUTOR = Executors.newFixedThreadPool(10);

  @Autowired private ClockDataAccess  cda;
  @Autowired private TargetDataAccess tda;
  @Autowired private NodeService nodeService;
  @Autowired private DingTalkService dtService;

  /**
   * 删除闹钟任务
   * @param id
   * @return
   */
  public static Timeout removeTask(String id) {
    Timeout handle = TASKS.get(id);
    if (null != handle) {
      if (!handle.isCancelled()) {
        handle.cancel();
      }
      TASKS.remove(id);
    }
    return handle;
  }

  /**
   * 启动时加载本机未执行或需要重新执行的闹钟
   */
  @PostConstruct
  public void load() {
    load(nodeService.getNodeId());
  }

  /**
   * 按指定的 node 加载闹钟，当某个节点不能正常运行时，可以通过接口触发另一个正常的节点来加载异常的节点的闹钟
   * @param node
   */
  public void load(String node) throws AlarmClockException {
    Boolean loading = LOADING.get(node);
    if (null != loading && loading) {
      LOGGER.warn("加载闹钟：节点 [{}] 的闹钟正在加载中，请不要重复加载", node);
      throw new AlarmClockException(Errors.FORBIDDEN, "节点 [" + node + "] 的闹钟正在加载中，请不要重复加载");
    }
    LOADING.put(node, true);
    String current = nodeService.getNodeId();
    boolean move = !current.equals(node);
    for (AlarmClockResource clock : cda.load(node)) {
      LOGGER.info("加载闹钟：[{}] - {}", node, clock.toString());
      try {
        setClock(clock, current, false);
        if (move) {// 当新旧节点不相同时，删除旧节点上的闹钟
          deleteClock(clock.getId(), node, false); // 仅删除旧节点的闹钟，不删除 TASK。
        }
      } catch (AlarmClockException ex) {
        LOGGER.error("加载闹钟失败：From[{}] To[{}] - {} - {}", node, current, clock.toString(), ex.getMessage());
      } catch (Exception ex) {
        LOGGER.error("加载闹钟异常：From[{}] To[{}] - {} - {}", node, current, clock.toString(), ex.getMessage());
      }
    }
    LOADING.remove(node);
  }

  /**
   * 设置闹钟
   * @param clock
   * @param node
   * @return 返回新创建的闹钟
   * @throws AlarmClockException
   */
  public AlarmClockResource setClock(AlarmClockResource clock, String node) throws AlarmClockException {
    return setClock(clock, node, true);
  }

  /**
   * 根据闹钟ID删除闹钟。
   * @param id
   * @param node
   * @return 返回已删除的闹钟
   * @throws AlarmClockException
   */
  public AlarmClockResource deleteClock(String id, String node) throws AlarmClockException {
    return deleteClock(id, node, true);
  }

  private AlarmClockResource deleteClock(String id, String node, boolean isRemoveTask) throws AlarmClockException {
    AlarmClockResource c = cda.delClock(node, id);
    if (isRemoveTask) {
      removeTask(id);
    }
    LOGGER.info("删除闹钟成功：[{}] [{}] {}", node, id, c.toString());
    return c;
  }

  /**
   * 设置闹钟
   * @param clock
   * @param node
   * @param errorOnPast 当响铃时间为过去时，是否抛出异常。接口调用场景为 true，服务启动时加载为 false
   * @return
   * @throws AlarmClockException
   */
  private AlarmClockResource setClock(AlarmClockResource clock, String node, boolean errorOnPast) throws AlarmClockException {
    TargetResource target = validateTarget(validatePrecision(clock));
    long delay = validateRingTime(clock, errorOnPast);
    cda.setClock(node, clock); // append 之前要先保存，保证先生成 clockId
    TASKS.put(clock.getId(), appendToWheel(node, clock, target, delay));
    checkChainLength(clock);
    return clock;
  }

  /**
   * 添加到时间轮上
   * @param clock
   * @param target
   * @param delay
   * @throws AlarmClockException
   */
  private Timeout appendToWheel(String node, AlarmClockResource clock, TargetResource target, long delay) throws AlarmClockException {
    TimerTask task;
    switch (clock.getPrecision()) {
      case Constants.CLOCK_PRECISION_1S:
        task = TaskFactory.create(clock, target, cda, node, SECOND_EXECUTOR);
        return SECOND_TIMER.newTimeout(task, delay, TimeUnit.SECONDS);
      case Constants.CLOCK_PRECISION_1M:
        task = TaskFactory.create(clock, target, cda, node, MINUTE_EXECUTOR);
        return MINUTE_TIMER.newTimeout(task, delay / 60, TimeUnit.MINUTES);
      default:
        throw new AlarmClockException(Errors.BAD_REQUEST, "精度不正确", clock);
    }
  }

  /**
   * 验证响铃时间，返回 delay
   * @param clock
   * @return
   * @throws AlarmClockException
   */
  private long validateRingTime(AlarmClockResource clock, boolean errorOnPast) throws AlarmClockException {
    long now = System.currentTimeMillis() / 1000;
    if (clock.getRingAt() < now) {
      if (errorOnPast) {
        throw new AlarmClockException(Errors.BAD_REQUEST, "闹钟响铃时间是已过去的时间，请设置为未来时间。", clock);
      }
      return 0;
    }
    return clock.getRingAt() - now;
  }

  /**
   * 验证精度
   * @param clock
   * @return
   * @throws AlarmClockException
   */
  private AlarmClockResource validatePrecision(AlarmClockResource clock) throws AlarmClockException {
    String p = null == clock.getPrecision() ? Constants.CLOCK_PRECISION_1S : clock.getPrecision().trim().toLowerCase();
    if (p.isEmpty()) {
      p = Constants.CLOCK_PRECISION_1S;
    }
    switch (p) {
      case Constants.CLOCK_PRECISION_1S:
      case Constants.CLOCK_PRECISION_1M:
        break;
      default: throw new AlarmClockException(Errors.BAD_REQUEST, "未支持此精度：{" + clock.getPrecision() + "]", clock);
    }
    clock.setPrecision(p);
    return clock;
  }

  /**
   * 验证目标，返回目标
   * @param clock
   * @return
   * @throws AlarmClockException
   */
  private TargetResource validateTarget(AlarmClockResource clock) throws AlarmClockException {
    TargetResource target = tda.getTarget(clock.getTarget());
    if (null == target) {
      throw new AlarmClockException(Errors.BAD_REQUEST, "未配置响铃的目标动作", clock);
    }
    return target;
  }

  private void checkChainLength(AlarmClockResource clock) {
    CAL_CHAIN_LENGTH_EXECUTOR.submit(() -> {
      HashedWheelTimer wheelTimer;
      switch (clock.getPrecision()) {
        case Constants.CLOCK_PRECISION_1S:
          wheelTimer = SECOND_TIMER;
          break;
        case Constants.CLOCK_PRECISION_1M:
          wheelTimer = MINUTE_TIMER;
          break;
        default:
          throw new AlarmClockException(Errors.BAD_REQUEST, "精度不正确", clock);
      }
      int maxLength = wheelTimer.getMaxLength();
      LOGGER.info("最长链长为: [{}]", maxLength);
      if (maxLength >= Constants.MAX_CHAIN_LENGTH) {
        String warnMsg = "最长链长为[" + maxLength + "]";
        LOGGER.warn(warnMsg);
        dtService.sendMsg(warnMsg);
      }
    });
  }

}
