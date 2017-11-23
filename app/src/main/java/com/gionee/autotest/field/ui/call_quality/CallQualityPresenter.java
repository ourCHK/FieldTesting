package com.gionee.autotest.field.ui.call_quality;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.util.Constant;

/**
 * Created by Viking on 2017/11/22.
 *
 * presenter for call quality
 */

public class CallQualityPresenter extends BasePresenter<CallQualityContract.View>
        implements CallQualityContract.Presenter{

    private Context mContext ;

    CallQualityPresenter(Context mContext){
        this.mContext = mContext ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null ;
    }

    @Override
    public void checkRunningSingle(View view) {
        Log.i(Constant.TAG, "checkRunningSingle.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningSingle not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningSingle change multi button state to true.") ;
        getView().onSingleClicked(view);
    }

    @Override
    public void checkRunningMulti(View view) {
        Log.i(Constant.TAG, "checkRunningMulti.") ;
        if (!Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false)){
            getView().showNotRunningMsg();
            Log.i(Constant.TAG, "checkRunningMulti not running should show.") ;
            return ;
        }
        Log.i(Constant.TAG, "checkRunningMulti change multi button state to false.") ;
        getView().onMultiClicked(view);
    }

    @Override
    public void onStartClicked(String phone_num, String phone_num_o) {
        if (Preference.getBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true)){
            if (phone_num == null || "".equals(phone_num) || phone_num_o == null || "".equals(phone_num_o)){
                getView().showErrorPhoneNumMsg();
                return ;
            }
            getView().startWithSuccessfully();
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, false) ;
            Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, true) ;
        }else{
            getView().onNextRoundClicked();
        }
    }

    @Override
    public void onStopClicked() {
        getView().stopWithSuccessfully();
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_FIRST_ROUND, true) ;
        Preference.putBoolean(mContext, Constant.PREF_KEY_CALL_QUALITY_RUNNING, false) ;
    }
}
