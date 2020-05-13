package com.project.gulimallproduct.product.feign;


import io.renren.common.to.SkuHasStockTo;
import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author qcw
 * 库存系统服务远程调用
 */
@FeignClient("gulimall-ware-12000")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R hasStock(@RequestBody List<Long> skuIds);

}
