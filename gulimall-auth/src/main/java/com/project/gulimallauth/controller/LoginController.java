package com.project.gulimallauth.controller;

import com.project.gulimallauth.constant.AuthConst;
import com.project.gulimallauth.feign.ThirdPartyFeignService;
import com.project.gulimallauth.vo.RegisterVo;
import io.renren.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qcw
 */
@Controller
public class LoginController {


    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/register")
    public String register(RedirectAttributes model, @Valid RegisterVo registerVo, BindingResult result){
        if(result.hasErrors()){
            //校验出错，返回注册页面，并给出提示
            Map<String, String> resultMap = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
            //model.addAttribute("errors",resultMap);
            model.addFlashAttribute("errors",resultMap);
            // 转发会原封不动的将请求转发，包括请求方式
            return "redirect:http://127.0.0.4/register.html";
        }

        return "login";

    }



    @RequestMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){

        if(StringUtils.isEmpty(phone)){
            return R.error(AuthConst.SmsEnum.SMS_SEND_PHONENUM_NULL.getCode(),AuthConst.SmsEnum.SMS_SEND_PHONENUM_NULL.getMsg());
        }

        String redisCode = (String) redisTemplate.opsForValue().get(AuthConst.SMS_CODE_PREFIX+phone);
        if(!StringUtils.isEmpty(redisCode)){
            System.out.println("验证码是-----"+redisCode.split("_")[0]);
            long lastTime = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-lastTime<=60000){
                return R.error(AuthConst.SmsEnum.SMS_SEND_NOTIME.getCode(),AuthConst.SmsEnum.SMS_SEND_NOTIME.getMsg());
            }
        }

        //1.接口防刷
        //2.判断验证码是否正确  redis

        String code = UUID.randomUUID().toString().substring(0,4)+"_"+System.currentTimeMillis();
        //thirdPartyFeignService.sendCode(phone,code);
        redisTemplate.opsForValue().set(AuthConst.SMS_CODE_PREFIX+phone,code,5, TimeUnit.MINUTES);
        return R.ok();
    }

}
