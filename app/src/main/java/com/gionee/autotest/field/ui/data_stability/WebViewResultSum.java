package com.gionee.autotest.field.ui.data_stability;


import java.util.ArrayList;

public class WebViewResultSum {

    public boolean                              result       = true;
    public ArrayList<WebViewUtil.WebViewResult> resultBefore = new ArrayList<>();
    public ArrayList<WebViewUtil.WebViewResult> resultAfter  = new ArrayList<>();

    public WebViewResultSum() {

    }

    public WebViewResultSum(ArrayList<WebViewUtil.WebViewResult> resultBefore, ArrayList<WebViewUtil.WebViewResult> resultAfter) {
        this.resultBefore = resultBefore;
        this.resultAfter = resultAfter;
        setResult();
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
    }
}
