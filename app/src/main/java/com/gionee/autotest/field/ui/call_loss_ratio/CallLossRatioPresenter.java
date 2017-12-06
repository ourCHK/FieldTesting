package com.gionee.autotest.field.ui.call_loss_ratio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.CallLossRatioDBManager;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.outgoing.CallBack;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.ui.outgoing.model.CallRateTask;
import com.gionee.autotest.field.ui.outgoing.model.OutGoingReportCycle;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class CallLossRatioPresenter extends BasePresenter<BaseView> implements CallLossRatioContract.Presenter, AdapterView.OnItemSelectedListener {
    private Context mContext;
    private MyReceiver myReceiver;
    private ArrayAdapter mSpinnerAdapter;
    private ArrayList<String> batchs = new ArrayList<>();
    private ArrayList<String> batchIndex = new ArrayList<>();
    private CallLossRatioReportAdapter myAdapter;

    public CallLossRatioPresenter(Context context) {
        mContext = context;
    }

    private CallLossRatioContract.View getMainView() {
        return (CallLossRatioContract.View) getView();
    }

    private CallLossRatioContract.ReportView getReportView() {
        return (CallLossRatioContract.ReportView) super.getView();
    }

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        if (getView() instanceof CallLossRatioContract.View) {
            getMainView().setParams(getLastParams());
            myReceiver = new MyReceiver();
            LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver, new IntentFilter("CallLossRatioUpdateViews"));
            obtainCallRate();
        } else {
            myAdapter = new CallLossRatioReportAdapter();
            getReportView().getListView().setAdapter(myAdapter);
            getReportView().getListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    return true;
                }
            });
            mSpinnerAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, batchIndex);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            getReportView().getSpinner().setAdapter(mSpinnerAdapter);
            getReportView().getSpinner().setOnItemSelectedListener(this);
            updateBatchList();
        }
    }

    void obtainCallRate() {
        new CallRateTask(new CallBack() {
            @Override
            public void call(Object o) {
                String sum = (String) o;
                getMainView().updateCallRate(sum);
            }
        }).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CallParam getLastParams() {
        String callParams = Preference.getString(mContext, "callLossRatioParams", "");
        if (callParams == null || "".equals(callParams)) {
            Log.i("gionee.os.autotest", "null");
            return new CallParam();
        } else {
            Log.i("gionee.os.autotest", "non null");
            return new Gson().fromJson(callParams, CallParam.class);
        }
    }

    @Override
    public void startCallTest() {
        try {
            CallLossRatioUtil.isTest = true;
            final CallParam p = getMainView().getUserParams();
            Preference.putString(mContext, "callLossRatioParams", new Gson().toJson(p));
            CallLossRatioUtil.addBatch(p, new Consumer<CallParam>() {
                @Override
                public void accept(CallParam callParam) throws Exception {
                    mContext.startService(new Intent(mContext, CallLossRatioService.class).putExtra("params", new Gson().toJson(p)));
                    mContext.startService(new Intent(mContext, SignalMonitorService.class));
                }
            });
        } catch (Exception e) {
            stop();
        }
    }

    private void stop() {
        CallLossRatioUtil.isTest = false;
        mContext.stopService(new Intent(mContext, CallLossRatioService.class));
    }

    @Override
    public void handleStartBtnClicked() {
        if (CallLossRatioUtil.isTest) {
            stop();
        } else {
            startCallTest();
        }
        getMainView().updateViews();
    }

    @Override
    public void clearAllReport() {
        DialogHelper.create(mContext, "警告", "确定要清除全部报告数据?", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CallLossRatioDBManager.delete();
                    }
                });
                builder.setNegativeButton("取消", null);
            }
        }).show();
    }

    @Override
    public void showReport() {
        Intent intent = new Intent(mContext, CallLossRatioReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void openExcelFile() {
        CallLossRatioUtil.exportExcelFile(new Consumer<Integer>() {
            @Override
            public void accept(Integer size) throws Exception {
                Util.openExcelByIntent(mContext, Constant.CALL_LOSS_RATIO_EXCEL_PATH);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    void exportExcelFile() {
        CallLossRatioUtil.exportExcelFile(new Consumer<Integer>() {
            @Override
            public void accept(Integer size) throws Exception {
                if (size == 0) {
                    Toast.makeText(mContext, "无报告", Toast.LENGTH_LONG).show();
                } else {
                    DialogHelper.create(mContext, "导出成功", "导出成功:" + Constant.CALL_LOSS_RATIO_EXCEL_PATH + ",立即打开?", new DialogHelper.OnBeforeCreate() {
                        @Override
                        public void setOther(AlertDialog.Builder builder) {
                            builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Util.openExcelByIntent(mContext, Constant.CALL_LOSS_RATIO_EXCEL_PATH);
                                }
                            });
                            builder.setNegativeButton("取消", null);
                        }
                    }).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void insertListData(int batchId) {
        CallLossRatioUtil.getReportListData(batchId, new Consumer<ArrayList<OutGoingReportCycle>>() {
            @Override
            public void accept(ArrayList<OutGoingReportCycle> outGoingCallResults) throws Exception {
                myAdapter.updateData(outGoingCallResults);
                for (int i = 0; i < myAdapter.getGroupCount(); i++) {
                    getReportView().getListView().expandGroup(i);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void updateBatchList() {
        new AsyncTask<Void, Void, ArrayList<String>>() {
            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                ArrayList<String> allBatch = CallLossRatioDBManager.getAllBatch();
                Log.i(Constant.TAG, "allBatchs" + allBatch.size());
                batchs.clear();
                batchs.addAll(allBatch);
                batchIndex.clear();
                for (int i = 1; i < batchs.size() + 1; i++) {
                    batchIndex.add(String.valueOf(i));
                }
                return allBatch;
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                try {
                    mSpinnerAdapter.notifyDataSetChanged();
                    if (strings.size() != 0) {
                        getReportView().getSpinner().setSelection(Integer.parseInt(batchIndex.get(strings.size() - 1)));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == -1) return;
        insertListData(Integer.parseInt(batchs.get(position)));
        Log.i(Constant.TAG, "打开" + position + "的报告");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMainView().updateViews();
        }
    }

}
