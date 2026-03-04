package com.sportdataauth.util;

import java.time.Instant;

public class SystemClock implements Clock {
    private Instant fixedTime;

    public SystemClock() {
    }
    public void setFixedTime(Instant fixedTime) {
        this.fixedTime = fixedTime;
    }
    @Override
    public Instant now() {
        return fixedTime != null ? fixedTime : Instant.now();
    }

}
