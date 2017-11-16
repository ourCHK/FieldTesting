package com.gionee.autotest.field.ui.data_reset;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.util.Constant;

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
        getView().setDefaultInterval(interval + "");
        if (Preference.getBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false)){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }
    }

    @Override
    public void isIntervalValid(String time) {
        boolean isValid = false ;
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
    public void setInterval(String time) {
        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_INTERVAL, Long.parseLong(time)) ;
    }

    @Override
    public void registerDataResetListener(String interval) {
        setInterval(interval);
        setDataResetRunning(true);
        Preference.putBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, true) ;

    }

    @Override
    public void unregisterDataResetListener() {
        setDataResetRunning(false);
        Preference.putBoolean(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false) ;

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
