package com.gionee.autotest.field.ui.main;

import android.os.Bundle;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by viking on 11/6/17.
 *
 * Main screen activity
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUnBinder(ButterKnife.bind(this));
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

}
