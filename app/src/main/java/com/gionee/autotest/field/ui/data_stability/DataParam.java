package com.gionee.autotest.field.ui.data_stability;

public class DataParam {
    public int     waitTime;
    public int     testTimes;
    public boolean isForbidSleep;

    public DataParam() {
        this(5, 5, false);
    }

    public DataParam(int waitTime, int testTimes, boolean isForbidSleep) {
        this.waitTime = waitTime;
        this.testTimes = testTimes;
        this.isForbidSleep = isForbidSleep;
    }
}
