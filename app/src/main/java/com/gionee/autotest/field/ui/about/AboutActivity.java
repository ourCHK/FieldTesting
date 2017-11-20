package com.gionee.autotest.field.ui.about;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by viking on 11/20/17.
 *
 * entry for about
 */

public class AboutActivity extends BaseActivity implements AboutContract.View{

    AboutPresenter aboutPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        aboutPresenter = new AboutPresenter() ;
        super.presenter = aboutPresenter ;
        aboutPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aboutPresenter.onDetach();
    }
}
