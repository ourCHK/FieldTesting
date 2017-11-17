package com.gionee.autotest.field.ui.signal.entity;

public class SimSignalInfo {

    public int mLevel = -1;

    public boolean mIsActive = false;

    public String mNetType = "N/A";

    public String mSignal = "N/A";

    @Override
    public String toString() {
        return "sim卡是否有效:" + mIsActive +
                " 网络类型:" + mNetType +
                " 信号格数:" + mLevel +
                " 信号强度:" + mSignal;
    }
}