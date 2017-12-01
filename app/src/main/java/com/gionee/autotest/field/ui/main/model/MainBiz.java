package com.gionee.autotest.field.ui.main.model;

import com.gionee.autotest.field.data.db.AppsDBManager;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.util.Util;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viking on 11/9/17.
 *
 * main screen logic stuff
 */

public class MainBiz {

    public void fetchAllInstallApps(final BaseCallback<List<App>> listener){
        Observable.create(new ObservableOnSubscribe<List<App>>() {
            @Override
            public void subscribe(ObservableEmitter<List<App>> e) throws Exception {
                //fetch data from db
                List<App> mApps = AppsDBManager.fetchAllApps(true) ;
                Collections.sort(mApps, Util.APP_COMPARATOR);
                e.onNext(mApps);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<App>>() {
                    @Override
                    public void accept(List<App> apps) throws Exception {
                        listener.onSuccess(apps);
                    }
                }) ;

    }

    public void uninstallApp(final BaseCallback<Boolean> listener , final App app){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                //fetch data from db
                AppsDBManager.updateApp(app.getKey(), false) ;
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        listener.onSuccess(result);
                    }
                }) ;
    }
}
