package com.huanghe.aspect;

import com.huanghe.annotation.ParameterCheck;
import com.huanghe.model.CheckResult;
import com.huanghe.service.AbsAopServiceParamterCheck;
import com.huanghe.service.ICheckFailedHandler;
import com.huanghe.service.impl.CheckFailedHandlerWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 首先只要方法上有ParameterCheck这个注解，系统都会针对方法里边所有的参数进行基本的为NULL校验。
 * 系统通过AOP自动拦截有ParameterCheck注解的方法。AOP拦截实现如下：
 */
@Component
@Aspect
@Order(1)
public class ParamCheckAop extends AbsAopServiceParamterCheck {
    private static final Logger logger = LoggerFactory.getLogger("aopServiceParameterCheck");

    /**
     * 只要有ParameterCheck 这个注解的方法，都会被拦截
     *
     * @param pjp 连接点
     * @return 返回对象
     * @throws Throwable
     */
    @Around("@annotation(parameterCheck)")
    public Object around(ProceedingJoinPoint pjp, ParameterCheck parameterCheck) throws Throwable {

        long startTime = System.currentTimeMillis();

        // 执行校验方法
        CheckResult checkSuccess = super.check(pjp, true);
        long annExecTime = System.currentTimeMillis() - startTime;
        if (logger.isDebugEnabled()) {
            logger.debug(pjp.getTarget().getClass().getSimpleName() + "|checkTime=" + annExecTime);
        }
        if (!checkSuccess.isSuccess()) {
            Method method = getMethod(pjp);
            ICheckFailedHandler handler = CheckFailedHandlerWrapper.getInstance()
                    .getCheckFailedHander(parameterCheck.exceptionHandler());

            return handler.validateFailed(checkSuccess.getMsg(),
                    method.getReturnType(),
                    pjp.getArgs());
        }


        return pjp.proceed();
    }

    private Method getMethod(JoinPoint pjp) {
        MethodSignature method = (MethodSignature) pjp.getSignature();
        return method.getMethod();
    }
}
