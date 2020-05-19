package com.project.gulimallproduct.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.validGroup.Add;
import io.renren.validGroup.Update;
import io.renren.validGroup.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.service.BrandService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;

import javax.validation.Valid;

/**
 * 品牌
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 品牌列表
     */
    @RequestMapping("/infos")
    //@RequiresPermissions("product:brand:info")
    public R info(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id",brandIds));

        return R.ok().put("brands", list);
    }


    /**
     * 保存
     * @Valid 开启校验
     * result 获得校验结果信息
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {Add.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改品牌名时，其他关联表的品牌名也应该同步更新
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {Update.class}) @RequestBody BrandEntity brand){
		//brandService.updateById(brand);
        brandService.updateRelation(brand);
        return R.ok();
    }

    /**
     * 修改显示状态
     */
    @PostMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(value = {UpdateStatus.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
