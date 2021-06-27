package com.huanghe.service.impl;

import com.huanghe.annotation.ServiceCheckPoint;
import com.huanghe.annotation.ServiceFiledCheck;
import com.huanghe.constance.AnnotationCheckType;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import com.huanghe.service.IAnnotationManager;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 该服务是针对方法入参里面的字段级别进行校验
 */
@ServiceCheckPoint
public class ServiceFieldAnnotationManager implements IAnnotationManager, ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(ServiceFieldAnnotationManager.class);

    /**
     * 每个类对应的字段列表
     */
    private final Map<String, List<Field>> modelFieldMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Resource
    private ParamCheckRuleManager paramCheckRuleManager;

    @Override
    public void init() throws ServiceCheckException {
        // 获取到所有注解@ServiceFiledCheck的Java Bean
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(ServiceFiledCheck.class);
        try {
            for (Object o : objectMap.values()) {
                Class<?> clazz = o.getClass();
                Field[] declaredFields = clazz.getDeclaredFields();
                if (ArrayUtils.isEmpty(declaredFields)) {
                    break;
                }
                for (Field field:declaredFields){
                    if (field.isAnnotationPresent(Annotation.class)){
                        System.out.println(1);
                    }
                }
                List<Field> fields = Arrays.stream(declaredFields).filter(field -> field.isAnnotationPresent(Annotation.class)).collect(Collectors.toList());
                modelFieldMap.put(clazz.getName(), fields);
            }
        } catch (Exception e) {
            logger.error("ServiceMethodAnnotationManager error!", e);
            throw new ServiceCheckException("ServiceMethodAnnotationManager Init Error! " + e.getMessage());
        }
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

    @SneakyThrows
    @Override
    public CheckResult check(Object... args) {
        CheckResult checkResult = new CheckResult(true);
        // 参数列表为空，直接返回true，不做校验
        if (ArrayUtils.isEmpty(args)) {
            return checkResult;
        }
        Object obj = args[0];
        // 如果这个类没有被@ServiceFileCheck那么就不进行校验
        if (!obj.getClass().isAnnotationPresent(ServiceFiledCheck.class)) {
            return checkResult;
        }
        List<Field> fields = modelFieldMap.getOrDefault(obj.getClass().getName(), new ArrayList<>());
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getDeclaredAnnotations();
            // 如果字段没有被注解则认为是成功的
            if (ArrayUtils.isEmpty(annotations)) {
                continue;
            }
            for (Annotation annotation : annotations) {
//                checkResult = paramCheckRuleManager.check(field.get(obj), field.getType(),
//                        Collections.singletonList(annotation.check()),
//                        annotation.condition(),
//                        annotation.msg());
            }
        }
        return checkResult;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
