package com.gionee.autotest.field.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.gionee.autotest.field.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viking on 11/6/17.
 *
 * Adapter for main screen's applications
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    private LayoutInflater mInflater ;

    private List<String> mItems ;

    public AppsAdapter(Context mContext){
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public AppsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.layout_main_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(AppsAdapter.MyViewHolder holder, int position) {
        final String item = mItems.get(position) ;
        holder.mTitle.setText(item);
    }

    @Override
    public int getItemCount() {
        // If no items are present, there's no need for loader
        if (mItems == null || mItems.size() == 0) {
            return 0;
        }
        return mItems.size() ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    public void setItems(List<String> items) {
        mItems = items;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_title)
        TextView mTitle ;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView) ;
        }
    }
}
