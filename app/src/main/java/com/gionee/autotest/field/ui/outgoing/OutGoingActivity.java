package com.gionee.autotest.field.ui.outgoing;


import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;

import butterknife.BindView;
import butterknife.OnClick;

public class OutGoingActivity extends BaseActivity implements OutGoingContract.View {

    @BindView(R.id.number_et)
    EditText mNumberET;
    @BindView(R.id.count_et)
    EditText mCountET;
    @BindView(R.id.gap_time_et)
    EditText mGapTimeET;
    @BindView(R.id.call_time_et)
    EditText mCallTimeET;
    @BindView(R.id.call_time_sum_et)
    EditText mCallTimeSumET;
    @BindView(R.id.is_speaker_phone_open)
    CheckBox mIsSpeakerPhoneOpenCB;
    @BindView(R.id.out_going_start)
    Button   mStartBtn;

    @OnClick(R.id.out_going_start)
    public void OutGoingStartClicked() {

    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_outgoing;
    }

    @Override
    protected void initializePresenter() {
        OutGoingPresenter outGoingPresenter = new OutGoingPresenter(this);
        super.presenter = outGoingPresenter;
        outGoingPresenter.onAttach(this);
    }


    @Override
    public void setParams(CallParam p) {
        mNumberET.setText(p.number);
        mCountET.setText(p.cycle);
        mGapTimeET.setText(p.gap_time);
        mCallTimeET.setText(p.call_time);
        mCallTimeSumET.setText(p.call_time_sum);
        mIsSpeakerPhoneOpenCB.setChecked(p.is_speaker_on);
    }
}
