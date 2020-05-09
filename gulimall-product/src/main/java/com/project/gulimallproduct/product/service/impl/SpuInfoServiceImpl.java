package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.SpuInfoDescEntity;
import com.project.gulimallproduct.product.feign.CouponFeignService;
import com.project.gulimallproduct.product.service.*;
import com.project.gulimallproduct.product.vo.BaseAttrs;
import com.project.gulimallproduct.product.vo.Bounds;
import com.project.gulimallproduct.product.vo.Skus;
import com.project.gulimallproduct.product.vo.SpuSaveVo;
import io.renren.common.to.SkuReductionTo;
import io.renren.common.to.SpuBoundTo;
import io.renren.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.SpuInfoDao;
import com.project.gulimallproduct.product.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueServiceImpl productAttrValueService;
    
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
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = {})
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {

        // 1. 保存商品基本信息         pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo,spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.saveSpuBaseInfo(spuInfo);

        // 2. 保存 spu 描述图片       pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfDesc(spuInfoDesc);

        // 3. 保存 spu 图片集         pms_spu_images
        List<String> spuImages = spuSaveVo.getImages();
        if(spuImages!=null && spuImages.size()>0){
            spuImagesService.saveImages(spuInfo.getId(),spuImages);
        }

        // 4. 保存 spu 的规格参数     pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if(baseAttrs!=null && baseAttrs.size()>0){
            productAttrValueService.saveBaseAttrs(spuInfo.getId(),baseAttrs);
        }

        // 保存 spu的积分信息 跨库跨表 + 远程服务调用 sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfo.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0){
            log.error("spu 积分信息服务远程调用失败");
        }

        // 5. 保存 spu 的所有 sku信息
                //5.1  sku的基本信息       pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        skuInfoService.saveSkusInfo(spuInfo,skus);

        //5.2 sku图片信息          pms_sku_images
                //5.3 sku销售属性信息       pms_sku_sale_attr_value
                //5.4 sku 的优惠,满减信息  跨库跨表

    }


    /**
     * 保存spu基本信息
     */
    @Override
    public void saveSpuBaseInfo(SpuInfoEntity spuInfo) {

        this.baseMapper.insert(spuInfo);

    }


    /**
     * @param params 查询条件
     * @return spu列表
     */
    @Override
    public PageUtils spuInfoList(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String status = (String) params.get("status");

        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper->{
                wrapper.eq("id",key)
                        .or()
                        .like("spu_name",key);
            }));
        }

        if(!StringUtils.isEmpty(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        if(!StringUtils.isEmpty(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }

        if(!StringUtils.isEmpty(status)){
            //queryWrapper.eq("publish_status",status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}