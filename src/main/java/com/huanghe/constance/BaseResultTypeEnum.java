package com.huanghe.constance;

import lombok.Getter;

@Getter
public enum BaseResultTypeEnum {

    PARAM_ERROR(400, "参数错误");

    private final Integer code;

    private final String message;

    BaseResultTypeEnum(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
