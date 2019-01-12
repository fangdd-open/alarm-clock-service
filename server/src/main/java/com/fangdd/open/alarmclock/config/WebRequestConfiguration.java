package com.fangdd.open.alarmclock.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web 请求配置
 */
public class WebRequestConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebRequestConfiguration.class);

  public static final String ATTR_REQUEST_TIME = "REQUEST_TIME";
  public static final String ATTR_REQUEST_REAL_IP = "Request_Real_Ip";

  public static boolean isPrivateIpAddress(String ipOrHostname) {
    try {
      InetAddress addr = InetAddress.getByName(ipOrHostname);
      return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
    } catch (UnknownHostException ex) {
      LOGGER.error("无法解析IP地址：{}", ipOrHostname, ex);
      return false;
    }
  }

}
