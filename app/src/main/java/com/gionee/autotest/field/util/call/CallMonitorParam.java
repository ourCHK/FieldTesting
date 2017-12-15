package com.gionee.autotest.field.util.call;

import java.io.Serializable;

public class CallMonitorParam implements Serializable{
    public boolean isAutoReject = false;
    public int autoRejectTime = 0;
    public boolean isAutoAnswer = false;
    public boolean isAnswerHangup = false;
    public int answerHangUptime = 10;
    public int gapTime = 60;
    public boolean isHangUpPressPower = false;

    public CallMonitorParam() {

    }

    public CallMonitorParam(boolean isAutoReject, int autoRejectTime, boolean isAutoAnswer, boolean isAnswerHangup, int answerHanguptime, int gapTime, boolean isHangUpPressPower) {
        this.isAutoReject = isAutoReject;
        this.autoRejectTime = autoRejectTime;
        this.isAutoAnswer = isAutoAnswer;
        this.isAnswerHangup = isAnswerHangup;
        this.answerHangUptime = answerHanguptime;
        this.gapTime = gapTime;
        this.isHangUpPressPower = isHangUpPressPower;
    }

    public CallMonitorParam setHangUpPressPower(boolean hangUpPressPower) {
        isHangUpPressPower = hangUpPressPower;
        return this;
    }

    public CallMonitorParam setAnswerHangup(boolean answerHangup) {
        isAnswerHangup = answerHangup;
        return this;
    }

    public CallMonitorParam setAnswerHangUptime(int answerHangUptime) {
        this.answerHangUptime = answerHangUptime;
        return this;
    }

    public CallMonitorParam setAutoAnswer(boolean autoAnswer) {
        isAutoAnswer = autoAnswer;
        return this;
    }

    public CallMonitorParam setAutoReject(boolean autoReject) {
        isAutoReject = autoReject;
        return this;
    }

    public CallMonitorParam setAutoRejectTime(int autoRejectTime) {
        this.autoRejectTime = autoRejectTime;
        return this;
    }

    public CallMonitorParam setGapTime(int gapTime) {
        this.gapTime = gapTime;
        return this;
    }

    @Override
    public String toString() {
        return "isAutoReject:"+isAutoReject+" autoRejectTime:"+autoRejectTime+" isAutoAnswer:"+isAutoAnswer+" isAnswerHangup:"+isAnswerHangup+" isHangUpPressPower:"+isHangUpPressPower+" answerHangUptime:"+answerHangUptime+" gapTime:"+gapTime;
    }
}