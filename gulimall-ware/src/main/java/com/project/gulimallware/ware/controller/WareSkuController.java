package com.project.gulimallware.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.project.gulimallware.ware.vo.SkuHasStockVo;
import io.renren.common.to.SkuHasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallware.ware.entity.WareSkuEntity;
import com.project.gulimallware.ware.service.WareSkuService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 商品库存
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:27:07
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/hasstock")
    //@RequiresPermissions("ware:waresku:list")
    public R hasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockTo> skuHasStockToList = wareSkuService.getSkuHasStock(skuIds);
        R r = R.ok();
        //List<SkuHasStockTo> list = (List<SkuHasStockTo>) r.get("data");
        return r.setData(skuHasStockToList);
    }


    /**
     * 库存列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
