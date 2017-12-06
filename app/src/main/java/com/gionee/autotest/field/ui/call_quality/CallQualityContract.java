package com.gionee.autotest.field.ui.call_quality;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.call_quality.model.ReportFile;

import java.io.File;
import java.util.List;

/**
 * Created by Viking on 2017/11/22.
 *
 * contract for call quality
 */

interface CallQualityContract {

    int EXPORT_ERROR_CODE_NO_DATA                           = 0 ;
    int EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE      = 1 ;
    int EXPORT_ERROR_CODE_FAILURE                           = 2 ;

    interface View extends BaseView {

        void showErrorPhoneNumMsg() ;

        void startWithSuccessfully() ;

        void stopWithSuccessfully() ;

        void onNextRoundClicked() ;

        void showNotRunningMsg() ;

        void onSingleClicked(android.view.View view) ;

        void onMultiClicked(android.view.View view) ;

        void showExportErrorInformation(int type) ;

        void showExportSuccessInformation(String path) ;

        void showEmptyReport() ;

        void showReport(List<ReportFile> files) ;
    }

    interface Presenter {

        void checkRunningSingle(android.view.View view, int phone_num, int quality_type, int event_type) ;

        void checkRunningMulti(android.view.View view, int phone_num, int quality_type, int event_type) ;

        void onStartClicked(String phone_num, String phone_num_o) ;

        void onStopClicked() ;

        void doExport(String phone_num, String phone_num_o, File target, File destination) ;

        void handleBackPressedAction(CallQualityActivity context) ;

        void fetchResults() ;
    }
}
