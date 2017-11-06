package com.gionee.autotest.field.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import com.gionee.autotest.field.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {

    private SplashPresenter<SplashView> splashPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setUnBinder(ButterKnife.bind(this)) ;
        splashPresenter = new SplashPresenter<>() ;
        splashPresenter.onAttach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashPresenter.relayAndStartMainScreen();
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
