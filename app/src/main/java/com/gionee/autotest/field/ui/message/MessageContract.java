package com.gionee.autotest.field.ui.message;

import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by xhk on 2017/11/24.
 */

public interface MessageContract {

    interface View extends BaseView {

        void setDefaultInterval(String time) ;

        void showMessageTypeError() ;

        void showSimError() ;

        void showPhoneError() ;

        void showFrequencyError() ;

        void showStartToast() ;

        void setStartButtonVisibility(boolean visibility) ;

        void setStopButtonVisibility(boolean visibility) ;

    }

    interface Presenter {

        void isIntervalValid(int message_type,int sim,CharSequence et_phone,String time);

        void setInterval(String et_phone,String time) ;

        void registerMessageListener(String phone,String interval,int message_type) ;

        void unregisterMessageListener() ;

        void setMessageRunning(boolean isRunning) ;


    }
}
