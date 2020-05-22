package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.*;
import com.project.gulimallproduct.product.feign.CouponFeignService;
import com.project.gulimallproduct.product.service.*;
import com.project.gulimallproduct.product.vo.Attr;
import com.project.gulimallproduct.product.vo.Images;
import com.project.gulimallproduct.product.vo.SkuItemVo;
import com.project.gulimallproduct.product.vo.Skus;
import io.renren.common.to.SkuReductionTo;
import io.renren.common.utils.R;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.SkuInfoDao;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired(required = false)
    ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper->{
                wrapper.eq("sku_id",key)
                        .or()
                        .like("sku_name",key);
            }));
        }

        if(!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        if(!StringUtils.isEmpty(brandId) && !"0".equals(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }

        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        if(!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.valueOf(0))>0){
            queryWrapper.le("price",max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkusInfo(SpuInfoEntity spuInfo, List<Skus> skus) {

            List<SkuInfoEntity> skuInfoList = skus.stream().map((sku -> {
                //sku的图片信息
                List<Images> skuImages = sku.getImages();
                String dftImg = "";
                for (Images img:skuImages){
                    if(img.getDefaultImg()==1){
                        dftImg = img.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfo = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setSpuId(spuInfo.getId());
                skuInfo.setBrandId(spuInfo.getBrandId());
                skuInfo.setCatalogId(spuInfo.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSkuDefaultImg(dftImg);
                System.out.println("保存了:====>"+skuInfo);
                skuInfoService.save(skuInfo);
                //this.baseMapper.insert(skuInfo);
                //保存sku的图片信息
                List<SkuImagesEntity> skuImageList = skuImages.stream().map((skuImageVo -> {
                            SkuImagesEntity skuImage = new SkuImagesEntity();
                            skuImage.setSkuId(skuInfo.getSkuId());
                            skuImage.setImgUrl(skuImageVo.getImgUrl());
                            skuImage.setDefaultImg(skuImageVo.getDefaultImg());
                            return skuImage;
                        })
                )//如果图片地址不存在，过滤掉
                .filter((skuImage-> !StringUtils.isEmpty(skuImage.getImgUrl())))
                .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImageList);

                //保存sku的销售属性信息
                List<Attr> saleAttrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrs.stream().map(
                        (saleAttr -> {
                            SkuSaleAttrValueEntity skuSaleAttrEntity = new SkuSaleAttrValueEntity();
                            BeanUtils.copyProperties(saleAttr, skuSaleAttrEntity);
                            skuSaleAttrEntity.setSkuId(skuInfo.getSkuId());
                            return skuSaleAttrEntity;
                        })
                ).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.4 sku 的优惠,满减信息  跨库跨表
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(skuInfo.getSkuId());
                //满减限制,如果小于0，不调用远程服务
                if(skuReductionTo.getFullCount()>0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()!=0){
                        log.error("sku 优惠满减信息服务远程调用失败");
                    }
                }

                return skuInfo;
            })).collect(Collectors.toList());

            //this.saveBatch(skuInfoList);

    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    /**
     * @param skuId skuid
     * @return sku详情
     * 使用异步编排
     */
    @Override
    @Cacheable(value = "item",key = "#root.args[0]")
    public SkuItemVo getItem(Long skuId) {

        SkuItemVo skuItemVo = new SkuItemVo();
        //1.sku基本信息
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
            skuItemVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            Long spuId = res.getSpuId();
            //3.spu销售属性组合
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttrs = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId, res.getCatalogId());
            skuItemVo.setSaleAttrVos(saleAttrs);
        }, executor);

        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync((res)->{
            //4.spu介绍(描述)
            skuItemVo.setSpuInfoDesc(spuInfoDescService.getById(res.getSpuId()));
        },executor);

        CompletableFuture<Void> baseAttrFuture = skuInfoFuture.thenAcceptAsync((res)->{
            //5.spu规格参数信息
            List<SkuItemVo.SpuItemBaseAttrVo> skuItemBaseAttrList = attrGroupService.getGroupAttrsBySpuId(res.getSpuId(),res.getCatalogId());
            skuItemVo.setGroupAttrs(skuItemBaseAttrList);
        },executor);

         //2.sku图片信息
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            skuItemVo.setSkuImages(skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId)));
        }, executor);

        try {
            CompletableFuture.allOf(baseAttrFuture,saleAttrFuture,descFuture,imageFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("获取sku详情出错");
        }

        return skuItemVo;
    }
}