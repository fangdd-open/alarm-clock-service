package com.fangdd.open.alarmclock.resource.representation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 目标资源
 */
@Getter
@Setter
public class TargetResource implements Serializable {

  /**
   * 目标ID
   */
  private String id;

  /**
   * 目标类型
   */
  private String type;

  /**
   * 目标配置
   */
  private Object config;

}
