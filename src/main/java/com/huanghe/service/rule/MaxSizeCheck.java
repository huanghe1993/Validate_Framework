package com.huanghe.service.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanghe.annotation.CheckRule;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import com.huanghe.service.ParamCheck;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@CheckRule
public class MaxSizeCheck implements ParamCheck {

    private final static Logger logger = LoggerFactory.getLogger(MaxSizeCheck.class);

    /**
     * 所有需要校验的逻辑类，都需要实现这个方法
     *
     * @param t          待校验的值
     * @param objectType 待校验值的数据类型
     * @param c          自定义校验时的规则内容
     * @return CheckResult 校验结果&描述
     */
    @Override
    public CheckResult check(Object t, Class<?> objectType, String c) throws ServiceCheckException {
        CheckResult checkResult = new CheckResult(true);
        //如果校验列表的个数是否超越设定的阀值，没有传阀值过来，默认通过
        if (StringUtils.isEmpty(c)) {
            return checkResult;
        }
        Integer maxSize;
        try {
            // 这里可以做优化，将所有个性化的校验条件加载时都初始化好
            JSONObject objectCondition = JSON.parseObject(c);
            maxSize = objectCondition.getInteger("maxSize");
            if (null == maxSize) {
                return checkResult;
            }
        } catch (Exception e) {
            logger.error("MaxSizeCheck Error: msg=" + c, e);
            throw new ServiceCheckException("MaxSizeCheck Error: msg=" + c + e.getMessage());
        }

        return new CheckResult(true);
    }

    /**
     * 检验规则的名称，通过这个名称来动态找到注解里配置的校验规则类
     *
     * @return
     */
    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

}
