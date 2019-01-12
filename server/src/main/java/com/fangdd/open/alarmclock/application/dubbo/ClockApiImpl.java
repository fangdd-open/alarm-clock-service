package com.fangdd.open.alarmclock.application.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.fangdd.open.alarmclock.api.ClockApi;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.service.AlarmClockService;
import com.fangdd.open.alarmclock.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ClockApiImpl implements ClockApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClockApiImpl.class);

  @Autowired
  private AlarmClockService alarmClockService;
  @Autowired private NodeService nodeService;

  @Override
  public AlarmClockResource createClock(AlarmClockResource alarm) {
    String nid = nodeService.getNodeId(null);
    LOGGER.info("Dubbo接口设置闹钟请求：[{}] - {}", nid, alarm.toString());
    AlarmClockResource clock = alarmClockService.setClock(alarm, nid);
    LOGGER.info("Dubbo接口设置闹钟成功：[{}] - {}", nid, clock.toString());
    return clock;
  }

  @Override
  public AlarmClockResource removeClock(String clockId) {
    LOGGER.info("Dubbo接口删除闹钟请求 ID[{}]", clockId);
    return alarmClockService.deleteClock(clockId, nodeService.getNodeId(null));
  }

  @Override
  public void loadClocks(String node) {
    alarmClockService.load(node);
  }

}
