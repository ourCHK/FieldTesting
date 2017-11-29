package com.gionee.autotest.field.ui.outgoing;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class OutGoingUtil {
    public static boolean isTest=false;

    public static  void writeBook(String filePath, ArrayList<ArrayList<OutGoingCallResult>> list) {
        WritableWorkbook workBook= null;
        try {
            File file =new  File(filePath);
            if (file.exists()) {
                file.delete();
            }
                file.createNewFile();
            workBook = Workbook.createWorkbook(file);
            for (int i = 0; i < list.size(); i++) {
                WritableSheet sheet = workBook.createSheet("第" + (i + 1) + "批次", i);
                sheet.addCell(new Label(0, 0, "轮次"));
                sheet.addCell(new Label(1, 0, "号码"));
                sheet.addCell(new Label(2, 0, "拨号时间"));
                sheet.addCell(new Label(3, 0, "挂断时间"));
                sheet.addCell(new Label(4, 0, "结果"));
                int roundRow=1;
                int cycleIndex=0;
                int callsSize=0;
                ArrayList<OutGoingCallResult> results = list.get(i);
                for (int j = 0; j < results.size(); j++) {
                    OutGoingCallResult result = results.get(j);
                    if (result.cycleIndex!=cycleIndex){
                            cycleIndex=result.cycleIndex;
                            callsSize=0;
                         sheet.addCell(new Label(0, roundRow, "第"+(cycleIndex+1)+"轮"));
                    }
                    callsSize++;
                    sheet.addCell(new Label(1, roundRow + j, result.number));
                    sheet.addCell(new Label(2, roundRow + j, result.dialTime));
                    sheet.addCell(new Label(3, roundRow + j, result.hangUpTime));
                    sheet.addCell(new Label(4, roundRow + j,  (result.result? "成功" : "失败")+(result.isVerify? "(复测)" : "")));
                    roundRow += callsSize;
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
}
