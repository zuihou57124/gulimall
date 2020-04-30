package com.project.gulimallmember.member.feign;

import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Arrays;

/**
 * @author qcw
 */
@FeignClient("gulimall-coupon-8000")
public interface CouponFeign {

    /**
     * @return 远程调用coupon服务，获取会员的所有优惠券
     */
    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();

}
