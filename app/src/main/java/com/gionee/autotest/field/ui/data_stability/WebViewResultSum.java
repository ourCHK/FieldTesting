package com.gionee.autotest.field.ui.data_stability;


import com.gionee.autotest.field.util.DataStabilityUtil;

import java.util.ArrayList;

public class WebViewResultSum {

    public boolean                              result         = true;
    public ArrayList<WebViewUtil.WebViewResult> resultBefore   = new ArrayList<>();
    public ArrayList<WebViewUtil.WebViewResult> resultAfter    = new ArrayList<>();
    public ArrayList<SimNetInfo>           errorSimNetInfoList = new ArrayList<>();

    public WebViewResultSum() {

    }

    public WebViewResultSum(ArrayList<WebViewUtil.WebViewResult> resultBefore, ArrayList<WebViewUtil.WebViewResult> resultAfter) {
        this.resultBefore = resultBefore;
        this.resultAfter = resultAfter;
        setResult();
        setFailSimNetInfo();
    }

    public void setResult() {
        int fail = 0;
        for (WebViewUtil.WebViewResult webViewResult : resultBefore) {
            if (!webViewResult.result) {
                fail++;
            }
        }
        if (fail > 2) {
            result = false;
        } else {
            fail = 0;
            for (WebViewUtil.WebViewResult webViewResult : resultAfter) {
                if (!webViewResult.result) {
                    fail++;
                }
            }
            result = fail < 3;
        }
        DataStabilityUtil.i("fail times:"+ fail+"  result:"+result);
    }

    /**
     * 设置失败信息
     */
    public void setFailSimNetInfo() {
        int index = 1;
        for (WebViewUtil.WebViewResult webViewResult : resultBefore) {
            if (!webViewResult.result) {
                SimNetInfo simNetInfo = new SimNetInfo();
                simNetInfo.setIndex(index);
                simNetInfo.setmIsActive1(webViewResult.isActive1);
                simNetInfo.setmNetType1(webViewResult.netType1);
                simNetInfo.setmLevel1(webViewResult.level1);
                simNetInfo.setmSignal1(webViewResult.signal1);
                simNetInfo.setmOperator1(webViewResult.operator1);
                simNetInfo.setmIsActive2(webViewResult.isActive2);
                simNetInfo.setmNetType2(webViewResult.netType2);
                simNetInfo.setmLevel2(webViewResult.level2);
                simNetInfo.setmSignal2(webViewResult.signal2);
                simNetInfo.setmOperator2(webViewResult.operator2);
                errorSimNetInfoList.add(simNetInfo);
            }
            index++;
        }

        for (WebViewUtil.WebViewResult webViewResult : resultAfter) {
            if (!webViewResult.result) {
                SimNetInfo simNetInfo = new SimNetInfo();
                simNetInfo.setIndex(index);
                simNetInfo.setmIsActive1(webViewResult.isActive1);
                simNetInfo.setmNetType1(webViewResult.netType1);
                simNetInfo.setmLevel1(webViewResult.level1);
                simNetInfo.setmSignal1(webViewResult.signal1);
                simNetInfo.setmOperator1(webViewResult.operator1);
                simNetInfo.setmIsActive2(webViewResult.isActive2);
                simNetInfo.setmNetType2(webViewResult.netType2);
                simNetInfo.setmLevel2(webViewResult.level2);
                simNetInfo.setmSignal2(webViewResult.signal2);
                simNetInfo.setmOperator2(webViewResult.operator2);
                errorSimNetInfoList.add(simNetInfo);
            }
            index++;
        }
    }


    /**
     * 用于将simNetInfo转化为json对象存放至数据库中
     */
    class SimNetInfo {

        int index = -1;

        boolean mIsActive1 = false;

        public String mNetType1 = "N/A";

        public int mLevel1 = -1;

        public String mSignal1 = "N/A";

        public String mOperator1 = "N/A" ;

        boolean mIsActive2 = false;

        public String mNetType2 = "N/A";

        public int mLevel2 = -1;

        public String mSignal2 = "N/A";

        public String mOperator2 = "N/A" ;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean ismIsActive1() {
            return mIsActive1;
        }

        public void setmIsActive1(boolean mIsActive1) {
            this.mIsActive1 = mIsActive1;
        }

        public String getmNetType1() {
            return mNetType1;
        }

        public void setmNetType1(String mNetType1) {
            this.mNetType1 = mNetType1;
        }

        public int getmLevel1() {
            return mLevel1;
        }

        public void setmLevel1(int mLevel1) {
            this.mLevel1 = mLevel1;
        }

        public String getmSignal1() {
            return mSignal1;
        }

        public void setmSignal1(String mSignal1) {
            this.mSignal1 = mSignal1;
        }

        public String getmOperator1() {
            return mOperator1;
        }

        public void setmOperator1(String mOperator1) {
            this.mOperator1 = mOperator1;
        }

        public boolean ismIsActive2() {
            return mIsActive2;
        }

        public void setmIsActive2(boolean mIsActive2) {
            this.mIsActive2 = mIsActive2;
        }

        public String getmNetType2() {
            return mNetType2;
        }

        public void setmNetType2(String mNetType2) {
            this.mNetType2 = mNetType2;
        }

        public int getmLevel2() {
            return mLevel2;
        }

        public void setmLevel2(int mLevel2) {
            this.mLevel2 = mLevel2;
        }

        public String getmSignal2() {
            return mSignal2;
        }

        public void setmSignal2(String mSignal2) {
            this.mSignal2 = mSignal2;
        }

        public String getmOperator2() {
            return mOperator2;
        }

        public void setmOperator2(String mOperator2) {
            this.mOperator2 = mOperator2;
        }
    }
}
