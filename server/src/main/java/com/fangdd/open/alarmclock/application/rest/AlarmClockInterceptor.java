package com.fangdd.open.alarmclock.application.rest;

import com.fangdd.open.alarmclock.config.WebRequestConfiguration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器
 */
@Component
public class AlarmClockInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlarmClockInterceptor.class);

  private static final String[] REMOTE_IP_HEADERS = new String[] {
    "X-Real-IP", "Client-Ip", "X-Forwarded-For", "X-Forwarded",
    "X-Cluster-Client-Ip", "Forwarded-For", "Forwarded",
  };

  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    init(req);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView mv) throws Exception {
    // nothing to do
  }

  @Override
  public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
    logAccess(req, res);
  }

  /**
   * 请求属性的初始化
   */
  private void init(HttpServletRequest req) {
    req.setAttribute(WebRequestConfiguration.ATTR_REQUEST_TIME, System.currentTimeMillis());
    req.setAttribute(WebRequestConfiguration.ATTR_REQUEST_REAL_IP, getRemoteIp(req));
  }

  /**
   * 记录 Access 日志
   * @param req
   * @param res
   */
  private void logAccess(HttpServletRequest req, HttpServletResponse res) {
    Long reqAt = (Long)req.getAttribute(WebRequestConfiguration.ATTR_REQUEST_TIME);
    if (null == reqAt) {
      LOGGER.warn("HttpServletRequest is NULL");
      return;
    }
    long time = System.currentTimeMillis() - reqAt;
    int status = res.getStatus();
    String method = req.getMethod();
    String uri = req.getRequestURL().append(null == req.getQueryString() ? "" : "?" + req.getQueryString()).toString();
    String ip = (String)req.getAttribute(WebRequestConfiguration.ATTR_REQUEST_REAL_IP);
    if ((301 == status) || (302 == status)) {
      LOGGER.info("[Access] [{}] [{} {}] {}ms {} {}", ip, status, res.getHeader("Location"), time, method, uri);
    } else {
      LOGGER.info("[Access] [{}] {} {}ms {} {}", ip, status, time, method, uri);
    }
  }

  /**
   * 获取远程IP地址
   * @param req
   * @return
   */
  private String getRemoteIp(HttpServletRequest req) {
    String ip;
    for (String h : REMOTE_IP_HEADERS) {
      ip = req.getHeader(h);
      if ((null == ip) || ip.trim().isEmpty()) {
        continue;
      }
      int pos = ip.indexOf(',');
      if (pos > 0) {
        return ip.substring(0, pos).trim();
      } else {
        return ip.trim();
      }
    }
    return req.getRemoteAddr();
  }

}