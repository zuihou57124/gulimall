package com.project.gulimallauth.feign;

import com.project.gulimallauth.vo.LoginVo;
import com.project.gulimallauth.vo.RegisterVo;
import com.project.gulimallauth.vo.SocialUserVo;
import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author qcw
 * 会员远程服务
 */
@FeignClient("gulimall-member-9000")
public interface MemberFeignService {

    @PostMapping("/member/member/regsiter")
    R regsiter(@RequestBody RegisterVo registerVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody LoginVo loginVo);

    @PostMapping("/auth/login")
    R socialLogin(@RequestBody SocialUserVo socialUserVo);

}
