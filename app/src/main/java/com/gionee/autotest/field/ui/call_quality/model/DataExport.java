package com.gionee.autotest.field.ui.call_quality.model;

import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.call_quality.entity.BaseEvent;
import com.gionee.autotest.field.ui.call_quality.entity.CallQualityConstant;
import com.gionee.autotest.field.ui.call_quality.entity.QualityEventWrapper;
import com.gionee.autotest.field.ui.call_quality.entity.RoundInfoData;
import com.gionee.autotest.field.ui.call_quality.entity.SimSignalInfoWrapper;
import com.gionee.autotest.field.util.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viking on 11/24/17.
 *
 * export data
 */

public class DataExport {

    public void exportExcel(final String phone_num, final String phone_num_o, final File target, final File destination, final BaseCallback<String> callback){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    //fetch data from db
                    doConvertStuff(phone_num, phone_num_o , target, destination) ;
                    e.onNext(destination.getAbsolutePath());
                    e.onComplete();
                }catch (Exception ex){
                    ex.printStackTrace();
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        callback.onSuccess(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(Constant.TAG, "export exception : " + e.getMessage()) ;
                        callback.onFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                }) ;
    }

    private void doConvertStuff(String phone_num, String phone_num_o, File target, File destination) throws Exception{

        List<BaseEvent> data = convertDataFromFile(target) ;

        List<RoundInfoData> roundInfos = analysisData(data) ;

        int i = 1 ;
        for (RoundInfoData round : roundInfos){
            Log.i(Constant.TAG, "-------------- round " + i + " ----------------") ;
            List<QualityEventWrapper> events = round.getRoundDatas() ;
            if (events != null && events.size() > 0){
                for (QualityEventWrapper event : events){
                    Log.i(Constant.TAG, "quality event -------------") ;
                    Log.i(Constant.TAG, event.getTime() + " " + event.getPhone_num() + " "
                            + event.getQuality_type() + " " + event.getEvent_type()) ;
                    List<SimSignalInfoWrapper> signals = event.getSignals() ;
                    if (signals != null && signals.size() > 0){
                        for (SimSignalInfoWrapper signal : signals){
                            Log.i(Constant.TAG, signal.getTime() + " " + signal.getmIsActive() + " "
                                    + signal.getmLevel() + " " + signal.getmNetType() + " " + signal.getmSignal()
                                    + " " + signal.getmIsActiveO() + " " + signal.getmLevelO() + " "
                                    + signal.getmNetTypeO() + " " + signal.getmSignalO()) ;
                        }
                    }
                }
            }
            i++ ;
        }
    }

    private List<BaseEvent> convertDataFromFile(File target) throws IOException{
        List<BaseEvent> data = new LinkedList<>() ;
        FileReader fr = new FileReader(target) ;
        BufferedReader mReader = new BufferedReader(fr) ;
        QualityEventWrapper qualityEventWrapper ;
        SimSignalInfoWrapper simSignalInfoWrapper ;
        try {
            String line ;
            while((line = mReader.readLine()) != null) {
                if (line.contains(Constant.SEPARATOR)) {
                    String[] items = line.split(Constant.SEPARATOR) ;
                    if (items.length == 9){
                        simSignalInfoWrapper = new SimSignalInfoWrapper(
                                Boolean.valueOf(items[1]) ?"存在" : "不存在",
                                items[3], Integer.valueOf(items[2]), items[4],
                                Boolean.valueOf(items[5]) ?"存在" : "不存在",
                                items[7], Integer.valueOf(items[6]), items[8]) ;
                        simSignalInfoWrapper.setTime(items[0]);
                        data.add(simSignalInfoWrapper) ;
                    }else if (items.length == 4){
                        int phone_num_ = Integer.parseInt(items[1]) ;
                        int quality_type_ = Integer.parseInt(items[2]) ;
                        int event_type_ = Integer.parseInt(items[3]) ;
                        qualityEventWrapper = new QualityEventWrapper(phone_num_, quality_type_, event_type_) ;
                        qualityEventWrapper.setTime(items[0]);
                        data.add(qualityEventWrapper) ;
                    }
                }
            }
            return data ;
        }finally {
            fr.close();
            mReader.close();
        }
    }

    private List<RoundInfoData> analysisData(List<BaseEvent> data){
        //if no data, throw exception
        if (data.isEmpty()){
            Log.i(Constant.TAG, "data is empty") ;
            throw new IllegalStateException("data is empty") ;
        }
        // print it out
        for (BaseEvent event : data){
            if (event instanceof QualityEventWrapper){
                QualityEventWrapper wrapper = (QualityEventWrapper) event ;
                Log.i(Constant.TAG, event.getTime() + " " + wrapper.getPhone_num() + " "
                        + wrapper.getQuality_type() + " " + wrapper.getEvent_type()) ;
            }else if (event instanceof SimSignalInfoWrapper){
                SimSignalInfoWrapper wrapper = (SimSignalInfoWrapper) event ;
                Log.i(Constant.TAG, event.getTime() + " " + wrapper.getmIsActive() + " "
                        + wrapper.getmLevel() + " " + wrapper.getmNetType() + " " + wrapper.getmSignal()
                        + " " + wrapper.getmIsActiveO() + " " + wrapper.getmLevelO() + " "
                        + wrapper.getmNetTypeO() + " " + wrapper.getmSignalO()) ;
            }
        }

        //analysis it
        List<RoundInfoData>   roundInfos = new ArrayList<>() ;

        List<QualityEventWrapper> roundInfo = new ArrayList<>() ;

        for (int i = 0 ; i < data.size() ; i++){
            BaseEvent event = data.get(i) ;
            if (event instanceof QualityEventWrapper){
                QualityEventWrapper qualityEvent = (QualityEventWrapper) event;
                //new round info reached
                if (qualityEvent.isNextRoundEvent()){
                    //check it empty or not first
                    if (!roundInfo.isEmpty()){
                        RoundInfoData roundInfoData = new RoundInfoData() ;
                        roundInfoData.setRoundDatas(roundInfo);
                        roundInfos.add(roundInfoData) ;
                    }
                    //init all variables
                    roundInfo = new ArrayList<>() ;
                    continue;
                }else{
                    int event_type = qualityEvent.getEvent_type() ;
                    int quality_type = qualityEvent.getQuality_type() ;
                    List<SimSignalInfoWrapper> signals = new ArrayList<>() ;
                    //if just a single event , find last signal information for it
                    if (event_type == CallQualityConstant.EVENT_TYPE_SINGLE){
                        for (int j = i - 1 ; j >= 0 ; j--){
                            BaseEvent event_ = data.get(j) ;
                            if (event_ instanceof SimSignalInfoWrapper){
                                signals.add((SimSignalInfoWrapper) event_) ;
                                qualityEvent.setSignals(signals);
                                break;
                            }
                        }
                    }else{
                        //this will complicate....
                        for (int j = i - 1; j > 0 ; j--){
                            BaseEvent event_ = data.get(j) ;
                            if (event_ instanceof QualityEventWrapper){
                                QualityEventWrapper qualityEvent_ = (QualityEventWrapper) event_;
                                if ((qualityEvent_.getQuality_type() == quality_type
                                        && qualityEvent_.getEvent_type() == CallQualityConstant.EVENT_TYPE_SINGLE)
                                        || qualityEvent_.isNextRoundEvent()) {
                                    //should end here...
                                    qualityEvent.setSignals(signals);
                                    break;
                                }
                            }else if (event_ instanceof SimSignalInfoWrapper){
                                SimSignalInfoWrapper simSignalEvent_ = (SimSignalInfoWrapper) event_;
                                signals.add(simSignalEvent_) ;
                            }
                        }
                    }
                    roundInfo.add(qualityEvent) ;
                }
            }
        }
        return roundInfos ;
    }
}
