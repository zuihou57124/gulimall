package com.project.gulimallauth.controller;

import com.project.gulimallauth.constant.AuthConst;
import com.project.gulimallauth.feign.ThirdPartyFeignService;
import io.renren.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qcw
 */
@Controller
public class LoginController {


    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){

        String redisCode = (String) redisTemplate.opsForValue().get(AuthConst.SMS_CODE_PREFIX+phone);
        if(!StringUtils.isEmpty(redisCode)){
            long lastTime = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-lastTime<=60000){
                return R.error(AuthConst.SmsEnum.SMS_SEND_NOTIME.getCode(),AuthConst.SmsEnum.SMS_SEND_NOTIME.getMsg());
            }
        }

        //1.接口防刷
        //2.判断验证码是否正确  redis

        String code = UUID.randomUUID().toString().substring(0,4);
        thirdPartyFeignService.sendCode(phone,code);
        redisTemplate.opsForValue().set(AuthConst.SMS_CODE_PREFIX+phone+"_"+System.currentTimeMillis(),code,5, TimeUnit.MINUTES);
        return R.ok();
    }

}
