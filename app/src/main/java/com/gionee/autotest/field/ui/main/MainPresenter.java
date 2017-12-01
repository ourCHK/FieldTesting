package com.gionee.autotest.field.ui.main;

import android.content.Context;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.main.model.MainBiz;
import com.gionee.autotest.field.util.Constant;

import java.util.List;

/**
 * Created by viking on 11/9/17.
 *
 * main screen presenter
 */

class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private MainBiz mainBiz ;

    MainPresenter(){
        mainBiz = new MainBiz() ;
    }

    @Override
    public void getInstallApps() {
        if (!isViewAttached()) return ;
        getView().setNoDataVisibility(false);
        getView().setListVisibility(false);
        getView().showLoading(null);
        mainBiz.fetchAllInstallApps(callback);
    }

    @Override
    public void uninstallApp(App app, final int position) {
        if (!isViewAttached()) return ;
        mainBiz.uninstallApp(new BaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                getView().uninstallSuccess(position);
            }

            @Override
            public void onFail() {
                //do nothing
            }
        }, app);
    }

    @Override
    public void shouldShowNoticeOrNot(Context context) {
        if (Preference.getBoolean(context, Constant.PREF_KEY_FIRST_NOTICE_LAUNCH, true)){
            getView().showNoticeDialog();
            //set it to false
            Preference.putBoolean(context, Constant.PREF_KEY_FIRST_NOTICE_LAUNCH, false) ;
        }
    }

    private void showList(boolean isVisible) {
        getView().setNoDataVisibility(!isVisible);
        getView().setListVisibility(isVisible);
    }

    private final BaseCallback<List<App>> callback = new BaseCallback<List<App>>() {

        @Override
        public void onSuccess(List<App> apps) {
            getView().hideLoading();
            //must not empty and size greater that 1
            if (apps != null && apps.size() > 0){
                getView().initializeAppsList(apps);
                showList(true);
            }else{
                showList(false);
            }
        }

        @Override
        public void onFail() {
            getView().hideLoading();
        }
    } ;
}
