package com.project.gulimallproduct.product.feign;

import io.renren.common.to.SkuReductionTo;
import io.renren.common.to.SpuBoundTo;
import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qcw
 */
@FeignClient("gulimall-coupon-8000")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveSkuReductionInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
