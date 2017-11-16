package com.gionee.autotest.field.ui.signal.model;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;

import java.io.File;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viking on 11/16/17.
 *
 * do export stuff
 */

public class ExportModel {

    public void exportExcel(final File target, final File destination, final BaseCallback<String> callback){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    //fetch data from db
                    doConvertStuff(target, destination) ;
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
                        callback.onFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                }) ;
    }

    private void doConvertStuff(File target, File destination) {

    }
}
