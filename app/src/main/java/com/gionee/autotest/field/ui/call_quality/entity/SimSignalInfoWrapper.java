package com.gionee.autotest.field.ui.call_quality.entity;

/**
 * Created by viking on 11/24/17.
 *
 */

public class SimSignalInfoWrapper extends BaseEvent{

    private String mIsActive = "N/A";

    private String mNetType = "N/A";

    private int mLevel = -1;

    private String mSignal = "N/A";


    private String mIsActiveO = "N/A";

    private String mNetTypeO = "N/A";

    private int mLevelO = -1;

    private String mSignalO = "N/A";

    public SimSignalInfoWrapper(String mIsActive, String mNetType, int mLevel, String mSignal,
                                String mIsActiveO, String mNetTypeO, int mLevelO, String mSignalO) {
        this.mIsActive = mIsActive;
        this.mNetType = mNetType;
        this.mLevel = mLevel;
        this.mSignal = mSignal;
        this.mIsActiveO = mIsActiveO;
        this.mNetTypeO = mNetTypeO;
        this.mLevelO = mLevelO;
        this.mSignalO = mSignalO;
    }

    public String getmIsActive() {
        return mIsActive;
    }

    public String getmNetType() {
        return mNetType;
    }

    public int getmLevel() {
        return mLevel;
    }

    public String getmSignal() {
        return mSignal;
    }

    public String getmIsActiveO() {
        return mIsActiveO;
    }

    public String getmNetTypeO() {
        return mNetTypeO;
    }

    public int getmLevelO() {
        return mLevelO;
    }

    public String getmSignalO() {
        return mSignalO;
    }
}
