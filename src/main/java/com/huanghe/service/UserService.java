package com.huanghe.service;

import com.huanghe.annotation.Log;
import com.huanghe.model.Result;
import com.huanghe.model.User;
import com.huanghe.utils.LogUtils;
import com.huanghe.utils.ResultUtils;

import org.springframework.stereotype.Service;

import javassist.NotFoundException;

@Service
public class UserService {

//    @Log(logStr = "添加一个用户")
//    public Result add(User user) {
//        return ResultUtils.success();
//    }

    @Log
    public Result add(User user) throws NotFoundException {
        if (user.getAge() < 18) {
            LogUtils.get().setLog("添加用户失败，因为用户未成年");
            return ResultUtils.error("未成年不能注册");
        }
        if ("男".equalsIgnoreCase(user.getSex())) {
            LogUtils.get().setLog("添加用户失败，因为用户是个男的");
            return ResultUtils.error("男性不能注册");
        }

        LogUtils.get().setLog("添加用户成功，是一个" + user.getAge() + "岁的美少女");

        return ResultUtils.success();
    }

}
