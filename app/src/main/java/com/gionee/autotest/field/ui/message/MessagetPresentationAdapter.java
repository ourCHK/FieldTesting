package com.gionee.autotest.field.ui.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.data_reset.OnCustomListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by xhk on 2017/11/30.
 */

public class MessagetPresentationAdapter extends RecyclerView.Adapter<MessagetPresentationAdapter.SelectionTypeHolder> {

    private Context mContext;
    protected OnCustomListener listener;
    private ArrayList<File> dirFileXlsList;

    public MessagetPresentationAdapter(Context mContext, ArrayList<File> dirFileXlsList) {
        this.mContext = mContext;
        this.dirFileXlsList = dirFileXlsList;
    }

    @Override
    public SelectionTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_selection_type,null);
        return new SelectionTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectionTypeHolder holder, final int position) {
        holder.tv_type.setText(dirFileXlsList.get(position).getName());
        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onCustomerListener(view,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dirFileXlsList.size();
    }


    public class SelectionTypeHolder extends RecyclerView.ViewHolder{

        RelativeLayout rl_item;
        TextView tv_type;
        public SelectionTypeHolder(View itemView) {
            super(itemView);
            rl_item = (RelativeLayout) itemView.findViewById(R.id.rl_item);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);

        }
    }

    /**
     *
     * 设置适配器上，某个控件的监听事件
     *
     * @param listener
     */
    public void setOnCustomListener(OnCustomListener listener) {
        this.listener = listener;
    }
}
