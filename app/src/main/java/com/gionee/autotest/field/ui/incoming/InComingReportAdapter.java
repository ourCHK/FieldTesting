package com.gionee.autotest.field.ui.incoming;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.InComingReportBean;

public class InComingReportAdapter extends BaseAdapter {
    private       InComingReportBean mData;
    private final Context            context;

    public final InComingReportBean getMData() {
        return this.mData;
    }

    public final void setMData(InComingReportBean var1) {
        this.mData = var1;
    }

    public final void updateData(InComingReportBean data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    public Object getItem(int position) {
        return mData.data.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getCount() {
        return mData.data.size();
    }

    public View getView(int i, View v, ViewGroup parent) {
        InComingReportAdapter.ResultViewHolder holder;
        if (v == null) {
            v = View.inflate(this.context, R.layout.incoming_report_item, parent);
            holder = new InComingReportAdapter.ResultViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ResultViewHolder) v.getTag();
        }
        CallMonitorResult testData = this.mData.data.get(i);
        holder.index.setText(String.valueOf(i + 1));
        holder.number.setText(String.valueOf(testData.number));
        holder.type.setText(this.mData.type);
        holder.result.setText(testData.result ? "成功" : "失败");
        holder.failMsg.setText(testData.failMsg);
        holder.timeTv.setText(testData.time);
        v.setBackgroundResource(testData.result ? R.drawable.item_color_green : R.drawable.item_color_red);
        return v;
    }


    public final Context getContext() {
        return this.context;
    }

    public InComingReportAdapter(Context context) {
        super();
        this.context = context;
        this.mData = new InComingReportBean();
    }

    public static final class ResultViewHolder {

        private TextView index;

        private TextView number;

        private TextView result;

        private TextView type;

        private TextView failMsg;

        private TextView timeTv;

        ResultViewHolder(View v) {
            index = (TextView) v.findViewById(R.id.test_index);
            number = (TextView) v.findViewById(R.id.number);
            result = (TextView) v.findViewById(R.id.result);
            type = (TextView) v.findViewById(R.id.type);
            failMsg = (TextView) v.findViewById(R.id.failMsg);
            timeTv = (TextView) v.findViewById(R.id.timeTV);
        }
    }
}
