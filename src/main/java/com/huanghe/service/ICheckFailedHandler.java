package com.huanghe.service;

public interface ICheckFailedHandler {
    Object validateFailed(String msg, Class<?> returnType, Object... args);
}
