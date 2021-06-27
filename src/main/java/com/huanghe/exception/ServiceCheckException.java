package com.huanghe.exception;

public class ServiceCheckException extends Exception{

    private String msg;

    public ServiceCheckException(String msg){
        this.msg = msg;
    }

}
