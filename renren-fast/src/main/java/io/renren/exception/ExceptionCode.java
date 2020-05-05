package io.renren.exception;


/**
 * @author qcw
 */

public enum ExceptionCode {

    VALID_EXCEPTION(10000,"参数校验错误"),
    UNKNOW_EXCEPTION(10001,"系统未知错误");

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
