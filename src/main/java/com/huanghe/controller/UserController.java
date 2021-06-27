package com.huanghe.controller;

import com.huanghe.model.Result;
import com.huanghe.model.User;
import com.huanghe.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javassist.NotFoundException;

@RestController
public class UserController {

    @Autowired
    UserService mUserService;

    @PostMapping("/add")
    public Result add(User user) throws NotFoundException {
        return mUserService.add(user);
    }
}
