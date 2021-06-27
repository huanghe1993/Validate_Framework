package com.huanghe.service.rule;


import com.huanghe.annotation.CheckRule;
import com.huanghe.model.CheckResult;
import com.huanghe.service.ParamCheck;

@CheckRule
public class ObjectCheck implements ParamCheck {
    /**
     * 所有需要校验的逻辑类，都需要实现这个方法
     *
     * @param t          待校验的值
     * @param objectType 待校验值的数据类型
     * @param c          自定义校验时的规则内容
     * @return CheckResult 校验结果&描述
     */
    @Override
    public CheckResult check(Object t, Class<?> objectType, String c) {
        if (null == t) {
            return new CheckResult(false);
        }
        if (!t.getClass().equals(objectType)) {
            return new CheckResult(false);
        }
        return new CheckResult(true);
    }

    /**
     * 检验规则的名称，通过这个名称来动态找到注解里配置的校验规则类
     *
     * @return 检验规则的名称
     */
    @Override
    public String name() {
        return Object.class.getName();
    }
}
