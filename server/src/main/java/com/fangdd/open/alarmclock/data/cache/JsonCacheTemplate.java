package com.fangdd.open.alarmclock.data.cache;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JSON 对象缓存模板
 * @param <T>
 */
@Component
public class JsonCacheTemplate<T> {

  @Autowired
  private RedisCache cache;

  public Set<String> keys(String pattern) {
    return cache.keys(pattern);
  }

  public long getTtl(String key) {
    return cache.getTtl(key);
  }

  public T get(String key, Class<T> clazz) {
    String val = cache.get(key);
    if ((null == val) || val.isEmpty()) {
      return null;
    }
    return JSON.parseObject(val, clazz);
  }

  public List<T> getList(String key, Class<T> clazz) {
    String val = cache.get(key);
    if ((null == val) || val.isEmpty()) {
      return new ArrayList<>();
    }
    return JSON.parseArray(val, clazz);
  }

  public void set(String key, T val, int expire) {
    cache.set(key, JSON.toJSONString(val), expire);
  }

  public void set(String key, List<T> vals, int expire) {
    cache.set(key, JSON.toJSONString(vals), expire);
  }

  public void del(String key) {
    cache.del(key);
  }

  public T getAndDelete(String key, Class<T> clazz) {
    T val = get(key, clazz);
    if (null != val) {
      del(key);
    }
    return val;
  }

}
