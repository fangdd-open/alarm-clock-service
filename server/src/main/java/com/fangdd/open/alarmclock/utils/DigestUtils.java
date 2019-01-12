package com.fangdd.open.alarmclock.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author hzm
 */
public abstract class DigestUtils {

  private static final String MD5 = "MD5";
  private static final byte[] MD5_NULL_BYTES = ByteUtils.fromHexString("d41d8cd98f00b204e9800998ecf8427e");

  private static final String SHA1 = "SHA1";
  private static final byte[] SHA1_NULL_BYTES = ByteUtils.fromHexString("da39a3ee5e6b4b0d3255bfef95601890afd80709");

  public static String md5(String input) {
    return ByteUtils.toHexString(digest(input, MD5), false);
  }

  public static byte[] md5Digest(String input) {
    return digest(input, MD5);
  }

  public static String sha1(String input) {
    return ByteUtils.toHexString(digest(input, SHA1), false);
  }

  public static byte[] sha1Digest(String input) {
    return digest(input, SHA1);
  }

  private static byte[] digest(String input, String method) {
    if ((null == input) || input.isEmpty()) {
      return getNullDigest(method);
    }
    try {
      MessageDigest digest = MessageDigest.getInstance(method);
      digest.update(input.getBytes(StandardCharsets.UTF_8));
      return digest.digest();
    } catch (NoSuchAlgorithmException ex) {
      ignored(ex);
    }
    return getNullDigest(method);
  }

  private static byte[] getNullDigest(String method) {
    switch (method) {
      case MD5: return MD5_NULL_BYTES;
      case SHA1: return SHA1_NULL_BYTES;
      default: return new byte[0];
    }
  }

  protected static void ignored(Exception ex) {
    // 使用 public 或 protected 方法来忽略异常
  }

}
