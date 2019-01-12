package com.fangdd.open.alarmclock.api;

import com.fangdd.open.alarmclock.resource.representation.AlarmClockResource;

public interface ClockApi {

  AlarmClockResource createClock(AlarmClockResource alarm);

  AlarmClockResource removeClock(String clockId);

  void loadClocks(String node);

}
