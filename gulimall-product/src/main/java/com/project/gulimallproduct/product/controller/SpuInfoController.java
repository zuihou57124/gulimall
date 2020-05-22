package com.project.gulimallproduct.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.project.gulimallproduct.product.feign.CouponFeignService;
import com.project.gulimallproduct.product.vo.SpuSaveVo;
import io.renren.common.to.SpuBoundTo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallproduct.product.entity.SpuInfoEntity;
import com.project.gulimallproduct.product.service.SpuInfoService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * spu信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:07
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    CouponFeignService couponFeignService;

    @PostMapping("/saveTest")
    public R test(@RequestBody SpuBoundTo spuBoundTo){
        couponFeignService.saveSpuBounds(spuBoundTo);
        return R.ok();
    }


    /**
     * 商品上架
     */
    @RequestMapping("/{spuId}/up")
    //@RequiresPermissions("product:spuinfo:list")
    public R spuUp(@PathVariable("spuId") Long spuId){
        //PageUtils page = spuInfoService.queryPage(params);
        spuInfoService.spuUp(spuId);
        return R.ok();
    }


    /**
     * 商品列表信息
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.spuInfoList(params);
        return R.ok().put("page", page);
    }


    /**
     * 通过id查找商品信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存商品信息
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
		//spuInfoService.save(spuSaveVo);
        spuInfoService.saveSpuInfo(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改商品信息
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除商品信息
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
