package com.fangdd.open.alarmclock.error;

import lombok.Getter;

/**
 * 错误码定义。错误码的前三位表示 HTTP 状态码。
 */
@Getter
public enum Errors {

  OK(20000, "成功"),

  BAD_REQUEST  (40000, "非法请求"),
  PARAM_INVALID(40001, "缺少必要参数或者参数错误"),

  AUTHENTICATION(40100, "认证失败"),

  FORBIDDEN(40300, "禁止访问"),

  NOT_FOUND(40400, "资源不存在"),

  METHOD(40500, "请求方法不正确"),

  CONFLICT_DUPLICATE_SUBMISSION(40901, "重复提交"),

  SERVER(50000, "服务器内部错误"),

  ;

  /**
   * 代码代码
   */
  private final int errorCode;

  /**
   * 状态代码
   */
  private final int statusCode;

  /**
   * 错误消息
   */
  private final String message;

  public static Errors fromStatusCode(int status) {
    for (Errors v : values()) {
      if (v.getErrorCode() == 100 * status) {
        return v;
      }
    }
    return SERVER;
  }

  private Errors(int code, String message) {
    this.errorCode  = code;
    this.message    = message;
    this.statusCode = calculateStatusCode(code);
  }

  private int calculateStatusCode(int errorCode) {
    if ((9999 < errorCode) && (errorCode <= 99999)) {
      return errorCode / 100;
    }
    if ((999 < errorCode) && (errorCode <= 9999)) {
      return errorCode / 10;
    }
    if ((99 < errorCode) && (errorCode <= 999)) {
      return errorCode;
    }
    if (errorCode <= 99) {
      return 400;
    }
    int code = errorCode / 100;
    while (code > 999) {
      code /= 10;
    }
    return code;
  }

}
