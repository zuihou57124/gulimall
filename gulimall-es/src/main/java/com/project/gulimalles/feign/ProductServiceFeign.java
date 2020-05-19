package com.project.gulimalles.feign;

import io.renren.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qcw
 */
@FeignClient("gulimall-product-11000")
public interface ProductServiceFeign {

    @RequestMapping("product/attr/info/{attrId}")
    R getAttrInfo(@PathVariable("attrId") Long attrId);

    @RequestMapping("product/category/info/{catId}")
    R getCategoryInfo(@PathVariable("catId") Long catId);

    @RequestMapping("product/brand/infos")
    R getBrandInfos(@RequestParam("brandIds") List<Long> brandIds);
}
