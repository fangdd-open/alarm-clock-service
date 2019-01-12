package com.fangdd.open.alarmclock.resource.representation;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * RabbitMQ 配置
 */
@Getter
@Setter
public class TargetConfigForRabbitMqResource {

  /**
   * RabbitMQ服务器
   */
  private String host;

  /**
   * RabbitMQ端口
   */
  private Integer port;

  /**
   * RabbitMQ虚拟主机
   */
  @JsonProperty("virtual-host")
  @JSONField(name = "virtual-host")
  private String virtualHost;

  /**
   * RabbitMQ用户名
   */
  private String username;

  /**
   * RabbitMQ密码
   */
  private String secret;

  /**
   * 接收事件消息的类型，exchange 或 queue
   */
  private String type;

  /**
   * exchange 或 queue 的名称
   */
  private String name;

  /**
   * exchange 的类型（如果 type 是 exchange 的话），有 topic/fanout/direct/headers，默认为 direct
   */
  @JsonProperty("exchange-type")
  @JSONField(name = "exchange-type")
  private String exchangeType;

  /**
   * exchange 或 queue 的持久化属性，有 durable/transient，默认为 durable
   */
  private String durability;

  /**
   * exchange 或 queue 是否自动删除，有 yes/no，默认为 no
   */
  @JsonProperty("auto-delete")
  @JSONField(name = "auto-delete")
  private String autoDelete;

  public String identify() {
    return username + ":" + secret + "@" + host + ":" + port + "/" + virtualHost;
  }

  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  public boolean isExchange() {
    return (null != type) && "exchange".equalsIgnoreCase(type.trim()); // 明确指定为 exchange 才为 true
  }

  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  public boolean isDurable() {
    return (null == durability) || !"transient".equalsIgnoreCase(durability.trim()); // 明确指定为 transient 才为 false
  }

  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  public boolean isAutoDelete() {
    return (null != autoDelete) && "yes".equalsIgnoreCase(autoDelete.trim()); // 明确指定为 yes 才为 true
  }

}
