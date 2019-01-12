package com.fangdd.open.alarmclock.error;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import lombok.Getter;

/**
 * 闹钟服务异常
 */
@Getter
public class AlarmClockException extends RuntimeException {

  private final Errors error;

  public AlarmClockException() {
    this(Errors.NOT_FOUND);
  }

  public AlarmClockException(Errors error) {
    super(error.getMessage());
    this.error = error;
  }

  public AlarmClockException(Errors error, String message) {
    super(message);
    this.error = error;
  }

  public AlarmClockException(Errors error, String message, AlarmClockResource clock) {
    super(message + "[" + JSON.toJSONString(clock) + "]");
    this.error = error;
  }

  public AlarmClockException(Errors error, String message, TargetResource target) {
    super(message + "[" + JSON.toJSONString(target) + "]");
    this.error = error;
  }

  public AlarmClockException(Errors error, Throwable cause) {
    this(error, error.getMessage(), cause);
  }

  public AlarmClockException(Errors error, String message, Throwable cause) {
    super(message, cause);
    this.error = error;
  }

  /**
   * 获取错误码
   * @return
   */
  public int getErrorCode() {
    return this.error.getErrorCode();
  }

  /**
   * 获取 HTTP 状态码
   * @return
   */
  public int getStatusCode() {
    return this.error.getStatusCode();
  }

}
