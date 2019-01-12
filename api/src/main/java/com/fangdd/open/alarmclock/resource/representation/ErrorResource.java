package com.fangdd.open.alarmclock.resource.representation;

import com.fangdd.open.alarmclock.error.Errors;
import lombok.Getter;

/**
 * 错误响应
 */
public class ErrorResource {

  @Getter
  private final int code;

  @Getter
  private final String message;

  public ErrorResource(Errors error) {
    this(error, null);
  }

  public ErrorResource(Errors error, String msg) {
    this.code = error.getErrorCode();
    this.message = (null == msg) || msg.isEmpty() ? error.getMessage() : msg.trim();
  }

}
