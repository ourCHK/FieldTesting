package com.gionee.autotest.field.ui.signal;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.signal.model.ExportModel;
import com.gionee.autotest.field.util.Constant;

import java.io.File;
import java.io.IOException;

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
        if (Preference.getBoolean(context, Constant.PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING, false)){
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
            getView().showStartToast();
        }else{
            getView().showFrequencyError();
        }
    }

    @Override
    public void registerSignalListener(String interval) {
        Preference.putLong(context, Constant.PREF_KEY_SIGNAL_INTERVAL, Long.parseLong(interval)) ;
        getView().setStartButtonVisibility(false);
        getView().setStopButtonVisibility(true);
        Intent service = new Intent(context, SignalMonitorService.class) ;
        service.putExtra(Constant.PREF_KEY_SIGNAL_DATA_COLLECT, true) ;
        context.startService(service) ;
    }

    @Override
    public void unregisterSignalListener() {
        getView().setStartButtonVisibility(true);
        getView().setStopButtonVisibility(false);
        Intent service = new Intent(context, SignalMonitorService.class) ;
        service.putExtra(Constant.PREF_KEY_SIGNAL_DATA_DISCOLLECT, true) ;
        context.startService(service) ;
    }

    @Override
    public void doExport(File target, File destination) {
        //first check signal_data.txt exist or not
        if (!target.exists()){
            getView().showSignalExportError(SignalContract.EXPORT_ERROR_CODE_NO_SIGNAL_DATA) ;
            return ;
        }
        try {
            if (!destination.exists() && !destination.createNewFile()){
                getView().showSignalExportError(SignalContract.EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE);
                return ;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //do export
        getView().showLoading(context.getString(R.string.export_loading));
        ExportModel export = new ExportModel() ;
        export.exportExcel(target, destination, new BaseCallback<String>() {
            @Override
            public void onSuccess(String path) {
                getView().hideLoading();
                getView().showSignalExportSuccess(path);
            }

            @Override
            public void onFail() {
                getView().hideLoading();
                getView().showSignalExportError(SignalContract.EXPORT_ERROR_CODE_FAILURE);
            }
        });

    }
}
