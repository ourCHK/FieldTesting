package com.gionee.autotest.field.ui.data_stability;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.util.About;
import com.gionee.autotest.field.util.DataStabilityUtil;

public class DataStabilityActivity extends AppCompatActivity implements View.OnClickListener, IMain {
    private Button mStartBtn;
    private EditText mWaitTimeET;
    private EditText mTestTimesET;
    private MainAction mainAction;
    private CheckBox forbidSleepCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_stability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
        mWaitTimeET.setText(lastParam.waitTime + "");
        mTestTimesET.setText(lastParam.testTimes + "");
        forbidSleepCB.setChecked(lastParam.isForbidSleep);
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
        int     waitTimeInt   = Integer.parseInt(mWaitTimeET.getText().toString());
        int     testTimes     = Integer.parseInt(mTestTimesET.getText().toString());
        boolean isForbidSleep = forbidSleepCB.isChecked();
        return new DataParam(waitTimeInt, testTimes, isForbidSleep);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        DataStabilityUtil.i("requestCode=" + requestCode + "resultCode=" + resultCode);
//        mainAction.onActivityResults(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void initViews() {
        mStartBtn = (Button) findViewById(R.id.startBtn);
        mWaitTimeET = (EditText) findViewById(R.id.waitTimeET);
        mTestTimesET = (EditText) findViewById(R.id.testTimesET);
        forbidSleepCB = (CheckBox) findViewById(R.id.forbidSleepCB);
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
            About.showAboutDialog(this);
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
        setViewsEnabled(!DataStabilityUtil.isTest, mWaitTimeET, mTestTimesET, forbidSleepCB);
    }

    public void setViewsEnabled(boolean isEnabled, View... views) {
        for (View view : views) {
            view.setEnabled(isEnabled);
        }
    }
}
