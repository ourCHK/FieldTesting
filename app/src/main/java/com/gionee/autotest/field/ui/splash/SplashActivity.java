package com.gionee.autotest.field.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by viking on 11/6/17.
 *
 * Splash screen activity
 */
public class SplashActivity extends BaseActivity implements SplashView {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    private SplashPresenter<SplashView> splashPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUnBinder(ButterKnife.bind(this)) ;
        splashPresenter = new SplashPresenter<>() ;
        splashPresenter.onAttach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //relay about one seconds to start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashPresenter.startMainScreen();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDetach();
    }

    @Override
    public void openMainActivity() {
        Intent main = new Intent(this, MainActivity.class) ;
        startActivity(main);
        //always remember to finish itself
        finish();
    }
}
