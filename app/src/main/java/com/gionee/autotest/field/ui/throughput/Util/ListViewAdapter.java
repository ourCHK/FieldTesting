package com.gionee.autotest.field.ui.throughput.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;

import java.util.ArrayList;



public class ListViewAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<SpeedBean> list;

    public ListViewAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateData(ArrayList<SpeedBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<SpeedBean> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.way=(TextView)convertView.findViewById(R.id.way);
            holder.textSize = (TextView) convertView.findViewById(R.id.text_size);
            holder.averageSpeed = (TextView) convertView.findViewById(R.id.average_speed);
            holder.useTime = (TextView) convertView.findViewById(R.id.use_time);
            holder.web = (TextView) convertView.findViewById(R.id.web);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SpeedBean bean = list.get(position);
        holder.time.setText(bean.id);
        holder.way.setText(bean.way);
        holder.textSize.setText(bean.type);
        holder.averageSpeed.setText(bean.speed_average + "");
        holder.useTime.setText(bean.use_time + "");
        holder.web.setText(bean.web);
        return convertView;
    }

    private class ViewHolder {
        TextView time;
        TextView way;
        TextView textSize;
        TextView averageSpeed;
        TextView useTime;
        TextView web;
    }
}
