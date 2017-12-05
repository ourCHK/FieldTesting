package com.gionee.autotest.field.ui.incoming.model;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Preference;
import com.gionee.autotest.field.util.call.CallMonitorResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class InComingCall {

    public static boolean isTest = false;

    @SuppressLint("StaticFieldLeak")
    public static void writeData(CallMonitorResult callMonitorResult) {
        new AsyncTask<CallMonitorResult, Void, Void>() {
            @Override
            protected Void doInBackground(CallMonitorResult... data) {
                InComingDBManager.writeData(data[0]);
                return null;
            }
        }.execute(callMonitorResult);
    }

    @SuppressLint("StaticFieldLeak")
    public static void clearAllReport(final Consumer<Void> c) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                InComingDBManager.delete();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (c != null) {
                    try {
                        c.accept(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void getBatchList(final Consumer<ArrayList<String>> c) {
        new AsyncTask<Void, Void, ArrayList<String>>() {

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                return InComingDBManager.getAllBatch();
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                try {
                    c.accept(strings);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void insertListData(final int batchId, final Consumer<InComingReportBean> c) {
        new AsyncTask<Integer, Void, InComingReportBean>() {

            @Override
            protected InComingReportBean doInBackground(Integer... voids) {
                return InComingDBManager.getReportBean(voids[0]);
            }

            @Override
            protected void onPostExecute(InComingReportBean inComingReportBean) {
                super.onPostExecute(inComingReportBean);
                try {
                    c.accept(inComingReportBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(batchId);
    }

    @SuppressLint("StaticFieldLeak")
    public static void exportExcelFile(final Consumer<Integer> c) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    ArrayList<InComingReportBean> reportBeans = new ArrayList<>();
                    ArrayList<String> allBatch = InComingDBManager.getAllBatch();
                    for (String batch : allBatch) {
                        InComingReportBean reportBean = InComingDBManager.getReportBean(Integer.parseInt(batch));
                        reportBeans.add(reportBean);
                    }
                    writeAllExcel(reportBeans, Constant.INCOMING_EXCEL_PATH);
                    return reportBeans.size();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer size) {
                super.onPostExecute(size);
                try {
                    c.accept(size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private static void writeAllExcel(ArrayList<InComingReportBean> beans, String filePath) {
        WritableWorkbook workBook = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                boolean mkdirs = file.getParentFile().mkdir();
                Log.i(Constant.TAG, "incoming excelFilePath mkdirs " + mkdirs);
            }
            if (file.exists()) {
                boolean delete = file.delete();
                Log.i(Constant.TAG, "incoming excelFile delete " + delete);
            }
            boolean newFile = file.createNewFile();
            Log.i(Constant.TAG, "incoming excel file create " + newFile);
            workBook = Workbook.createWorkbook(file);
            for (int batchIndex = 0; batchIndex < beans.size(); batchIndex++) {
                InComingReportBean bean = beans.get(batchIndex);
                WritableSheet sheet = workBook.createSheet("第" + (batchIndex + 1) + "批次", batchIndex);
                sheet.addCell(new Label(0, 0, "序号"));
                sheet.addCell(new Label(1, 0, "接听号码"));
                sheet.addCell(new Label(2, 0, "接听时间"));
                sheet.addCell(new Label(3, 0, "结果"));
                sheet.addCell(new Label(4, 0, "失败原因"));
                for (int callIndex = 0; callIndex < bean.data.size(); callIndex++) {
                    CallMonitorResult result = bean.data.get(callIndex);
                    sheet.addCell(new Label(0, callIndex + 1, (result.index + 1) + ""));
                    sheet.addCell(new Label(1, callIndex + 1, result.number));
                    sheet.addCell(new Label(2, callIndex + 1, result.time));
                    sheet.addCell(new Label(3, callIndex + 1, result.result ? "成功" : "失败"));
                    sheet.addCell(new Label(4, callIndex + 1, result.failMsg));
                }
            }
            workBook.write();
        } catch (Exception e) {
            e.printStackTrace();
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

    public static String getSumString(InComingReportBean bean) {
        int success = 0;
        for (CallMonitorResult result : bean.data) {
            if (result.result) {
                success++;
            }
        }
        return "总共" + bean.data.size() + "\n成功接通" + success + "\n失败" + (bean.data.size() - success);
    }
}
