package com.project.gulimallproduct.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.vo.BrandVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;
import com.project.gulimallproduct.product.service.CategoryBrandRelationService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:08
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 获取某个分类下的所有品牌
     */
    @RequestMapping("/brands/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R brandsList(@RequestParam("catId") Long catlogId){

        List<BrandEntity> brandList = categoryBrandRelationService.getBrandRelationList(catlogId);
        List<BrandVo> brandVoList;
        brandVoList = brandList.stream().map(
                (brand->{
                    BrandVo brandVo = new BrandVo();
                    brandVo.setBrandId(brand.getBrandId());
                    brandVo.setBrandName(brand.getName());
                    return brandVo;
                })
        ).collect(Collectors.toList());
        return R.ok().put("data", brandVoList);
    }


    /**
     * 获取某个品牌关联的所有分类
     */
    @RequestMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(Long brandId){
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>()
                        .eq("brand_id",brandId));

        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		//categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
