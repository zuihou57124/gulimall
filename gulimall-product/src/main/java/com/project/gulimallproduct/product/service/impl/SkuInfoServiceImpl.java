package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.SkuImagesEntity;
import com.project.gulimallproduct.product.entity.SkuSaleAttrValueEntity;
import com.project.gulimallproduct.product.entity.SpuInfoEntity;
import com.project.gulimallproduct.product.feign.CouponFeignService;
import com.project.gulimallproduct.product.service.SkuImagesService;
import com.project.gulimallproduct.product.service.SkuSaleAttrValueService;
import com.project.gulimallproduct.product.vo.Attr;
import com.project.gulimallproduct.product.vo.Images;
import com.project.gulimallproduct.product.vo.Skus;
import io.renren.common.to.SkuReductionTo;
import io.renren.common.utils.R;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
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

import com.project.gulimallproduct.product.dao.SkuInfoDao;
import com.project.gulimallproduct.product.entity.SkuInfoEntity;
import com.project.gulimallproduct.product.service.SkuInfoService;


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

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
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
                skuInfoService.save(skuInfo);

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
                if(skuReductionTo.getFullCount()>0 && skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0){
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r.getCode()!=0){
                        log.error("sku 优惠满减信息服务远程调用失败");
                    }
                }

                return skuInfo;
            })).collect(Collectors.toList());

            this.saveBatch(skuInfoList);

    }
}