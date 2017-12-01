package com.gionee.autotest.field.ui.debug;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by viking on 11/27/17.
 *
 *
 */

public class DebugActivity extends BaseActivity implements DebugContract.View{

    private DebugPresenter mPresenter ;


    @Override
    protected int layoutResId() {
        return R.layout.layout_debug;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mPresenter = new DebugPresenter() ;
        super.presenter = mPresenter ;
        mPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
