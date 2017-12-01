package com.gionee.autotest.field.util.call;

public class CallMonitorResult implements Cloneable {
    public long batchId = 0;
    public String number = "";
    public boolean result = false;
    public int index = 0;
    public String failMsg = "";
    public String time = "";
    public String hangUpTime = "";
    public String ringingTime;
    public String offHookTime;

    public CallMonitorResult() {

    }

    public CallMonitorResult(long batchId, String number, boolean result, int index, String failMsg, String time) {
        this.batchId = batchId;
        this.number = number;
        this.result = result;
        this.index = index;
        this.failMsg = failMsg;
        this.time = time;
    }

    public CallMonitorResult setBatchId(long batchId) {
        this.batchId = batchId;
        return this;
    }

    public CallMonitorResult setIndex(int index) {
        this.index = index;
        return this;
    }

    public CallMonitorResult setFailMsg(String failMsg) {
        this.failMsg = failMsg;
        return this;
    }

    public CallMonitorResult setNumber(String number) {
        this.number = number;
        return this;
    }

    public CallMonitorResult setResult(boolean result) {
        this.result = result;
        return this;
    }

    public CallMonitorResult setTime(String time) {
        this.time = time;
        return this;
    }

    public CallMonitorResult setHangUpTime(String hangUpTime) {
        this.hangUpTime = hangUpTime;
        return this;
    }

    public CallMonitorResult setRingingTime(String ringingTime) {
        this.ringingTime = ringingTime;
        return this;
    }

    public CallMonitorResult setOffHookTime(String offHookTime) {
        this.offHookTime = offHookTime;
        return this;
    }

    @Override
    public CallMonitorResult clone()  {
        try {
            return (CallMonitorResult) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
