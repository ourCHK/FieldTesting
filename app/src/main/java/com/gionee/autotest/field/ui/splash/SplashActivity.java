package com.gionee.autotest.field.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by viking on 11/6/17.
 *
 * Splash screen activity
 */
public class SplashActivity extends BaseActivity implements SplashView {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    private SplashPresenter<SplashView> splashPresenter ;

    private Handler mHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow() ;
        if (window != null){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        splashPresenter = new SplashPresenter<>() ;
        splashPresenter.onAttach(this);
        mHandler = new Handler() ;
        //check should we load all data to database or not, this must be async
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                splashPresenter.checkLoadAllDataToDB(getApplicationContext());
                //relay about one seconds to start main activity
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        splashPresenter.startMainScreen();
                    }
                }, SPLASH_TIME_OUT);
            }
        }) ;
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
