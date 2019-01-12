package com.fangdd.open.alarmclock.service;

import com.alibaba.fastjson.JSONObject;
import com.fangdd.open.alarmclock.data.cache.RedisCache;
import com.fangdd.open.alarmclock.data.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 与钉钉交互
 */
@Service
public class DingTalkService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DingTalkService.class);

  private static final String DING_ROBOT_URL = "https://oapi.dingtalk.com/robot/send?access_token=";
  private static final String DING_CONTENT = "content";
  private static final String DING_MESSAGE_TYPE = "msgtype";
  private static final String DING_TEXT_KEY = "text";

  private static final String KEY = "alarmclock:clock:robotLock";
  private static final String LOCK_VALUE = "locked";
  private static final int LOCK_TIME = 5 * 60;

  @Value("${fangdd.alarm.ding-robot-token}")
  private String accessToken;

  @Autowired
  RedisCache redis;

  public void sendMsg(String msg) {
    if (!redis.setNx(KEY, LOCK_VALUE, LOCK_TIME)) {
      //防止发的太频繁
      return;
    }
    String url = DING_ROBOT_URL.concat(accessToken);
    JSONObject text = new JSONObject();
    text.put(DING_CONTENT, msg);
    JSONObject json = new JSONObject();
    json.put(DING_MESSAGE_TYPE, "text");
    json.put(DING_TEXT_KEY, text);
    LOGGER.info("发送钉钉告警: [{}]", msg);
    HttpClient.postJsonAndExpectJson(url, json);
  }

}
