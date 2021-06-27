package com.huanghe.controller;

import com.huanghe.model.Result;
import com.huanghe.model.User;
import com.huanghe.service.demo.TestService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    TestService testService;

    @PostMapping("/demo")
    public Result test(User user) throws NotFoundException {
        System.out.println("======================");
        testService.tagAdd(user,1);
        return new Result();
    }
}