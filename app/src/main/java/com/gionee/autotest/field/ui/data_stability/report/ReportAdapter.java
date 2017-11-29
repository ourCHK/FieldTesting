package com.gionee.autotest.field.ui.data_stability.report;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.data_stability.ReportViewHolder;
import com.gionee.autotest.field.ui.data_stability.WebViewResultSum;
import com.gionee.autotest.field.util.DataStabilityUtil;

import java.util.ArrayList;

class ReportAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WebViewResultSum> mData;

    ReportAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
    }

    void updateData(ArrayList<WebViewResultSum> data) {
        mData.clear();
        if (!data.isEmpty()) {
            mData.addAll(data);
        }
        this.notifyDataSetChanged();
        DataStabilityUtil.i("刷新数据");
    }

    @Override
    public int getCount() {
        return mData.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ReportViewHolder holder;
        if (view == null) {
            holder = new ReportViewHolder();
            view = View.inflate(context, R.layout.item_report_list2, null);
            holder.textIndexTV = (TextView) view.findViewById(R.id.textIndexTV);
            holder.resultTV = (TextView) view.findViewById(R.id.resultTV);
            view.setTag(holder);
        } else {
            holder = (ReportViewHolder) view.getTag();
        }
        WebViewResultSum webViewResultSum = mData.get(i);
        DataStabilityUtil.i("报告item="+i+" "+(webViewResultSum.result ? "成功" : "失败"));
        holder.textIndexTV.setText("第" + (i + 1) + "次");//测试用
        holder.resultTV.setText(webViewResultSum.result ? "成功" : "失败");
        return view;
    }

    public ArrayList<WebViewResultSum> getData() {
        return mData;
    }
}
