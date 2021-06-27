package com.huanghe.service.impl;


import com.huanghe.annotation.CheckRule;
import com.huanghe.service.ParamCheck;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有注册的检验类（对象类型的、集合类型的、自定义类型等）
 */
@Service
public class ParamCheckRuleCollection implements ApplicationContextAware {
    private Map<String, ParamCheck> paramCheckMap;
    private ApplicationContext applicationContext;

    /**
     * 初始化完成所有校验的规则类，并按照名称植入map中
     */
    @PostConstruct
    protected void init() {
        // 获取到所有添加了@CheckRule的规则类
        Map<String, Object> tempParamCheckMap = applicationContext.getBeansWithAnnotation(CheckRule.class);

        if (!CollectionUtils.isEmpty(tempParamCheckMap)) {
            paramCheckMap = new HashMap<>(tempParamCheckMap.size());
            for (Object o : tempParamCheckMap.values()) {
                if (o instanceof ParamCheck) {
                    ParamCheck paramCheck = (ParamCheck) o;
                    paramCheckMap.put(paramCheck.name(), paramCheck);
                }
            }
        }
    }

    /**
     * 返回对应规则名称的校验类,如果没有找到对应的规则类，那么返回对象检验规则类
     *
     * @param checkName 校验名称
     * @return 返回对应规则名称的校验类
     */
    public ParamCheck getParamCheckInstance(String checkName) {
        if (StringUtils.isEmpty(checkName)) {
            return paramCheckMap.get(Object.class.getName());
        }
        ParamCheck iParamCheck = paramCheckMap.get(checkName);
        return (null != iParamCheck) ? iParamCheck : paramCheckMap.get(Object.class.getName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
