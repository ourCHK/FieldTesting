package com.gionee.autotest.field.ui.main;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.views.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by viking on 11/6/17.
 *
 * Main screen activity
 */
public class MainActivity extends BaseActivity {

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
        mAdapter = new AppsAdapter(getApplicationContext());
        mAdapter.setHasStableIds(true);
        List<String> mItems = new ArrayList<>() ;

        mItems.add("1") ;
        mItems.add("2") ;
        mItems.add("3") ;
        mItems.add("4") ;
        mItems.add("5") ;
        mItems.add("6") ;
        mItems.add("7") ;
        mItems.add("8") ;
        mItems.add("9") ;
        mAdapter.setItems(mItems);
    }


}
