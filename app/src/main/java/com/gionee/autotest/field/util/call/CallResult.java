package com.gionee.autotest.field.util.call;


public class CallResult implements Cloneable {

    public String number;
    public String dialTime;
    public String offHookTime;
    public String hangUpTime;
    public boolean result;
    public String time;

    public CallResult(String number, String dialTime, String offHookTime, String hangUpTime, boolean result, String time) {
        this.number = number;
        this.dialTime = dialTime;
        this.offHookTime = offHookTime;
        this.hangUpTime = hangUpTime;
        this.result = result;
        this.time = time;
    }

    public CallResult() {
        this("10086", "", "", "", true, "");
    }

    public CallResult setNumber(String number) {
        this.number = number;
        return this;
    }

    public CallResult setDialTime(String dialTime) {
        this.dialTime = dialTime;
        return this;
    }

    public CallResult setOffHookTime(String offHookTime) {
        this.offHookTime = offHookTime;
        return this;
    }

    public CallResult setHangUpTime(String hangUpTime) {
        this.hangUpTime = hangUpTime;
        return this;
    }

    public CallResult setResult(boolean result) {
        this.result = result;
        return this;
    }

    public CallResult setTime(String time) {
        this.time = time;
        return this;
    }

    @Override
    public CallResult clone() {
        try {
            return (CallResult) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
