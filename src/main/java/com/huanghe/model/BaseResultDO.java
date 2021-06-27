package com.huanghe.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResultDO<T> {

    private boolean success;

    private int resultCode;

    private String resultMessage;

    private T data;

}
