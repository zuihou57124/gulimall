package com.project.gulimallware.ware.service.impl;

import com.project.gulimallware.ware.entity.PurchaseDetailEntity;
import com.project.gulimallware.ware.service.PurchaseDetailService;
import com.project.gulimallware.ware.vo.MergeVo;
import io.renren.common.myconst.WareConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        purchaseDetailLsit = items.stream().map(
                (id-> {
                    PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                    purchaseDetail.setId(id);
                    purchaseDetail.setStatus(WareConst.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    purchaseDetail.setPurchaseId(purchase.getId());
                    return  purchaseDetail;
                }))
                .collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailLsit);
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);

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

}