package com.project.gulimallcoupon.coupon.service.impl;

import com.project.gulimallcoupon.coupon.entity.MemberPriceEntity;
import com.project.gulimallcoupon.coupon.entity.SkuLadderEntity;
import com.project.gulimallcoupon.coupon.service.MemberPriceService;
import com.project.gulimallcoupon.coupon.service.SkuLadderService;
import io.renren.common.to.MemberPrice;
import io.renren.common.to.SkuReductionTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallcoupon.coupon.dao.SkuFullReductionDao;
import com.project.gulimallcoupon.coupon.entity.SkuFullReductionEntity;
import com.project.gulimallcoupon.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = {})
    @Override
    public void saveSkuReductionInfo(SkuReductionTo skuReductionTo) {
        //保存阶梯价格信息(满几件打几折)
        if(skuReductionTo.getFullCount()>0){
            SkuLadderEntity skuLadder = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadder);
            skuLadder.setSkuId(skuReductionTo.getSkuId());
            skuLadder.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadder);
        }

        //保存满减信息(满多少减多少)
        if(skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
            SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReduction);
            skuFullReduction.setSkuId(skuReductionTo.getSkuId());
            this.save(skuFullReduction);
        }

        //保存会员价格(不同等级的会员对应不同的价格)
        List<MemberPrice> memberPriceList = skuReductionTo.getMemberPrice();
        if(memberPriceList!=null && memberPriceList.size()>0){
            List<MemberPriceEntity> memberPriceEntityList = memberPriceList.stream().map((memberPrice -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }))
            .filter((memberPriceEntity -> memberPriceEntity.getMemberPrice().compareTo(new BigDecimal("0"))>0))
            .collect(Collectors.toList());

            memberPriceService.saveBatch(memberPriceEntityList);
        }

    }

}