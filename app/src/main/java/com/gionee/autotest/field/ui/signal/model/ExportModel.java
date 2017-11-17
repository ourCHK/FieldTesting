package com.gionee.autotest.field.ui.signal.model;

import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.util.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by viking on 11/16/17.
 *
 * do export stuff
 */

public class ExportModel {

    private static final String SEPARATOR = "::" ;

    public void exportExcel(final File target, final File destination, final BaseCallback<String> callback){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    //fetch data from db
                    doConvertStuff(target, destination) ;
                    e.onNext(destination.getAbsolutePath());
                    e.onComplete();
                }catch (Exception ex){
                    ex.printStackTrace();
                    e.onError(ex);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        callback.onSuccess(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(Constant.TAG, "export exception : " + e.getMessage()) ;
                        callback.onFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                }) ;
    }

    private static final String SHEET_NAME = "监控数据%d" ;

    private static final int SHEET_MAX_LINES = 50000 ;

    private void doConvertStuff(File target, File destination) throws Exception{
        FileReader fr = new FileReader(target) ;
        BufferedReader mReader = new BufferedReader(fr) ;

        int sheetFrom = 0 ;
        Workbook workbook     = null;
        WritableWorkbook book = Workbook.createWorkbook(destination) ;
        WritableSheet   sheet = book.createSheet(String.format(Locale.US, SHEET_NAME, sheetFrom + 1), sheetFrom) ;

        WritableFont bold2 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
        bold2.setColour(Colour.BLACK);

        WritableCellFormat contentFormat = new WritableCellFormat(bold2);
        contentFormat.setAlignment(Alignment.CENTRE);
        contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        contentFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        contentFormat.setBackground(Colour.GRAY_25);

        addHeadTitle(sheet);

        String line ;
        int i = 1 ;
        while((line = mReader.readLine()) != null){
            if (line.contains(SEPARATOR)){
                String[] items = line.split(SEPARATOR) ;
                if (items.length == 9){
                    //should use another sheet now...
                    if (i > SHEET_MAX_LINES){
                        book.write();
                        book.close();

                        workbook = Workbook.getWorkbook(destination) ;
                        book = Workbook.createWorkbook(destination, workbook) ;

                        sheetFrom ++ ;
                        sheet = book.createSheet(String.format(Locale.US, SHEET_NAME, sheetFrom + 1), sheetFrom) ;
                        addHeadTitle(sheet);
                        //reset content from tag
                        i = 1 ;
                    }

                    Label timeLabel             = new Label(0, i, items[0], contentFormat);

                    boolean isSim1Active = Boolean.valueOf(items[1]) ;
                    Label sim0ActiveLabel       = new Label(1, i, isSim1Active ? "存在" : "不存在", contentFormat);
                    Label sim0Type              = new Label(2, i, items[2], contentFormat);
                    Label sim0Level             = new Label(3, i, items[3], contentFormat);
                    Label sim0Signal            = new Label(4, i, items[4], contentFormat);

                    boolean isSim2Active = Boolean.valueOf(items[1]) ;
                    Label sim1ActiveLabel       = new Label(5, i, isSim2Active ? "存在" : "不存在", contentFormat);
                    Label sim1Type              = new Label(6, i, items[6], contentFormat);
                    Label sim1Level             = new Label(7, i, items[7], contentFormat);
                    Label sim1Signal            = new Label(8, i, items[8], contentFormat);

                    sheet.addCell(timeLabel);
                    sheet.addCell(sim0ActiveLabel);
                    sheet.addCell(sim0Type);
                    sheet.addCell(sim0Level);
                    sheet.addCell(sim0Signal);
                    sheet.addCell(sim1ActiveLabel);
                    sheet.addCell(sim1Type);
                    sheet.addCell(sim1Level);
                    sheet.addCell(sim1Signal);
                    i ++ ;
                }
            }
        }

        book.write();
        book.close();
        if (workbook != null){
            workbook.close();
        }
        fr.close();
        mReader.close();
    }

    private void addHeadTitle(WritableSheet sheet) throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat titleFormat = new WritableCellFormat(bold);
        titleFormat.setAlignment(Alignment.CENTRE);
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        Label timeLabel             = new Label(0, 0, "时间", titleFormat);
        Label sim0ActiveLabel       = new Label(1, 0, "卡1是否存在", titleFormat);
        Label sim0Type              = new Label(2, 0, "卡1网络类型", titleFormat);
        Label sim0Level             = new Label(3, 0, "卡1信号级别", titleFormat);
        Label sim0Signal            = new Label(4, 0, "卡1信号", titleFormat);
        Label sim1ActiveLabel       = new Label(5, 0, "卡2是否存在", titleFormat);
        Label sim1Type              = new Label(6, 0, "卡2网络类型", titleFormat);
        Label sim1Level             = new Label(7, 0, "卡2信号级别", titleFormat);
        Label sim1Signal            = new Label(8, 0, "卡2信号", titleFormat);

        sheet.addCell(timeLabel);
        sheet.addCell(sim0ActiveLabel);
        sheet.addCell(sim0Type);
        sheet.addCell(sim0Level);
        sheet.addCell(sim0Signal);
        sheet.addCell(sim1ActiveLabel);
        sheet.addCell(sim1Type);
        sheet.addCell(sim1Level);
        sheet.addCell(sim1Signal);

        sheet.setColumnView(0, 28);
        sheet.setColumnView(1, 20);
        sheet.setColumnView(2, 20);
        sheet.setColumnView(3, 20);
        sheet.setColumnView(4, 20);
        sheet.setColumnView(5, 20);
        sheet.setColumnView(6, 20);
        sheet.setColumnView(7, 20);
        sheet.setColumnView(8, 20);
    }
}
