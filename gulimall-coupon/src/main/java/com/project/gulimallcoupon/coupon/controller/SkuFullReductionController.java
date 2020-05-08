package com.project.gulimallcoupon.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.project.gulimallcoupon.coupon.service.SkuLadderService;
import io.renren.common.to.SkuReductionTo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gulimallcoupon.coupon.entity.SkuFullReductionEntity;
import com.project.gulimallcoupon.coupon.service.SkuFullReductionService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 商品满减信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:12:20
 */
@RestController
@RequestMapping("coupon/skufullreduction")
public class SkuFullReductionController {
    @Autowired
    SkuFullReductionService skuFullReductionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:skufullreduction:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuFullReductionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:skufullreduction:info")
    public R info(@PathVariable("id") Long id){
		SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return R.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存sku的优惠,满减等信息
     */
    @RequestMapping("/saveSkuReductionInfo")
    //@RequiresPermissions("coupon:skufullreduction:save")
    public R saveSkuReductionInfo(@RequestBody SkuReductionTo skuReductionTo){
        skuFullReductionService.saveSkuReductionInfo(skuReductionTo);

        return R.ok();
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:skufullreduction:save")
    public R save(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.save(skuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:skufullreduction:update")
    public R update(@RequestBody SkuFullReductionEntity skuFullReduction){
		skuFullReductionService.updateById(skuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:skufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
		skuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
