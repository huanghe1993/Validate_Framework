package com.huanghe.service.impl;

import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ParamCheckRuleManager {
    @Resource
    private ParamCheckRuleCollection paramCheckRuleCollection;

    /**
     * 基础校验&自定义校验
     * 基础校验是必须要做的，自定义校验根据注解上的配置来决定是否要做
     * @param v
     * @param objectType 待校验值的数据类型
     * @param selfChecks 自定义校验
     * @param condition 自定义校验时的规则内容
     * @param failMsg 失败信息
     * @return 返回模型类
     */
    public CheckResult check(Object v,
                             Class<?> objectType,
                             List<String> selfChecks,
                             String condition,
                             String failMsg) throws ServiceCheckException {

        // 基础的校验，对象为NULL，为EMPTY
        CheckResult baseCheck = paramCheckRuleCollection.getParamCheckInstance(objectType.getName()).check(v, objectType, condition);

        // 基础校验不通过，直接返回false
        if (!baseCheck.isSuccess()) {
            return baseCheck;
        }

        // 加载自定义的数据校验
        if (!CollectionUtils.isEmpty(selfChecks)) {
            for (String selfCheck : selfChecks) {
                if (!StringUtils.isEmpty(selfCheck)) {
                    CheckResult checkRult = paramCheckRuleCollection.getParamCheckInstance(selfCheck).check(v, objectType, condition);

                    // 自定义校验不通过，直接返回false
                    if (!checkRult.isSuccess()) {
                        // 使用用户自定义的校验失败信息
                        if (StringUtils.isNotBlank(failMsg)) {
                            checkRult.setMsg(failMsg);
                        }
                        return checkRult;
                    }
                }
            }
        }
        return new CheckResult(true);
    }
}

