package com.fangdd.open.alarmclock.api;

import com.fangdd.open.alarmclock.resource.representation.TargetResource;

public interface TargetApi {

  TargetResource createTarget(TargetResource target);

  TargetResource removeTarget(String targetId);

}
