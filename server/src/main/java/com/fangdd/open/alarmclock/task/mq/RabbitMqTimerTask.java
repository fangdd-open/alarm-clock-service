package com.fangdd.open.alarmclock.task.mq;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.data.access.ClockDataAccess;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.resource.representation.TargetConfigForRabbitMqResource;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import com.fangdd.open.alarmclock.service.AlarmClockService;
import com.fangdd.open.alarmclock.task.ConcurrentTimerTask;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class RabbitMqTimerTask extends ConcurrentTimerTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqTimerTask.class);

  private static final Map<String, Connection> POOL = new ConcurrentHashMap<>();

  private final ClockDataAccess cda;
  private final AlarmClockResource clock;
  private final TargetConfigForRabbitMqResource config;
  private final String node;
  private final String connId;

  public RabbitMqTimerTask(AlarmClockResource clock, TargetResource target, ClockDataAccess cda, String node,
                           ExecutorService executorService) {
    super(executorService);
    this.cda = cda;
    this.clock = clock;
    this.config = getConfig(target);
    this.connId = this.config.identify();
    this.node = node;
  }

  private TargetConfigForRabbitMqResource getConfig(TargetResource target) {
    if (target.getConfig() instanceof JSON) {
      return JSON.toJavaObject((JSON)target.getConfig(), TargetConfigForRabbitMqResource.class);
    }
    return JSON.parseObject(JSON.toJSONString(target.getConfig()), TargetConfigForRabbitMqResource.class);
  }

  private Connection getConnection() throws IOException, TimeoutException {
    Connection conn = POOL.get(connId);
    if (null == conn) {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(config.getHost());
      factory.setPort(config.getPort());
      factory.setVirtualHost(config.getVirtualHost());
      factory.setUsername(config.getUsername());
      factory.setPassword(config.getSecret());
      conn = factory.newConnection();
      POOL.put(connId, conn);
    }
    return conn;
  }

  @Override
  public void run() {
    if (null == cda.getClock(node, clock.getId())) { // 闹钟已删除
      LOGGER.warn("[响铃]闹钟已被删除：[{}]", clock.toString());
      timeout.cancel();
      return;
    }
    if (timeout.isCancelled()) {
      LOGGER.warn("[响铃]闹钟已被取消：[{}]", clock.toString());
      return;
    }
    try (Channel channel = getConnection().createChannel()) {
      if (config.isExchange()) {
        toExchange(channel);
      } else {
        toQueue(channel);
      }
      cda.delClock(node, clock.getId());
      AlarmClockService.removeTask(clock.getId());
    } catch (IOException ex) {
      LOGGER.error("[响铃]发送消息失败：{} {} {}", clock.toString(), ex.getMessage(), ex);
    } catch (TimeoutException ex) {
      LOGGER.error("[响铃]发送消息超时：{} {} {}", clock.toString(), ex.getMessage(), ex);
    }
  }

  private void toExchange(Channel channel) throws IOException {
    channel.exchangeDeclare(config.getName(), config.getExchangeType(), config.isDurable(), config.isAutoDelete(), null);
    channel.basicPublish(config.getName(), "", null, clock.getPayload().getBytes("UTF-8"));
    LOGGER.info("[响铃]发送消息到 Exchange[{}] - ID[{}] RINGAT[{}] [{}]", config.getName(), clock.getId(), clock.getRingAt(), clock.getPayload());
  }

  private void toQueue(Channel channel) throws IOException {
    channel.queueDeclare(config.getName(), config.isDurable(), false, config.isAutoDelete(), null);
    channel.basicPublish("", config.getName(), null, clock.getPayload().getBytes("UTF-8"));
    LOGGER.info("[响铃]发送消息到 Queue[{}] - ID[{}] RINGAT[{}] [{}]", config.getName(), clock.getId(), clock.getRingAt(), clock.getPayload());
  }

}
