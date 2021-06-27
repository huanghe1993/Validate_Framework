package com.huanghe.service.impl;


import com.huanghe.constance.BaseResultTypeEnum;
import com.huanghe.model.BaseResultDO;
import com.huanghe.service.ICheckFailedHandler;

public class ServiceCheckFailedHandler implements ICheckFailedHandler {

    /**
     *
     * @param msg 返回信息
     * @param returnType return Type
     * @param args 参数
     * @return 业务的错误信息封装
     */
    @Override
    public Object validateFailed(String msg, Class<?> returnType, Object... args) {

        // todo，这里就可以写自己业务的错误信息封装代码了
        BaseResultDO<Boolean> result = new BaseResultDO<>();
        result.setSuccess(false);
        result.setResultCode(BaseResultTypeEnum.PARAM_ERROR.getCode());
        result.setResultMessage(msg);
        return result;
    }
}


