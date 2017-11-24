package com.gionee.autotest.field.ui.call_quality;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Viking on 2017/11/22.
 *
 * presenter for call quality
 */

public class CallQualityPresenter extends BasePresenter<CallQualityContract.View>
        implements CallQualityContract.Presenter{

    private Context mContext ;

    private Timer mTimer ;

    private SimSignalInfo mSim1SignalInfo ;
    private SimSignalInfo mSim2SignalInfo ;

    FileWriter      fw = null ;
    BufferedWriter  writer = null ;

    CallQualityPresenter(Context mContext){
        this.mContext = mContext ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null ;
    }

    @Override
    public void checkRunningSingle(View view) {
        Log.i(Constant.TAG, "checkRunningSingle.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningSingle not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningSingle change multi button state to true.") ;
        getView().onSingleClicked(view);
    }

    @Override
    public void checkRunningMulti(View view) {
        Log.i(Constant.TAG, "checkRunningMulti.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningMulti not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningMulti change multi button state to false.") ;
        getView().onMultiClicked(view);
    }

    @Override
    public void onStartClicked(String phone_num, String phone_num_o) {
        if (Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true)){
            if (phone_num == null || "".equals(phone_num) || phone_num_o == null || "".equals(phone_num_o)){
                getView().showErrorPhoneNumMsg();
                return ;
            }
            clearCallQualityEnvironment();
            startSignalCollectService() ;
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, false) ;
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, true) ;
            startTimerTask();
            getView().startWithSuccessfully();
        }else{
            getView().onNextRoundClicked();
        }
    }

    @Override
    public void onStopClicked() {
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true) ;
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false) ;
        endTimerTask();
        getView().stopWithSuccessfully();
    }

    private void startSignalCollectService(){
        Intent signalService = new Intent(mContext, SignalMonitorService.class) ;
        mContext.startService(signalService) ;
    }

    private void clearCallQualityEnvironment(){
        //make sure external storage usable
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constant.TAG, "external storage state not mounted...") ;
            return ;
        }
        //make sure signal directory exist
        File call_quality_home = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME, Constant.CALL_QUALITY_HOME);
        if (call_quality_home.exists() && call_quality_home.isDirectory()){
            Log.i(Constant.TAG, "delete call quality...") ;
            FileUtils.deleteQuietly(call_quality_home);
        }
        if (!call_quality_home.exists() && !call_quality_home.mkdirs()){
            Log.i(Constant.TAG, "make call quality directory failed...") ;
            return ;
        }
        File call_quality_data = new File(call_quality_home, Constant.CALL_QUALITY_DATA_NAME) ;
        if (call_quality_data.exists() && !call_quality_data.delete()){
            Log.i(Constant.TAG, "fail to delete exist signal data file...") ;
            return ;
        }
        try{
            if (!call_quality_data.createNewFile()){
                Log.i(Constant.TAG, "create signal data file fail...") ;
                return ;
            }
            fw = new FileWriter(call_quality_data, true) ;
            writer = new BufferedWriter(fw) ;
        }catch (IOException e){ e.printStackTrace();}
    }

    private void startTimerTask(){
        //cancel it first, when it exist
        if (mTimer != null){
            mTimer.cancel();
        }
        mTimer = new Timer() ;
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(Constant.TAG, "enter call quality timer task logic...") ;
                SignalHelper mSignalHelper = SignalHelper.getInstance(mContext) ;
                mSim1SignalInfo = mSignalHelper.getSimSignalInfo(SignalHelper.SIM_CARD_0);
                mSim1SignalInfo = mSignalHelper.getSimSignalInfo(SignalHelper.SIM_CARD_1) ;
                if (mSim1SignalInfo != null && mSim1SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim0 fetched : " + mSim1SignalInfo.toString() ) ;
                }
                if (mSim2SignalInfo != null && mSim2SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim1 fetched : " + mSim2SignalInfo.toString() ) ;
                }
                writeContentToFile(mSim1SignalInfo, mSim2SignalInfo) ;
            }
        } ;
        mTimer.schedule(mTask, 3 * 1000, 1000);
    }

    private void writeContentToFile(SimSignalInfo infoSim0, SimSignalInfo infoSim2){
        String time = TimeUtil.getTime() ;
        StringBuilder content = new StringBuilder() ;
        content.append(time) ;
        if (infoSim0 == null) infoSim0 = new SimSignalInfo() ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim0.mIsActive) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim0.mLevel) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim0.mNetType) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim0.mSignal) ;
        content.append(Constant.SEPARATOR) ;
        if (infoSim2 == null) infoSim2 = new SimSignalInfo() ;
        content.append(infoSim2.mIsActive) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim2.mLevel) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim2.mNetType) ;
        content.append(Constant.SEPARATOR) ;
        content.append(infoSim2.mSignal) ;
        try {
            writer.write(content.toString());
            writer.newLine();
            writer.flush();
        }catch (IOException e){
            Log.i(Constant.TAG, "write sim signal info to file exception : " + e.getMessage()) ;
            e.printStackTrace();
        }
    }

    private void endTimerTask(){
        //cancel Timer
        if (mTimer != null ){
            mTimer.cancel();
        }
        //close all stream
        if (fw != null){
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
