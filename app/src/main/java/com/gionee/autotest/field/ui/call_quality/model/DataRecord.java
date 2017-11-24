package com.gionee.autotest.field.ui.call_quality.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.ui.call_quality.entity.QualityEvent;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by viking on 11/24/17.
 *
 * data record stuff
 */

public class DataRecord {

    private static final int INTERVAL   = 1000 ;

    private Context mContext ;

    private Timer mTimer ;

    private FileWriter      fw          = null ;
    private BufferedWriter  writer      = null ;
/*    private FileWriter      fwRound     = null ;
    private BufferedWriter  writerRound = null ;*/

    private SimSignalInfo   mSim1SignalInfo ;
    private SimSignalInfo   mSim2SignalInfo ;

    public DataRecord(Context mContext){
        this.mContext = mContext ;
    }

    public void onEventClicked(QualityEvent event){
        synchronized (this){
            String time = TimeUtil.getTime() ;
            StringBuilder content = new StringBuilder() ;
            content.append(time) ;
            content.append(Constant.SEPARATOR) ;
            content.append(event.toString()) ;
            try {
                writer.write(content.toString());
                writer.newLine();
                writer.flush();
            }catch (IOException e){
                Log.i(Constant.TAG, "write sim signal info to file exception : " + e.getMessage()) ;
                e.printStackTrace();
            }
        }
    }

    public void initEnvironment(){
        //make sure external storage usable
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constant.TAG, "external storage state not mounted...") ;
            return ;
        }
        //make sure signal directory exist
        File call_quality_home = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME, Constant.CALL_QUALITY_HOME);
        if (!call_quality_home.exists() && !call_quality_home.mkdirs()){
            Log.i(Constant.TAG, "make call quality directory failed...") ;
            return ;
        }

        String time_stamp = TimeUtil.getTime(Constant.TIME_STAMP_DIR_FORMAT) ;
        Preference.putString(mContext, Constant.CALL_QUALITY_LAST_TIME, time_stamp) ;

        File time_dir = new File(call_quality_home, time_stamp) ;
        if (!time_dir.exists() && !time_dir.mkdirs()){
            Log.i(Constant.TAG, "make call quality time stamp directory failed...") ;
            return ;
        }

        //for call quality signal data
        File call_quality_data = new File(time_dir, Constant.CALL_QUALITY_DATA_NAME) ;
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

        //for call qualify round info
/*        File call_quality_round_data = new File(time_dir, Constant.CALL_QUALITY_ROUND_NAME) ;
        if (call_quality_round_data.exists() && !call_quality_round_data.delete()){
            Log.i(Constant.TAG, "fail to delete exist round data file...") ;
            return ;
        }
        try{
            if (!call_quality_round_data.createNewFile()){
                Log.i(Constant.TAG, "create round data file fail...") ;
                return ;
            }
            fwRound = new FileWriter(call_quality_round_data, true) ;
            writerRound = new BufferedWriter(fwRound) ;
        }catch (IOException e){ e.printStackTrace();}*/
    }

    public void startTimerTask(){
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
                mSim2SignalInfo = mSignalHelper.getSimSignalInfo(SignalHelper.SIM_CARD_1) ;
                if (mSim1SignalInfo != null && mSim1SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim0 fetched : " + mSim1SignalInfo.toString() ) ;
                }
                if (mSim2SignalInfo != null && mSim2SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim1 fetched : " + mSim2SignalInfo.toString() ) ;
                }
                writeContentToFile(mSim1SignalInfo, mSim2SignalInfo) ;
            }
        } ;
        // delay 3 seconds...
        mTimer.schedule(mTask, 3 * INTERVAL, INTERVAL);
    }

    private void writeContentToFile(SimSignalInfo infoSim0, SimSignalInfo infoSim2){
        synchronized (this){
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
    }

    public void endTimerTask(){
        //cancel Timer
        if (mTimer != null ){
            mTimer.cancel();
        }
        //close all stream
        closeQuietly(fw);
        closeQuietly(writer);
/*        closeQuietly(fwRound);
        closeQuietly(writerRound);*/
    }

    private void closeQuietly(Closeable stream){
        if (stream != null){
            try {
                stream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
