package com.gionee.autotest.field.ui.throughput;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.ui.throughput.Util.ListViewAdapter;

import java.util.List;



public class ResultStartActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mLv;
    ListViewAdapter mAdapter;
    List newList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLogo();
        setContentView(R.layout.result_start);
        mLv = (ListView) findViewById(R.id.start_result);
        mLv.setOnItemClickListener(this);
        newList = Helper.getNewList(ResultStartActivity.this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ResultStartActivity.this,android.R.layout.simple_list_item_1, this.newList);
        mLv.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
//        new UpdateData().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String startTime = newList.get(position).toString();
        Helper.i("点击的时间为："+startTime);
        Intent intent=new Intent(ResultStartActivity.this,ResultActivity.class);
        intent.putExtra("start",startTime);
        startActivity(intent);
    }
    private void setLogo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }
}
