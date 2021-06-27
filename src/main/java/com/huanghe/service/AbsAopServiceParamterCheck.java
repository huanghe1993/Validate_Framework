package com.huanghe.service;

import com.alibaba.fastjson.JSON;
import com.huanghe.annotation.ParameterCheck;
import com.huanghe.model.CheckResult;
import com.huanghe.service.impl.ServiceParameterCheck;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * *
 */
public abstract class AbsAopServiceParamterCheck {
    private static Logger logger = LoggerFactory.getLogger(AbsAopServiceParamterCheck.class);

    @Resource
    private ServiceParameterCheck serviceParameterCheck;

    protected CheckResult check(ProceedingJoinPoint pjp, boolean isWriteLog) throws Throwable {

        Signature sig = pjp.getSignature();
        MethodSignature msig;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

        // 当前方法是否ParameterCheck这个注解
        if (currentMethod.isAnnotationPresent(ParameterCheck.class)) {

            // 方法参数
            Object[] args = pjp.getArgs();
            Object[] params = new Object[args.length + 2];
            // 类名全路径
            params[0] = pjp.getTarget();
            // 方法名
            params[1] = currentMethod.getName();
            for (int i = 0; i < args.length; i++) {
                params[i + 2] = args[i];
            }

            // 执行校验方法- 参数的基本校验 + 自定义的校验
            CheckResult checkBaseParamResult = serviceParameterCheck.checkMethod(params);
            if (!checkBaseParamResult.isSuccess()) {
                logger.warn(pjp.getTarget().getClass().getSimpleName() + "." + currentMethod.getName() + "|checkSuccess=false" + "|param=" + JSON.toJSONString(args));
                return checkBaseParamResult;
            }

            // 执行校验方法 - 参数如果是自定义对象还需要校验一下 对象里的属性是否有校验规则
            CheckResult checkObjectParamResult = serviceParameterCheck.batchCheckObjecs(args);
            if (!checkObjectParamResult.isSuccess()) {
                logger.warn(pjp.getTarget().getClass().getSimpleName() + "." + currentMethod.getName() + "|checkSuccess=false" + "|param=" + JSON.toJSONString(args));
                return checkObjectParamResult;
            }

            if (isWriteLog) {
                logger.warn("look i am here");
            }
        }
        return new CheckResult(true);
    }
}
