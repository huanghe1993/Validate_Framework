package com.huanghe.service.impl;


import com.huanghe.service.ICheckFailedHandler;

public class CheckFailedHandlerWrapper {

    public static CheckFailedHandlerWrapper getInstance() {
        return new CheckFailedHandlerWrapper();
    }

    public ICheckFailedHandler getCheckFailedHander(Class<?> exceptionHandler) {
        ICheckFailedHandler o = null;
        try {
            o = (ICheckFailedHandler) exceptionHandler.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}
