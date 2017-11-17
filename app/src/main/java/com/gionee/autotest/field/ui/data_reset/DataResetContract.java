package com.gionee.autotest.field.ui.data_reset;

import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by xhk on 2017/11/15.
 */

public  interface DataResetContract {

    interface View extends BaseView {

        void setDefaultInterval(String time) ;

        void showFrequencyError() ;

        void showStartToast() ;

        void setStartButtonVisibility(boolean visibility) ;

        void setStopButtonVisibility(boolean visibility) ;

    }

     interface Presenter {

         void isIntervalValid(String time);

         void setInterval(String time) ;

         void registerDataResetListener(String interval) ;

         void unregisterDataResetListener() ;

         void setDataResetRunning(boolean isRunning) ;


    }

//    interface RefreshUi{
//
//         void setRefreshUi(boolean isRunning);
//    }
}
