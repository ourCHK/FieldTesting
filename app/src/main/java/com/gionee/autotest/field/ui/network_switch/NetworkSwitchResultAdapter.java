package com.gionee.autotest.field.ui.network_switch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;

import java.util.ArrayList;


class NetworkSwitchResultAdapter extends BaseAdapter {
    private Context                        context;
    private ArrayList<NetworkSwitchResult> mData;

    NetworkSwitchResultAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
    }

    void updateData(ArrayList<NetworkSwitchResult> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, android.view.View view, ViewGroup viewGroup) {
        ResultViewHolder holder;
        if (view == null) {
            holder = new ResultViewHolder();
            view = View.inflate(context, R.layout.item_network_switch, null);
            holder.count = (TextView) view.findViewById(R.id.item_count);
            holder.SignNetwork_1 = (TextView) view.findViewById(R.id.item_SignNetwork_1);
            holder.SignNetwork_2 = (TextView) view.findViewById(R.id.item_SignNetwork_2);
            holder.readSim_1 = (TextView) view.findViewById(R.id.item_readSim_1);
            holder.readSim_2 = (TextView) view.findViewById(R.id.item_readSim_2);
            holder.isNet = (TextView) view.findViewById(R.id.item_isNet);
            holder.isSwitchedTV = (TextView) view.findViewById(R.id.is_switched);
            holder.result = (TextView) view.findViewById(R.id.item_result);
            holder.test_time = (TextView) view.findViewById(R.id.test_time);
            view.setTag(holder);
        } else {
            holder = (ResultViewHolder) view.getTag();
        }
        NetworkSwitchResult bean = mData.get(i);
        holder.count.setText(String.valueOf(i + 1));
        holder.SignNetwork_1.setText(bean.SignNetwork_1);
        holder.SignNetwork_2.setText(bean.SignNetwork_2);
        holder.readSim_1.setText(bean.readSim_1);
        holder.readSim_2.setText(bean.readSim_2);
        holder.isNet.setText(bean.isNet);
        holder.isSwitchedTV.setText(bean.isSwitched);
        holder.result.setText(bean.result);
        holder.test_time.setText(bean.test_time);
        holder.test_time.setVisibility(bean.isShow_Test_Time ? View.VISIBLE : View.GONE);
        return view;
    }

    public ArrayList<NetworkSwitchResult> getData() {
        return mData;
    }

    void setShowTestTime(int i) {
        NetworkSwitchResult resultBean = getData().get(i);
        resultBean.isShow_Test_Time = !resultBean.isShow_Test_Time;
        getData().set(i, resultBean);
        notifyDataSetChanged();
    }

    private static class ResultViewHolder {
        public TextView count;
        TextView SignNetwork_1;
        TextView SignNetwork_2;
        TextView readSim_1;
        TextView readSim_2;
        TextView isNet;
        public TextView result;
        TextView test_time;
        TextView isSwitchedTV;
    }
}