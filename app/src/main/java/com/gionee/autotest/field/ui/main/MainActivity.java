package com.gionee.autotest.field.ui.main;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.AppsDBManager;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Util;
import com.gionee.autotest.field.views.EmptyRecyclerView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by viking on 11/6/17.
 *
 * Main screen activity
 */
public class MainActivity extends BaseActivity implements AppsAdapter.OnItemClickListener{

    @BindView(R.id.list_recycler_view)
    EmptyRecyclerView mRecyclerView ;

    @BindView(R.id.list_empty_view)
    View emptyView ;

    private AppsAdapter mAdapter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constant.TAG, "enter MainActivity onCreate") ;
        initViews() ;
    }

    private void initViews() {
        Log.i(Constant.TAG, "enter MainActivity initViews") ;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3) ;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(controller);
        // Fetch the empty view from the layout and set it on
        // the new recycler view
        mRecyclerView.setEmptyView(emptyView);
        setupAdapter() ;
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupAdapter() {
        Log.i(Constant.TAG, "enter setupAdapter function.") ;
        mAdapter = new AppsAdapter(getApplicationContext(), this);
        mAdapter.setHasStableIds(true);

        List<App> apps = AppsDBManager.fetchAllApps(true) ;
        Collections.sort(apps, Util.APP_COMPARATOR);
        for (App app : apps){
            Log.i(Constant.TAG, "key : " + app.getKey()) ;
            Log.i(Constant.TAG, "label : " + app.getLabel()) ;
            Log.i(Constant.TAG, "icon : " + app.getIcon()) ;
            Log.i(Constant.TAG, "activity : " + app.getActivity()) ;
        }

        mAdapter.setItems(apps);
    }


    @Override
    public void onItemClick(App item) {
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
}
