package com.fangdd.open.alarmclock;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication
@EnableDubbo
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public String toString() {
    return "Alarm Clock Server";
  }

}