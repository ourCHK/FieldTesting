package com.gionee.autotest.field.ui.outgoing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;
import com.gionee.autotest.field.util.SimUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.functions.Consumer;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class OutGoingUtil {
    public static boolean isTest = false;

    public static String getSimNetInfo(Context context) {
        SimSignalInfo simSignalInfo = SignalHelper.getInstance(context).getSimSignalInfo(SimUtil.getDefaultDataSubId());
        if (simSignalInfo == null) return "";
        try {
            return new Gson().toJson(simSignalInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeBook(String filePath, ArrayList<ArrayList<OutGoingCallResult>> list) {
        WritableWorkbook workBook = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            workBook = Workbook.createWorkbook(file);
            Gson gson = new Gson();
            for (int i = 0; i < list.size(); i++) {
                WritableSheet sheet = workBook.createSheet("第" + (i + 1) + "批次", i);
                sheet.addCell(new Label(0, 0, "轮次"));
                sheet.addCell(new Label(1, 0, "号码"));
                sheet.addCell(new Label(2, 0, "拨号时间"));
                sheet.addCell(new Label(3, 0, "挂断时间"));
                sheet.addCell(new Label(4, 0, "结果"));
                sheet.addCell(new Label(5, 0, "失败点网络信息"));
                int cycleIndex = -1;
                ArrayList<OutGoingCallResult> results = list.get(i);
                for (int j = 0; j < results.size(); j++) {
                    int rowIndex = j + 1;
                    OutGoingCallResult result = results.get(j);
                    if (result.cycleIndex != cycleIndex) {
                        cycleIndex = result.cycleIndex;
                        sheet.addCell(new Label(0, rowIndex, "第" + (cycleIndex + 1) + "轮"));
                    }
                    Log.i(Constant.TAG, result.toString());
                    sheet.addCell(new Label(1, rowIndex, result.number));
                    sheet.addCell(new Label(2, rowIndex, result.dialTime));
                    sheet.addCell(new Label(3, rowIndex, result.hangUpTime));
                    sheet.addCell(new Label(4, rowIndex, (result.result ? "成功" : "失败") + (result.isVerify ? "(复测)" : "")));
                    try {
                        if (result.simNetInfo != null && !"".equals(result.simNetInfo)) {
                            SimSignalInfo simSignalInfo = gson.fromJson(result.simNetInfo, SimSignalInfo.class);
                            sheet.addCell(new Label(5, rowIndex, simSignalInfo.toString()));
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                sheet.addCell(new Label(0, results.size() + 1, getSumString(results)));
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

    public static String getSumString(ArrayList<OutGoingCallResult> results) {
        int verifyCount = 0;
        int testSuccess = 0;
        for (OutGoingCallResult result : results) {
            if (result.isVerify) {
                verifyCount++;
            } else {
                if (result.result) {
                    testSuccess++;
                }
            }
        }
        return "总拨号" + results.size() + "通\n测试" + (results.size() - verifyCount) + "通\n复测" + verifyCount + "通\n接通率为" + ((float) testSuccess / results.size()) * 100 + "%";
    }

    @SuppressLint("StaticFieldLeak")
    public static void getReportListData(final int batchId, final Consumer<ArrayList<ArrayList<OutGoingCallResult>>> consumer) {
        new AsyncTask<Void, Void, ArrayList<ArrayList<OutGoingCallResult>>>() {
            @Override
            protected ArrayList<ArrayList<OutGoingCallResult>> doInBackground(Void... voids) {
                ArrayList<OutGoingCallResult> reportBean = OutGoingDBManager.getReportBean(batchId);
                ArrayList<ArrayList<OutGoingCallResult>> data = new ArrayList<>();
                SparseArray<ArrayList<OutGoingCallResult>> array = new SparseArray<>();
                for (OutGoingCallResult result : reportBean) {
                    if (array.indexOfKey(result.cycleIndex) >= 0) {
                        ArrayList<OutGoingCallResult> results = array.get(result.cycleIndex);
                        results.add(result);
                        array.put(result.cycleIndex,results);
                    } else {
                        ArrayList<OutGoingCallResult> results = new ArrayList<>();
                        results.add(result);
                        array.put(result.cycleIndex, results);
                    }
                }
                for (int i = 0; i < array.size(); i++) {
                    data.add(array.valueAt(i));
                }
                return data;
            }

            @Override
            protected void onPostExecute(ArrayList<ArrayList<OutGoingCallResult>> outGoingCallResults) {
                super.onPostExecute(outGoingCallResults);
                if (consumer != null) {
                    try {
                        consumer.accept(outGoingCallResults);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

}
