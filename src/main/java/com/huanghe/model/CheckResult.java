package com.huanghe.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckResult {

    private boolean success;

    private String msg;

    public CheckResult(boolean success){
        this.success = success;
    }

    public void setMsg(String failMsg) {
    }
}
