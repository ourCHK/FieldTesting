package com.gionee.autotest.field.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.gionee.autotest.common.FLog;
import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.MessageHelper;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xhk on 2017/11/24.
 */

public class MessageServices extends Service {

    private Receiverlistner receiverlistner;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播
        receiverlistner = new Receiverlistner();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.SMS_SEND_ACTION);
        registerReceiver(receiverlistner, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取报告名字
        String message_presentation_name = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
        //创建测试报告
        MessageHelper.exportExcel(Constant.DIR_MESSAGE + message_presentation_name);
        Bundle bundle = intent.getExtras();
        int message_type = bundle.getInt("message_type");
        String phone = Preference.getString(getApplicationContext(), Constant.PREF_KEY_MESSAGE_PHONE, "");
        FLog.i("message_type = " + message_type + ",phone = " + phone);
        long current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);

        SmsManager sms = SmsManager.getDefault();
        // 创建ACTION常数的Intent，作为PendingIntent的参数
        Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);
        // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
        PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), 0, SendIt, PendingIntent.FLAG_CANCEL_CURRENT);
        // 发送短信
        sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + current_cycle, SendPendIt, null);

        Preference.putString(getApplicationContext(), Constant.MESSAGE_CENTEXT, phone + ",深圳市金立通信设备有限公司，" + current_cycle);

        return super.onStartCommand(intent, flags, startId);
    }

    public class Receiverlistner extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取短信状态
            switch (getResultCode()) {
                // 短信发送成功
                case RESULT_OK:
//                    Toast.makeText(context, "短信内容："+Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, ""), Toast.LENGTH_SHORT).show();
                    String message_presentation_name = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                    String string = Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, "");
                    MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name), new String[]{MessageHelper.getTimeDatas(), string, "成功"});

                    long current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                    long message_interval = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_INTERVAL, 1);
                    if (current_cycle == message_interval) {

                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);

//                     //发送测试完成广播
                        sendBroadcast(new Intent(Constant.MESSAGE_RECEIVER));

                    } else {

                        Preference.putLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, (current_cycle + 1));
                        String phone = Preference.getString(context, Constant.PREF_KEY_MESSAGE_PHONE, "");
                        SmsManager sms = SmsManager.getDefault();
                        // 创建ACTION常数的Intent，作为PendingIntent的参数
                        Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);

                        // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
                        PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), 0, SendIt, PendingIntent.FLAG_CANCEL_CURRENT);
                        // 发送短信
                        sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + (current_cycle + 1), SendPendIt, null);

                    }

                    break;
                // 短信发送不成功
                default:
//                    Toast.makeText(context, "短信发送失败:"+Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, ""), Toast.LENGTH_SHORT).show();
                    String message_presentation_name1 = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                    String string1 = Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, "");
                    MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name1), new String[]{MessageHelper.getTimeDatas(), string1, "失败"});

                    long current_cycle1 = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                    long message_interval1 = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_INTERVAL, 1);
                    if (current_cycle1 == message_interval1) {

                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);

//                      //发送测试完成广播
                        sendBroadcast(new Intent(Constant.MESSAGE_RECEIVER));

                    } else {

                        Preference.putLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, (current_cycle1 + 1));
                        String phone = Preference.getString(context, Constant.PREF_KEY_MESSAGE_PHONE, "");
                        SmsManager sms = SmsManager.getDefault();
                        // 创建ACTION常数的Intent，作为PendingIntent的参数
                        Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);

                        // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
                        PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), 0, SendIt, PendingIntent.FLAG_CANCEL_CURRENT);
                        // 发送短信
                        sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + (current_cycle1 + 1), SendPendIt, null);

                    }


                    break;

            }
        }
    }


}
