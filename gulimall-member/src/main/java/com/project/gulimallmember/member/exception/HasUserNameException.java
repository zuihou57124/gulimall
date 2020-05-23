package com.project.gulimallmember.member.exception;

/**
 * @author qcw
 *
 */
public class HasUserNameException extends RuntimeException{

    public HasUserNameException() {
        super("用户名已存在");
    }
}
