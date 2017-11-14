package com.gionee.autotest.field.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;

import java.io.File;

import butterknife.BindView;

/**
 * Created by viking on 11/6/17.
 *
 * Splash screen activity
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101 ;

    private SplashPresenter splashPresenter ;

    @BindView(R.id.root_layout)
    View mLayout ;

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
        splashPresenter = new SplashPresenter(getApplicationContext()) ;
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
        //request read sdcard permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Log.i(Constant.TAG, "permission WRITE_EXTERNAL_STORAGE not granted...") ;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Snackbar.make(mLayout, R.string.permission_write_external_storage_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(SplashActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        })
                        .show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }else{
            Log.i(Constant.TAG, "permission WRITE_EXTERNAL_STORAGE granted...") ;
            createHomeDirectory();
            realNavigateToMainScreen() ;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createHomeDirectory();
                    realNavigateToMainScreen() ;
                }else{
                    //show permission denied dialog
                    Snackbar.make(mLayout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
                }
                break ;
        }
    }

    private void createHomeDirectory(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constant.TAG, "external storage state is not mounted...") ;
            return ;
        }
        File home = new File(Environment.getExternalStorageDirectory() , Constant.HOME) ;
        if (!home.exists() && !home.mkdirs()){
            Log.i(Constant.TAG, "make home directory failed...ignore this") ;
        }
    }

    private void realNavigateToMainScreen(){
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
