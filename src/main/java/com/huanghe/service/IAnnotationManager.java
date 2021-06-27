package com.huanghe.service;


import com.huanghe.constance.AnnotationCheckType;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;

public interface IAnnotationManager {
    AnnotationCheckType getAnnotationCheckType();

    void init() throws ServiceCheckException;

    CheckResult check(Object ...args);
}
