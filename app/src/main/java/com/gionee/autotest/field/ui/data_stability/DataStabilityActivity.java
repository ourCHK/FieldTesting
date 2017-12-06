package com.gionee.autotest.field.ui.data_stability;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.util.About;
import com.gionee.autotest.field.util.DataStabilityUtil;

public class DataStabilityActivity extends AppCompatActivity implements View.OnClickListener, IMain {
    private Button mStartBtn;
    private EditText mWaitTimeET;
    private EditText mTestTimesET;
    private MainAction mainAction;
    private RadioButton forbidSleepCB;
    private RadioButton mSleepAfterTestCB;
    private RadioButton mCallAfterTestCB;
    private EditText mVerifyCount;
    private EditText mTimeOfCallET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_stability);
        initViews();
        mainAction = new MainAction(this);
        DataParam lastParam = mainAction.getLastParam();
        updateParams(lastParam);
        updateViews();
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateViews();
            }
        }, new IntentFilter("updateViews"));
    }

    private void updateParams(DataParam lastParam) {
        mWaitTimeET.setText(String.valueOf(lastParam.waitTime));
        mTestTimesET.setText(String.valueOf(lastParam.testTimes));
        forbidSleepCB.setChecked(lastParam.isForbidSleep);
        mSleepAfterTestCB.setChecked(lastParam.sleepAfterTest);
        mCallAfterTestCB.setChecked(lastParam.callAfterTest);
        mVerifyCount.setText(String.valueOf(lastParam.verifyCount));
    }

    @Override
    public void onClick(View v) {
        if (DataStabilityUtil.isTest) {
            DataStabilityUtil.isTest = false;
            mainAction.stopTest();
        } else {
            DataStabilityUtil.isTest = true;
            mainAction.startTest(getInputParam());
        }
        updateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    DataParam getInputParam() {
        int waitTimeInt = Integer.parseInt(mWaitTimeET.getText().toString());
        int testTimes = Integer.parseInt(mTestTimesET.getText().toString());
        int verifyCount = Integer.parseInt(mVerifyCount.getText().toString());
        int timeOfCall = Integer.parseInt(mTimeOfCallET.getText().toString());
        boolean isForbidSleep = forbidSleepCB.isChecked();
        return new DataParam(waitTimeInt, testTimes, isForbidSleep, mSleepAfterTestCB.isChecked(), mCallAfterTestCB.isChecked(), verifyCount, timeOfCall);
    }

    private void initViews() {
        mStartBtn = (Button) findViewById(R.id.startBtn);
        mWaitTimeET = (EditText) findViewById(R.id.waitTimeET);
        mTestTimesET = (EditText) findViewById(R.id.testTimesET);
        forbidSleepCB = (RadioButton) findViewById(R.id.forbidSleepCB);
        mSleepAfterTestCB = (RadioButton) findViewById(R.id.sleepAfterTestCB);
        mCallAfterTestCB = (RadioButton) findViewById(R.id.callAfterTestCB);
        mVerifyCount = (EditText) findViewById(R.id.webViewTestVerifyCount);
        mTimeOfCallET = (EditText) findViewById(R.id.timeOfCallET);
        mStartBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_stability, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(AboutActivity.getAboutIntent(this, getString(R.string.data_stability_about), true));
            return true;
        } else if (id == R.id.clear_report) {
            mainAction.clearReport();
            return true;
        } else if (id == R.id.action_report) {
            mainAction.showReportPage();
            return true;
        } else if (id == R.id.export_data_stability_excel) {
            mainAction.exportDataStabilityExcelFile();
        } else if (id == R.id.open_data_stability_excel) {
            mainAction.openDataStabilityExcelFile();
        } else if (id == android.R.id.home) {
            if (DataStabilityUtil.isTest) {
                mainAction.showExitWarningDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void updateViews() {
        mStartBtn.setText(DataStabilityUtil.isTest ? "停止测试" : "开始测试");
        setViewsEnabled(!DataStabilityUtil.isTest, mWaitTimeET, mTestTimesET, forbidSleepCB, mCallAfterTestCB, mSleepAfterTestCB);
    }

    @Override
    public void doFinish() {
        finish();
    }

    public void setViewsEnabled(boolean isEnabled, View... views) {
        for (View view : views) {
            view.setEnabled(isEnabled);
        }
    }

    @Override
    public void onBackPressed() {
        if (DataStabilityUtil.isTest) {
            mainAction.showExitWarningDialog();
        } else {
            super.onBackPressed();
        }
    }
}
