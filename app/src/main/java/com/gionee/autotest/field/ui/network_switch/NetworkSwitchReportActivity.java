package com.gionee.autotest.field.ui.network_switch;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.NetworkSwitchDBManager;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;

import java.util.ArrayList;


public class NetworkSwitchReportActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView result_filename_tv;
    private LinearLayout listView_HeadLine;
    private NetworkSwitchResultAdapter mAdapter_Result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_switch_report_layout);
        ListView result_listView = (ListView) findViewById(R.id.main_listView_result);
        Button select_dialog_btn = (Button) findViewById(R.id.select_dialog_btn);
        result_filename_tv = (TextView) findViewById(R.id.result_filename_tv);
        listView_HeadLine = (LinearLayout) findViewById(R.id.listView_HeadLine);
        mAdapter_Result = new NetworkSwitchResultAdapter(this);
        result_listView.setAdapter(mAdapter_Result);
        result_listView.setOnItemClickListener(this);
        select_dialog_btn.setOnClickListener(this);
        showLastReport();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        mAdapter_Result.setShowTestTime(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_dialog_btn:
                getResultFileAndShowDialog();
                break;

            default:
                break;
        }
    }

    private void emptyFile_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NetworkSwitchReportActivity.this);
        builder.setIcon(R.drawable.networkswitch_report_logo);
        builder.setTitle(getResources().getString(R.string.app_name));// 标题
        builder.setMessage("无测试内容，请测试后再查看");// 内容
        builder.setNegativeButton("返回", null);
        builder.show();
    }

    private void getResultFileAndShowDialog() {
        ArrayList<String> resultFileNameList = NetworkSwitchDBManager.getResultFileNameList();
        if (resultFileNameList.isEmpty()) {
            emptyFile_Dialog();
            return;
        }
        showSelectFile_Dialog(resultFileNameList);
    }

    private void showSelectFile_Dialog(ArrayList<String> fileNames) {
        new SelectFileDialog(this, fileNames).show();
    }

    @SuppressLint("StaticFieldLeak")
    private void showLastReport() {
        new AsyncTask<Void, Void, ArrayList<NetworkSwitchResult>>() {

            private ArrayList<String> resultFileNameList;

            @Override
            protected ArrayList<NetworkSwitchResult> doInBackground(Void... voids) {
                try {
                    resultFileNameList = NetworkSwitchDBManager.getResultFileNameList();
                    return NetworkSwitchDBManager.getNetworkSwitchResultList(resultFileNameList.get(resultFileNameList.size() - 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<NetworkSwitchResult> resultBeen_list) {
                super.onPostExecute(resultBeen_list);
                try {
                    try {
                        result_filename_tv.setText(resultFileNameList.get(resultFileNameList.size() - 1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listView_HeadLine.setVisibility(View.VISIBLE);
                    mAdapter_Result.updateData(resultBeen_list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private class SelectFileDialog extends AlertDialog.Builder {

        SelectFileDialog(Context context, final ArrayList<String> fileNames) {
            super(context);
            setIcon(R.mipmap.ic_launcher);
            setTitle("报告列表");
            String[] strings = fileNames.toArray(new String[]{});
            for (int i = 0; i < strings.length; i++) {
                strings[i] = (i + 1) + "." + strings[i];
            }
            setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String fileName = fileNames.get(i);
                    result_filename_tv.setText(fileName);
                    ArrayList<NetworkSwitchResult> resultBeen_list = NetworkSwitchDBManager.getNetworkSwitchResultList(fileName);
                    listView_HeadLine.setVisibility(View.VISIBLE);
                    mAdapter_Result.updateData(resultBeen_list);
                }
            });
        }

    }
}
