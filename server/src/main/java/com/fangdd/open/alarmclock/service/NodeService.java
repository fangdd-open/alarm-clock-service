package com.fangdd.open.alarmclock.service;

import com.fangdd.open.alarmclock.config.NetworkInterfaceConfig;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import java.net.Inet4Address;
import java.net.SocketException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 节点服务
 */
@Service
public class NodeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeService.class);

  private String id;

  public String getNodeId() throws AlarmClockException {
    return id;
  }

  public String getNodeId(String node) throws AlarmClockException {
    if (null == node) {
      return id;
    }
    String n = node.trim();
    return n.isEmpty() ? id : n;
  }

  @PostConstruct
  public void init() throws AlarmClockException {
    try {
      Inet4Address ia = NetworkInterfaceConfig.getBaseServiceLocalNetworkInet4Address();
      id = ia.getHostAddress().trim();
      LOGGER.info("主机节点IP[{}]", id);
    } catch (SocketException ex) {
      throw new AlarmClockException(Errors.SERVER, "获取本机 IP 及主机名失败", ex);
    }
  }

}
