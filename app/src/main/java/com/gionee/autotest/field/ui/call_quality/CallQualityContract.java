package com.gionee.autotest.field.ui.call_quality;

import android.view.View;

import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by Viking on 2017/11/22.
 *
 * contract for call quality
 */

interface CallQualityContract {

    interface View extends BaseView {

        void showErrorPhoneNumMsg() ;

        void startWithSuccessfully() ;

        void stopWithSuccessfully() ;

        void onNextRoundClicked() ;

        void showNotRunningMsg() ;

        void onSingleClicked(android.view.View view) ;

        void onMultiClicked(android.view.View view) ;
    }

    interface Presenter {

        void checkRunningSingle(android.view.View view, int phone_num, int quality_type, int event_type) ;

        void checkRunningMulti(android.view.View view, int phone_num, int quality_type, int event_type) ;

        void onStartClicked(String phone_num, String phone_num_o) ;

        void onStopClicked() ;
    }
}
