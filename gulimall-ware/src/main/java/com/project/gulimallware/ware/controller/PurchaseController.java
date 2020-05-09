package com.project.gulimallware.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.project.gulimallware.ware.vo.MergeVo;
import com.project.gulimallware.ware.vo.PurchaseDoneVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallware.ware.entity.PurchaseEntity;
import com.project.gulimallware.ware.service.PurchaseService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 采购信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:27:07
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 员工完成采购单
     */
    @PostMapping("/done")
    //@RequiresPermissions("ware:purchase:list")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo) {
        //PageUtils page = purchaseService.queryPage(params);
        purchaseService.purchaseDone(purchaseDoneVo);
        return R.ok();
    }


    /**
     * 员工领取采购单
     */
    @PostMapping("/received")
    //@RequiresPermissions("ware:purchase:list")
    public R received(@RequestBody List<Long> purchaseIds) {
        //PageUtils page = purchaseService.queryPage(params);
        purchaseService.received(purchaseIds);
        return R.ok();
    }

    /**
     * 合并采购需求
     */
    @PostMapping("/merge")
    //@RequiresPermissions("ware:purchase:list")
    public R merge(@RequestBody MergeVo mergeVo) {
        //PageUtils page = purchaseService.queryPage(params);
        purchaseService.merge(mergeVo);
        return R.ok();

    }
    /**
     * 查询还未领取的采购单列表
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        //PageUtils page = purchaseService.queryPage(params);
        PageUtils page = purchaseService.queryUnreceivePage(params);
        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
