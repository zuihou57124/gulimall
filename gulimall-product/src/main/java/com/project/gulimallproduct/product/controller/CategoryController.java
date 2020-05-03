package com.project.gulimallproduct.product.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import io.renren.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品三级分类
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //@Resource(name = "OSSClient")
    @Autowired(required = false)
    private OSS ossClient;

    @RequestMapping("/upload")
    public String ossTest() throws Exception {

        ossClient = (OSSClient)ossClient;
    // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\root\\Desktop\\测试图片\\01.jpg");
        ossClient.putObject("qinfengoss", "01.jpg", inputStream);

    // 关闭OSSClient。
        ossClient.shutdown();
        return "yes";
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:category:list")
    public R list(){
        List<CategoryEntity> catelist = categoryService.listWithTree();
        return R.ok().put("data", catelist);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
