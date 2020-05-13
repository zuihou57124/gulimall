package com.project.gulimallproduct.product.feign;

import io.renren.common.to.es.SkuEsModel;
import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author qcw
 * es远程服务调用
 */
@FeignClient("gulimall-es-14000")
public interface EsFeignService {

    @PostMapping("/es/productSave")
    R productSave(@RequestBody List<SkuEsModel> skuEsModels);

}
