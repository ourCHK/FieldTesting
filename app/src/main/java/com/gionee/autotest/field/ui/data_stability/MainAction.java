package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.data.db.DatabaseHelper;
import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.gionee.autotest.field.services.WebViewService;
import com.gionee.autotest.field.ui.data_stability.report.ReportActivity;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

class MainAction {
    private Context mContext;
    private IMain main;

    MainAction(IMain main) {
        this.main = main;
        this.mContext = main.getContext();
    }

    public void showExitWarningDialog() {
        try {
            DialogHelper.create(main.getActivity(), "警告", "将退出到首页并停止测试", new DialogHelper.OnBeforeCreate() {
                @Override
                public void setOther(AlertDialog.Builder builder) {
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopTest();
                            main.updateViews();
                            main.doFinish();
                        }
                    }).setNegativeButton("取消", null);
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startTest(DataParam p) {
        DataStabilityUtil.isTest = true;
        String paramJson = new Gson().toJson(p);
        Preference.putString(mContext, "DataStabilityLastParams", paramJson);
        Intent intent = new Intent(mContext, WebViewService.class);
        Configurator.getInstance().setParam(p);
        mContext.startService(intent);
    }

    DataParam getLastParam() {
        String lastParams = Preference.getString(mContext, "DataStabilityLastParams", "");
        if (lastParams.equals("")) {
            return new DataParam();
        }
        try {
            return new Gson().fromJson(lastParams, DataParam.class);
        } catch (JsonSyntaxException e) {
            return new DataParam();
        }
    }


    void showReportPage() {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    void stopTest() {
        DataStabilityUtil.isTest = false;
        mContext.stopService(new Intent(mContext, WebViewService.class));
    }

    @SuppressLint("StaticFieldLeak")
    void clearReport() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                databaseUtil.deleteTable(DatabaseHelper.TABLE_RESULT);
                databaseUtil.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(mContext, "清除报告成功", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    void openDataStabilityExcelFile() {
        exportExcel(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer > 0) {
                    Util.openExcelByIntent(mContext, Constant.DATA_STABILITY_EXCEL_PATH);
                } else {
                    Toast.makeText(mContext, "无数据", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    void exportDataStabilityExcelFile() {
        exportExcel(new Consumer<Integer>() {
            @Override
            public void accept(Integer size) throws Exception {
                if (size > 0) {
                    DialogHelper.create(main.getActivity(), "提示", "导出成功,是否立即打开?", new DialogHelper.OnBeforeCreate() {
                        @Override
                        public void setOther(AlertDialog.Builder builder) {
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Util.openExcelByIntent(mContext, Constant.DATA_STABILITY_EXCEL_PATH);
                                }
                            }).setNegativeButton("取消", null);
                        }
                    }).show();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void exportExcel(final Consumer<Integer> c) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                ArrayList<DataStabilityBean> dataStabilityBeans = databaseUtil.getDataStabilityBeans();
                writeAllExcel2(dataStabilityBeans, Constant.DATA_STABILITY_EXCEL_PATH);
                databaseUtil.close();
                return dataStabilityBeans.size();
            }

            @Override
            protected void onPostExecute(Integer writeExcelResult) {
                super.onPostExecute(writeExcelResult);
                if (c != null) {
                    try {
                        c.accept(writeExcelResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    boolean writeAllExcel2(ArrayList<DataStabilityBean> beans, String filePath) {
        String[] titles = new String[]{"轮次", "网页序号", "结果", "失败点信息", "时间"};
        WritableWorkbook workBook = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                boolean mkdir = file.getParentFile().mkdir();
                DataStabilityUtil.i("创建" + mkdir);
            }
            if (file.exists()) {
                boolean delete = file.delete();
                DataStabilityUtil.i("删除" + delete);
            }
            boolean newFile = file.createNewFile();
            DataStabilityUtil.i("新建文件" + newFile);
            workBook = Workbook.createWorkbook(file);

            ArrayList<ArrayList<DataStabilityBean>> batchBeans = getBatchBeans(beans);

            Gson gson = new Gson();
            for (int batchIndex = 0; batchIndex < batchBeans.size(); batchIndex++) {
                WritableSheet sheet = workBook.createSheet("第" + (batchIndex + 1) + "批次", batchIndex);
                for (int i = 0; i < titles.length; i++) {
                    sheet.addCell(new Label(i, 0, titles[i]));
                }
                for (int titleIndex = 0; titleIndex < titles.length; titleIndex++) {
                    sheet.setColumnView(titleIndex, 10);
                }
                ArrayList<DataStabilityBean> beanArrayList = batchBeans.get(batchIndex);
                int row = 1;
                DataStabilityUtil.i("beanArrayList size =" + beanArrayList.size());
                int success = 0, fail = 0;
                for (int testIndex = 0; testIndex < beanArrayList.size(); testIndex++) {
                    DataStabilityBean b = beanArrayList.get(testIndex);
                    sheet.addCell(new Label(0, row, "第" + (testIndex + 1) + "轮"));
                    String result = b.getResult();
                    WebViewResultSum webViewResultSum = gson.fromJson(result, WebViewResultSum.class);
                    DataStabilityUtil.i("before size =" + webViewResultSum.resultBefore.size());
                    for (int beforeIndex = 0; beforeIndex < webViewResultSum.resultBefore.size(); beforeIndex++) {
                        WebViewUtil.WebViewResult webViewResult = webViewResultSum.resultBefore.get(beforeIndex);
                        sheet.addCell(new Label(1, row, String.valueOf(beforeIndex + 1)));
                        sheet.addCell(new Label(2, row, webViewResult.result ? "成功" : "失败"));
                        if (webViewResult.result) {
                            success++;
                        } else {
                            fail++;
                        }
                        SimSignalInfo info = null;
                        try {
                            if (!"".equals(webViewResult.netSimInfo)) {
                                info = gson.fromJson(webViewResult.netSimInfo, SimSignalInfo.class);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        sheet.addCell(new Label(3, row, info == null ? "" : info.toString()));
                        sheet.addCell(new Label(4, row, TimeUtil.getTime(webViewResult.loadWebTime, "yyyy-MM-dd HH:mm:ss")));
                        row++;
                    }
                    DataStabilityUtil.i("after size =" + webViewResultSum.resultAfter.size());
                    for (int afterIndex = 0; afterIndex < webViewResultSum.resultAfter.size(); afterIndex++) {
                        WebViewUtil.WebViewResult webViewResult = webViewResultSum.resultAfter.get(afterIndex);
                        sheet.addCell(new Label(1, row, String.valueOf(afterIndex + 1)));
                        sheet.addCell(new Label(2, row, webViewResult.result ? "成功" : "失败"));
                        if (webViewResult.result) {
                            success++;
                        } else {
                            fail++;
                        }
                        SimSignalInfo info = null;
                        try {
                            if (!webViewResult.netSimInfo.equals("")) {
                                info = gson.fromJson(webViewResult.netSimInfo, SimSignalInfo.class);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        sheet.addCell(new Label(3, row, info == null ? "" : info.toString()));
                        sheet.addCell(new Label(4, row, TimeUtil.getTime(webViewResult.loadWebTime, "yyyy-MM-dd HH:mm:ss")));
                        row++;
                    }
                    sheet.addCell(new Label(0, row+1, "总打开网页"+(success+fail)+"个,成功"+success+"个,失败"+fail+"个"));
                }
            }
            workBook.write();
            workBook.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private ArrayList<ArrayList<DataStabilityBean>> getBatchBeans(ArrayList<DataStabilityBean> beans) {
        SparseArray<ArrayList<DataStabilityBean>> arrays = new SparseArray<>();
        for (DataStabilityBean bean : beans) {
            if (arrays.indexOfKey(bean.batchId) > 0) {
                ArrayList<DataStabilityBean> dataStabilityBeans = arrays.get(bean.batchId);
                dataStabilityBeans.add(bean);
                arrays.put(bean.batchId, dataStabilityBeans);
            } else {
                ArrayList<DataStabilityBean> dataStabilityBeans = new ArrayList<>();
                dataStabilityBeans.add(bean);
                arrays.put(bean.batchId, dataStabilityBeans);
            }
        }
        ArrayList<ArrayList<DataStabilityBean>> batchBeans = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++) {
            ArrayList<DataStabilityBean> dataStabilityBeans = arrays.valueAt(i);
            batchBeans.add(dataStabilityBeans);
        }
        return batchBeans;
    }

    /**
     * 将数据输出至Excel文件
     *
     * @param beans
     * @param filePath
     * @return 输出成功返回true, 失败返回false
     */
    boolean writeAllExcel(ArrayList<DataStabilityBean> beans, String filePath) {
        WritableWorkbook workBook = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) file.delete();
            file.createNewFile();
            workBook = Workbook.createWorkbook(file);

            if (beans != null && beans.size() != 0) {
                Gson gson = new Gson();
                WritableSheet sheet = workBook.createSheet("sheet", 1);
                sheet.addCell(new Label(0, 0, "id"));
                sheet.addCell(new Label(1, 0, "batchId"));
                sheet.addCell(new Label(2, 0, "testIndex"));
                sheet.addCell(new Label(3, 0, "result"));
                sheet.addCell(new Label(4, 0, "errorIndex"));
                sheet.addCell(new Label(5, 0, "isActive1"));
                sheet.addCell(new Label(6, 0, "NetType1"));
                sheet.addCell(new Label(7, 0, "Level1"));
                sheet.addCell(new Label(8, 0, "Signal1"));
                sheet.addCell(new Label(9, 0, "运营商1"));
                sheet.addCell(new Label(10, 0, "isActive2"));
                sheet.addCell(new Label(11, 0, "NetType2"));
                sheet.addCell(new Label(12, 0, "Level2"));
                sheet.addCell(new Label(13, 0, "Signal2"));
                sheet.addCell(new Label(14, 0, "运营商2"));
                CellView cellView = new CellView();
                cellView.setAutosize(true);
                for (int titleIndex = 0; titleIndex < 6; titleIndex++) {
                    sheet.setColumnView(titleIndex, cellView);
                }
                int row = 1;
                for (DataStabilityBean dataStabilityBean : beans) {
                    sheet.addCell(new Label(0, row, dataStabilityBean.getId() + ""));
                    sheet.addCell(new Label(1, row, dataStabilityBean.getBatchId() + ""));
                    sheet.addCell(new Label(2, row, dataStabilityBean.getTestIndex() + ""));
                    WebViewResultSum webViewResultSum = gson.fromJson(dataStabilityBean.result, WebViewResultSum.class);

                    if (webViewResultSum.result) {  //成功
                        sheet.addCell(new Label(3, row, "成功"));
                    } else {    //失败
                        sheet.addCell(new Label(3, row, "失败"));
                        ArrayList<WebViewResultSum.SimNetInfo> errorSimNetInfoList = webViewResultSum.errorSimNetInfoList;
                        if (errorSimNetInfoList != null && errorSimNetInfoList.size() != 0) {
                            row += 1;
                            for (WebViewResultSum.SimNetInfo errorSimNetInfo : errorSimNetInfoList) {
                                sheet.addCell(new Label(4, row, errorSimNetInfo.getIndex() + ""));
                                sheet.addCell(new Label(5, row, errorSimNetInfo.ismIsActive1() + ""));
                                sheet.addCell(new Label(6, row, errorSimNetInfo.getmNetType1() + ""));
                                sheet.addCell(new Label(7, row, errorSimNetInfo.getmLevel1() + ""));
                                sheet.addCell(new Label(8, row, errorSimNetInfo.getmSignal1() + ""));
                                sheet.addCell(new Label(9, row, errorSimNetInfo.getmOperator1() + ""));
                                sheet.addCell(new Label(10, row, errorSimNetInfo.ismIsActive2() + ""));
                                sheet.addCell(new Label(11, row, errorSimNetInfo.getmNetType2() + ""));
                                sheet.addCell(new Label(12, row, errorSimNetInfo.getmLevel2() + ""));
                                sheet.addCell(new Label(13, row, errorSimNetInfo.getmSignal2() + ""));
                                sheet.addCell(new Label(14, row, errorSimNetInfo.getmOperator2() + ""));
                                row++;
                            }
                            row -= 1;
                        }
                    }
                    row++;
                }

            }
            workBook.write();
            workBook.close();
            return true;
        } catch (Exception e) {
            Log.i(Constant.TAG, e.toString());
            return false;
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    Log.i(Constant.TAG, e.toString());
                }

            }
        }
    }

}
