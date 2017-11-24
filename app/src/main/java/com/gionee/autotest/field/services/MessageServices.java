package com.gionee.autotest.field.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.information.SMSUtils;
import com.gionee.autotest.field.util.Constant;

/**
 * Created by xhk on 2017/11/24.
 */

public class MessageServices extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        int message_type = bundle.getInt("message_type");
        String phone = bundle.getString("phone");
//        FLog.i("message_type = "+message_type+",phone = "+phone);
        long aLong = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
        SMSUtils.SendSMS(phone,phone+",深圳市金立通信设备有限公司，"+aLong);
        return super.onStartCommand(intent, flags, startId);
    }
}
