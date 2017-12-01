package com.gionee.autotest.field.ui.call_quality.model;

import android.util.Log;

import com.gionee.autotest.field.ui.base.listener.BaseCallback;
import com.gionee.autotest.field.ui.call_quality.entity.BaseEvent;
import com.gionee.autotest.field.ui.call_quality.entity.CallQualityConstant;
import com.gionee.autotest.field.ui.call_quality.entity.QualityEventWrapper;
import com.gionee.autotest.field.ui.call_quality.entity.RoundInfoData;
import com.gionee.autotest.field.ui.call_quality.entity.SimSignalInfoWrapper;
import com.gionee.autotest.field.util.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by viking on 11/24/17.
 *
 * export data
 */

public class DataExport {

    public void exportExcel(final String phone_num, final String phone_num_o, final File target, final File destination, final BaseCallback<String> callback){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    //fetch data from db
                    doStuff(phone_num, phone_num_o , target, destination) ;
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

    private void doStuff(String phone_num, String phone_num_o, File target, File destination) throws Exception{
        //file to object
        List<BaseEvent> data = convertDataFromFile(target) ;
        //analysis object
        List<RoundInfoData> roundInfos = analysisData(data) ;
        //export to excel file
        realExportToExcel(roundInfos, phone_num, phone_num_o, destination) ;
    }

    private static final String SHEET_NAME = "第%d轮测试" ;

    private void realExportToExcel(List<RoundInfoData> roundInfos, String phone_num, String phone_num_o, File destination) throws Exception{
        int sheetFrom = 0 ;
        Workbook workbook     = null;
        WritableWorkbook book = Workbook.createWorkbook(destination) ;
        WritableSheet sheet = book.createSheet(String.format(Locale.US, SHEET_NAME, sheetFrom + 1), sheetFrom) ;

        addHeadTitle(phone_num, phone_num_o, sheetFrom + 1, sheet);

        int i = 1 ;
        int row_num   = 3 ;
        int row_num_o = 3 ;
        boolean isFirst = true ;
        for (RoundInfoData round : roundInfos){
            Log.i(Constant.TAG, "-------------- round " + i + " ----------------") ;

            if (isFirst){
                isFirst = false ;
            }else{
                // start with new sheet
                book.write();
                book.close();

                row_num     = 3 ;
                row_num_o   = 3 ;

                workbook = Workbook.getWorkbook(destination) ;
                book = Workbook.createWorkbook(destination, workbook) ;

                sheetFrom ++ ;
                sheet = book.createSheet(String.format(Locale.US, SHEET_NAME, sheetFrom + 1), sheetFrom) ;
                addHeadTitle(phone_num, phone_num_o, sheetFrom + 1,  sheet);
            }

            List<QualityEventWrapper> events = round.getRoundDatas() ;
            if (events != null && events.size() > 0){
                for (QualityEventWrapper event : events){
                    String qualityName = getQualityName(event.getQuality_type()) ;
                    String eventName = getEventName(event.getQuality_type(), event.getEvent_type()) ;
                    if (event.getPhone_num() == CallQualityConstant.PHONE_NUM_1){
                        List<SimSignalInfoWrapper> signals = event.getSignals() ;
                        if (signals != null && signals.size() > 0) {
                            for (int k = 0; k < signals.size() ; k++){
                                Label qualityNameLabel = new Label(0, row_num + k, qualityName, getTitleFormat()) ;
                                Label eventNameLabel   = new Label(1, row_num + k, eventName, getTitleFormat()) ;
                                sheet.addCell(qualityNameLabel);
                                sheet.addCell(eventNameLabel);
                            }
                            if (signals.size() > 1) {
                                //merge cells
                                sheet.mergeCells(0, row_num, 0, row_num + (signals.size() - 1));
                                sheet.mergeCells(1, row_num, 1, row_num + (signals.size() - 1));
                            }

                            for (SimSignalInfoWrapper signal : signals) {
                                Label timeLabel             = new Label(2, row_num, signal.getTime(), getContentFormat());
                                Label sim0ActiveLabel       = new Label(3, row_num, signal.getmIsActive(), getContentFormat());
                                Label sim0Operator          = new Label(4, row_num, signal.getmOperator(), getContentFormat());
                                Label sim0Type              = new Label(5, row_num, signal.getmNetType(), getContentFormat());
                                Label sim0Level             = new Label(6, row_num, signal.getmLevel() + "", getContentFormat());
                                Label sim0Signal            = new Label(7, row_num, signal.getmSignal(), getContentFormat());
                                Label sim1ActiveLabel       = new Label(8, row_num, signal.getmIsActiveO(), getContentFormat());
                                Label sim1Operator          = new Label(9, row_num, signal.getmOperatorO(), getContentFormat());
                                Label sim1Type              = new Label(10, row_num, signal.getmNetTypeO(), getContentFormat());
                                Label sim1Level             = new Label(11, row_num, signal.getmLevelO() + "", getContentFormat());
                                Label sim1Signal            = new Label(12,row_num, signal.getmSignalO(), getContentFormat());
                                sheet.addCell(timeLabel);
                                sheet.addCell(sim0ActiveLabel);
                                sheet.addCell(sim0Operator);
                                sheet.addCell(sim0Type);
                                sheet.addCell(sim0Level);
                                sheet.addCell(sim0Signal);
                                sheet.addCell(sim1ActiveLabel);
                                sheet.addCell(sim1Operator);
                                sheet.addCell(sim1Type);
                                sheet.addCell(sim1Level);
                                sheet.addCell(sim1Signal);
                                row_num ++ ;
                            }
                        }
                    }else{
                        List<SimSignalInfoWrapper> signals = event.getSignals() ;
                        if (signals != null && signals.size() > 0) {
                            for (int k = 0; k < signals.size() ; k++){
                                Label qualityNameLabel = new Label(13, row_num_o + k, qualityName, getTitleFormat()) ;
                                Label eventNameLabel   = new Label(14, row_num_o + k, eventName, getTitleFormat()) ;
                                sheet.addCell(qualityNameLabel);
                                sheet.addCell(eventNameLabel);
                            }
                            if (signals.size() > 1){
                                //merge cells
                                sheet.mergeCells(13, row_num_o, 13, row_num_o + (signals.size() - 1)) ;
                                sheet.mergeCells(14, row_num_o, 14, row_num_o + (signals.size() - 1)) ;
                            }


                            for (SimSignalInfoWrapper signal : signals) {
                                Label timeLabel             = new Label(15, row_num_o, signal.getTime(), getContentFormat());
                                Label sim0ActiveLabel       = new Label(16, row_num_o, signal.getmIsActive(), getContentFormat());
                                Label sim0Operator          = new Label(17, row_num_o, signal.getmOperator(), getContentFormat());
                                Label sim0Type              = new Label(18, row_num_o, signal.getmNetType(), getContentFormat());
                                Label sim0Level             = new Label(19, row_num_o, signal.getmLevel() + "", getContentFormat());
                                Label sim0Signal            = new Label(20, row_num_o, signal.getmSignal(), getContentFormat());
                                Label sim1ActiveLabel       = new Label(21, row_num_o, signal.getmIsActiveO(), getContentFormat());
                                Label sim1Operator          = new Label(22, row_num_o, signal.getmOperatorO(), getContentFormat());
                                Label sim1Type              = new Label(23, row_num_o, signal.getmNetTypeO(), getContentFormat());
                                Label sim1Level             = new Label(24, row_num_o, signal.getmLevelO() + "", getContentFormat());
                                Label sim1Signal            = new Label(25, row_num_o, signal.getmSignalO(), getContentFormat());
                                sheet.addCell(timeLabel);
                                sheet.addCell(sim0ActiveLabel);
                                sheet.addCell(sim0Operator);
                                sheet.addCell(sim0Type);
                                sheet.addCell(sim0Level);
                                sheet.addCell(sim0Signal);
                                sheet.addCell(sim1ActiveLabel);
                                sheet.addCell(sim1Operator);
                                sheet.addCell(sim1Type);
                                sheet.addCell(sim1Level);
                                sheet.addCell(sim1Signal);
                                row_num_o ++ ;
                            }
                        }
                    }
                }
            }
            i++ ;
        }
        book.write();
        book.close();
        if (workbook != null){
            workbook.close();
        }
    }

    private String getEventName(int qualityType, int eventType){
        switch (qualityType){
            case CallQualityConstant.QUALITY_TYPE_CONTINUE :
            case CallQualityConstant.QUALITY_TYPE_LOW_VOL :
            case CallQualityConstant.QUALITY_TYPE_NOISE :
            case CallQualityConstant.QUALITY_TYPE_LOOP :
            case CallQualityConstant.QUALITY_TYPE_NO_VOL :
            case CallQualityConstant.QUALITY_TYPE_LOSE_VOL :
            case CallQualityConstant.QUALITY_TYPE_CURRENT :
            case CallQualityConstant.QUALITY_TYPE_HAO :
                if (eventType == CallQualityConstant.EVENT_TYPE_SINGLE){
                    return "单次" ;
                }
                return "持续" ;
            case CallQualityConstant.QUALITY_TYPE_LOSE_CALL :
                if (eventType == CallQualityConstant.EVENT_TYPE_SINGLE){
                    return "无声" ;
                }
                return "断续" ;
        }
        return "N/A" ;
    }

    private String getQualityName(int qualityType){
        switch (qualityType){
            case CallQualityConstant.QUALITY_TYPE_CONTINUE :
                return "断续" ;
            case CallQualityConstant.QUALITY_TYPE_LOW_VOL :
                return "声音小" ;
            case CallQualityConstant.QUALITY_TYPE_NOISE :
                return "杂音" ;
            case CallQualityConstant.QUALITY_TYPE_LOOP :
                return "回音" ;
            case CallQualityConstant.QUALITY_TYPE_NO_VOL :
                return "无声" ;
            case CallQualityConstant.QUALITY_TYPE_LOSE_VOL :
                return "失真" ;
            case CallQualityConstant.QUALITY_TYPE_CURRENT :
                return "电流音" ;
            case CallQualityConstant.QUALITY_TYPE_HAO :
                return "啸叫声" ;
            case CallQualityConstant.QUALITY_TYPE_LOSE_CALL :
                return "掉话" ;
        }
        return "N/A" ;
    }

    private static WritableCellFormat getTitleFormat() throws WriteException {
        WritableFont wf_title = new WritableFont(WritableFont.ARIAL, 12,
                WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
                jxl.format.Colour.BLACK);
        WritableCellFormat titleFormat = new WritableCellFormat(wf_title);
        titleFormat.setAlignment(Alignment.CENTRE);
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        titleFormat.setBackground(Colour.LIGHT_GREEN);
        return titleFormat ;
    }

    private static WritableCellFormat getHeadFormat() throws WriteException {
        WritableFont wf_head = new WritableFont(WritableFont.ARIAL, 12,
                WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
                jxl.format.Colour.BLACK);
        WritableCellFormat headFormat = new WritableCellFormat(wf_head);
        headFormat.setAlignment(Alignment.CENTRE);
        headFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        headFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        headFormat.setBackground(Colour.LIGHT_GREEN);
        return headFormat ;
    }

    private static WritableCellFormat getContentFormat() throws WriteException {
        WritableFont bold2 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
        bold2.setColour(Colour.BLACK);

        WritableCellFormat contentFormat = new WritableCellFormat(bold2);
        contentFormat.setAlignment(Alignment.CENTRE);
        contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        contentFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        contentFormat.setBackground(Colour.GRAY_25);
        return contentFormat ;
    }

    private void addHeadTitle(String phone_num, String phone_num_o, int round, WritableSheet sheet) throws WriteException {
        sheet.setColumnView(0, 15);
        sheet.setColumnView(1, 15);
        sheet.setColumnView(2, 28);
        sheet.setColumnView(3, 15);
        sheet.setColumnView(4, 15);
        sheet.setColumnView(5, 15);
        sheet.setColumnView(6, 15);
        sheet.setColumnView(7, 15);
        sheet.setColumnView(8, 15);
        sheet.setColumnView(9, 15);
        sheet.setColumnView(10, 15);
        sheet.setColumnView(11, 15);
        sheet.setColumnView(12, 15);
        sheet.setColumnView(13, 15);
        sheet.setColumnView(14, 15);
        sheet.setColumnView(15, 28);
        sheet.setColumnView(16, 15);
        sheet.setColumnView(17, 15);
        sheet.setColumnView(18, 15);
        sheet.setColumnView(19, 15);
        sheet.setColumnView(20, 15);
        sheet.setColumnView(21, 15);
        sheet.setColumnView(22, 15);
        sheet.setColumnView(23, 15);
        sheet.setColumnView(24, 15);
        sheet.setColumnView(25, 15);
        //for round information
        Label roundLabel            = new Label(0, 0, String.format(Locale.US, SHEET_NAME, round), getHeadFormat()) ;
        Label roundLabel1            = new Label(25, 0, String.format(Locale.US, SHEET_NAME, round), getHeadFormat()) ;
        //for title
        Label phone_num_label       = new Label(0, 1, "机型/编号:" + phone_num, getTitleFormat()) ;
        Label phone_num_label_o     = new Label(13, 1, "机型/编号:" + phone_num_o, getTitleFormat()) ;
        //for device one
        Label qualityLabel          = new Label(0, 2, "类型", getTitleFormat());
        Label eventLabel            = new Label(1, 2, "事件", getTitleFormat());
        Label timeLabel             = new Label(2, 2, "时间", getTitleFormat());
        Label sim0ActiveLabel       = new Label(3, 2, "卡1是否存在", getTitleFormat());
        Label sim0Operator          = new Label(4, 2, "卡1网络运营商", getTitleFormat());
        Label sim0Type              = new Label(5, 2, "卡1网络类型", getTitleFormat());
        Label sim0Level             = new Label(6, 2, "卡1信号级别", getTitleFormat());
        Label sim0Signal            = new Label(7, 2, "卡1信号", getTitleFormat());
        Label sim1Operator          = new Label(8, 2, "卡2是否存在", getTitleFormat());
        Label sim1ActiveLabel       = new Label(9, 2, "卡2网络运营商", getTitleFormat());
        Label sim1Type              = new Label(10, 2, "卡2网络类型", getTitleFormat());
        Label sim1Level             = new Label(11, 2, "卡2信号级别", getTitleFormat());
        Label sim1Signal            = new Label(12, 2, "卡2信号", getTitleFormat());
        //for device two
        Label qualityLabelO         = new Label(13, 2, "类型", getTitleFormat());
        Label eventLabelO           = new Label(14, 2, "事件", getTitleFormat());
        Label timeLabelO            = new Label(15, 2, "时间", getTitleFormat());
        Label sim0ActiveLabelO      = new Label(16, 2, "卡1是否存在", getTitleFormat());
        Label sim0OperatorO         = new Label(17, 2, "卡1网络运营商", getTitleFormat());
        Label sim0TypeO             = new Label(18, 2, "卡1网络类型", getTitleFormat());
        Label sim0LevelO            = new Label(19, 2, "卡1信号级别", getTitleFormat());
        Label sim0SignalO           = new Label(20, 2, "卡1信号", getTitleFormat());
        Label sim1ActiveLabelO      = new Label(21, 2, "卡2是否存在", getTitleFormat());
        Label sim1OperatorO         = new Label(22, 2, "卡2网络运营商", getTitleFormat());
        Label sim1TypeO             = new Label(23, 2, "卡2网络类型", getTitleFormat());
        Label sim1LevelO            = new Label(24, 2, "卡2信号级别", getTitleFormat());
        Label sim1SignalO           = new Label(25, 2, "卡2信号", getTitleFormat());

        sheet.addCell(roundLabel);
        sheet.addCell(roundLabel1);

        sheet.addCell(phone_num_label);
        sheet.addCell(phone_num_label_o);

        sheet.addCell(qualityLabel);
        sheet.addCell(eventLabel);
        sheet.addCell(timeLabel);
        sheet.addCell(sim0ActiveLabel);
        sheet.addCell(sim0Operator);
        sheet.addCell(sim0Type);
        sheet.addCell(sim0Level);
        sheet.addCell(sim0Signal);
        sheet.addCell(sim1ActiveLabel);
        sheet.addCell(sim1Operator);
        sheet.addCell(sim1Type);
        sheet.addCell(sim1Level);
        sheet.addCell(sim1Signal);

        sheet.addCell(qualityLabelO);
        sheet.addCell(eventLabelO);
        sheet.addCell(timeLabelO);
        sheet.addCell(sim0ActiveLabelO);
        sheet.addCell(sim0OperatorO);
        sheet.addCell(sim0TypeO);
        sheet.addCell(sim0LevelO);
        sheet.addCell(sim0SignalO);
        sheet.addCell(sim1ActiveLabelO);
        sheet.addCell(sim1OperatorO);
        sheet.addCell(sim1TypeO);
        sheet.addCell(sim1LevelO);
        sheet.addCell(sim1SignalO);

        //merge cells
        sheet.mergeCells(0, 0, 25, 0) ;

        sheet.mergeCells(0, 1, 12, 1) ;
        sheet.mergeCells(13, 1, 25, 1) ;
    }

    private List<BaseEvent> convertDataFromFile(File target) throws IOException{
        List<BaseEvent> data = new LinkedList<>() ;
        FileReader fr = new FileReader(target) ;
        BufferedReader mReader = new BufferedReader(fr) ;
        QualityEventWrapper qualityEventWrapper ;
        SimSignalInfoWrapper simSignalInfoWrapper ;
        try {
            String line ;
            while((line = mReader.readLine()) != null) {
                if (line.contains(Constant.SEPARATOR)) {
                    String[] items = line.split(Constant.SEPARATOR) ;
                    if (items.length == 11){
                        simSignalInfoWrapper = new SimSignalInfoWrapper(
                                Boolean.valueOf(items[1]) ?"存在" : "不存在", items[2],
                                items[4], Integer.valueOf(items[3]), items[5],
                                Boolean.valueOf(items[6]) ?"存在" : "不存在", items[7],
                                items[9], Integer.valueOf(items[8]), items[10]) ;
                        simSignalInfoWrapper.setTime(items[0]);
                        data.add(simSignalInfoWrapper) ;
                    }else if (items.length == 4){
                        int phone_num_ = Integer.parseInt(items[1]) ;
                        int quality_type_ = Integer.parseInt(items[2]) ;
                        int event_type_ = Integer.parseInt(items[3]) ;
                        qualityEventWrapper = new QualityEventWrapper(phone_num_, quality_type_, event_type_) ;
                        qualityEventWrapper.setTime(items[0]);
                        data.add(qualityEventWrapper) ;
                    }
                }
            }
            return data ;
        }finally {
            fr.close();
            mReader.close();
        }
    }

    private List<RoundInfoData> analysisData(List<BaseEvent> data){
        //if no data, throw exception
        if (data.isEmpty()){
            Log.i(Constant.TAG, "data is empty") ;
            throw new IllegalStateException("data is empty") ;
        }
        // print it out
        for (BaseEvent event : data){
            if (event instanceof QualityEventWrapper){
                QualityEventWrapper wrapper = (QualityEventWrapper) event ;
                Log.i(Constant.TAG, event.getTime() + " " + wrapper.getPhone_num() + " "
                        + wrapper.getQuality_type() + " " + wrapper.getEvent_type()) ;
            }else if (event instanceof SimSignalInfoWrapper){
                SimSignalInfoWrapper wrapper = (SimSignalInfoWrapper) event ;
                Log.i(Constant.TAG, event.getTime() + " " + wrapper.getmIsActive() + " " + wrapper.getmOperator()
                        + wrapper.getmLevel() + " " + wrapper.getmNetType() + " " + wrapper.getmSignal()
                        + " " + wrapper.getmIsActiveO()  + " " + wrapper.getmOperatorO() + " " + wrapper.getmLevelO() + " "
                        + wrapper.getmNetTypeO() + " " + wrapper.getmSignalO()) ;
            }
        }

        //analysis it
        List<RoundInfoData>   roundInfos = new ArrayList<>() ;

        List<QualityEventWrapper> roundInfo = new ArrayList<>() ;

        for (int i = 0 ; i < data.size() ; i++){
            BaseEvent event = data.get(i) ;
            if (event instanceof QualityEventWrapper){
                QualityEventWrapper qualityEvent = (QualityEventWrapper) event;
                //new round info reached
                if (qualityEvent.isNextRoundEvent()){
                    //check it empty or not first
                    if (!roundInfo.isEmpty()){
                        RoundInfoData roundInfoData = new RoundInfoData() ;
                        roundInfoData.setRoundDatas(roundInfo);
                        roundInfos.add(roundInfoData) ;
                    }
                    //init all variables
                    roundInfo = new ArrayList<>() ;
                }else{
                    int event_type = qualityEvent.getEvent_type() ;
                    int quality_type = qualityEvent.getQuality_type() ;
                    List<SimSignalInfoWrapper> signals = new ArrayList<>() ;
                    //if just a single event , find last signal information for it
                    if (event_type == CallQualityConstant.EVENT_TYPE_SINGLE){
                        for (int j = i - 1 ; j >= 0 ; j--){
                            BaseEvent event_ = data.get(j) ;
                            if (event_ instanceof SimSignalInfoWrapper){
                                signals.add((SimSignalInfoWrapper) event_) ;
                                qualityEvent.setSignals(signals);
                                break;
                            }
                        }
                    }else{
                        //this will complicate....
                        for (int j = i - 1; j > 0 ; j--){
                            BaseEvent event_ = data.get(j) ;
                            if (event_ instanceof QualityEventWrapper){
                                QualityEventWrapper qualityEvent_ = (QualityEventWrapper) event_;
                                if ((qualityEvent_.getQuality_type() == quality_type
                                        && qualityEvent_.getEvent_type() == CallQualityConstant.EVENT_TYPE_SINGLE)
                                        || qualityEvent_.isNextRoundEvent()) {
                                    //should end here...
                                    if (!signals.isEmpty()){
                                        //reverse all signal info
                                        Collections.reverse(signals);
                                    }
                                    qualityEvent.setSignals(signals);
                                    break;
                                }
                            }else if (event_ instanceof SimSignalInfoWrapper){
                                SimSignalInfoWrapper simSignalEvent_ = (SimSignalInfoWrapper) event_;
                                signals.add(simSignalEvent_) ;
                            }
                        }
                    }
                    roundInfo.add(qualityEvent) ;
                }
            }
        }
        return roundInfos ;
    }
}
