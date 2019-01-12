package com.fangdd.open.alarmclock.task;

import com.fangdd.open.alarmclock.config.Constants;
import com.fangdd.open.alarmclock.data.access.ClockDataAccess;
import com.fangdd.open.alarmclock.error.AlarmClockException;
import com.fangdd.open.alarmclock.error.Errors;
import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;
import com.fangdd.open.alarmclock.resource.representation.TargetResource;
import com.fangdd.open.alarmclock.task.mq.RabbitMqTimerTask;
import io.netty.util.TimerTask;

import java.util.concurrent.ExecutorService;

/**
 * 任务工厂
 */
public class TaskFactory {

  public static TimerTask create(AlarmClockResource clock, TargetResource target, ClockDataAccess cda, String node, ExecutorService executorService) throws AlarmClockException {
    switch (target.getType()) {
      case Constants.TARGET_TYPE_RABBIT_MQ: return new RabbitMqTimerTask(clock, target, cda, node, executorService);
      case Constants.TARGET_TYPE_ACTIVE_MQ:
      case Constants.TARGET_TYPE_HTTP:
        throw new AlarmClockException(Errors.BAD_REQUEST, "本闹钟关联的目标类型未实现", clock);
      default:
        throw new AlarmClockException(Errors.BAD_REQUEST, "本闹钟关联的目标类型不正确", clock);
    }
  }

}
