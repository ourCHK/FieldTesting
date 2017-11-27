package com.gionee.autotest.field.ui.throughput;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.throughput.SQL.DatabaseUtil;
import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.ui.throughput.Util.ListViewAdapter;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean.RateBean.Speed;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mLv;
    ListViewAdapter mAdapter;
    String start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLogo();
        setContentView(R.layout.result_listview);
        mLv = (ListView) findViewById(R.id.result);
        mLv.setOnItemClickListener(this);
        Bundle bundle = getIntent().getExtras();
        start= bundle.getString("start");
        mAdapter = new ListViewAdapter(this);
        mLv.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new UpdateData().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<SpeedBean> data = mAdapter.getData();
        SpeedBean speedBean = data.get(position);
        SpeedBean.RateBean speed = speedBean.speed;
        ArrayList<Speed> speeds = speed.speeds;
        ArrayList<String> speedDetial=new ArrayList<>();
        ArrayList<String> timeDetial=new ArrayList<>();
        for (int i = 0; i < speeds.size(); i++) {
            speedDetial.add(speeds.get(i).speed);
            timeDetial.add(speeds.get(i).time);
        }
        Intent intent=new Intent(ResultActivity.this,SpeedActivity.class);
        intent.putExtra("speed",speedDetial);
        intent.putExtra("time",timeDetial);
        startActivity(intent);
    }

    class UpdateData extends AsyncTask<Void, Void, ArrayList<SpeedBean>> {

        @Override
        protected ArrayList<SpeedBean> doInBackground(Void... params) {
            DatabaseUtil db = new DatabaseUtil(ResultActivity.this);
            Helper.i(start+"");
            ArrayList<SpeedBean> content = db.getTimeContent(start);
            db.close();
            return content;
        }

        @Override
        protected void onPostExecute(ArrayList<SpeedBean> list) {
            mAdapter.updateData(list);
            super.onPostExecute(list);
        }
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
