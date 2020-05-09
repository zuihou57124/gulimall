package com.project.gulimallware.ware.feign;

import io.renren.common.to.SkuTo;
import io.renren.common.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author qcw
 * sku的远程服务接口
 */
@FeignClient(value = "gulimall-product-11000")
public interface SkuInfoFeignService {

    /**
     * 获取sku的信息
     * @param skuId 参数id
     */
    @RequestMapping("/product/skuinfo/info")
    public Map<String, SkuTo> skuInfo(@RequestParam("skuId") Long skuId);

}
