package com.gionee.autotest.field.ui.install;

import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.install.model.InstallBiz;

import java.util.List;

/**
 * Created by viking on 11/9/17.
 *
 * Presenter for install screen
 */

public class InstallAppPresenter extends BasePresenter<InstallAppContract.View> implements InstallAppContract.Presenter {

    private InstallBiz mInstallBiz ;

    public InstallAppPresenter(){
        mInstallBiz = new InstallBiz() ;
    }

    @Override
    public void getUninstalledApps() {
        if (isViewAttached()){
            getView().setNoDataVisibility(false);
            getView().setListVisibility(false);
            getView().showLoading();
            mInstallBiz.fetchAllUnInstalledApps(callback);
        }
    }

    private void showList(boolean isVisible) {
        getView().setNoDataVisibility(!isVisible);
        getView().setListVisibility(isVisible);
    }

    private final BaseCallback callback = new BaseCallback<List<App>>() {

        @Override
        public void onSuccess(List<App> apps) {
            getView().hideLoading();
            //must not empty and size greater that 1
            if (apps != null && apps.size() > 0){
                getView().initializeUninstalledAppsList(apps);
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
