package com.gionee.autotest.field.ui.throughput.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.autotest.field.R;

import java.util.ArrayList;

/**
 * Created by siqiliu on 2017/5/16.
 */

public class SpeedListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> speed;
    private ArrayList<String> time;
    public SpeedListViewAdapter(Context context,ArrayList<String> speed,ArrayList<String> time){
        this.context=context;
        this.speed=speed;
        this.time=time;
    }
    @Override
    public int getCount() {
        return speed.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InitView initView;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_speed, null);
            initView = new InitView();
            initView.speed = (TextView) convertView.findViewById(R.id.speed);
            initView.data = (TextView) convertView.findViewById(R.id.data);
            convertView.setTag(initView);
        } else {
            initView = (SpeedListViewAdapter.InitView) convertView.getTag();
        }
        String bean = speed.get(position);
        String data = time.get(position);
        initView.speed.setText(bean);
        initView.data.setText(data);
        return convertView;
    }

    public class InitView{
        TextView speed;
        TextView data;
    }
}
