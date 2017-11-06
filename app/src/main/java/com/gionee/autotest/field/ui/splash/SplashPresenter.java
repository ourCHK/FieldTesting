package com.gionee.autotest.field.ui.splash;

import com.gionee.autotest.field.ui.base.BasePresenter;

/**
 * Created by viking on 11/6/17.
 *
 * Presenter implementation for Splash screen.
 */

public class SplashPresenter <V extends SplashView> extends BasePresenter<V>{

    public void startMainScreen(){
        if (getMvpView() != null){
            getMvpView().openMainActivity();
        }
    }
}
