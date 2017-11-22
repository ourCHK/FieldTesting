package com.gionee.autotest.field.ui.network_switch;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

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
    @BindView(R.id.startBtn)
    Button   mStartBtn;
    @BindView(R.id.checkBox_SignNetwork)
    CheckBox mSignNetworkCB;
    @BindView(R.id.checkBox_readSim)
    CheckBox mReadSimCB;
    @BindView(R.id.checkBox_isNet)
    CheckBox mIsNetCB;

    private NetworkSwitchPresenter mPresenter;


    @OnClick(R.id.incoming_start_btn)
    void networkSwitchStartClicked() {

    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_in_coming;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }
//checkBox_Flight_Mode.setChecked(Preference.getBoolean(KEY_FLIGHT_MODE,true));
    //        checkBox_Reboot.setChecked(Preference.getBoolean(KEY_REBOOT, true));
//    boolean isSwitch = Preference.getBoolean(KEY_SWITCH_SIM, true);
//        checkBox_Switch_Sim.setChecked(isSwitch && Util.getSimCount() == 2);
//        testRound.setText(Preference.getLong(KEY_TEST_ROUND, 1L) + "");
//        testRound.setSelection(testRound.getText().length());
//        startBtn.setText(Preference.getBoolean(KEY_IS_TEST) ? "停止测试" : "开始测试");
//        checkBox_SignNetwork.setChecked(Preference.getBoolean(KEY_SIGN_NETWORK, true));
//        checkBox_readSim.setChecked(Preference.getBoolean(KEY_READ_SIM, true));
//        checkBox_isNet.setChecked(Preference.getBoolean(KEY_IS_NET, true));
//        checkBox_Flight_Mode.setOnClickListener(this);
//        checkBox_Reboot.setOnClickListener(this);
//        checkBox_Switch_Sim.setOnCheckedChangeListener(this);
//        startBtn.setOnClickListener(this);
//        checkBox_SignNetwork.setOnClickListener(this);
//        checkBox_readSim.setOnClickListener(this);
//        checkBox_isNet.setOnClickListener(this);
    @Override
    protected void initializePresenter() {
        mPresenter = new NetworkSwitchPresenter(getApplicationContext());
        super.presenter = mPresenter;
        mPresenter.onAttach(this);
        mPresenter.getLastParams();
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
