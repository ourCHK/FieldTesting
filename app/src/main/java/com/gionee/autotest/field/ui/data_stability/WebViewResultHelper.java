package com.gionee.autotest.field.ui.data_stability;


import android.content.Context;

import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.DataStabilityUtil;

import java.util.ArrayList;

public class WebViewResultHelper {

    private ArrayList<WebViewUtil.WebViewResult> resultsBefore = new ArrayList<>();
    private ArrayList<WebViewUtil.WebViewResult> resultsAfter = new ArrayList<>();

    public void addResult(boolean isBefore, WebViewUtil.WebViewResult result) {
        if (isBefore) {
            DataStabilityUtil.i("before list 加 1 ");
            resultsBefore.add(result);
            DataStabilityUtil.i(" 目前：b=" + resultsBefore.size()+" a="+ resultsAfter.size());
        } else {
            DataStabilityUtil.i("after list 加 1 目前：" + resultsAfter.size());
            resultsAfter.add(result);
            DataStabilityUtil.i(" 目前：b=" + resultsBefore.size()+" a="+ resultsAfter.size());
        }
    }

    public void write(Context context, CallBack callback) {
        final Configurator instance = Configurator.getInstance();
        DataStabilityUtil.i("size = " + resultsBefore.size() + " size after=" + resultsAfter.size());
        WebViewResultSum webViewResultSum = new WebViewResultSum(resultsBefore, resultsAfter);
        new WriteResultTask(context, instance.batchId, instance.testIndex, callback).execute(webViewResultSum);
    }
}
