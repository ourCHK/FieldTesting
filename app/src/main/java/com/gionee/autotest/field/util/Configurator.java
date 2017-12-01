package com.gionee.autotest.field.util;


import com.gionee.autotest.field.ui.data_stability.DataParam;

public class Configurator {
    private static Configurator instance  = new Configurator();
    public String[]     urls      = new String[]{
            "https://www.gionee.com/zh/about/",
            "https://www.gionee.com/zh/honour/",
            "https://www.gionee.com/zh/mediabd/",
            "https://www.gionee.com/zh/comNews/",
            "https://www.gionee.com/zh/gioneeds/"};
    //    public         String[]     urls      = new String[]{"https://www.baidu.com/","http://www.sina.com.cn/","https://hao.360.cn/","https://www.hao123.com/","https://hao.qq.com/"};
    public DataParam param     = new DataParam();
    public         int          batchId   = 0;
    public         int          testIndex = 0;

    public synchronized static Configurator getInstance() {
        if (instance == null) {
            synchronized (Configurator.class) {
                instance = new Configurator();
            }
        }
        return instance;
    }

    public Configurator setUrls(String[] urls) {
        this.urls = urls;
        return this;
    }

    public Configurator setParam(DataParam param) {
        this.param = param;
        return this;
    }

    public Configurator setBatchId(int batchId) {
        this.batchId = batchId;
        return this;
    }

    public Configurator setTestIndex(int testIndex) {
        this.testIndex = testIndex;
        return this;
    }
}
