package com.fangdd.open.alarmclock.data.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fangdd.open.alarmclock.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Http 客户端
 */
public abstract class HttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

  private static final RestTemplate REST_TEMPLATE = new RestTemplate();
  private static final String LOG_PREFIX = "[HTTP] ";

  private static final String STR_CONTENT_TYPE = "Content-Type";
  private static final String STR_UTF8 = "UTF-8";

  public static JSONObject getAndExpectJson(String url, HttpHeaders headers) {
    String res = execute(HttpMethod.GET, url, headers, null);
    return getJsonFromString(res);
  }

  public static JSONObject getAndExpectJson(String url) {
    String res = execute(HttpMethod.GET, url, null, null);
    return getJsonFromString(res);
  }

  public static String getAndExpectString(String url) {
    return execute(HttpMethod.GET, url, null, null);
  }

  public static JSONObject postJsonAndExpectJson(String url, Object body) {
    return postJsonAndExpectJson(url, body, null);
  }

  public static JSONObject postJsonAndExpectJson(String url, Object body, HttpHeaders headers) {
    return getJsonFromString(postJsonAndExpectString(url, body, headers));
  }

  public static String postJsonAndExpectString(String url, Object body, HttpHeaders headers) {
    if (null == headers) {
      headers = new HttpHeaders();
    }
    headers.set(STR_CONTENT_TYPE, "application/json; charset=" + STR_UTF8);
    String res = execute(HttpMethod.POST, url, headers, getUtf8BytesFrom8String(JSON.toJSONString(body)));
    return res;
  }

  public static String postStringAndExpectString(String url, String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(STR_CONTENT_TYPE, "text/plain; charset=" + STR_UTF8);
    return execute(HttpMethod.POST, url, headers, getUtf8BytesFrom8String(body));
  }

  public static String postFormAndExpectString(String url, Map<String, String> body) {
    if (null == body || body.isEmpty()) {
      return postFormAndExpectString(url, "");
    }
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entity : body.entrySet()) {
      try {
        sb.append('&').append(entity.getKey()).append('=').append(URLEncoder.encode(entity.getValue(), STR_UTF8));
      } catch (UnsupportedEncodingException ex) {
        LOGGER.debug("", ex);
      }
    }
    return postFormAndExpectString(url, sb.substring(1));
  }

  public static String postFormAndExpectString(String url, String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(STR_CONTENT_TYPE, "application/x-www-form-urlencoded");
    return execute(HttpMethod.POST, url, headers, getUtf8BytesFrom8String(body));
  }

  public static JSONObject putJsonAndExpectJson(String url, Object body, HttpHeaders headers) {
    byte[] bytes = (null == body) ? null : getUtf8BytesFrom8String(JSON.toJSONString(body));
    if (null == headers) {
      headers = new HttpHeaders();
    }
    headers.set(STR_CONTENT_TYPE, "application/json; charset=" + STR_UTF8);
    String res = execute(HttpMethod.PUT, url, headers, bytes);
    return getJsonFromString(res);
  }

  private static String execute(HttpMethod method, String url, HttpHeaders headers, byte[] body) {
    LOGGER.info(LOG_PREFIX + "{} {} BODY[{}]", method.name(), url, getStringFromUtf8Bytes(body));
    HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
    try {
      URI uri = new URI(url);
      ResponseEntity<byte[]> res = REST_TEMPLATE.exchange(uri, method, entity, byte[].class);
      if ((null == res) || (null == res.getBody())) {
        LOGGER.warn(LOG_PREFIX + "{} {} NULL response.", method.name(), url);
        return "";
      }
      MediaType mediatype = res.getHeaders().getContentType();
      Charset charset = (null == mediatype) ? StandardCharsets.UTF_8 : mediatype.getCharset();
      return new String(res.getBody(), (null == charset) ? StandardCharsets.UTF_8 : charset);
    } catch (HttpServerErrorException | HttpClientErrorException ex) {
      LOGGER.error(LOG_PREFIX + "{} {} [{}] {}", method.name(), url, ex.getStatusCode().toString(), ex.getResponseBodyAsString());
      LOGGER.debug("", ex);
    } catch (URISyntaxException ex) {
      LOGGER.error(LOG_PREFIX + "非法URL：[{}] {}", method.name(), url);
    }
    return null;
  }

  private static String getStringFromUtf8Bytes(byte[] bytes) {
    if (null == bytes || bytes.length == 0) {
      return "";
    }
    try {
      return new String(bytes, STR_UTF8);
    } catch (UnsupportedEncodingException ex) {
      LOGGER.error(LOG_PREFIX + "使用 " + STR_UTF8 + " 创建字符串失败 ");
      LOGGER.debug("", ex);
      return new String(bytes);
    }
  }

  private static byte[] getUtf8BytesFrom8String(String str) {
    try {
      return str.getBytes(STR_UTF8);
    } catch (UnsupportedEncodingException ex) {
      LOGGER.warn(LOG_PREFIX + "转换编码失败，无法转成 " + STR_UTF8 + " 字节流");
      LOGGER.debug("", ex);
      return str.getBytes();
    }
  }

  private static JSONObject getJsonFromString(String str) {
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    return JSON.parseObject(str);
  }

}
