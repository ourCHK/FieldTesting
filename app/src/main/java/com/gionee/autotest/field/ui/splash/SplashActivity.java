package com.gionee.autotest.field.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by viking on 11/6/17.
 * <p>
 * Splash screen activity
 */
public class SplashActivity extends BaseActivity implements SplashContract.View {

    private static final int MY_PERMISSION_REQUEST = 101;

    private SplashPresenter splashPresenter;

    @BindView(R.id.root_layout)
    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initializePresenter() {
        splashPresenter = new SplashPresenter(getApplicationContext());
        super.presenter = splashPresenter;
        splashPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDetach();
    }

    @Override
    public void navigateToMainScreen() {
        String[] requestedPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
        final List<String> notGrantedPerms = new ArrayList<>();
        for (String perm : requestedPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                Log.i(Constant.TAG, "not granted permission : " + perm);
                notGrantedPerms.add(perm);
            }
        }
        if (!notGrantedPerms.isEmpty()) {
            String[] permissions = notGrantedPerms.toArray(new String[notGrantedPerms.size()]);
            Log.i(Constant.TAG, "permission not granted...");
            for (String perm : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    Log.i(Constant.TAG, "show request permission rationale : " + perm);
                    int resId = -1;
                    if (perm.equals(Manifest.permission.READ_PHONE_STATE)) {
                        resId = R.string.permission_read_phone_state_rationale;
                    } else if (perm.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        resId = R.string.permission_write_external_storage_rationale;
                    } else if (perm.equals(Manifest.permission.CALL_PHONE)) {
                        resId = R.string.permission_call_phone_rationale;
                    }else if (perm.equals(Manifest.permission.READ_CALL_LOG)) {
                        resId = R.string.permission_read_call_log_rationale;
                    }
                    Snackbar.make(mLayout, resId, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.go_settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goSettings();
                                }
                            })
                            .show();
                    return;
                }
            }
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_REQUEST);
        } else {
            Log.i(Constant.TAG, "all permissions granted...");
            createHomeDirectory();
            realNavigateToMainScreen();
        }
    }

    private void goSettings() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                boolean isAllGranted = isAllGranted(grantResults);
                if (isAllGranted) {
                    createHomeDirectory();
                    realNavigateToMainScreen();
                } else {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String perm = permissions[i];
                            int resId = -1;
                            if (perm.equals(Manifest.permission.READ_PHONE_STATE)) {
                                resId = R.string.permission_read_phone_state_rationale_denied;
                            } else if (perm.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                resId = R.string.permission_write_external_storage_rationale_denied;
                            }else if (perm.equals(Manifest.permission.CALL_PHONE)) {
                                resId = R.string.permission_call_phone_rationale;
                            }else if (perm.equals(Manifest.permission.READ_CALL_LOG)) {
                                resId = R.string.permission_read_call_log_rationale;
                            }
                            Snackbar.make(mLayout, resId, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.go_settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            goSettings();
                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                }
                break;
        }
    }

    private boolean isAllGranted(int[] grantResults) {
        if (grantResults.length > 0) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createHomeDirectory() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i(Constant.TAG, "external storage state is not mounted...");
            return;
        }
        File home = new File(Environment.getExternalStorageDirectory(), Constant.HOME);
        if (!home.exists() && !home.mkdirs()) {
            Log.i(Constant.TAG, "make home directory failed...ignore this");
        }
    }

    private void realNavigateToMainScreen() {
        //TODO fix this!!!
        //init signal monitor service
        Log.i(Constant.TAG, "PREF_KEY_MONITOR_SIGNAL : "
                + Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_MONITOR_SIGNAL, false));
        Log.i(Constant.TAG, "PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL : "
                + Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL, false));
        if (!Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_MONITOR_SIGNAL, false)
                //is manual stop service?
                && !Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL, false)) {
            Log.i(Constant.TAG, "enable signal monitor");
            Intent monitor = new Intent(getApplicationContext(), SignalMonitorService.class);
            startService(monitor);
            Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_MONITOR_SIGNAL, true);
        }
        //relay about one seconds to start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(main);
                //always remember to finish itself
                finish();
            }
        }, Constant.SPLASH_TIME_OUT);
    }
}
