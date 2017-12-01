package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.util.AlarmHelper;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.WakeHelper;

import java.util.ArrayList;

public class WebViewAction implements CallBack {
    private int urlIndex = 0;
    private Context context;
    private MyWebView   mWebView;
    private WebViewUtil webViewUtil;
    private ArrayList<WebViewUtil.WebViewResult> resultsBefore = new ArrayList<>();
    private ArrayList<WebViewUtil.WebViewResult> resultsAfter  = new ArrayList<>();
    private       boolean          isBefore;
    private       IWebViewActivity iWeb;
    private final MyReceiver       myReceiver;
    private final WakeHelper wakeHelper;

    WebViewAction(IWebViewActivity iWeb, MyWebView webView) {
        this.iWeb = iWeb;
        this.context = iWeb.getContext();
        this.mWebView = webView;
        webViewUtil = new WebViewUtil(mWebView);
        myReceiver = new MyReceiver();
        wakeHelper = new WakeHelper(context);
        startSignalCollectService();
        context.registerReceiver(myReceiver, new IntentFilter("wait_finish"));
    }

    void testLoad(boolean isBefore) {
        this.isBefore = isBefore;
        urlIndex = 0;
        String url = Configurator.getInstance().urls[urlIndex];
        webViewUtil.load(url, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void todo(Object o) {
        WebViewUtil.WebViewResult webViewResult = (WebViewUtil.WebViewResult) o;
        urlIndex++;
        if (isBefore) {
            DataStabilityUtil.i("before list 加1");
            resultsBefore.add(webViewResult);
        } else {
            DataStabilityUtil.i("after list 加1");
            resultsAfter.add(webViewResult);
        }
        if (urlIndex < Configurator.getInstance().urls.length) {
            webViewUtil.load(Configurator.getInstance().urls[urlIndex], WebViewAction.this);
        } else {
            if (isBefore) {
                DataStabilityUtil.i("isBefore=true");
                int waitTime = Configurator.getInstance().param.waitTime;
                if (waitTime == 0) {
                    testLoad(false);
                } else {
                    DataStabilityUtil.i("休眠=" + waitTime + "分钟");
                    AlarmHelper.set(context, "wait_finish", waitTime * 60 * 1000);
                    if (Configurator.getInstance().param.isForbidSleep) {
                        wakeHelper.getWakeLock().acquire(waitTime * 60 * 1000);
                    }
                }
            } else {
                DataStabilityUtil.i("isBefore=false");
                DataStabilityUtil.i("第" + (Configurator.getInstance().testIndex + 1) + "次测试结束");
                WebViewResultSum webViewResultSum = new WebViewResultSum(resultsBefore, resultsAfter);
                new WriteResultTask(context, Configurator.getInstance().batchId, Configurator.getInstance().testIndex, new CallBack() {
                    @Override
                    public void todo(Object o) {
                        DataStabilityUtil.i("写入结果");
                        iWeb.testFinish((WebViewResultSum) o);
                    }
                }).execute(webViewResultSum);
                try {
                    context.unregisterReceiver(myReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 启动服务监控网络访问错误时的网络状态
     */
    private void startSignalCollectService() {
        if (context != null) {
            Intent intent  = new Intent(context, SignalMonitorService.class);
            context.startService(intent);
            DataStabilityUtil.i("启动SignalMonitorService服务");
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DataStabilityUtil.i("onReceive");
            testLoad(false);
        }
    }
}
