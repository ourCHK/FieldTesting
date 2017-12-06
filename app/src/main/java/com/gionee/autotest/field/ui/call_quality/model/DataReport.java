package com.gionee.autotest.field.ui.call_quality.model;

import android.os.Environment;
import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.util.Constant;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viking on 12/6/17.
 *
 * fetch data report
 */

public class DataReport {

    private List<ReportFile> doStuff(){
        //fetch data from db
        List<ReportFile> files = new ArrayList<>() ;
        File call_quality_home = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME, Constant.CALL_QUALITY_HOME);
        if (!call_quality_home.exists() && !call_quality_home.mkdirs()){
            Log.i(Constant.TAG, "make call quality directory failed...") ;
            return files ;
        }
        File[] dirs = call_quality_home.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                Log.i(Constant.TAG, "path name : " + pathname.getName()) ;
                return pathname.isDirectory() && isTimestampDir(pathname.getName());
            }
        }) ;
        Log.i(Constant.TAG, "check directory ") ;
        if (dirs == null || !(dirs.length > 0)) return files ;

        for (File dir :  dirs){
            Log.i(Constant.TAG, "dir : " + dir) ;
            File[] xlss = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getAbsolutePath().endsWith(".xls");
                }
            }) ;
            if (xlss != null && xlss.length > 0){
                files.add(new ReportFile(dir.getName(), xlss[0].getAbsolutePath())) ;
            }
        }

        return files ;
    }

    private boolean isTimestampDir(String name){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
            simpleDateFormat.parse(name);
            return true ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false ;
    }

    public void getAllReports(final BaseCallback<List<ReportFile>> callback){
        Observable.create(new ObservableOnSubscribe<List<ReportFile>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ReportFile>> e) throws Exception {
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
                .subscribe(new Consumer<List<ReportFile>>() {
                    @Override
                    public void accept(List<ReportFile> s) throws Exception {
                        callback.onSuccess(s);
                    }
                }) ;
    }
}
