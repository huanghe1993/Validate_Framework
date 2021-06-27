package com.huanghe.model;

import com.huanghe.annotation.MinNum;
import com.huanghe.annotation.ServiceFiledCheck;

/**
 * @创建者 CSDN_LQR
 * @描述 用户
 */
@ServiceFiledCheck
public class User {
    @MinNum()
    private String name;
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
