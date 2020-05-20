package com.project.gulimallproduct.product.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import com.project.gulimallproduct.product.service.SkuImagesService;
import com.project.gulimallproduct.product.service.SkuInfoService;
import com.project.gulimallproduct.product.service.SpuInfoService;
import com.project.gulimallproduct.product.vo.Catelog2Vo;
import com.project.gulimallproduct.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author qcw
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuInfoService skuInfoService;

    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatelogJson(){

        Map<String, List<Catelog2Vo>> catelog2Json = categoryService.getCatelog2Json();

        return catelog2Json;
    }


    @RequestMapping({"/","index.html"})
    public String index(Model model){

        List<CategoryEntity> categoryList = categoryService.getCategorys1();
        model.addAttribute("categorys",categoryList);
        return "index";
    }

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId,Model model){

        SkuItemVo item = skuInfoService.getItem(skuId);
        model.addAttribute("item",item);
        return "item";
    }

}
