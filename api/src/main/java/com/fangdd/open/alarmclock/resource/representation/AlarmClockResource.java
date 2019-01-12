package com.fangdd.open.alarmclock.resource.representation;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 闹钟资源
 */
@Getter
@Setter
public class AlarmClockResource implements Serializable {

  /**
   * 闹钟ID
   */
  private String id;

  /**
   * 响铃时间
   */
  @NotNull(message = "响铃时间不能为空")
  @Min(value = 1532567890, message = "响铃时间不正确")
  @JsonProperty("ring-at")
  @JSONField(name = "ring-at")
  private Long ringAt;

  /**
   * 响铃目标
   */
  @NotBlank(message = "响铃目标不能为空")
  private String target;

  /**
   * 响铃精度：1s - 1秒，1m - 1分钟
   */
  @NotBlank(message = "响铃精度不能空")
  private String precision;

  /**
   * 消息负荷
   */
  @NotBlank(message = "消息负荷不能空")
  private String payload;

  @Override
  public String toString() {
    return "ID[" + id + "] Target[" + target + "] Ring At[" + ringAt + "] Precision[" + precision + "] - [" + payload + "]";
  }

}
