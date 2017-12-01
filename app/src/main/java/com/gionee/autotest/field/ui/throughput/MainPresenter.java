package com.gionee.autotest.field.ui.throughput;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.ui.throughput.Util.JExcelUtil;
import com.gionee.autotest.field.util.SignalHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.gionee.autotest.field.ui.throughput.SQL.DatabaseUtil;
import com.gionee.autotest.field.ui.throughput.Util.DialogHelper;
import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean.RateBean;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean.RateBean.Speed;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;

import static android.content.Context.WIFI_SERVICE;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROE_RESULT_PATH;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROR_FILE_NAME;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.HAS_ERROR;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ISLOADING;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.WAIT_STOP;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.isGioneeWeb;
import static com.gionee.autotest.field.ui.throughput.Util.DialogHelper.dialog;
import static com.gionee.autotest.field.ui.throughput.Util.Helper.getTime;
import static com.gionee.autotest.field.ui.throughput.Util.Helper.isNetWorkEnable;
import static com.gionee.autotest.field.ui.throughput.Util.Helper.timerInitialize;
import static com.gionee.autotest.field.util.SignalHelper.SIM_CARD_0;
import static com.gionee.autotest.field.util.SignalHelper.SIM_CARD_1;
import static com.gionee.autotest.field.util.SimUtil.getDefaultDataSubId;

public class MainPresenter {
    private MainActivity iMain;
    private int lastTimeStamp, nowTime, serial;
    private long lastTotalBytes;
    private Timer timer = new Timer();
    private int startTime;
    private String time;
    private WifiManager wifiManager;
    private boolean isNeedWait = false;
    ArrayList<Speed> data = new ArrayList<>();
    private Callback myCallback;

    private SimSignalInfo simSignalInfo;
    private String failTime;
    private String webType;
    private String signals;
    private String signalStrength;
    private String operator;
    private String type;//上传或下载的文件大小
    private String way;//测试方式 -上传/下载
    private String web;//uri网络状态 -内网/外网


    public MainPresenter(MainActivity iMain) {
        this.iMain = iMain;
        wifiManager = (WifiManager) iMain.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void download(final String uri, final String fileName, final String type, final String way, final String web, final int mWaitTimeInt, final int mDownTimesInt) {
        Helper.i("uri 地址为：" + uri + ",fileName的名称是：" + fileName + ",type为：" + type + ",way的值为：" + way + "，取到的web为：" + web);
        PowerManager powerManager = (PowerManager) iMain.getContext()
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK, "My Lock");
        serial = Preference.getInt("serial");
        nowTime = serial - (mDownTimesInt - 1);
        final boolean isDownload = way.equals("下载");
        myCallback = new MyCallback(fileName, way);
        this.type = type;
        this.way = way;
        this.web = web;
        getSimInfo();

        final UIProgressListener uiProgressListener = new UIProgressListener() {

            @Override
            public void onUIProgress(long bytesRead, long contentLength, boolean done) {
                iMain.updateProgressBar((int) ((100 * bytesRead) / contentLength));
            }

            @Override
            public void onUIStart(long currentBytes, long contentLength, boolean done) {
                super.onUIStart(currentBytes, contentLength, done);
                WAIT_STOP = true;
                lastTotalBytes = 0;
                lastTimeStamp = 0;
                data.clear();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        showNetSpeed(isDownload);
                    }
                }, 1000, 1000);
                // 1s后启动任务，每1s执行一次
                Preference.putInt("newTime", nowTime);
                iMain.showSecial("当前进行第" + nowTime + "次测试中...");
                time = Helper.getTime();
                startTime = Helper.getsecond(time);
                Helper.i("开始时间为：" + startTime + "s,时间显示为:" + time);
                lastTotalBytes = isDownload ? getTotalRxBytes() : getTotalTxBytes();
                String time1 = Helper.getTime();
                lastTimeStamp = Helper.getsecond(time1);
            }

            @Override
            public void onUIFinish(long currentBytes, long contentLength, boolean done) {
                super.onUIFinish(currentBytes, contentLength, done);
                String time2 = Helper.getTime();
                int endTime = Helper.getsecond(time2);
                int useTime = endTime - startTime;
                long average = contentLength / (1024 * useTime);
                Helper.i("WiFi下载平均速率为" + average + "KB/s");
                Helper.i("结束时间为：" + endTime + "s" + "----下载用时为：" + useTime);
                timer = timerInitialize(timer);
                iMain.updateUseTimeTv(useTime + "(s)");
                DatabaseUtil db = new DatabaseUtil(iMain.getContext());
                int times = db.getTimes(type);
                String start = Preference.getString("start", 0);
                SpeedBean speedBean = new SpeedBean().setSerial(nowTime).setSuccess("YES").setStart(start).setTime(time).setWay(way).setSpeed(new RateBean().setSpeeds(data)).setSpeed_average(average + "")
                        .setTimes(times + 1).setType(type).setUse_time(useTime).setWeb(web);
                db.insertData(speedBean);
                db.close();
                WAIT_STOP = false;
                Helper.i("times=" + (serial - (mDownTimesInt - 1)));
                if (mDownTimesInt - 1 > 0 && ISLOADING) {
                    isNeedWait = true;
                    final int newTime = mDownTimesInt - 1;
                    Helper.i("mDownTimesInt=" + mDownTimesInt + ";newTime=" + newTime);
                    Helper.i("进行下载~~~~~~~~~~~~~~~");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (isNetWorkEnable(iMain.getContext())) {
                                download(uri, fileName, type, way, web, mWaitTimeInt, newTime);
                            } else {
                                ISLOADING = false;
//                                onError();
                                Helper.i("没网了");
                                Preference.putInt("newTime", nowTime + 1);
                                iMain.dialogNetEnable();
                            }
                        }
                    }, mWaitTimeInt * 1000);
                } else {
                    Helper.i("下载完成，恢复界面~~~");
                    iMain.setViewEnabled();
                    ISLOADING = false;
                    isNeedWait = false;
                    iMain.showSecial("测试完成");
                }


            }
        };
        sendClient(uri, fileName, way, isDownload, uiProgressListener);
    }

    private void sendClient(String uri, String fileName, String way, boolean isDownload, UIProgressListener uiProgressListener) {
        if (isDownload) {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(10, TimeUnit.SECONDS);
            Request request = new Request.Builder().url(uri).build();
            ProgressHelper.addProgressResponseListener(client, uiProgressListener).newCall(request).enqueue(new MyCallback(fileName, way));
        } else {
            File file = new File("/sdcard/" + fileName);
            if (!file.exists()) {
                ISLOADING = false;
                iMain.showDialog("提示", "请将需要上传的文件放置到内部存储器中！");
                iMain.setViewEnabled();
                return;
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart(file.getName(), file.getName(), RequestBody.create(null, file)).build();
            Request request = new Request.Builder().url(uri).post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressListener)).build();
            //开始请求
            Helper.i("来过");
            client.newCall(request).enqueue(myCallback);
        }
    }


    private void showNetSpeed(final boolean isDownload) {
        long nowTotalRxBytes = isDownload ? getTotalRxBytes() : getTotalTxBytes();
        String time3 = Helper.getTime();
        int nowTimeStamp = Helper.getsecond(time3);
//        int nowTimeStamp = Helper.getMillisecond(time3);
        int userTime = (nowTimeStamp - lastTimeStamp) == 0 ? 1000 : (nowTimeStamp - lastTimeStamp);
        long speed = ((nowTotalRxBytes - lastTotalBytes) / userTime);
        lastTimeStamp = nowTimeStamp;
        lastTotalBytes = nowTotalRxBytes;
//        Helper.i("下载速率为" + String.valueOf(speed) + " kb/s");
        data.add(new Speed().setSpeed(String.valueOf(speed)).setTime(Helper.getTime("yyyy-MM-dd HH:mm:ss")));
        int rssi = Helper.wifiRssi(wifiManager);
//        Helper.i("网络rssi值为：" + rssi);
//        if(!isWifiEnabled(MainActivity.this)||HAS_ERROR){
        if (!isNetWorkEnable(iMain.getContext()) || HAS_ERROR) {
            Helper.i("网络异常处理");
            ISLOADING = false;
            HAS_ERROR = false;
            onError();
            iMain.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isGioneeWeb) {
                        isGioneeWeb = false;
                        DialogHelper.dialog(iMain.getContext(), "提醒", "内网断开，稍后下载~");
                        Helper.i("还原界面");
                        iMain.setInitializeView();
                        iMain.showSecial("第" + nowTime + "次测试时，内网断开，测试停止");
                    } else {
                        iMain.dialogNetEnable();
                    }

                }
            });

            timer = timerInitialize(this.timer);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void getSimInfo() {
        if (Helper.isWifiEnabled(iMain.getContext())) {
            WifiInfo info = wifiManager.getConnectionInfo();
            int strength = 0;
            int speed = 0;
            String units = " ";
            String ssid = "";
            if (info.getBSSID() != null) {
                // 链接信号强度，5为获取的信号强度值在5以内
                strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
                // 链接速度
                speed = info.getLinkSpeed();
                // 链接速度单位
                units = WifiInfo.LINK_SPEED_UNITS;
                // Wifi源名称
                ssid = info.getSSID();
            }

            webType = "无线";
            signals = String.valueOf(strength);
            signalStrength = String.valueOf(speed) + String.valueOf(units);
            operator = ssid;
        } else {
            int id = getDefaultDataSubId();
            SignalHelper signalHelper = SignalHelper.getInstance(iMain.getContext());
            if (id == 1) {
                simSignalInfo = signalHelper.getSimSignalInfo(1);
                webType = "sim1: " + simSignalInfo.mNetType;
                signals = "sim1: " + String.valueOf(simSignalInfo.mLevel);
                signalStrength = "sim1: " + simSignalInfo.mSignal;
                operator = "sim1: " + getSimName(0) + "(联网) ";
                boolean isSimExist2 = signalHelper.isSimExist(SIM_CARD_1);
                if (isSimExist2) {
                    simSignalInfo = signalHelper.getSimSignalInfo(2);
                    webType = webType + " - " + "sim2: " + simSignalInfo.mNetType;
                    signals = signals + " - " + "sim2: " + String.valueOf(simSignalInfo.mLevel);
                    signalStrength = signalStrength + " - " + "sim2: " + simSignalInfo.mSignal;
                    operator = operator + " - " + "sim2: " + getSimName(1);
                }
            }
            if (id == 2) {
                boolean isSimExist1 = signalHelper.isSimExist(SIM_CARD_0);
                if (isSimExist1) {
                    simSignalInfo = signalHelper.getSimSignalInfo(2);
                    webType = "sim1: " + simSignalInfo.mNetType;
                    signals = "sim1: " + String.valueOf(simSignalInfo.mLevel);
                    signalStrength = "sim1: " + simSignalInfo.mSignal;
                    operator = "sim1: " + getSimName(0);
                }
                simSignalInfo = signalHelper.getSimSignalInfo(1);
                webType = webType + " - " + "sim2: " + simSignalInfo.mNetType;
                signals = signals + " - " + "sim2: " + String.valueOf(simSignalInfo.mLevel);
                signalStrength = signalStrength + " - " + "sim2: " + simSignalInfo.mSignal;
                operator = operator + " - " + "sim2: " + getSimName(1) + "(联网) ";
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private String getSimName(int id) {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(iMain.getContext());
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSimSlotIndex() == id) {
                return String.valueOf(subscriptionInfo.getCarrierName());
            }
        }
        return "N/A";
    }

    private void onError() {
        failTime = getTime();

        DatabaseUtil db = new DatabaseUtil(iMain.getContext());
        String start = Preference.getString("start", 0);
        SpeedBean speedBean = new SpeedBean().setSerial(nowTime).setStart(start).setSuccess("NO").setTime(time).setWay(way).setType(type).setWeb(web).setFailTime(failTime).setWebType(webType).setSignals(signals).setSignalStrength(signalStrength).setOperator(operator);
        db.insertData(speedBean);
        db.close();

        File file = new File(ERROE_RESULT_PATH, ERROR_FILE_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        JExcelUtil.exportExcel(new File(ERROR_FILE_NAME), "error");
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(iMain.getContext().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    private long getTotalTxBytes() {
        return TrafficStats.getUidTxBytes(iMain.getContext().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() / 1024);//转为KB
    }

    private class MyCallback implements Callback {
        private String fileName;
        private String way;

        MyCallback(String fileName, String way) {
            this.fileName = fileName;
            this.way = way;
        }

        @Override
        public void onFailure(Request request, IOException e) {
            Helper.i("请求失败");
            Helper.i("error:" + e.toString());
            onError();
            iMain.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Helper.i("是否为内网" + isGioneeWeb);
                    if (isGioneeWeb) {
                        Helper.i("内网下载，却没有开启内网");
                        isGioneeWeb = false;
                        dialog(iMain.getContext(), "提醒", "进行内网下载，请连接内网WiFi！");
                    } else {
                        iMain.dialogNetEnable();
                    }
                    Helper.i("还原界面");
                    ISLOADING = false;
                    WAIT_STOP = false;
                    iMain.setInitializeView();
                }
            });
        }

        @Override
        public void onResponse(Response response) throws IOException {
            Helper.i("请求成功");
            Looper.prepare();
            if (way.equals("下载")) {
                Helper.downFile(iMain.getContext(), response, fileName);
            }
        }
    }


}
