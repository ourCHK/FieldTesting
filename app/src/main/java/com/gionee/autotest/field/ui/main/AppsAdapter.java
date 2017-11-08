package com.gionee.autotest.field.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.views.CircleImageView;

import java.lang.reflect.Field;
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

    private List<App> mItems ;

    public interface OnItemClickListener {

        void onItemClick(App item, int position);

        void onItemLongClick(App item, int position) ;
    }

    private final OnItemClickListener listener;

    public AppsAdapter(Context mContext , OnItemClickListener listener){
        mInflater = LayoutInflater.from(mContext);
        this.listener = listener ;
    }

    @Override
    public AppsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.layout_main_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(AppsAdapter.MyViewHolder holder, final int position) {
        final App app = mItems.get(position) ;
        holder.mTitle.setText(app.getLabel());
        holder.mIcon.setImageResource(getImageByReflect(app.getIcon()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (listener != null) listener.onItemClick(app, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) listener.onItemLongClick(app, position);
                return true;
            }
        });
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

    public void setItems(List<App> items) {
        mItems = items;
    }

    private int getImageByReflect(String image) {
        try {
            Field field = Class.forName("com.gionee.autotest.field.R$drawable").getField(image);
            return field.getInt(field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0 ;
    }

        class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_title)
        TextView mTitle ;

        @BindView(R.id.item_icon)
        CircleImageView mIcon ;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView) ;
        }
    }
}
