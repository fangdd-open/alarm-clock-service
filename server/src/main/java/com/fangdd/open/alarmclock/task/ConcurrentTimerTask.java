package com.fangdd.open.alarmclock.task;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.ExecutorService;

public abstract class ConcurrentTimerTask implements TimerTask, Runnable {

  private ExecutorService executorService;
  protected Timeout timeout;

  public ConcurrentTimerTask(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void run(Timeout timeout) {
    this.timeout = timeout;
    executorService.submit(this);
  }

}
