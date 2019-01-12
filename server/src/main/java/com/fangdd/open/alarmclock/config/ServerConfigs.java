package com.fangdd.open.alarmclock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fangdd.alarmclock")
public class ServerConfigs {


}
