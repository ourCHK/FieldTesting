package com.gionee.autotest.field.ui.throughput.Util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gionee.autotest.field.FieldApplication;
import com.gionee.autotest.field.ui.throughput.SQL.DatabaseUtil;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.gionee.autotest.field.ui.throughput.Util.Configuration.RESULT_PATH;

/**
 * 创建表格，添加字段
 *
 * @author pbl
 */
public class JExcelUtil {

    public static boolean exportExcel(File file, String type) {
        Helper.i("跳转到JExcelUtil");
        return createExcel(file, type);
    }

    private static boolean createExcel(File file, String type) {
        try {
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    Helper.i("excel表创建失败");
                    return false;
                }
            }
            Helper.i("创建表格成功");
            WritableWorkbook book = Workbook.createWorkbook(file);
            createSheet(book, type);
            book.write();
            book.close();
        } catch (Exception e) {
//            Helper.delAllFiles(RESULT_PATH);
            Helper.i(e.toString());
            Helper.i("excel表写入失败");
            return false;
        }
        return true;
    }

    private static void createSheet(WritableWorkbook book, String type) throws WriteException, IOException {
        Label content, content1, content2, content3, content4, content5, content6, content7, content8, content9, content10, content11, content12;
        ArrayList newList = Helper.getNewList(FieldApplication.getContext());
        for (int k = 0; k < newList.size(); k++) {
//            String string ="第"+(k+1)+"次循环下载结果";
            String string = newList.get(k).toString();
            String[] split = string.split(":");
            String name = split[0] + split[1] + split[2];

            ArrayList<SpeedBean> speedBean = new DatabaseUtil(FieldApplication.getContext()).getTimeContent(string);
            if (type.equals("error") && speedBean.get(speedBean.size() - 1).success.equals("YES")) {
                continue;
            }
            WritableSheet sheet = book.createSheet(name, 0);//创建第一个表
            WritableFont nf = new WritableFont(WritableFont.createFont("宋体"), 10);
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            wcfN.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
            wcfN.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
            wcfN.setAlignment(Alignment.CENTRE);//左右居中
            // 水平对齐
            wcfN.setWrap(true);
            Helper.i("添加每列标题");
            String[] firstLine = {"序号", "是否通过", "开始时间", "测试类型", "网络类型", "文件大小", "平均速率(KB/S)", "耗时(S)", "失败时间", "运营商", "网络类型", "信号格数", "信号强度"};
            for (int i = 0; i < firstLine.length; i++) {
                Label label = new Label(i, 0, firstLine[i], getTitleFormat());
                sheet.addCell(label);
            }

            for (int j = 0; j < speedBean.size(); j++) {
                Helper.i("写到第" + (j + 1) + "行 ；");

                if (speedBean.get(j).success.equals("YES")) {
                    content = new Label(0, j + 1, speedBean.get(j).id, getContentFormat());
                    content2 = new Label(2, j + 1, speedBean.get(j).time, getContentFormat());
                    content3 = new Label(3, j + 1, speedBean.get(j).way, getContentFormat());
                    content4 = new Label(4, j + 1, speedBean.get(j).web, getContentFormat());
                    content5 = new Label(5, j + 1, speedBean.get(j).type, getContentFormat());
                    content6 = new Label(6, j + 1, speedBean.get(j).speed_average, getContentFormat());
                    content7 = new Label(7, j + 1, speedBean.get(j).use_time + "", getContentFormat());

                    content1 = new Label(1, j + 1, speedBean.get(j).success, getContentFormat());
                    content8 = new Label(8, j + 1, speedBean.get(j).failTime, getContentFormat());
                    content9 = new Label(10, j + 1, speedBean.get(j).webType, getContentFormat());
                    content10 = new Label(11, j + 1, speedBean.get(j).signals, getContentFormat());
                    content11 = new Label(12, j + 1, speedBean.get(j).signalStrength, getContentFormat());
                    content12 = new Label(9, j + 1, speedBean.get(j).operator, getContentFormat());
                } else {
                    content = new Label(0, j + 1, speedBean.get(j).id, getErrorFormat());
                    content2 = new Label(2, j + 1, speedBean.get(j).time, getErrorFormat());
                    content3 = new Label(3, j + 1, speedBean.get(j).way, getErrorFormat());
                    content4 = new Label(4, j + 1, speedBean.get(j).web, getErrorFormat());
                    content5 = new Label(5, j + 1, speedBean.get(j).type, getErrorFormat());
                    content6 = new Label(6, j + 1, speedBean.get(j).speed_average, getErrorFormat());
                    content7 = new Label(7, j + 1, speedBean.get(j).use_time + "", getErrorFormat());

                    content1 = new Label(1, j + 1, speedBean.get(j).success, getErrorFormat());
                    content8 = new Label(8, j + 1, speedBean.get(j).failTime, getErrorFormat());
                    content9 = new Label(10, j + 1, speedBean.get(j).webType, getErrorFormat());
                    content10 = new Label(11, j + 1, speedBean.get(j).signals, getErrorFormat());
                    content11 = new Label(12, j + 1, speedBean.get(j).signalStrength, getErrorFormat());
                    content12 = new Label(9, j + 1, speedBean.get(j).operator, getErrorFormat());
                }

                sheet.addCell(content);
                sheet.addCell(content1);
                sheet.addCell(content2);
                sheet.addCell(content3);
                sheet.addCell(content4);
                sheet.addCell(content5);
                sheet.addCell(content6);
                sheet.addCell(content7);
                sheet.addCell(content8);
                sheet.addCell(content9);
                sheet.addCell(content10);
                sheet.addCell(content11);
                sheet.addCell(content12);
            }
        }
    }


    @NonNull
    private static WritableCellFormat getContentFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat contentFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        contentFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        contentFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return contentFormat;
    }

    @NonNull
    private static WritableCellFormat getTitleFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat titleFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        titleFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return titleFormat;
    }

    @NonNull
    private static WritableCellFormat getErrorFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat titleFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        titleFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return titleFormat;
    }
}
