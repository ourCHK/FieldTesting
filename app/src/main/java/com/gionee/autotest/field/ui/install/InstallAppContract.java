package com.gionee.autotest.field.ui.install;

import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BaseView;

import java.util.List;

/**
 * Created by viking on 11/9/17.
 *
 * Contract for install app screen
 */

public interface InstallAppContract {

    interface View extends BaseView {
        void setNoDataVisibility(boolean isVisible);

        void setListVisibility(boolean isVisible) ;

        void initializeUninstalledAppsList(List<App> apps) ;
    }

    interface Presenter {
        void getUninstalledApps() ;
    }
}
