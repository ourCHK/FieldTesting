package com.gionee.autotest.field.ui.data_reset;

import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by xhk on 2017/11/15.
 */

public  interface DataResetContract {

    interface View extends BaseView {

        void setDefaultInterval(String time,String retest_times) ;

        void showFrequencyError() ;

        void showRetesTimesError();

        void showStartToast() ;

        void setStartButtonVisibility(boolean visibility) ;

        void setStopButtonVisibility(boolean visibility) ;

    }

     interface Presenter {

         void isIntervalValid(String time,String retest_times);

         void setInterval(String time,String retest_times) ;

         void registerDataResetListener(String interval,String retest_times) ;

         void unregisterDataResetListener() ;

         void setDataResetRunning(boolean isRunning) ;


    }

}
