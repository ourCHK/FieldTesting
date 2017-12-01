package com.gionee.autotest.field.ui.throughput;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.throughput.Util.SpeedListViewAdapter;

import java.util.ArrayList;


/**
 * Created by siqiliu on 2017/5/16.
 */

public class SpeedActivity extends AppCompatActivity {
    private ListView mListView;
    private SpeedListViewAdapter mSpeedAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLogo();
        setContentView(R.layout.speed_detils);
        mListView=(ListView)findViewById(R.id.speed_result);
        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<String> speed = bundle.getStringArrayList("speed");
        ArrayList<String> data = bundle.getStringArrayList("time");
        mSpeedAdapter=new SpeedListViewAdapter(this,speed,data);
        mListView.setAdapter(mSpeedAdapter);
    }
    private void setLogo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }
}
