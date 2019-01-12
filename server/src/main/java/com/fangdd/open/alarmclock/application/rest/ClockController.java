package com.fangdd.open.alarmclock.application.rest;

import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.resource.ResourceNames;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.service.AlarmClockService;
import com.fangdd.open.alarmclock.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 闹钟接口
 */
@RestController
public class ClockController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClockController.class);

  @Autowired private AlarmClockService alarmClockService;
  @Autowired private NodeService nodeService;

  /**
   * 创建闹钟
   * @param alarm
   * @param node
   * @return
   * @throws AlarmClockException
   */
  @RequestMapping(path = ResourceNames.ALARM_CLOCK, method = RequestMethod.POST)
  public AlarmClockResource createClock(
                              @Validated @RequestBody AlarmClockResource alarm,
          @RequestParam(name = "node",           required = false) String node
  ) throws AlarmClockException {
    String nid = nodeService.getNodeId(node);
    LOGGER.info("设置闹钟请求：[{}] - {}", nid, alarm.toString());
    AlarmClockResource clock = alarmClockService.setClock(alarm, nid);
    LOGGER.info("设置闹钟成功：[{}] - {}", nid, clock.toString());
    return clock;
  }

  /**
   * 删除闹钟
   * @param id
   * @param node
   * @return
   * @throws AlarmClockException
   */
  @RequestMapping(path = ResourceNames.ALARM_CLOCK_ID, method = RequestMethod.DELETE)
  public AlarmClockResource removeClock(
          @PathVariable(name = "id")                               String id,
          @RequestParam(name = "node",          required = false) String node
  ) throws AlarmClockException {
    LOGGER.info("删除闹钟请求：NODE[{}], ID[{}]", node, id);
    return alarmClockService.deleteClock(id, nodeService.getNodeId(node));
  }

  /**
   * 加载指定节点的闹钟
   * @param node
   * @throws AlarmClockException
   */
  @RequestMapping(path = ResourceNames.ALARM_CLOCK_LOAD, method = RequestMethod.POST)
  public void loadClocks(@RequestParam(name = "node", required = true) String node) throws AlarmClockException {
    alarmClockService.load(node);
  }

}
