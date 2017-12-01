package com.gionee.autotest.field.ui.call_quality;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.call_quality.entity.CallQualityConstant;
import com.gionee.autotest.field.ui.call_quality.entity.QualityEvent;
import com.gionee.autotest.field.ui.call_quality.model.DataExport;
import com.gionee.autotest.field.ui.call_quality.model.DataRecord;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.views.StandardDialog;

import java.io.File;
import java.io.IOException;

/**
 * Created by Viking on 2017/11/22.
 *
 * presenter for call quality
 */

public class CallQualityPresenter extends BasePresenter<CallQualityContract.View>
        implements CallQualityContract.Presenter{

    private Context mContext ;

    private DataRecord mRecord ;

    CallQualityPresenter(Context mContext){
        this.mContext = mContext ;
        mRecord = new DataRecord(this.mContext) ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null ;
    }

    @Override
    public void checkRunningSingle(View view, int phone_num, int quality_type, int event_type) {
        Log.i(Constant.TAG, "checkRunningSingle.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningSingle not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningSingle change multi button state to true.") ;
        getView().onSingleClicked(view);
        mRecord.onEventClicked(new QualityEvent(phone_num, quality_type, event_type)) ;
    }

    @Override
    public void checkRunningMulti(View view, int phone_num, int quality_type, int event_type) {
        Log.i(Constant.TAG, "checkRunningMulti.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningMulti not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningMulti change multi button state to false.") ;
        getView().onMultiClicked(view);
        mRecord.onEventClicked(new QualityEvent(phone_num, quality_type, event_type)) ;
    }

    @Override
    public void onStartClicked(String phone_num, String phone_num_o) {
        if (Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true)){
            if (phone_num == null || "".equals(phone_num) || phone_num_o == null || "".equals(phone_num_o)){
                getView().showErrorPhoneNumMsg();
                return ;
            }
            mRecord.initEnvironment();
            startSignalCollectService() ;
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, false) ;
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, true) ;
            mRecord.startTimerTask();
            getView().startWithSuccessfully();
        }else{
            getView().onNextRoundClicked();
            //SEND next event
            mRecord.onEventClicked(new QualityEvent(CallQualityConstant.NEXT_ROUND, -1, -1));
        }
    }

    @Override
    public void onStopClicked() {
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true) ;
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false) ;
        //SEND next event
        mRecord.onEventClicked(new QualityEvent(CallQualityConstant.NEXT_ROUND, -1, -1));
        mRecord.endTimerTask();
        getView().stopWithSuccessfully();
    }

    @Override
    public void doExport(String phone_num, String phone_num_o, File target, File destination) {
        //first check signal_data.txt exist or not
        if (!target.exists()){
            getView().showExportErrorInformation(CallQualityContract.EXPORT_ERROR_CODE_NO_DATA);
            return ;
        }
        try {
            if (!destination.exists() && !destination.createNewFile()){
                getView().showExportErrorInformation(CallQualityContract.EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE);
                return ;
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        //do export
        getView().showLoading(mContext.getString(R.string.export_loading));
        DataExport export = new DataExport() ;
        export.exportExcel(phone_num, phone_num_o, target, destination, new BaseCallback<String>() {
            @Override
            public void onSuccess(String path) {
                getView().hideLoading();
                getView().showExportSuccessInformation(path);
            }

            @Override
            public void onFail() {
                getView().hideLoading();
                getView().showExportErrorInformation(CallQualityContract.EXPORT_ERROR_CODE_FAILURE);
            }
        });
    }

    private void startSignalCollectService(){
        Intent signalService = new Intent(mContext, SignalMonitorService.class) ;
        mContext.startService(signalService) ;
    }

    public void handleBackPressedAction(final CallQualityActivity context){
        new StandardDialog(context)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info_outline_white_36dp)
                .setTitle(R.string.running_title)
                .setMessage(R.string.running_message)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true) ;
                        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false) ;
                        // do nothing
                        context.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

}
