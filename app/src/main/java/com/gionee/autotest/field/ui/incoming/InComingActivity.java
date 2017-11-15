package com.gionee.autotest.field.ui.incoming;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class InComingActivity extends BaseActivity implements InComingContract.View, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.isAutoRejectCB)
    CheckBox mAutoRejectCB;
    @BindView(R.id.isAnswertHangup_cb)
    CheckBox mAnswerHangUpCB;
    @BindView(R.id.answerHangupTime_et)
    EditText mAnswerHangupTimeET;
    @BindView(R.id.autoReject_layout)
    CheckBox mAutoReject_layout;
    @BindView(R.id.autoAnswerHangup_layout)
    CheckBox mAutoAnswerHangup_layout;
    @BindView(R.id.isAutoAnswer)
    CheckBox mAutoAnswerCB;
    @BindView(R.id.isAutoReject_et)
    CheckBox mAutoRejectET;
    @BindView(R.id.spaceTime)
    CheckBox mSpaceTime;
    private InComingPresenter mInComingPresenter;

    @OnClick(R.id.incoming_start_btn)
    void incomingStartClicked(){
        mInComingPresenter.startMonitor(getCallMonitorParam());
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_in_coming;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mInComingPresenter = new InComingPresenter();
        super.presenter = mInComingPresenter;
        mInComingPresenter.onAttach(this);
        mAutoRejectCB.setOnCheckedChangeListener(this);
        mAnswerHangUpCB.setOnCheckedChangeListener(this);
        mAutoAnswerCB.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.isAutoRejectCB:
                mAutoReject_layout.setVisibility(isChecked ? VISIBLE : GONE);
                setAutoAnswerViewEnable(!isChecked);
                break;
            case R.id.isAnswertHangup_cb:
                mAnswerHangupTimeET.setEnabled(isChecked);
                break;
            case R.id.isAutoAnswer:
                mAnswerHangUpCB.setVisibility(isChecked ? VISIBLE : GONE);
                mAutoAnswerHangup_layout.setVisibility(isChecked ? VISIBLE : GONE);
                break;
            default:
                break;
        }
    }
    private CallMonitorParam getCallMonitorParam() {
        int autoEndTime = Integer.parseInt(mAnswerHangupTimeET.getText().toString().trim());
        String distinguishTime_text = mAutoRejectET.getText().toString().trim();
        int distinguishEndTime = distinguishTime_text.isEmpty()?0:Integer.parseInt(distinguishTime_text);
        CallMonitorParam param = new CallMonitorParam(mAutoRejectCB.isChecked(), distinguishEndTime, mAutoAnswerCB.isChecked(), mAnswerHangUpCB.isChecked(), autoEndTime, Integer.parseInt(mSpaceTime.getText().toString()));
        return param;
    }
    private void setAutoAnswerViewEnable(boolean enable) {
        if (!enable) {
            mAutoAnswerCB.setEnabled(false);
        }
        mAnswerHangUpCB.setEnabled(enable);
        mAnswerHangupTimeET.setEnabled(enable);
    }
}
