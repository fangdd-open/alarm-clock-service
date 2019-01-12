package com.fangdd.open.alarmclock.application.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.api.TargetApi;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import com.fangdd.open.alarmclock.service.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TargetApiImpl implements TargetApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(TargetApiImpl.class);

  @Autowired
  private TargetService targetService;

  @Override
  public TargetResource createTarget(TargetResource target) {
    LOGGER.info("Dubbo接口创建目标：{}", JSON.toJSONString(target));
    return targetService.setTarget(target);
  }

  @Override
  public TargetResource removeTarget(String targetId) {
    LOGGER.info("Dubbo接口删除目标：{}", targetId);
    return targetService.deleteTarget(targetId);
  }
}
