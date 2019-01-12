package com.fangdd.open.alarmclock.config;

import com.fangdd.open.alarmclock.application.rest.AlarmClockInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Autowired private AlarmClockInterceptor alarmClockInterceptor;

  /**
   * 拦截器
   * @param registry
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(alarmClockInterceptor);
  }

}
