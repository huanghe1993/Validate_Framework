package com.huanghe.service.impl;

import com.huanghe.annotation.ServiceCheckPoint;
import com.huanghe.constance.AnnotationCheckType;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import com.huanghe.service.IAnnotationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 该服务是针对方法入参里面的字段级别进行校验
 */
@ServiceCheckPoint
public class ServiceFieldAnnotationManager implements IAnnotationManager, ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(ServiceFieldAnnotationManager.class);

    private ApplicationContext applicationContext;

    @Override
    public void init() throws ServiceCheckException {

    }

    /**
     * 每个实现这个接口，都需要返回一个校验的级别:方法入参级别
     *
     * @return 校验的级别
     */
    @Override
    public AnnotationCheckType getAnnotationCheckType() {
        return AnnotationCheckType.OBJECT;
    }

    @Override
    public CheckResult check(Object... args) {
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
