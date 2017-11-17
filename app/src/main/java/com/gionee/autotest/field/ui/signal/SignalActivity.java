package com.gionee.autotest.field.ui.signal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.util.Constant;

import java.io.File;

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

    @Override
    public void showSignalExportError(int errorCode) {
        switch (errorCode){
            case SignalContract.EXPORT_ERROR_CODE_NO_SIGNAL_DATA :
                Toast.makeText(this, R.string.export_signal_error_no_data, Toast.LENGTH_SHORT).show();
                break ;
            case SignalContract.EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE:
                Toast.makeText(this, R.string.export_signal_error_create_excel_failure, Toast.LENGTH_SHORT).show();
                break ;
            case SignalContract.EXPORT_ERROR_CODE_FAILURE:
                Toast.makeText(this, R.string.export_signal_error_failure, Toast.LENGTH_SHORT).show();
                break ;
        }
    }

    @Override
    public void showSignalExportSuccess(String filePath) {
        Toast.makeText(this, String.format(getString(R.string.export_signal_success), filePath),
                Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.signal_stop)
    void onSignalStopClicked(){
        Log.i(Constant.TAG, "enter onSignalStopClicked") ;
        Toast.makeText(this, R.string.signal_monitor_stopped, Toast.LENGTH_SHORT).show();
        mSignalPresenter.unregisterSignalListener();
        File target = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME +File.separator + Constant.SIGNAL_DIR, Constant.SIGNAL_DATA_NAME) ;
        File destination = new File(Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME +File.separator + Constant.SIGNAL_DIR,
                String.format(Constant.EXPORT_SIGNAL_DATA_NAME, TimeUtil.getTime("yyyy-MM-dd_HH-mm-ss"))) ;
        mSignalPresenter.doExport(target, destination);
        Log.i(Constant.TAG, "end onSignalStopClicked") ;
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
