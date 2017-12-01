package com.gionee.autotest.field.ui.install;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.views.CircleImageView;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viking on 11/8/17.
 *
 * Adapter for install apps
 */

public class InstallAppsAdapter extends RecyclerView.Adapter<InstallAppsAdapter.InstallViewHolder> {

    private LayoutInflater mInflater ;

    private List<App> mItems ;

    interface InstallListener{

        void onItemInstallClicked(int position) ;
    }

    private InstallListener listener ;

    InstallAppsAdapter(Context mContext, InstallListener listener){
        mInflater = LayoutInflater.from(mContext);
        this.listener = listener ;
    }

    @Override
    public InstallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InstallViewHolder(mInflater.inflate(R.layout.layout_install_item, parent,
                false));
    }

    @Override
    public void onBindViewHolder(InstallViewHolder holder, final int position) {
        final App app = mItems.get(position) ;
        holder.mTitle.setText(app.getLabel());
        holder.mIcon.setImageResource(getImageByReflect(app.getIcon()));
        holder.mInstall.setText(app.isInstalled() ? R.string.installed : R.string.install);
        holder.mInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onItemInstallClicked(position);
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

    class InstallViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_title)
        TextView mTitle ;

        @BindView(R.id.item_icon)
        CircleImageView mIcon ;

        @BindView(R.id.item_install)
        Button mInstall ;

        InstallViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView) ;
        }
    }
}
