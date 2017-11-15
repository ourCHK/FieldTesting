package com.gionee.autotest.field.ui.data_reset;

import android.view.MenuItem;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by xhk on 2017/11/15.
 */

public class DataResetActivity extends BaseActivity implements DataResetContract.View{


    @Override
    protected int layoutResId() {
        return R.layout.activity_data_reset;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected int menuResId() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                //                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.help:
                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                //                startActivity(new Intent(this, AboutActivity.class));
                return true ;
        }
        return super.onOptionsItemSelected(item);
    }
}
