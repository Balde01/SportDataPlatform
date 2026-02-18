package com.sportdataauth.util;

import java.time.LocalDateTime;

public class SystemClock implements Clock {
    private LocalDateTime fixedTime;

    public SystemClock() {
    }
    public void setFixedTime(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }
    @Override
    public LocalDateTime now() {
        return fixedTime != null ? fixedTime : LocalDateTime.now();
    }

}
