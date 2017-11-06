package com.gionee.autotest.field.ui.splash;

import android.os.Handler;

import com.gionee.autotest.field.ui.base.BasePresenter;

/**
 * Created by viking on 11/6/17.
 *
 * Presenter implementation for Splash screen.
 */

public class SplashPresenter <V extends SplashView> extends BasePresenter<V>{

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    public void relayAndStartMainScreen(){
        //relay about one seconds to start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getMvpView() != null){
                    getMvpView().openMainActivity();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
