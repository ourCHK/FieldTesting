package com.gionee.autotest.field.util;

import java.lang.reflect.Method;

public class MySystemProperties {
    private static Class<?> mClassType    = null;
    private static Method   mGetMethod    = null;
    private static Method   mGetIntMethod = null;


    /**
     * 获取系统配置（字符串）
     *
     * @Author SONGYC
     * create at 2016-08-05 14:42
     */
    public static String get(String key) {
        init();
        String value = null;
        try {
            value = (String) mGetMethod.invoke(mClassType, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int getInt(String key, int def) {
        init();
        int value = def;
        try {
            value = (Integer) mGetIntMethod.invoke(mClassType, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 初始化
     */
    private static void init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");
                mGetMethod = mClassType.getDeclaredMethod("get", String.class);
                mGetIntMethod = mClassType.getDeclaredMethod("getInt", String.class, int.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
