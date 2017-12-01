package com.gionee.autotest.field.ui.network_switch.model;

public class InterruptException extends RuntimeException {
    public InterruptException() {
        System.out.print("打断异常，操作被打断");
    }

    public InterruptException(String msg) {
        super(msg);
    }

}
