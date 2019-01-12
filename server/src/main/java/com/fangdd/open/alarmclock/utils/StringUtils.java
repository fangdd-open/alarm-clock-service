package com.fangdd.open.alarmclock.utils;

import java.util.UUID;

/**
 * 字符串工具
 */
public class StringUtils {

  public static String newRandomUuid() {
    return UUID.randomUUID().toString().replace("-", "").toLowerCase();
  }

  public static boolean isEmpty(String text) {
    return (null == text) || text.isEmpty();
  }

}
