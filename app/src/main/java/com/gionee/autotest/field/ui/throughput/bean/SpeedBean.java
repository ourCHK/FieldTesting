package com.gionee.autotest.field.ui.throughput.bean;


import java.util.ArrayList;

public class SpeedBean {
    public String type;//上传或下载的文件大小
    public String way;//测试方式 -上传/下载
    public String speed_average;//一轮测试下来的平均速率
    public RateBean speed;//一次测试中所有的时间以及测试速率
    public int use_time;//一次测试的使用时间
    public int times;//一次测试的耗时
    public String time;//一次测试的开始时间
    public String id;
    public String web;//uri网络状态 -内网/外网
    public String start;//一轮循环的开始时间
    public int serial;//一轮循环的序号

    public SpeedBean setType(String type) {
        this.type = type;
        return this;
    }

    public SpeedBean setWeb(String web) {
        this.web=web;
        return this;
    }//
    public SpeedBean setStart(String start) {
        this.start=start;
        return this;
    }//一轮循环的开始时间

    public SpeedBean setWay(String way) {
        this.way = way;
        return this;
    }

    public SpeedBean setID(String id) {
        this.id = id;
        return this;
    }

    public SpeedBean setSpeed_average(String speed_average) {
        this.speed_average = speed_average;
        return this;
    }

    public SpeedBean setSpeed(RateBean speed) {
        this.speed = speed;
        return this;
    }

    public SpeedBean setUse_time(int use_time) {
        this.use_time = use_time;
        return this;
    }

    public SpeedBean setTimes(int times) {
        this.times = times;
        return this;
    }

    public SpeedBean setTime(String time) {
        this.time = time;
        return this;
    }

    public SpeedBean setSerial(int serial) {
        this.serial = serial;
        return this;
    }

    public static class RateBean {
        public ArrayList<Speed> speeds;

        public RateBean setSpeeds(ArrayList<Speed> speeds) {
            this.speeds = speeds;
            return this;
        }

        public static class Speed {

            public String time;
            public String speed;

            public RateBean.Speed setTime(String time) {
                this.time = time;
                return this;
            }

            public RateBean.Speed setSpeed(String speed) {
                this.speed = speed;
                return this;
            }
        }
    }
}
