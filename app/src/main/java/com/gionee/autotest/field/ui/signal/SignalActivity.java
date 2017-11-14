package com.gionee.autotest.field.ui.signal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by viking on 11/13/17.
 *
 * For signal information test
 */

public class SignalActivity extends BaseActivity implements SignalContract.View,
        ActivityCompat.OnRequestPermissionsResultCallback, SignalHelper.SignalStateListener{

    private static final int MY_PERMISSION_REQUEST_READ_PHONE_STATE = 101 ;

    @BindView(R.id.signal_frequency)
    EditText mFrequency ;

    @BindView(R.id.signal_start)
    Button mBtnStart ;

    @BindView(R.id.signal_stop)
    Button mBtnStop ;

    @BindView(R.id.root_layout)
    View mLayout ;

    private SignalPresenter mSignalPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_signal;
    }

    @Override
    protected void initializePresenter() {
        mSignalPresenter = new SignalPresenter(getApplicationContext()) ;
        super.presenter = mSignalPresenter ;
        mSignalPresenter.onAttach(this);
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @OnClick(R.id.signal_start)
    void onSignalStartClicked(){
        mSignalPresenter.isIntervalValid(mFrequency.getText().toString()) ;
    }

    @Override
    public void showFrequencyError() {
        Toast.makeText(this, R.string.frequency_set_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotSupportedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.signal_not_supported_message)
                .setPositiveButton(R.string.signal_not_supported_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent main = new Intent(SignalActivity.this, MainActivity.class) ;
                        startActivity(main);
                        finish();
                    }
                }) ;
        builder.show() ;
    }

    @Override
    public void requestReadPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            Log.i(Constant.TAG, "permission READ_PHONE_STATE not granted...") ;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
                Snackbar.make(mLayout, R.string.permission_read_phone_state_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(SignalActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        MY_PERMISSION_REQUEST_READ_PHONE_STATE);
                            }
                        })
                        .show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }else{
            Log.i(Constant.TAG, "permission READ_PHONE_STATE granted...") ;
            mSignalPresenter.registerSignalListener(this, mFrequency.getText().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSignalPresenter.registerSignalListener(this, mFrequency.getText().toString());
                }else{
                    //show permission denied dialog
                    Snackbar.make(mLayout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
                }
                break ;
        }
    }

    @OnClick(R.id.signal_stop)
    void onSignalStopClicked(){
        Toast.makeText(this, "Signal stop clicked.", Toast.LENGTH_SHORT).show();
        mSignalPresenter.unregisterSignalListener(this);
    }

    @Override
    public void onSimStateChanged(boolean sim1Exist, boolean sim2Exist) {
        Log.i(Constant.TAG, "onSimStateChanged : " + sim1Exist + " " + sim2Exist) ;
    }

    @Override
    public void onSignalStrengthsChanged(int simId, SignalHelper.SimSignalInfo signalInfo) {
        Log.i(Constant.TAG, "onSignalStrengthsChanged : simId " + simId + " " + signalInfo.toString()) ;
    }

    @Override
    public void setDefaultInterval(String time) {
        mFrequency.setText(time);
    }

    @Override
    public void setStartButtonVisibility(boolean visibility) {
        mBtnStart.setEnabled(visibility);
    }

    @Override
    public void setStopButtonVisibility(boolean visibility) {
        mBtnStop.setEnabled(visibility);
    }
}
