package com.project.gulimallauth.constant;

import lombok.Data;

/**
 * @author qcw
 */
public class AuthConst {

    public static String SMS_CODE_PREFIX = "sms:code";

    public enum SmsEnum{

        SMS_SEND_NOTIME(701,"发送频率过高"),
        SMS_SEND_PHONENUM_NULL(702,"请输入手机号");

        int code;

        String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        SmsEnum(int code, String msg) {

            this.code = code;
            this.msg = msg;

        }
    }

}
