package com.gionee.autotest.field.ui.signal;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by viking on 11/13/17.
 *
 * For signal information test
 */

public class SignalActivity extends BaseActivity implements SignalContract.View{

    @BindView(R.id.signal_frequency)
    EditText mFrequency ;

    @BindView(R.id.signal_start)
    Button mBtnStart ;

    @BindView(R.id.signal_stop)
    Button mBtnStop ;

    private SignalPresenter mSignalPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_signal;
    }

    @Override
    protected void initializePresenter() {
        mSignalPresenter = new SignalPresenter() ;
        super.presenter = mSignalPresenter ;
        mSignalPresenter.onAttach(this);
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @OnClick(R.id.signal_start)
    void onSignalStartClicked(){
        Toast.makeText(this, "Signal start clicked.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.signal_stop)
    void onSignalStopClicked(){
        Toast.makeText(this, "Signal stop clicked.", Toast.LENGTH_SHORT).show();
    }
}
