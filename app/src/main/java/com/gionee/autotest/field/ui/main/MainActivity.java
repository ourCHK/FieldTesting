package com.gionee.autotest.field.ui.main;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.AppsDBManager;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.base.listener.RecyclerItemListener;
import com.gionee.autotest.field.ui.install.InstallAppActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.NpaGridLayoutManager;
import com.gionee.autotest.field.util.Util;
import com.gionee.autotest.field.views.GNRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by viking on 11/6/17.
 *
 * Main screen activity
 */
public class MainActivity extends BaseActivity implements RecyclerItemListener<App>, MainContract.View{

    @BindView(R.id.list_recycler_view)
    GNRecyclerView mRecyclerView ;

    @BindView(R.id.list_empty_view)
    View emptyView ;

    @BindView(R.id.fab)
    FloatingActionButton fab ;

    private AppsAdapter mAdapter ;
    private List<App> mApps ;

    private MainPresenter mainPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializePresenter() {
        mainPresenter = new MainPresenter() ;
        super.presenter = mainPresenter ;
        mainPresenter.onAttach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFABAnimation() ;
        mainPresenter.getInstallApps();
    }

    private void showFABAnimation() {
        Animation show_fab = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.fab_show) ;
        fab.setClickable(false);
        fab.startAnimation(show_fab);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fab != null){
                    fab.setClickable(true);
                }
            }
        }, 1000) ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDetach();
    }

    @Override
    public void onItemClick(App item, int position) {
        Log.i(Constant.TAG, "Item clicked : " + item.getLabel()) ;
        String activity = item.getActivity() ;
        if (activity == null || "".equals(activity)) {
            Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return ;
        }
        try {
            Intent intent = new Intent() ;
            intent.setComponent(new ComponentName(getPackageName(),
                    activity.startsWith(".") ? getPackageName() + activity : activity)) ;
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            Log.i(Constant.TAG, "Start Exception : " + e.getMessage()) ;
            Toast.makeText(getApplicationContext(), R.string.start_app_exception, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClick(final App item, final int position) {
        Log.i(Constant.TAG, "Item long clicked : " + item.getLabel() + " position : " + position) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.delete_app_message)
                .setPositiveButton(R.string.delete_app_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do db stuff
                        AppsDBManager.updateApp(item.getKey(), false) ;
                        //reload all
                        mApps.remove(item) ;
                        mAdapter.setItems(mApps);
                        mAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.delete_app_btn_discard, null) ;
        builder.show() ;
    }

    @OnClick(R.id.fab)
    void onFABClicked(){
        Intent install = new Intent(this, InstallAppActivity.class) ;
        startActivity(install);
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
    public void initializeAppsList(List<App> apps) {
        GridLayoutManager layoutManager = new NpaGridLayoutManager(this, 3) ;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(controller);
        Log.i(Constant.TAG, "enter setupAdapter function.") ;
        mAdapter = new AppsAdapter(getApplicationContext(), this);
        mAdapter.setHasStableIds(true);
        mApps = apps ;
        //maybe all apps are deleted, forbidden NullPointerException occur
        for (App app : mApps){
            Log.i(Constant.TAG, "key : " + app.getKey()) ;
            Log.i(Constant.TAG, "label : " + app.getLabel()) ;
            Log.i(Constant.TAG, "icon : " + app.getIcon()) ;
            Log.i(Constant.TAG, "activity : " + app.getActivity()) ;
        }
        mAdapter.setItems(mApps);
        mRecyclerView.setAdapter(mAdapter);
    }
}
