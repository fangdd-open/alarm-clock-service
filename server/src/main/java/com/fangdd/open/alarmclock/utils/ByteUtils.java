package com.fangdd.open.alarmclock.utils;

/**
 * 字节工具
 * @author hzm
 */
public abstract class ByteUtils {

  private static final char[] HEX_LOWER_CASE = "0123456789abcdef".toCharArray();
  private static final char[] HEX_UPPER_CASE = "0123456789ABCDEF".toCharArray();

  private ByteUtils() {}

  /**
   * 将字节数组转成十六进制的字符串
   * @param bytes
   * @param upcase
   * @return
   */
  public static String toHexString(byte[] bytes, boolean upcase) {
    if (null == bytes || bytes.length == 0) {
      return "";
    }
    char[] hexChars = upcase ? HEX_UPPER_CASE : HEX_LOWER_CASE;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(hexChars[(0xF0 & bytes[i]) >> 4]).append(hexChars[0x0F & bytes[i]]);
    }
    return sb.toString();
  }

  /**
   * 将十六进制字符串转成字节数组
   * @param input
   * @return
   */
  public static byte[] fromHexString(String input) {
    if ((null == input) || input.isEmpty()) {
      return new byte[0];
    }
    String hex = input.trim().toUpperCase();
    if (hex.length() % 2 != 0) {
      hex = "0" + hex;
    }
    byte[] ret = new byte[hex.length() / 2];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = (byte)((position(HEX_UPPER_CASE, hex.charAt(i * 2)) << 4) | position(HEX_UPPER_CASE, hex.charAt(i * 2 + 1)));
    }
    return ret;
  }

  private static int position(char[] chars, char w) {
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == w) {
        return i;
      }
    }
    return -1;
  }

}
