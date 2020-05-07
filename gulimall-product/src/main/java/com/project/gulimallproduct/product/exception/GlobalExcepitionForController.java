package com.project.gulimallproduct.product.exception;

import io.renren.common.utils.R;
import io.renren.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

/**
 * @author QCW
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice(basePackages = "com.project.gulimallproduct.product.controller")
public class GlobalExcepitionForController {

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidExecption(MethodArgumentNotValidException e){
        log.error("数据校验出错{},异常{}",e.getMessage(),e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String,Object> map = new HashMap<>();
        if(result.hasErrors()){
            result.getFieldErrors().forEach((error)->{
                String field = error.getField();
                String defaultMessage = error.getDefaultMessage();
                map.put(field,defaultMessage);
            });
        }
        return R.error(ExceptionCode.VALID_EXCEPTION.getCode(),ExceptionCode.VALID_EXCEPTION.getMessage()).put("data",map);
    }

    /*@ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public R handleExecption(Throwable t){
        log.error("全局异常{},异常{}",t.getMessage(),t.getClass());

        return R.error(ExceptionCode.UNKNOW_EXCEPTION.getCode(),ExceptionCode.UNKNOW_EXCEPTION.getMessage());
    }*/

}
