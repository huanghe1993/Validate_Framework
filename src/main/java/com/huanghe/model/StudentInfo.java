package com.huanghe.model;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Setter
@Getter
public class StudentInfo {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "年龄不能为空")
    @Pattern(regexp = "^[0-9]{1,2}$", message = "年龄是整数")
    private String age;

    /**
     * 如果是空，则不校验，如果不为空，则校验
     */
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "出生日期格式不正确")
    private String birthday;

    @NotBlank(message = "学校不能为空")
    private String school;


}
