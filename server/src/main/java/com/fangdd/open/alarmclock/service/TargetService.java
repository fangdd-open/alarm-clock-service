package com.fangdd.open.alarmclock.service;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.config.Constants;
import com.fangdd.open.alarmclock.data.access.TargetDataAccess;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 目标服务
 */
@Service
public class TargetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TargetService.class);

  @Autowired private TargetDataAccess da;

  /**
   * 设置目标
   * @param target
   * @return 返回新创建的目标
   * @throws AlarmClockException
   */
  public TargetResource setTarget(TargetResource target) throws AlarmClockException {
    TargetResource t = da.setTarget(validateType(target));
    LOGGER.info("保存目标：" + JSON.toJSONString(t));
    return t;
  }

  /**
   * 根据ID删除目标。
   * @param id
   * @return 返回已删除的目标
   * @throws AlarmClockException
   */
  public TargetResource deleteTarget(String id) throws AlarmClockException {
    TargetResource t = da.delClock(id);
    LOGGER.info("删除目标：" + JSON.toJSONString(t));
    return t;
  }

  /**
   * 根据id查询目标
   * @param id
   * @return
   * @throws AlarmClockException
   */
  public TargetResource getTarget(String id) {
    return da.getTarget(id);
  }

  private TargetResource validateType(TargetResource target) throws AlarmClockException {
    switch (target.getType()) {
      case Constants.TARGET_TYPE_RABBIT_MQ: return target;
      case Constants.TARGET_TYPE_ACTIVE_MQ:
      case Constants.TARGET_TYPE_HTTP:
        throw new AlarmClockException(Errors.BAD_REQUEST, "未实现的目标类型", target);
      default:
        throw new AlarmClockException(Errors.BAD_REQUEST, "错误的目标类型", target);
    }
  }

}
