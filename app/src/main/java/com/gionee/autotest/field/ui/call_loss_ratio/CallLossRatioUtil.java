package com.gionee.autotest.field.ui.call_loss_ratio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.gionee.autotest.field.data.db.CallLossRatioDBManager;
import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.ui.outgoing.model.OutGoingReportCycle;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;
import com.gionee.autotest.field.util.SimUtil;
import com.gionee.autotest.field.util.filterlog.FilterLogTask;
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


public class CallLossRatioUtil {
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
                sheet.addCell(new Label(3, 0, "接通时间"));
                sheet.addCell(new Label(4, 0, "挂断时间"));
                sheet.addCell(new Label(5, 0, "结果"));
                sheet.addCell(new Label(6, 0, "失败点网络信息"));
                CellView cellView = new CellView();
                cellView.setAutosize(true);
                for (int titleIndex = 0; titleIndex < 6; titleIndex++) {
                    sheet.setColumnView(titleIndex, cellView);
                }
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
                    sheet.addCell(new Label(3, rowIndex, result.offHookTime));
                    sheet.addCell(new Label(4, rowIndex, result.hangUpTime));
                    sheet.addCell(new Label(5, rowIndex, (result.result ? "成功" : "失败") + (result.isVerify ? "(复测)" : "")));
                    try {
                        if (result.simNetInfo != null && !"".equals(result.simNetInfo)) {
                            SimSignalInfo simSignalInfo = gson.fromJson(result.simNetInfo, SimSignalInfo.class);
                            sheet.addCell(new Label(6, rowIndex, simSignalInfo.toString()));
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
        int cycle = 0;
        int cycleIndex = -1;
        for (OutGoingCallResult result : results) {
            if (result.isVerify) {
                verifyCount++;
            } else {
                if (result.cycleIndex != cycleIndex) {
                    cycle++;
                }
                if (result.result) {
                    testSuccess++;
                }
            }
        }
        int allSize = results.size();
        return "总拨号" + allSize + "通\n测试" + (allSize - verifyCount) + "通\n成功" + testSuccess + "通\n失败" + (allSize - testSuccess) + "通\n呼损率为" +(allSize==0?"0":(1-(float) testSuccess / allSize) * 100)  + "%";
    }

    public static String getCycleSumString(ArrayList<OutGoingCallResult> results) {
        int success = 0;
        int verify = 0;
        for (OutGoingCallResult result : results) {
            if (result.isVerify) {
                verify++;
            }
            if (result.result) {
                success++;
            }
        }
        int allSize = results.size();
        return String.format("测试%1$s通 成功%2$s通 失败%3$s通", String.valueOf(allSize), String.valueOf(success), String.valueOf(allSize - success));
    }

    @SuppressLint("StaticFieldLeak")
    public static void getReportListData(final int batchId, final Consumer<ArrayList<OutGoingReportCycle>> consumer) {
        new AsyncTask<Void, Void, ArrayList<OutGoingReportCycle>>() {
            @Override
            protected ArrayList<OutGoingReportCycle> doInBackground(Void... voids) {
                ArrayList<OutGoingCallResult> reportBean = CallLossRatioDBManager.getReportBean(batchId);
                ArrayList<OutGoingReportCycle> data = new ArrayList<>();
                SparseArray<OutGoingReportCycle> array = new SparseArray<>();
                for (OutGoingCallResult result : reportBean) {
                    if (array.indexOfKey(result.cycleIndex) >= 0) {
                        OutGoingReportCycle outGoingReportCycle = array.get(result.cycleIndex).addResult(result);
                        array.put(result.cycleIndex, outGoingReportCycle);
                    } else {
                        OutGoingReportCycle outGoingReportCycle = new OutGoingReportCycle().addResult(result);
                        array.put(result.cycleIndex, outGoingReportCycle);
                    }
                }
                for (int i = 0; i < array.size(); i++) {
                    OutGoingReportCycle outGoingReportCycle = array.valueAt(i);
                    outGoingReportCycle.setSumString(getCycleSumString(outGoingReportCycle.results));
                    data.add(outGoingReportCycle);
                }
                return data;
            }

            @Override
            protected void onPostExecute(ArrayList<OutGoingReportCycle> outGoingCallResults) {
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

    @SuppressLint("StaticFieldLeak")
    public static void addBatch(CallParam p, final Consumer<CallParam> c) {
        new AsyncTask<CallParam, Void, CallParam>() {
            @Override
            protected CallParam doInBackground(CallParam... params) {
                CallParam param = params[0];
                param.setId(CallLossRatioDBManager.addBatch(param));
                return param;
            }

            @Override
            protected void onPostExecute(CallParam p) {
                super.onPostExecute(p);
                if (c != null) {
                    try {
                        c.accept(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(p);
    }

    @SuppressLint("StaticFieldLeak")
    public static void exportExcelFile(final Consumer<Integer> c) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                ArrayList<ArrayList<OutGoingCallResult>> results = new ArrayList<>();
                ArrayList<String> allBatch = CallLossRatioDBManager.getAllBatch();
                if (allBatch.size() == 0) {
                    return 0;
                }
                for (String batch : allBatch) {
                    results.add(CallLossRatioDBManager.getReportBean(Integer.parseInt(batch)));
                }
                CallLossRatioUtil.writeBook(Constant.CALL_LOSS_RATIO_EXCEL_PATH, results);
                return allBatch.size();
            }

            @Override
            protected void onPostExecute(Integer size) {
                super.onPostExecute(size);
                if (c != null) {
                    try {
                        c.accept(size);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

}
