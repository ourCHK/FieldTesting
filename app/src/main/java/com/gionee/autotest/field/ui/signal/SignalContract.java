package com.gionee.autotest.field.ui.signal;

import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.util.SignalHelper;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface SignalContract {

    interface View extends BaseView {

        void setDefaultInterval(String time) ;

        void setStartButtonVisibility(boolean visibility) ;

        void setStopButtonVisibility(boolean visibility) ;

        void showFrequencyError() ;

        void showNotSupportedDialog() ;

        void showStartToast() ;

        //for export


    }

    interface Presenter {

        void isIntervalValid(String time) ;

        void registerSignalListener(String interval) ;

        void unregisterSignalListener() ;
    }
}
