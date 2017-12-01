package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.common.call.Instrument;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.util.AlarmHelper;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.WakeHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewAction implements CallBack {
    private int urlIndex = 0;
    private Context context;
    private MyWebView mWebView;
    private WebViewUtil webViewUtil;
    private ArrayList<WebViewUtil.WebViewResult> resultsBefore = new ArrayList<>();
    private ArrayList<WebViewUtil.WebViewResult> resultsAfter = new ArrayList<>();
    private boolean isBefore;
    private IWebViewActivity iWeb;
    private final MyReceiver myReceiver;
    private final WakeHelper wakeHelper;
    private String currentUrl;
    private final DataParam param;

    WebViewAction(IWebViewActivity iWeb, MyWebView webView) {
        this.iWeb = iWeb;
        this.context = iWeb.getContext();
        this.mWebView = webView;
        webViewUtil = new WebViewUtil(mWebView);
        myReceiver = new MyReceiver();
        wakeHelper = new WakeHelper(context);
        startSignalCollectService();
        context.registerReceiver(myReceiver, new IntentFilter("wait_finish"));
        param = Configurator.getInstance().param;
    }

    void testLoad(boolean isBefore) {
        this.isBefore = isBefore;
        urlIndex = 0;
        currentUrl = Configurator.getInstance().urls[urlIndex];
        webViewUtil.load(currentUrl, this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void todo(Object o) {
        final Configurator instance = Configurator.getInstance();
        WebViewUtil.WebViewResult webViewResult = (WebViewUtil.WebViewResult) o;
        urlIndex++;
        if (isBefore) {
            DataStabilityUtil.i("before list 加1");
            resultsBefore.add(webViewResult);
        } else {
            DataStabilityUtil.i("after list 加1");
            resultsAfter.add(webViewResult);
        }
        if (urlIndex < instance.urls.length) {
            webViewUtil.load(instance.urls[urlIndex], WebViewAction.this);
        } else {
            if (isBefore) {
                DataStabilityUtil.i("isBefore=true");
                if (instance.param.isForbidSleep) {
                    if (instance.param.waitTime > 0) {
                        wakeHelper.getWakeLock().acquire(instance.param.waitTime * 60 * 1000);
                    }
                } else if (instance.param.sleepAfterTest) {
                    Instrument.clickKey(KeyEvent.KEYCODE_POWER);
                } else if (instance.param.callAfterTest) {
                    DataStabilityUtil.callAfterTest(context, new CallBack() {
                        @Override
                        public void todo(Object o) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    testLoad(false);
                                }
                            }, param.timeOfCall);
                        }
                    });
                }
                if (!instance.param.callAfterTest) {
                    int waitTime = instance.param.waitTime;
                    if (waitTime == 0) {
                        testLoad(false);
                    } else {
                        DataStabilityUtil.i("休眠=" + waitTime + "分钟");
                        AlarmHelper.set(context, "wait_finish", waitTime * 60 * 1000);
                    }
                }
            } else {
                DataStabilityUtil.i("isBefore=false");
                DataStabilityUtil.i("第" + (instance.testIndex + 1) + "次测试结束");
                final WebViewResultSum webViewResultSum = new WebViewResultSum(resultsBefore, resultsAfter);
                if (!webViewResultSum.result) {
                    new VerifyWebViewTest(mWebView, currentUrl, instance.param.verifyCount, new CallBack() {
                        @Override
                        public void todo(Object o) {
                            new WriteResultTask(context, instance.batchId, instance.testIndex, new CallBack() {
                                @Override
                                public void todo(Object o) {
                                    DataStabilityUtil.i("写入结果");
                                    iWeb.testFinish((WebViewResultSum) o);
                                }
                            }).execute(webViewResultSum);
                        }
                    }).test();
                }
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
            Intent intent = new Intent(context, SignalMonitorService.class);
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
