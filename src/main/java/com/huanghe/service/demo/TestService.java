package com.huanghe.service.demo;

import com.huanghe.annotation.ParameterCheck;
import com.huanghe.annotation.ServiceMethodCheck;
import com.huanghe.model.TestModel;
import com.huanghe.model.User;
import com.huanghe.service.ITestService;
import com.huanghe.service.impl.ServiceCheckFailedHandler;
import org.springframework.stereotype.Service;

/**
 * 使用刨析：
 * <p>
 * 第一步：需要在待进行参数校验的方法前面加 @ParameterCheck这个注解。
 * 第二步：在@ParameterCheck注解里指定一个校验不通过的错误信息返回对象，如上代码中的 ServiceCheckFailedHandler
 * 类。这个类的具体实现如下：
 */
@Service
@ServiceMethodCheck
public class TestService {


    /**
     * @param tagOption 参数
     * @return 字符串返回值
     */
    @ParameterCheck(exceptionHandler = ServiceCheckFailedHandler.class)
    public String tagAdd(User tagOption, Integer f) {
        System.out.println("hello");
        //  在做业务逻辑之前，系统其实已经通过ParameterCheck这个注解对 tagWriteOption这个对象&对象
        //        指定的属性做了基础的校验工作)
        return "";
    }

}
