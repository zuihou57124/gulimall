package com.project.gulimalles.controller;


import com.project.gulimalles.service.ProductSaveService;
import io.renren.common.to.es.SkuEsModel;
import io.renren.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("es")
public class EsSaveProductController {

    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/productSave")
    public R productSave(@RequestBody List<SkuEsModel> skuEsModels) throws Exception {

        Boolean hasError = productSaveService.productSave(skuEsModels);
        if(hasError){
            return R.error(400,"商品上架出现错误");
        }
        return R.ok();
    }

}
