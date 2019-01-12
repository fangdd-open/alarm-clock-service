package com.fangdd.open.alarmclock.data.cache;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 */
@Component
public class RedisCache implements Cache {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

  @Autowired StringRedisTemplate redisTemplate;
  private HashOperations<String, String, String> opsForHash;

  @PostConstruct
  public void init() {
    opsForHash = redisTemplate.opsForHash();
  }

  public Set<String> keys(String pattern) {
    return redisTemplate.keys(pattern);
  }

  @Override
  public byte[] get(byte[] key) {
    try {
      return get(new String(key)).getBytes("UTF-8");
    } catch (UnsupportedEncodingException ex) {
      LOGGER.debug("", ex);
      return get(new String(key)).getBytes();
    }
  }

  @Override
  public String get(String key) {
    return redisTemplate.boundValueOps(key).get();
  }

  @Override
  public void set(byte[] key, byte[] value, int expire) {
    set(new String(key), new String(value), expire);
  }

  @Override
  public void set(String key, String value, int expire) {
    redisTemplate.boundValueOps(key).set(value, expire, TimeUnit.SECONDS);
  }

  @Override
  public void del(byte[] key) {
    del(new String(key));
  }

  @Override
  public void del(String key) {
    redisTemplate.delete(key);
  }

  /**
   *
   * @param key
   * @param value
   * @param expire
   * @return 成功返回true，已经存在返回false
   */
  public boolean setNx(String key, String value, int expire) {
    return redisTemplate.boundValueOps(key).setIfAbsent(value, expire, TimeUnit.SECONDS);
  }

  /**
   * Redis 的 RPUSH
   * @param key
   * @param value
   */
  public void push(String key, String value) {
    redisTemplate.boundListOps(key).rightPush(value);
  }

  /**
   * 获取列表的所有值
   * @param key
   * @return
   */
  public List<String> getList(String key) {
    return redisTemplate.boundListOps(key).range(0, -1);
  }

  /**
   * 设置MAP键值
   * @param key
   * @param hkey
   * @param value
   */
  public void hset(String key, String hkey, String value) {
    opsForHash.put(key, hkey, value);
  }

  public String hget(String key, String hkey) {
    return opsForHash.get(key, hkey);
  }

  /**
   * 删除MAP的键值
   * @param key
   * @param hkey
   */
  public void hdel(String key, String hkey) {
    opsForHash.delete(key, hkey);
  }

  /**
   * 加载MAP所有键值
   * @param key
   * @return
   */
  public Map<String, String> hgetAll(String key) {
    return opsForHash.entries(key);
  }

  public List<String> hgetAllValues(String key) {
    return opsForHash.values(key);
  }

  public void renewal(String key, int expire) {
    if (null == key) {
      return;
    }
    redisTemplate.expire(key, expire, TimeUnit.SECONDS);
  }

  public String getAndDelete(String key) {
    String val = get(key);
    del(key);
    return val;
  }


  /**
   * 获取 TTL（秒），如果 key 不存在，返回 -2； key 存在但无 TTL，返回 -1
   * @param key
   * @return
   */
  public long getTtl(String key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }



}
