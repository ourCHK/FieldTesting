package com.gionee.autotest.field.ui.signal.entity;

public class SimSignalInfo {

    public boolean mIsActive = false;

    public String mNetType = "N/A";

    public int mLevel = -1;

    public String mSignal = "N/A";

    public String mOperator = "N/A" ;

    @Override
    public String toString() {
        return "sim卡是否有效:" + mIsActive +
                " 网络运营商:" + mOperator +
                " 网络类型:" + mNetType +
                " 信号格数:" + mLevel +
                " 信号强度:" + mSignal;
    }
}