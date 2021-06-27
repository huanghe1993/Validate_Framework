package com.huanghe.service.impl;

import com.huanghe.annotation.ServiceCheckPoint;
import com.huanghe.constance.AnnotationCheckType;
import com.huanghe.exception.ServiceCheckException;
import com.huanghe.model.CheckResult;
import com.huanghe.service.IAnnotationManager;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ServiceParameterCheck implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * 注册的初始化对象列表
     */
    private final Map<String, IAnnotationManager> annotationManagerMap = new HashMap<>();

    /**
     * 在初始化类的时候执行,将每一个注册的对象的属性缓存起来
     */
    @PostConstruct
    protected void init() throws ServiceCheckException {
        // 获取到所有注解了@ServiceCheckPoint 的Java Bean
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(ServiceCheckPoint.class);
        for (Object o : objectMap.values()) {
            if (o instanceof IAnnotationManager) {
                String typeName = ((IAnnotationManager) o).getAnnotationCheckType().name();
                annotationManagerMap.put(typeName, ((IAnnotationManager) o));
                ((IAnnotationManager) o).init();
            }
        }
    }

    /**
     * 根据方法上的入参做校验
     *
     * @param args 参数
     * @return 结果对象
     */
    public CheckResult checkMethod(Object... args) {
        return annotationManagerMap.get(AnnotationCheckType.METHOD.name()).check(args);
    }

    /**
     * 根据入参对象上的注解
     *
     * @param o 需要校验的对象
     * @return 结果对象
     */
    public CheckResult checkObject(Object o) {
        return annotationManagerMap.get(AnnotationCheckType.OBJECT.name()).check(o);
    }

    /**
     * 根据入参对象上的注解批量校验
     *
     * @param objects 需要校验的对象
     * @return 通用的返回值
     */
    public CheckResult batchCheckObjecs(Object[] objects) {

        IAnnotationManager iAnnotationManager = annotationManagerMap.get(AnnotationCheckType.OBJECT.name());

        if (ArrayUtils.isEmpty(objects)) {
            return new CheckResult(true);
        }

        for (Object o : objects) {
            Class<?> objectType = o.getClass();
            if (objectType.getSimpleName().endsWith("List")) {
                o = ((List<?>) o).get(0);
            } else if (objectType.getSimpleName().endsWith("Map")) {
                o = ((Map<?, ?>) o).values().toArray()[0];
            } else if (objectType.getSimpleName().endsWith("Set")) {
                o = ((Set<?>) o).toArray()[0];
            } else if (objectType.isArray()) {
                o = Collections.singletonList(o).get(0);
            }

            CheckResult checkResult = iAnnotationManager.check(o);
            if (!checkResult.isSuccess()) {
                return checkResult;
            }
        }
        return new CheckResult(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

