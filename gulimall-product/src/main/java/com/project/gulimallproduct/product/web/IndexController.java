package com.project.gulimallproduct.product.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import com.project.gulimallproduct.product.vo.Catelog2Vo;
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

    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatelogJson(){

        Map<String, List<Catelog2Vo>> catelog2Json = categoryService.getCatelog2Json();

        return catelog2Json;
    }


    @RequestMapping({"/","index"})
    public String index(Model model){

        List<CategoryEntity> categoryList = categoryService.list(new QueryWrapper<CategoryEntity>().eq("cat_level",1));
        model.addAttribute("categorys",categoryList);

        return "index";
    }




}
