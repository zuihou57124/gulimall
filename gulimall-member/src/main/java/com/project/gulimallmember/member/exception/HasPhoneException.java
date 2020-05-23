package com.project.gulimallmember.member.exception;

/**
 * @author qcw
 */
public class HasPhoneException extends RuntimeException{

    public HasPhoneException() {
        super("手机号已存在");
    }
}
