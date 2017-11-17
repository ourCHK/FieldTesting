package com.gionee.autotest.field.data.db.model;


public class OutGoingCallResult {

    public long    batchId;
    public int     cycleIndex;
    public long    number;
    public String  dialTime;
    public String  offHookTime;
    public String  hangUpTime;
    public boolean result;
    public String time;

    public OutGoingCallResult(long batchId, int cycleIndex, long number, String dialTime, String offHookTime, String hangUpTime, boolean result,String time) {

        this.batchId = batchId;
        this.cycleIndex = cycleIndex;
        this.number = number;
        this.dialTime = dialTime;
        this.offHookTime = offHookTime;
        this.hangUpTime = hangUpTime;
        this.result = result;
        this.time = time;
    }

    public OutGoingCallResult() {
        this(0, 0, 10086, "", "", "", true,"");
    }

    public OutGoingCallResult setBatchId(long batchId) {
        this.batchId = batchId;
        return this;
    }

    public OutGoingCallResult setCycleIndex(int cycleIndex) {
        this.cycleIndex = cycleIndex;
        return this;
    }

    public OutGoingCallResult setNumber(long number) {
        this.number = number;
        return this;
    }

    public OutGoingCallResult setDialTime(String dialTime) {
        this.dialTime = dialTime;
        return this;
    }

    public OutGoingCallResult setOffHookTime(String offHookTime) {
        this.offHookTime = offHookTime;
        return this;
    }

    public OutGoingCallResult setHangUpTime(String hangUpTime) {
        this.hangUpTime = hangUpTime;
        return this;
    }

    public OutGoingCallResult setResult(boolean result) {
        this.result = result;
        return this;
    }

    public OutGoingCallResult setTime(String time) {
        this.time = time;
        return this;
    }
}
