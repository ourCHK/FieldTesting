package com.gionee.autotest.field.ui.signal;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

/**
 * Created by viking on 11/13/17.
 *
 * Presenter for signal
 */

class SignalPresenter extends BasePresenter<SignalContract.View> implements SignalContract.Presenter{

    private Context context ;

    SignalPresenter(Context context) {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1){
            getView().showNotSupportedDialog();
            return ;
        }
        //read time interval
        long interval = Preference.getLong(context, Constant.PREF_KEY_SIGNAL_INTERVAL, 5) ;
        getView().setDefaultInterval(interval + "");
        if (Preference.getBoolean(context, Constant.PREF_KEY_SIGNAL_RUNNING, false)){
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
                if (_time > 0 && _time < 300){
                    isValid = true ;
                }
            }catch (NumberFormatException e){
                //catch this
            }
        }
        if (isValid){
            getView().requestReadPhoneStatePermission();
        }else{
            getView().showFrequencyError();
        }
    }

    @Override
    public void setInterval(String time) {
        Preference.putLong(context, Constant.PREF_KEY_SIGNAL_INTERVAL, Long.parseLong(time)) ;
    }

    @Override
    public void setSignalRunning(boolean isRunning) {
        Preference.putBoolean(context, Constant.PREF_KEY_SIGNAL_RUNNING, isRunning) ;
        if (isRunning){
            getView().setStartButtonVisibility(false);
            getView().setStopButtonVisibility(true);
        }else {
            getView().setStartButtonVisibility(true);
            getView().setStopButtonVisibility(false);
        }
    }

    @Override
    public void registerSignalListener(SignalHelper.SignalStateListener listener, String interval) {
        setInterval(interval);
        setSignalRunning(true);
        //TODO replace this to Service implementation!!!
        SignalHelper.getInstance(context).registerSimStateListener(listener);
    }

    @Override
    public void unregisterSignalListener(SignalHelper.SignalStateListener listener) {
        setSignalRunning(false);
        SignalHelper.getInstance(context).unregisterSimStateListener(listener);
        SignalHelper.getInstance(context).destroy();
    }
}
