package com.fangdd.open.alarmclock.data.access;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.data.cache.RedisCache;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 闹钟存储器
 */
@Component
public class ClockDataAccess {

  private static final String KEY = "alarmclock:clock:";

  @Autowired private RedisCache redis;

  public AlarmClockResource setClock(String node, AlarmClockResource clock) {
    if (null == clock.getId() || clock.getId().isEmpty()) {
      clock.setId(StringUtils.newRandomUuid());
    }
    redis.hset(KEY + node, clock.getId(), JSON.toJSONString(clock));
    return clock;
  }

  public AlarmClockResource getClock(String node, String id) {
    String clock = redis.hget(KEY + node, id);
    if (null == clock || clock.isEmpty()) {
      return null;
    }
    return JSON.parseObject(clock, AlarmClockResource.class);
  }

  public AlarmClockResource delClock(String node, String id) {
    AlarmClockResource clock = getClock(node, id);
    redis.hdel(KEY + node, id);
    return clock;
  }

  public List<AlarmClockResource> load(String node) {
    List<String> values = redis.hgetAllValues(KEY + node);
    List<AlarmClockResource> ret = new ArrayList<>(null == values ? 0 : values.size());
    if (null == values || values.isEmpty()) {
      return ret;
    }
    for (String v : values) {
      ret.add(JSON.parseObject(v, AlarmClockResource.class));
    }
    return ret;
  }

}
