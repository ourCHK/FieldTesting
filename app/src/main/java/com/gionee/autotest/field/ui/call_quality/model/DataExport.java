package com.gionee.autotest.field.ui.call_quality.model;

import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.call_quality.entity.BaseEvent;
import com.gionee.autotest.field.ui.call_quality.entity.QualityEventWrapper;
import com.gionee.autotest.field.ui.call_quality.entity.SimSignalInfoWrapper;
import com.gionee.autotest.field.util.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        }finally {
            fr.close();
            mReader.close();
        }

        if (data.isEmpty()){
            Log.i(Constant.TAG, "data is empty") ;
            throw new IllegalStateException("data is empty") ;
        }

    }
}
