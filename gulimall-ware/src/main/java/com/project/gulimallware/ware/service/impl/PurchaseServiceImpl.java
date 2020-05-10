package com.project.gulimallware.ware.service.impl;

import com.project.gulimallware.ware.entity.PurchaseDetailEntity;
import com.project.gulimallware.ware.entity.WareSkuEntity;
import com.project.gulimallware.ware.feign.SkuInfoFeignService;
import com.project.gulimallware.ware.service.PurchaseDetailService;
import com.project.gulimallware.ware.service.WareSkuService;
import com.project.gulimallware.ware.vo.MergeVo;
import com.project.gulimallware.ware.vo.PurchaseDoneVo;
import io.renren.common.myconst.WareConst;
import io.renren.common.to.SkuTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallware.ware.dao.PurchaseDao;
import com.project.gulimallware.ware.entity.PurchaseEntity;
import com.project.gulimallware.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Autowired
    SkuInfoFeignService skuInfoFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {

        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status",0,1);
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 合并采购需求
     */
    @Transactional(rollbackFor = {})
    @Override
    public void merge(MergeVo mergeVo) {

        PurchaseEntity purchase;
        if(mergeVo.getPurchaseId()==null){
            purchase = new PurchaseEntity();
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConst.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchase);
        }
        else {
            purchase = this.getById(mergeVo.getPurchaseId());
        }

        //采购需求如果正在采购中，那么取消合并
        List<PurchaseDetailEntity> purchaseDetailLsit;
        List<Long> items = mergeVo.getItems();
        purchaseDetailLsit = purchaseDetailService.listByIds(items);
        purchaseDetailLsit =purchaseDetailLsit.stream().filter((purchaseDetailEntity -> {
            return purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.CREATED.getCode() || purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.ASSIGNED.getCode();
        })).collect(Collectors.toList());

        purchaseDetailLsit = purchaseDetailLsit.stream().filter(
                (purchaseDetailEntity -> {

                    if (purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.CREATED.getCode()
                           || purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.ASSIGNED.getCode()){
                        purchaseDetailEntity.setPurchaseId(purchase.getId());
                        purchaseDetailEntity.setStatus(WareConst.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                        return true;
                    }
                    return false;
                }))
            .collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailLsit);
        purchase.setUpdateTime(new Date());
        if(purchaseDetailLsit.size()>0){
            this.updateById(purchase);
        }
    }

    @Transactional(rollbackFor = {})
    @Override
    public void received(List<Long> purchaseIds) {

        purchaseIds = purchaseIds.stream().filter((purchaseId -> {
            //筛选出可以领取的采购单id
            PurchaseEntity purchase = this.getById(purchaseId);
            if(purchase!=null)
            {
                return purchase.getStatus() == WareConst.PurchaseStatusEnum.ASSIGNED.getCode() || purchase.getStatus() == WareConst.PurchaseStatusEnum.CREATED.getCode();
            }
            return false;
        })).collect(Collectors.toList());

        List<PurchaseEntity> purchaseList = purchaseIds.stream().map((purchaseId -> {

            //改变采购单的状态
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.RECEIVED.getCode());
            purchaseEntity.setUpdateTime(new Date());

            //改变采购所属的采购需求的状态
            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseEntity.getId()));
            purchaseDetailEntityList = purchaseDetailEntityList.stream().map((item -> {
                item.setStatus(WareConst.PurchaseDetailStatusEnum.BUYING.getCode());
                return item;
            })).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntityList);

            return purchaseEntity;
        })).collect(Collectors.toList());

        this.updateBatchById(purchaseList);
    }

    @Transactional(rollbackFor = {})
    @Override
    public void purchaseDone(PurchaseDoneVo purchaseDoneVo) {

        // 1.改变采购单的状态 (所有采购项都完成，采购单才能完成)
        Long purchaseId = purchaseDoneVo.getId();

        // 2.改变采购选项的状态
        final Boolean[] isSuccess = {true};
        List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDoneVo.getItems().stream().map((purchaseItemDoneVo -> {
            if (purchaseItemDoneVo.getStatus()==WareConst.PurchaseDetailStatusEnum.ERROR.getCode()){
                isSuccess[0] = false;
            }
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            //purchaseDetailEntity.setPurchaseId(purchaseId);
            //purchaseDetailEntity.setId(purchaseItemDoneVo.getItemId());
            purchaseDetailEntity = purchaseDetailService.getById(purchaseItemDoneVo.getItemId());
            purchaseDetailEntity.setStatus(purchaseItemDoneVo.getStatus());

            if(purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.DONE.getCode()){

                WareSkuEntity wareSkuEntity = new WareSkuEntity();
                Map<String, SkuTo> r = skuInfoFeignService.skuInfo(purchaseDetailEntity.getSkuId());
                if(r.get("skuTo")==null){
                    log.error("sku远程服务调用失败");
                }else {
                    SkuTo skuTo = (SkuTo) r.get("skuTo");
                    wareSkuEntity.setSkuName(skuTo.getSkuName());
                    //设置采购选项的总金额
                    BigDecimal price = skuTo.getPrice();
                    BigDecimal toatl = price.multiply(new BigDecimal(purchaseDetailEntity.getSkuNum()));
                    purchaseDetailEntity.setSkuPrice(toatl);
                    //将采购选项的商品入库
                    wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
                    wareSkuEntity.setWareId(purchaseDetailEntity.getWareId());
                    wareSkuEntity.setStock(purchaseDetailEntity.getSkuNum());
                    wareSkuEntity.setSkuName(skuTo.getSkuName());
                    wareSkuService.addStock(wareSkuEntity);
                }
            }
            return purchaseDetailEntity;
        }))
        .filter((purchaseDetailEntity -> {
            return purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.DONE.getCode()
                   || purchaseDetailEntity.getStatus()==WareConst.PurchaseDetailStatusEnum.ERROR.getCode();
        }))
        .collect(Collectors.toList());
        //改变采购选项的状态
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        //改变采购单的状态
        PurchaseEntity purchaseEntity = this.getById(purchaseId);
        if(isSuccess[0]){
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.DONE.getCode());
        }
        else {
            purchaseEntity.setStatus(WareConst.PurchaseStatusEnum.ERROR.getCode());
        }
        this.updateById(purchaseEntity);

        //3.入库
        /*purchaseDetailEntityList.forEach((purchaseDetailEntity -> {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
            wareSkuEntity.setWareId(purchaseDetailEntity.getWareId());
            wareSkuEntity.setStock(purchaseDetailEntity.getSkuNum());
            wareSkuService.addStock(wareSkuEntity);
            //return wareSkuEntity;
        }));*/

    }

}