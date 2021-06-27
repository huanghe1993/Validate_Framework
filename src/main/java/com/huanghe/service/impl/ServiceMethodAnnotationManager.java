package com.huanghe.service.impl;

import com.alibaba.fastjson.JSON;

import com.huanghe.annotation.*;
import com.huanghe.constance.AnnotationCheckType;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import com.huanghe.service.IAnnotationManager;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对服务接口的方法参数做校验
 * 该服务是针对方法入参级别进行校验
 */
@ServiceCheckPoint
public class ServiceMethodAnnotationManager implements IAnnotationManager, ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(ServiceMethodAnnotationManager.class);

    /**
     * 前面两位用作其他用途
     */
    private final static Integer reserveLen = 2;


    /**
     * 每个服务对应的方法集合
     */
    private final Map<String, Method> methodMap = new HashMap<>();

    /**
     * 每个服务对应的方法参数列表
     */
    private final Map<String, List<Class<?>>> methodParamMap = new HashMap<>();

    @Resource
    ParamCheckRuleManager paramCheckRuleManager;

    private ApplicationContext applicationContext;

    @Override
    public void init() throws ServiceCheckException {
        // 获取到所有注解@ServiceMethodCheck的Java Bean
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(ServiceMethodCheck.class);
        try {
            for (Object o : objectMap.values()) {
                 Class<?> clazz = o.getClass().getSuperclass();
                // 获取方法列表，如果没有注册，直接跳出返回
                Method[] methods = clazz.getDeclaredMethods();
                if (ArrayUtils.isEmpty(methods)) {
                    break;
                }
                for (Method method : methods) {
                    // 方法上是否有ParameterCheck这个注解
                    if (method.isAnnotationPresent(ParameterCheck.class)) {
                        String key = clazz.getName() + "." + method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (!ArrayUtils.isEmpty(parameterTypes)) {
                            methodParamMap.put(key, Arrays.asList(parameterTypes));
                        }
                        methodMap.put(key, method);
                    }
                }
            }
            logger.warn(" ServiceMethodAnnotationManager init success ,methodMap:" + JSON.toJSONString(methodMap));
        } catch (Exception e) {
            logger.error("ServiceMethodAnnotationManager error!", e);
            throw new ServiceCheckException("ServiceMethodAnnotationManager Init Error! " + e.getMessage());
        }
    }

    /**
     * 具体执行校验的入口之一
     *
     * @param args 参数
     * @return CheckResult 校验结果 & 错误描述
     */
    @Override
    public CheckResult check(Object... args) {
        CheckResult checkResult = new CheckResult(true);

        // 参数列表为空，直接返回true，不做校验
        if (ArrayUtils.isEmpty(args)) {
            return checkResult;
        }

        // 参数长度必须大于两个，第一个是接口服务类对象，第二个是调用的方法签名，剩余的是入参
        if (args.length < reserveLen) {
            return checkResult;
        }

        Object[] objects = args;

        // 第二个是调用的方法签名
        String methodName = args[1].toString();
        // 类名+方法名作为key
        String key = args[0].getClass().getName() + "." + methodName;

        // 这个类+方法下的参数列表，methodParamMap在初始化的时候已经设置好
        List<Class<?>> paramTypeNameList = methodParamMap.get(key);

        // 说明不需要检验
        if (CollectionUtils.isEmpty(paramTypeNameList)) {
            return checkResult;
        }

        // 获取对应key的方法对象
        Method method = methodMap.get(key);

        // 获取对应方法上的注解 @ParameterCheck
        ParameterCheck annotation = method.getAnnotation(ParameterCheck.class);
        if (null == annotation) {
            return checkResult;
        }

        // 获取方法参数里对应的注解列表
        Map<Integer, Annotation> annotationAndParamIndexMap = getAnnotationAndParamIndex(method);

        /*
         * 循环校验传入的参数,为什么要从2开始，
         * 因为第一个参数是服务对象。
         * 第二个参数是方法签名。
         * 从第三个参数开始，才是方法的参数列表
         */
        try {
            for (int i = reserveLen; i < objects.length; i++) {
                int paramIndex = i - reserveLen;

                // 字段上有uncheck这个注解，说明不需要做检验，忽略
                if (isCheck(annotationAndParamIndexMap, paramIndex)) {
                    // 如果参数上没有自定义注解，那么就以方法注解上的自定义注解为检验的规则
                    if (isHaveSelfCheck(annotationAndParamIndexMap, paramIndex)) {
                        // 参数上的自定义检验规则注解
                        SelfCheck paramAnnotation = (SelfCheck) annotationAndParamIndexMap.get(paramIndex);
                        checkResult = paramCheckRuleManager.check(objects[i], paramTypeNameList.get(paramIndex),
                                Arrays.asList(paramAnnotation.check()),
                                paramAnnotation.condition(),
                                paramAnnotation.msg());
                    } else {
                        checkResult = paramCheckRuleManager.check(objects[i], paramTypeNameList.get(paramIndex),
                                Arrays.asList(annotation.selfCheck()),
                                annotation.condition(),
                                annotation.msg());
                    }

                    if (!checkResult.isSuccess()) {
                        return checkResult;
                    }
                }
            }
        } catch (Exception e) {
            // 如果检验里边发生了异常，默认通过校验。可以往下走
            logger.error("ServiceMethodAnnotationManager error ,msg=", e);
        }
        return new CheckResult(true);
    }

    /**
     * 每个实现这个接口，都需要返回一个校验的级别:方法入参级别
     *
     * @return 校验的级别
     */
    @Override
    public AnnotationCheckType getAnnotationCheckType() {
        return AnnotationCheckType.METHOD;
    }

    /**
     * 获取不需要检查的参数的索引集合
     *
     * @param method 反射方法对象
     * @return param annotation index map
     */
    private Map<Integer, Annotation> getAnnotationAndParamIndex(Method method) {
        Map<Integer, Annotation> annotationAndParamIndexMap = new HashMap<>();

        // 获取方法参数里是否有 uncheck这个注解
        Annotation[][] annotations = method.getParameterAnnotations();
        if (!ArrayUtils.isEmpty(annotations)) {
            for (int i = 0; i < annotations.length; i++) {
                for (int j = 0; j < annotations[i].length; j++) {
                    // 把参数及对应参数上的注解记录解析出来 循环下标是从0开始，i=0 实际上是指第一个参数。
                    if (null != annotations[i][j]) {
                        annotationAndParamIndexMap.put(i, annotations[i][j]);
                    }
                }
            }
        }

        return annotationAndParamIndexMap;
    }

    /**
     * 字段上有uncheck这个注解，不需要做检验，忽略
     *
     * @param annotationAndParamIndexMap param index map
     * @param paramIndex                 index
     * @return param annotation
     */
    private boolean isCheck(Map<Integer, Annotation> annotationAndParamIndexMap, Integer paramIndex) {
        if (CollectionUtils.isEmpty(annotationAndParamIndexMap)) {
            return true;
        }

        // 如果对应的参数里包含 Uncheck这个注解，就返回false，不校验这个参数
        return !(annotationAndParamIndexMap.get(paramIndex) instanceof Uncheck);
    }

    /**
     * 字段上是否有 自定义的注解 selfCheck
     *
     * @param annotationAndParamIndexMap param index map
     * @param paramIndex                 index
     * @return true含有自定义注解，false不含有自定义注解
     */
    private boolean isHaveSelfCheck(Map<Integer, Annotation> annotationAndParamIndexMap, Integer paramIndex) {
        if (CollectionUtils.isEmpty(annotationAndParamIndexMap)) {
            return false;
        }
        // 判断函数参数是否有自定义校验的注解
        return annotationAndParamIndexMap.get(paramIndex) instanceof SelfCheck;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

