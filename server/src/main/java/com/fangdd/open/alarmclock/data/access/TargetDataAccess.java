package com.fangdd.open.alarmclock.data.access;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.config.Constants;
import com.fangdd.open.alarmclock.data.cache.RedisCache;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import com.fangdd.open.alarmclock.resource.representation.TargetConfigForRabbitMqResource;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import com.fangdd.open.alarmclock.utils.DigestUtils;
import com.fangdd.open.alarmclock.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 目标数据存储器
 */
@Component
public class TargetDataAccess {

  private static final String KEY = "alarmclock:target";

  @Autowired private RedisCache redis;

  public TargetResource setTarget(TargetResource target) {
    if (null == target.getId() || target.getId().isEmpty()) {
      target.setId(generateId(target));
    }
    redis.hset(KEY, target.getId(), JSON.toJSONString(target));
    return target;
  }

  private String generateId(TargetResource target) {
    switch (target.getType()) {
      case Constants.TARGET_TYPE_RABBIT_MQ:
        TargetConfigForRabbitMqResource config;
        if (target.getConfig() instanceof JSON) {
          config = JSON.toJavaObject((JSON)target.getConfig(), TargetConfigForRabbitMqResource.class);
        } else {
          config = JSON.parseObject(JSON.toJSONString(target.getConfig()), TargetConfigForRabbitMqResource.class);
        }
        String unique = target.getType().concat(config.identify()).concat(config.getType()).concat(config.getName());
        if (StringUtils.isEmpty(unique)) {
          return StringUtils.newRandomUuid();
        }
        return DigestUtils.md5(unique);
      case Constants.TARGET_TYPE_ACTIVE_MQ:
      case Constants.TARGET_TYPE_HTTP:
        throw new AlarmClockException(Errors.BAD_REQUEST, "未实现的目标类型", target);
      default:
        throw new AlarmClockException(Errors.BAD_REQUEST, "错误的目标类型", target);
    }
  }

  public TargetResource getTarget(String id) {
    String target = redis.hget(KEY, id);
    if (null == target || target.isEmpty()) {
      return null;
    }
    return JSON.parseObject(target, TargetResource.class);
  }

  public TargetResource delClock(String id) {
    TargetResource clock = getTarget(id);
    redis.hdel(KEY, id);
    return clock;
  }

}
