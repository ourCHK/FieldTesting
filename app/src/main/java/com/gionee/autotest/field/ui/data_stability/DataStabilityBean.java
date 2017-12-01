package com.gionee.autotest.field.ui.data_stability;

/**
 * Created by chk on 17-12-1.
 * 用于存储从数据库查询的结果
 */

public class DataStabilityBean {
    int id;
    int batchId;
    int testIndex;
    String result;


    public DataStabilityBean(int id, int batchId, int testIndex, String result) {
        this.id = id;
        this.batchId = batchId;
        this.testIndex = testIndex;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getTestIndex() {
        return testIndex;
    }

    public void setTestIndex(int testIndex) {
        this.testIndex = testIndex;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
