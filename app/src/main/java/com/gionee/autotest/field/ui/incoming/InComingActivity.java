package com.gionee.autotest.field.ui.incoming;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

public class InComingActivity extends BaseActivity implements InComingContract.View {


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
        InComingPresenter inComingPresenter = new InComingPresenter();
        super.presenter = inComingPresenter;
        inComingPresenter.onAttach(this);
    }

}
