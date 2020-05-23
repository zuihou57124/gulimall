package com.project.gulimallmember.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author qcw
 * 用户注册vo
 */
@Data
public class RegisterVo {

    @NotEmpty(message = "用户名不能为空")
    @Size(min = 4,max = 12,message = "用户名必须在4-12个字符之间")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Size(min = 6,max = 12,message = "密码必须在6-12个字符之间")
    private String password;

    @NotEmpty(message = "手机不能为空")
    @Pattern(regexp = "^[1][3-9][0-9]{9}$",message = "手机格式不正确")
    private String phone;

//    @NotEmpty(message = "验证码不能为空")
//    private String code;

}
