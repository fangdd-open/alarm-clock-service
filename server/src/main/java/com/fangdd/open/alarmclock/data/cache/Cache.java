package com.fangdd.open.alarmclock.data.cache;

/**
 * 缓存接口
 * @author hzm
 */
public interface Cache {

  byte[] get(byte[] key);

  String get(String key);

  void set(byte[] key, byte[] value, int expire);

  void set(String key, String value, int expire);

  void del(byte[] key);

  void del(String key);

}
