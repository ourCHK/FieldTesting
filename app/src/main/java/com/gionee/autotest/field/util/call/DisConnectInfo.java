package com.gionee.autotest.field.util.call;


public class DisConnectInfo implements Cloneable{

    public int code = -1;
    public String number = "";
    public int type = TYPE.OUTGOING_TYPE;
    public long callTimeStart = 0;
    public long callDuration = 0;

    public DisConnectInfo setCode(int code) {
        this.code = code;
        return this;
    }

    public DisConnectInfo setNumber(String number) {
        this.number = number;
        return this;
    }

    public DisConnectInfo setType(int type) {
        this.type = type;
        return this;
    }

    public DisConnectInfo setCallTimeStart(long callTimeStart) {
        this.callTimeStart = callTimeStart;
        return this;
    }

    public DisConnectInfo setCallDuration(long callDuration) {
        this.callDuration = callDuration;
        return this;
    }

    public static int StringToCode(String s) {
        if (s.contains("LOCAL")) {
            return Code.LOCAL;
        } else if (s.contains("REMOTE")) {
            return Code.REMOTE;
        } else if (s.contains("BUSY")) {
            return Code.BUSY;
        } else if (s.contains("ERROE")) {
            return Code.ERROR;
        }
        return 0;
    }

    public static String codeToReason(int code) {
        switch (code) {
            case Code.LOCAL:
                return "本地挂断";
            case Code.REMOTE:
                return "对方挂断";
            case Code.BUSY:
                return "对方忙";
            case Code.ERROR:
                return "网络或其他异常";
        }
        return "";
    }

    @Override
    public DisConnectInfo clone() {
        try {
            return (DisConnectInfo) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    class Code {
        //            LOCAL本方挂断，REMOTE对方挂断，BUSY对方忙，ERROE网络异常挂断；
        public static final int LOCAL = 0;
        public static final int REMOTE = 1;
        public static final int BUSY = 2;
        public static final int ERROR = 3;
    }

    class TYPE {
        public static final int INCOMING_TYPE = 1;
        public static final int OUTGOING_TYPE = 2;
        public static final int MISSED_TYPE = 3;
        public static final int VOICEMAIL_TYPE = 4;
        public static final int REJECTED_TYPE = 5;
        public static final int BLOCKED_TYPE = 6;
        public static final int ANSWERED_EXTERNALLY_TYPE = 7;
        public static final int AUTO_REJECT_TYPE = 8;
        public static final int CIPHERING_IMCOMING_TYPE = 9;
        public static final int CIPHERING_OUTGOING_TYPE = 10;
        public static final int CIPHERING_MISSED_TYPE = 11;
    }
}
