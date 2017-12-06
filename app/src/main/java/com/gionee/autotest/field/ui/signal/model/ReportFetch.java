package com.gionee.autotest.field.ui.signal.model;

import android.os.Environment;
import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.util.Constant;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viking on 12/6/17.
 */

public class ReportFetch {

    private List<String> doStuff(){
        //fetch data from db
        List<String> files = new ArrayList<>() ;
        File signal_home = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME, Constant.SIGNAL_DIR);
        if (!signal_home.exists() && !signal_home.mkdirs()){
            Log.i(Constant.TAG, "make signal_home failed...") ;
            return files ;
        }
        File[] xlss = signal_home.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                Log.i(Constant.TAG, "path name : " + pathname.getName()) ;
                return pathname.isFile() && pathname.getAbsolutePath().endsWith(".xls");
            }
        }) ;
        Log.i(Constant.TAG, "check directory ") ;
        if (xlss == null || !(xlss.length > 0)) return files ;

        for (File xls : xlss){
            files.add(xls.getAbsolutePath()) ;
        }

        return files ;
    }

    public void getAllReports(final BaseCallback<List<String>> callback){
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                try {
                    e.onNext(doStuff());
                    e.onComplete();
                }catch (Exception ex){
                    ex.printStackTrace();
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> s) throws Exception {
                        callback.onSuccess(s);
                    }
                }) ;
    }
}
