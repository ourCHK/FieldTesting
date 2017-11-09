package com.gionee.autotest.field.ui.install;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.AppsDBManager;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Util;
import com.gionee.autotest.field.views.GNRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by viking on 11/8/17.
 *
 * Install app
 */

public class InstallAppActivity extends BaseActivity implements InstallAppsAdapter.InstallListener, InstallAppContract.View{

    @BindView(R.id.install_recycler_view)
    GNRecyclerView mRecyclerView ;

    @BindView(R.id.list_empty_view)
    View emptyView ;

    @BindView(R.id.empty_text)
    TextView mEmptyTextView ;

    private InstallAppsAdapter mAppsAdapter ;

    private List<App> mApps ;

    private InstallAppPresenter mInstallPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_install;
    }

    @Override
    protected void initializePresenter() {
        mInstallPresenter = new InstallAppPresenter() ;
        super.presenter = mInstallPresenter ;
        mInstallPresenter.onAttach(this);
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constant.TAG, "enter InstallAppActivity onCreate") ;
        initViews() ;
    }

    private void initViews() {
        mEmptyTextView.setText(R.string.empty_apps);
        mInstallPresenter.getUninstalledApps();
    }

    @Override
    public void onItemInstallClicked(int position) {
        App app = mApps.get(position) ;
        if (app.isInstalled()){
            Toast.makeText(this, R.string.app_has_installed, Toast.LENGTH_SHORT).show();
            return ;
        }
        AppsDBManager.updateApp(app.getKey(), true) ;
        app.setInstalled(true);
        mAppsAdapter.setItems(mApps);
        mAppsAdapter.notifyItemChanged(position);
    }

    @Override
    public void setNoDataVisibility(boolean isVisible) {
        emptyView.setVisibility(isVisible ? View.VISIBLE: View.GONE);
    }

    @Override
    public void setListVisibility(boolean isVisible) {
        mRecyclerView.setVisibility(isVisible ? View.VISIBLE: View.GONE);
    }

    @Override
    public void initializeUninstalledAppsList(List<App> apps) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Fetch the empty view from the layout and set it on
        // the new recycler view
        mAppsAdapter = new InstallAppsAdapter(getApplicationContext(), this) ;
        mAppsAdapter.setHasStableIds(true);
        mApps = apps ;
        //maybe all apps are deleted, forbidden NullPointerException occur
        for (App app : mApps){
            Log.i(Constant.TAG, "key : " + app.getKey()) ;
            Log.i(Constant.TAG, "label : " + app.getLabel()) ;
            Log.i(Constant.TAG, "icon : " + app.getIcon()) ;
            Log.i(Constant.TAG, "activity : " + app.getActivity()) ;
        }
        mAppsAdapter.setItems(mApps);
        mRecyclerView.setAdapter(mAppsAdapter);
    }
}
