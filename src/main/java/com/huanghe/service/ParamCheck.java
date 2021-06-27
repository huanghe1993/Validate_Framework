package com.huanghe.service;


import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;

/**
 * 校验规则的定义
 * 主要有两类：
 * 1、基本数据类型的为空为NULL等基本校验，这些我已经写好
 * 2、个性化自定义的校验，比如批量查询阀值校验，这些往往有特定的场景和背景，只需要实现该接口
 *
 * 这个接口定义了两个方法：
 * check()方法就是所有校验规则的基本校验逻辑.
 * name()方法返回校验规则的名称，系统会初始化加载这些规则并置入本地内存里，之后所有的校验工作都会由这些
 * 在内存中的校验规则类来完成。
 */
public interface ParamCheck {

    /**
     * 所有需要校验的逻辑类，都需要实现这个方法
     * @param t  待校验的值
     * @param objectType 待校验值的数据类型
     * @param c 自定义校验时的规则内容
     * @return CheckResult 校验结果&描述
     */
    CheckResult check(Object t, Class<?> objectType, String c) throws ServiceCheckException;

    /**
     * 检验规则的名称，通过这个名称来动态找到注解里配置的校验规则类
     * @return 检验规则的名称
     */
    String name();
}
