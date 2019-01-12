package com.fangdd.open.alarmclock.application.rest;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.resource.ResourceNames;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import com.fangdd.open.alarmclock.service.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 目标接口
 */
@RestController
public class TargetController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TargetController.class);

  @Autowired private TargetService targetService;

  /**
   * 创建目标
   * @param target
   * @return
   * @throws AlarmClockException
   */
  @RequestMapping(path = ResourceNames.TARGET, method = RequestMethod.PUT)
  public TargetResource createTarget(
          @Validated @RequestBody TargetResource target
  ) throws AlarmClockException {
    LOGGER.info("创建目标：{}", JSON.toJSONString(target));
    return targetService.setTarget(target);
  }

  /**
   * 删除目标
   * @param id
   * @return
   * @throws AlarmClockException
   */
  @RequestMapping(path = ResourceNames.TARGET_ID, method = RequestMethod.DELETE)
  public TargetResource removeTarget(
          @PathVariable(name = "id") String id
  ) throws AlarmClockException {
    LOGGER.info("删除目标：{}", id);
    return targetService.deleteTarget(id);
  }

}
