package com.gionee.autotest.field.ui.signal;

import com.gionee.autotest.field.ui.base.BaseView;

import java.io.File;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface SignalContract {

    int EXPORT_ERROR_CODE_NO_SIGNAL_DATA                   = 0 ;
    int EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE     = 1 ;
    int EXPORT_ERROR_CODE_FAILURE                          = 2 ;

    interface View extends BaseView {

        void setDefaultInterval(String time) ;

        void setStartButtonVisibility(boolean visibility) ;

        void setStopButtonVisibility(boolean visibility) ;

        void showFrequencyError() ;

        void showNotSupportedDialog() ;

        void showStartToast() ;

        //for export
        void showSignalExportError(int errorCode) ;

        void showSignalExportSuccess(String filePath) ;

    }

    interface Presenter {

        void isIntervalValid(String time) ;

        void registerSignalListener(String interval) ;

        void unregisterSignalListener() ;

        void doExport(File target, File destination) ;
    }
}
