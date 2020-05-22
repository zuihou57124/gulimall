package com.project.gulimallthirdparty.controller;

import com.project.gulimallthirdparty.component.SmsComponent;
import io.renren.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author root
 */
@Controller
@RequestMapping("sms")
public class SmsSendCodeController {

    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){

        System.out.println("即将发送验证码");
        smsComponent.sendCode(phone,code);
        return R.ok();
    }

}
