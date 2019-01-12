package com.fangdd.open.alarmclock.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 网卡配置
 */
public class NetworkInterfaceConfig {

  /**
   * 返回一个局域网的IPv4地址
   * @return
   * @throws SocketException
   */
  public static Inet4Address getBaseServiceLocalNetworkInet4Address() throws SocketException {
    List<Inet4Address> ips = getAllLocalNetworkInet4Address();
    return ips.isEmpty() ? null : ips.get(0);
  }

  /**
   * 获取所有局域网的IPv4地址
   * @return
   * @throws SocketException
   */
  public static List<Inet4Address> getAllLocalNetworkInet4Address() throws SocketException {
    List<Inet4Address> inetAddresses = new ArrayList<>();
    for (InetAddress ia : getAllLocalNetworkInetAddress()) {
      if (ia instanceof Inet4Address) {
        inetAddresses.add((Inet4Address)ia);
      }
    }
    return inetAddresses;
  }

  /**
   * 获取所有局域网IP地址
   * @return
   * @throws SocketException
   */
  public static List<InetAddress> getAllLocalNetworkInetAddress() throws SocketException {
    List<InetAddress> inetAddresses = new ArrayList<>();
    for (InetAddress ia : getAllInetAddresses()) {
      if (ia.isSiteLocalAddress()) {
        inetAddresses.add(ia);
      }
    }
    return inetAddresses;
  }

  /**
   * 获取所有网卡的IP地址
   * @return
   * @throws SocketException
   */
  public static List<InetAddress> getAllInetAddresses() throws SocketException {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    List<InetAddress> inetAddresses = new ArrayList<>();
    while(interfaces.hasMoreElements()) {
      NetworkInterface ni = interfaces.nextElement();
      Enumeration<InetAddress> addresss = ni.getInetAddresses();
      while(addresss.hasMoreElements()){
        inetAddresses.add(addresss.nextElement());
      }
    }
    return inetAddresses;
  }
}
