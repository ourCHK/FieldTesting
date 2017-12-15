package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.gionee.autotest.common.call.Instrument;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.util.AlarmHelper;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.WakeHelper;

import java.util.Timer;
import java.util.TimerTask;

public class WebViewAction implements CallBack {
    private int urlIndex    = 0;
    private int verifyIndex = 0;
    private       Context          context;
    private       WebViewUtil      webViewUtil;
    private       boolean          isBefore;
    private       IWebViewActivity iWeb;
    private final MyReceiver       myReceiver;
    private final WakeHelper       wakeHelper;
    private final DataParam        param;
    private boolean isVerify = false;
    private WebViewResultHelper webViewResultHelper;

    WebViewAction(IWebViewActivity iWeb, MyWebView webView) {
        this.iWeb = iWeb;
        this.context = iWeb.getContext();
        webViewUtil = new WebViewUtil(webView);
        myReceiver = new MyReceiver();
        wakeHelper = new WakeHelper(context);
        startSignalCollectService();
        context.registerReceiver(myReceiver, new IntentFilter("wait_finish"));
        param = Configurator.getInstance().param;
        webViewResultHelper = new WebViewResultHelper();
    }

    void testLoad(boolean isBefore) {
        this.isBefore = isBefore;
        urlIndex = 0;
        String currentUrl = Configurator.getInstance().urls[urlIndex];
        try {
            webViewUtil.load(currentUrl, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void todo(Object o) {
        WebViewUtil.WebViewResult webViewResult = (WebViewUtil.WebViewResult) o;
        Configurator              instance      = Configurator.getInstance();
        webViewResultHelper.addResult(isBefore, webViewResult);
        if (!isVerify) {
            if (!webViewResult.result && instance.param.verifyCount > 0) {
                DataStabilityUtil.i("失败 并且复测次数大于0: " + instance.param.verifyCount);
                isVerify = true;
            }
        }
        if (urlIndex < instance.urls.length - 1) {
            goTest();
        } else {
            afterTest();
        }
    }

    private void goTest() {
        Configurator instance = Configurator.getInstance();
        if (isVerify && verifyIndex < instance.param.verifyCount) {
            verifyIndex++;
            DataStabilityUtil.i("复测第" + verifyIndex + "次");
        } else {
            urlIndex++;
            verifyIndex = 0;
            isVerify = false;
        }
        webViewUtil.load(instance.urls[urlIndex], WebViewAction.this);
    }

    private void afterTest() {
        final Configurator instance = Configurator.getInstance();
        if (isBefore) {
            DataStabilityUtil.i("isBefore=true");
            if (instance.param.isForbidSleep) {
                forbidSleep(instance);
            } else if (instance.param.sleepAfterTest) {
                sleepByPressPower();
            } else if (instance.param.callAfterTest) {
                callAfterTest();
            }
            if (!instance.param.callAfterTest) {
                waitSleep(instance);
            }
        } else {
            DataStabilityUtil.i("isBefore=false");
            DataStabilityUtil.i("第" + (instance.testIndex + 1) + "轮测试结束");
            webViewResultHelper.write(context, new CallBack() {
                @Override
                public void todo(Object o) {
                    DataStabilityUtil.i("写入结果,第" + instance.batchId + "批次测试完成");
                    iWeb.testFinish((WebViewResultSum) o);
                }
            });
            destroy();
        }
    }

    private void destroy() {
        try {
            context.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitSleep(Configurator instance) {
        int waitTime = instance.param.waitTime;
        if (waitTime == 0) {
            testLoad(false);
        } else {
            DataStabilityUtil.i("休眠=" + waitTime + "分钟");
            AlarmHelper.set(context, "wait_finish", waitTime * 60 * 1000);
        }
    }

    private void callAfterTest() {
        DataStabilityUtil.i("测试后拨打电话");
        DataStabilityUtil.callAfterTest(context, new CallBack() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void todo(Object o) {
                DataStabilityUtil.i("挂断，并在" + param.timeOfCall + "秒后继续测试");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        long targetTime = System.currentTimeMillis() + param.timeOfCall * 1000;
                        while (System.currentTimeMillis() < targetTime) {
                            SystemClock.sleep(1);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        DataStabilityUtil.i("继续测试");
                        testLoad(false);
                    }
                }.execute();
            }
        });
    }

    private void sleepByPressPower() {
        DataStabilityUtil.i("按power键灭屏");
        Instrument.clickKey(KeyEvent.KEYCODE_POWER);
    }

    private void forbidSleep(Configurator instance) {
        DataStabilityUtil.i("阻止进入尝试休眠");
        if (instance.param.waitTime > 0) {
            wakeHelper.getWakeLock().acquire(instance.param.waitTime * 60 * 1000);
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
            if (!wakeHelper.getManager().isScreenOn()) {
                wakeHelper.getKeyguardLock().disableKeyguard();
                Instrument.clickKey(KeyEvent.KEYCODE_POWER);
            }
            testLoad(false);
        }
    }
}
