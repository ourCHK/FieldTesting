package com.gionee.autotest.field.ui.signal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by viking on 11/13/17.
 *
 * For signal information test
 */

public class SignalActivity extends BaseActivity implements SignalContract.View,
        ActivityCompat.OnRequestPermissionsResultCallback{

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
    public void showStartToast(){
        Toast.makeText(this, R.string.signal_monitor_started, Toast.LENGTH_SHORT).show();
        mSignalPresenter.registerSignalListener(mFrequency.getText().toString());
    }

    @OnClick(R.id.signal_stop)
    void onSignalStopClicked(){
        Toast.makeText(this, R.string.signal_monitor_stopped, Toast.LENGTH_SHORT).show();
        mSignalPresenter.unregisterSignalListener();
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
