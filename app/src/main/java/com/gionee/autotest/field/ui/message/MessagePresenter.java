package com.gionee.autotest.field.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.services.DataResetServices;
import com.gionee.autotest.field.services.MessageServices;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.data_reset.DataResetContract;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataResetHelper;
import com.gionee.autotest.field.util.MessageHelper;
import com.gionee.autotest.field.util.RegexUtils;

/**
 * Created by xhk on 2017/11/15.
 */

public class MessagePresenter extends BasePresenter<MessageContract.View> implements MessageContract.Presenter {

    private Context context;

    MessagePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        if (!isViewAttached()) return;
        //support or not
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1){
//            getView().showNotSupportedDialog();
//            return ;
//        }
        //read time interval
//        Preference.putLong(context, Constant.PREF_KEY_MESSAGE_INTERVAL, Long.parseLong(time)) ;
        long interval = Preference.getLong(context, Constant.PREF_KEY_MESSAGE_INTERVAL, 5) ;
        String phone = Preference.getString(context, Constant.PREF_KEY_MESSAGE_PHONE, "");
        getView().setDefaultInterval(interval + "",phone);
        if (Preference.getBoolean(context, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false)){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }
    }


    @Override
    public void isIntervalValid(int message_type, int sim, CharSequence et_phone, String time) {
        boolean isValid = false ;

        if (message_type == 0) {
            getView().showMessageTypeError();
            return;
        }


        int simCard = MessageHelper.isSIMCard(context);
        if (simCard==0&&simCard!=1){

            getView().showSim(1);
            return;
        }
        if (simCard==0&&simCard!=2){

            getView().showSim(2);
            return;
        }



        if (sim == 0) {
            getView().showSimError();
            return;
        }


//        boolean sim1 = MessageHelper.isSim(context);
//        if (!sim1){
//            getView().showSim();
//            return;
//        }

        if (TextUtils.isEmpty(et_phone.toString())) {
            getView().showPhoneError();
            return;

        }
        if ( !RegexUtils.isMobileExact(et_phone)){
            getView().showPhoneError();
            return;
        }

        if (time != null && !time.isEmpty()){
            long _time ;
            try {
                _time = Long.parseLong(time) ;
                //why 300, I don't know too -_-...
                if (_time > 0){
                    isValid = true ;
                }
            }catch (NumberFormatException e){
                //catch this
            }
        }
        if (isValid){
            getView().showStartToast();
        }else{
            getView().showFrequencyError();
        }

    }

    @Override
    public void setInterval(String et_phone,String time) {

        Preference.putString(context,Constant.MESSAGE_PRESENTATION_NAME, MessageHelper.getTimeData()+".xls");
        Preference.putBoolean(context, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, true) ;
        Preference.putLong(context, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1) ;
        Preference.putLong(context, Constant.PREF_KEY_MESSAGE_INTERVAL, Long.parseLong(time)) ;
        Preference.putString(context, Constant.PREF_KEY_MESSAGE_PHONE,et_phone) ;

    }

    @Override
    public void registerMessageListener(String phone,String interval,int message_type) {
        setInterval(phone,interval);
        setMessageRunning(true);
        Intent intent = new Intent(context, MessageServices.class);
        Bundle bundle = new Bundle();
//        bundle.putString("phone",phone);
        bundle.putInt("message_type",message_type);
        intent.putExtras(bundle);
        context.startService(intent);

    }


    @Override
    public void unregisterMessageListener() {
        setMessageRunning(false);
        Preference.putBoolean(context, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false) ;
        Intent intent = new Intent(context, MessageServices.class);
        context.stopService(intent);
    }

    @Override
    public void setMessageRunning(boolean isRunning) {

        if (isRunning){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }

    }
}
