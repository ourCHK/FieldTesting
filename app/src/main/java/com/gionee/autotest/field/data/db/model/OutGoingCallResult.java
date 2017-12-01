package com.gionee.autotest.field.data.db.model;


import com.gionee.autotest.field.util.call.CallResult;

public class OutGoingCallResult extends CallResult {

    public long batchId;
    public int cycleIndex;
    public String simNetInfo;
    public boolean isVerify;

    public OutGoingCallResult(long batchId, int cycleIndex, String number, String dialTime, String offHookTime, String hangUpTime, boolean result, String time, boolean isVerify,String simNetInfo) {
        this.batchId = batchId;
        this.cycleIndex = cycleIndex;
        this.simNetInfo = simNetInfo;
        this.number = number;
        this.dialTime = dialTime;
        this.offHookTime = offHookTime;
        this.hangUpTime = hangUpTime;
        this.result = result;
        this.time = time;
        this.isVerify = isVerify;
    }

    public OutGoingCallResult() {
        this(0, 0, "10086", "", "", "", true, "", false,"");
    }

    public OutGoingCallResult setBatchId(long batchId) {
        this.batchId = batchId;
        return this;
    }

    public OutGoingCallResult setCycleIndex(int cycleIndex) {
        this.cycleIndex = cycleIndex;
        return this;
    }

    public OutGoingCallResult setVerify(boolean verify) {
        isVerify = verify;
        return this;
    }

    @Override
    public OutGoingCallResult setDialTime(String dialTime) {
        super.setDialTime(dialTime);
        return this;
    }

    @Override
    public OutGoingCallResult setHangUpTime(String hangUpTime) {
        super.setHangUpTime(hangUpTime);
        return this;
    }

    @Override
    public OutGoingCallResult setNumber(String number) {
        super.setNumber(number);
        return this;
    }

    @Override
    public OutGoingCallResult setOffHookTime(String offHookTime) {
        super.setOffHookTime(offHookTime);
        return this;
    }

    @Override
    public OutGoingCallResult setResult(boolean result) {
        super.setResult(result);
        return this;
    }

    @Override
    public OutGoingCallResult setTime(String time) {
        super.setTime(time);
        return this;
    }

    public OutGoingCallResult setSimNetInfo(String simNetInfo) {
        this.simNetInfo = simNetInfo;
        return this;
    }

    public static OutGoingCallResult parse(CallResult r) {
        return new OutGoingCallResult().setNumber(r.number).setDialTime(r.dialTime).setHangUpTime(r.hangUpTime).setOffHookTime(r.offHookTime).setResult(r.result).setTime(r.time);
    }

    @Override
    public String toString() {
        return "id"+batchId+"cycleIndex"+cycleIndex+"simNetInfo"+simNetInfo+"number"+number+"dialTime"+dialTime+"offHookTime"+offHookTime+"hangUpTime"+hangUpTime+"result"+result+"time"+time+"isVerify"+isVerify;
    }
}
