package com.gionee.autotest.field.ui.data_reset;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.YUtils;
import com.gionee.autotest.field.services.DataResetServices;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataResetHelper;

/**
 * Created by xhk on 2017/11/15.
 */

public class DataResetPresenter extends BasePresenter<DataResetContract.View> implements DataResetContract.Presenter{

    private Context context ;

    DataResetPresenter(Context context) {
        this.context = context ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null ;
    }

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        if (!isViewAttached()) return ;
        //support or not
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1){
//            getView().showNotSupportedDialog();
//            return ;
//        }
        //read time interval
        long interval = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_INTERVAL, 1) ;

        long retest_times = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES, 1) ;
        getView().setDefaultInterval(interval + "",retest_times+"");

        if (Preference.getBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false)){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }
    }

    @Override
    public void isIntervalValid(String time,String retest_times) {
        if (TextUtils.isEmpty(time)){
            getView().showFrequencyError();
            return;
        }else{
            long times = Long.parseLong(time);
            if (times<=0){
                getView().showFrequencyError();
                return;
            }
        }
        if (TextUtils.isEmpty(retest_times)){
            getView().showRetesTimesError();
            return;
        }else{

            long retest_time = Long.parseLong(retest_times);
            if (retest_time<0){
                getView().showRetesTimesError();
                return;
            }
        }
        getView().showStartToast();

    }

    @Override
    public void setInterval(String time,String retest_times) {
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_INTERVAL, Long.parseLong(time)) ;
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES, Long.parseLong(time)) ;
    }

    @Override
    public void registerDataResetListener(String interval,String retest_times) {
        setInterval(interval,retest_times);
        setDataResetRunning(true);
        Preference.putString(context,Constant.DATA_RESET_PRESENTATION_NAME, DataResetHelper.getTimeData()+".xls");
        Preference.putBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, true) ;
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 0) ;
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE, -1) ;

//        YUtils.setMobileDataState(context,true);
        context.startService(new Intent(context, SignalMonitorService.class));
        Intent intent = new Intent(context, DataResetServices.class);
        context.startService(intent);


    }

    @Override
    public void unregisterDataResetListener() {
        setDataResetRunning(false);
        Preference.putBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false) ;
//        YUtils.setMobileDataState(context,false);
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 0) ;
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE, -1) ;
        Intent intent = new Intent(context, DataResetServices.class);
        context.stopService(intent);
    }

    @Override
    public void setDataResetRunning(boolean isRunning) {
        if (isRunning){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }

    }

}
