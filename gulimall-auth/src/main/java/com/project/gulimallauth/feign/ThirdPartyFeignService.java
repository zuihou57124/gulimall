package com.project.gulimallauth.feign;

import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qcw
 */
@FeignClient("gulimall-third-party-13000")
public interface ThirdPartyFeignService {

    @RequestMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
