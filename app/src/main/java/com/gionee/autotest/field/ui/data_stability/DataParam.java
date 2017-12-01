package com.gionee.autotest.field.ui.data_stability;

public class DataParam {
    public int waitTime = 5;
    public int testTimes = 5;
    public boolean isForbidSleep = false;
    public boolean sleepAfterTest = false;
    public boolean callAfterTest = false;
    public int verifyCount = 5;
    public int timeOfCall =5;

    public DataParam() {
        this(5, 5, false, false, false, 5,5);
    }

    public DataParam(int waitTime, int testTimes, boolean isForbidSleep, boolean sleepAfterTest, boolean callAfterTest, int verifyCount,int timeOfCall) {
        this.waitTime = waitTime;
        this.testTimes = testTimes;
        this.isForbidSleep = isForbidSleep;
        this.sleepAfterTest = sleepAfterTest;
        this.callAfterTest = callAfterTest;
        this.verifyCount = verifyCount;
        this.timeOfCall = timeOfCall;
    }

    public DataParam setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public DataParam setTestTimes(int testTimes) {
        this.testTimes = testTimes;
        return this;
    }

    public DataParam setForbidSleep(boolean forbidSleep) {
        isForbidSleep = forbidSleep;
        return this;
    }

    public DataParam setSleepAfterTest(boolean sleepAfterTest) {
        this.sleepAfterTest = sleepAfterTest;
        return this;
    }

    public DataParam setCallAfterTest(boolean callAfterTest) {
        this.callAfterTest = callAfterTest;
        return this;
    }

    public DataParam setVerifyCount(int verifyCount) {
        this.verifyCount = verifyCount;
        return this;
    }

    public DataParam setTimeOfCall(int timeOfCall) {
        this.timeOfCall = timeOfCall;
        return this;
    }
}
