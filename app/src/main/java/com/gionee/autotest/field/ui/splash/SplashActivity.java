package com.gionee.autotest.field.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;

/**
 * Created by viking on 11/6/17.
 *
 * Splash screen activity
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {

    private SplashPresenterLife splashPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow() ;
        if (window != null){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initializePresenter() {
        splashPresenter = new SplashPresenterLife(getApplicationContext()) ;
        super.presenter = splashPresenter ;
        splashPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDetach();
    }

    @Override
    public void navigateToMainScreen() {
        //relay about one seconds to start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashActivity.this, MainActivity.class) ;
                startActivity(main);
                //always remember to finish itself
                finish();
            }
        }, Constant.SPLASH_TIME_OUT);
    }
}
