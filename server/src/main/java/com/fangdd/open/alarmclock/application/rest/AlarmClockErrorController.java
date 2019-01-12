package com.fangdd.open.alarmclock.application.rest;

import com.alibaba.fastjson.JSON;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import com.fangdd.open.alarmclock.resource.ResourceNames;
import com.fangdd.open.alarmclock.resource.representation.ErrorResource;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest 应用的异常处理器
 */
@RestController
@ControllerAdvice
public class AlarmClockErrorController implements ErrorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlarmClockErrorController.class);

  /**
   * 处理参数校验失败的情况
   * @param ex
   * @return
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorResource handleConstraintValidationException(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<String> messages = new ArrayList<>();
    for (ObjectError item : result.getAllErrors()) {
      if (null != item) {
        messages.add(item.getDefaultMessage());
      }
    }
    String msg = Errors.PARAM_INVALID.getMessage() + "：" + String.join("；", messages);
    LOGGER.error("参数校验失败：{}", msg);
    return new ErrorResource(Errors.PARAM_INVALID, msg);
  }

  /**
   * 处理框架的请求数据绑定的异常
   * @param ex
   * @return
   */
  @ExceptionHandler(value = ServletRequestBindingException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorResource handleServletRequestBindingException(ServletRequestBindingException ex) {
    LOGGER.error("SpringMVC数据绑定错误：{}", ex.getMessage());
    return new ErrorResource(Errors.BAD_REQUEST, ex.getMessage());
  }

  /**
   * 处理微服务抛出的异常
   * @param ex
   * @param res
   * @return
   */
  @ExceptionHandler(value = AlarmClockException.class)
  public ErrorResource handleMicroServiceException(AlarmClockException ex, HttpServletResponse res) {
    res.setStatus(ex.getStatusCode());
    LOGGER.error("服务异常：状态码[{}]；错误码[{}]；{}", ex.getStatusCode(), ex.getErrorCode(), ex.getMessage());
    return new ErrorResource(ex.getError(), ex.getMessage());
  }

  /**
   * 处理一般异常
   * @param ex
   * @return
   */
  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResource handleException(Exception ex) {
    LOGGER.error("未处理的异常", ex);
    return new ErrorResource(Errors.SERVER, ex.getMessage());
  }

  @Override
  public String getErrorPath() {
    return ResourceNames.ERROR;
  }

  @RequestMapping(value = ResourceNames.ERROR)
  public ErrorResource error(HttpServletResponse res) {
    ErrorResource mr = new ErrorResource(Errors.fromStatusCode(res.getStatus()));
    LOGGER.error("请求响应异常：原始状态[{}]，响应内容：{}", res.getStatus(), JSON.toJSONString(mr));
    return mr;
  }

}
