package com.gionee.autotest.field.ui.network_switch;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

public class NetworkSwitchActivity extends BaseActivity implements NetworkSwitchContract.View {
    @BindView(R.id.checkBox_Flight_Mode)
    CheckBox mFlightModeCB;
    @BindView(R.id.checkBox_Reboot)
    CheckBox mRebootCB;
    @BindView(R.id.checkBox_Switch_Sim)
    CheckBox mSwitchSimCB;
    @BindView(R.id.testRound)
    EditText mTestRoundET;
    @BindView(R.id.network_switch_startBtn)
    Button   mStartBtn;
    @BindView(R.id.checkBox_SignNetwork)
    CheckBox mSignNetworkCB;
    @BindView(R.id.checkBox_readSim)
    CheckBox mReadSimCB;
    @BindView(R.id.checkBox_isNet)
    CheckBox mIsNetCB;

    private NetworkSwitchPresenter mPresenter;


    @OnClick(R.id.network_switch_startBtn)
    void networkSwitchStartClicked() {
        mPresenter.handleClicked();
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_network_switch;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mPresenter = new NetworkSwitchPresenter(getApplicationContext());
        super.presenter = mPresenter;
        mPresenter.onAttach(this);
    }

    @Override
    public void updateParams(NetworkSwitchParam param) {
        mFlightModeCB.setChecked(param.flightMode);
        mRebootCB.setChecked(param.reboot);
        mSwitchSimCB.setChecked(param.isSwitchSim);
        mTestRoundET.setText(param.testRound + "");
        mTestRoundET.setSelection(mTestRoundET.getText().length());
        mSignNetworkCB.setChecked(param.signNetwork);
        mReadSimCB.setChecked(param.readSim);
        mIsNetCB.setChecked(param.isNet);
    }

    @Override
    public void updateViews() {
        boolean isTest = Preference.getBoolean(this, "isTest");
        mStartBtn.setText(isTest ? "停止测试" : "开始测试");
        setViewsEnable(!isTest, mFlightModeCB, mRebootCB, mSwitchSimCB, mTestRoundET, mSignNetworkCB, mReadSimCB, mIsNetCB);
    }

    @Override
    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setViewsEnable(boolean isEnable, View... v) {
        for (View mV : v) {
            mV.setEnabled(isEnable);
        }
    }

    public NetworkSwitchParam getInputParam() throws IllegalAccessException {
        String testRound = mTestRoundET.getText().toString();
        if (TextUtils.isEmpty(mTestRoundET.getText())) {
            throw new IllegalAccessException("轮数输入异常");
        }
        long testRoundLong;
        try {
            testRoundLong = Long.parseLong(testRound);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalAccessException("轮数输入异常");
        }
        NetworkSwitchParam networkSwitchParam = new NetworkSwitchParam()
                .setFlightMode(mFlightModeCB.isChecked())
                .setReboot(mRebootCB.isChecked())
                .setSwitchSim(mSwitchSimCB.isChecked())
                .setTestRound(testRoundLong)
                .setSignNetwork(mSignNetworkCB.isChecked())
                .setReadSim(mReadSimCB.isChecked())
                .setNet(mIsNetCB.isChecked());
        Preference.putString(getApplicationContext(), "lastParams", new Gson().toJson(networkSwitchParam));
        return networkSwitchParam;
    }

    @Override
    protected int menuResId() {
        return R.menu.incoming_menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_report:
                mPresenter.showReport();
                break;
            case R.id.clear_report:
                mPresenter.clearAllReport();
                break;
            case R.id.export_excel:
                mPresenter.exportExcelFile();
                break;
            case R.id.action_abouts:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
