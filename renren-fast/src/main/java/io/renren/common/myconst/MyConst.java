package io.renren.common.myconst;


/**
 * @author qcw
 */
public class MyConst {


    /**
     * 用户登录与注册异常枚举类
     */
    public enum MemberEnum{
        HAS_USER_EXCEPTION(20001,"用户名已存在"),
        HAS_PHONE_EXCEPTION(20002,"手机号码已存在"),
        USER_LOGIN_EXCEPTION(20003,"用户名或密码错误");

        private  int code;
        private  String msg;

        MemberEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

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
    }

}
