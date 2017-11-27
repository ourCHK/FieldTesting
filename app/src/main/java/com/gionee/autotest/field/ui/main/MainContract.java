package com.gionee.autotest.field.ui.main;

import android.content.Context;

import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BaseView;

import java.util.List;

/**
 * Created by viking on 11/9/17.
 *
 * Contract for main screen
 */

interface MainContract {

    interface View extends BaseView {

        void setNoDataVisibility(boolean isVisible);

        void setListVisibility(boolean isVisible) ;

        void initializeAppsList(List<App> apps) ;

        void uninstallSuccess(int position) ;

        void showNoticeDialog() ;
    }

    interface Presenter {
        void getInstallApps() ;

        void uninstallApp(App app, int position) ;

        void shouldShowNoticeOrNot(Context context) ;
    }

}
