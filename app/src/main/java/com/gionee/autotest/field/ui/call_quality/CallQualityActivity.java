package com.gionee.autotest.field.ui.call_quality;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by Viking on 2017/11/22.
 */

public class CallQualityActivity extends BaseActivity implements CallQualityContract.View {

    private CallQualityPresenter mPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_call_quality;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mPresenter = new CallQualityPresenter() ;
        super.presenter = mPresenter ;
        mPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
