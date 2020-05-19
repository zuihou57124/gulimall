package com.project.gulimallproduct.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.project.gulimallproduct.product.entity.*;
import com.project.gulimallproduct.product.feign.CouponFeignService;
import com.project.gulimallproduct.product.feign.EsFeignService;
import com.project.gulimallproduct.product.feign.WareFeignService;
import com.project.gulimallproduct.product.service.*;
import com.project.gulimallproduct.product.vo.BaseAttrs;
import com.project.gulimallproduct.product.vo.Bounds;
import com.project.gulimallproduct.product.vo.Skus;
import com.project.gulimallproduct.product.vo.SpuSaveVo;
import io.renren.common.myconst.ProductConst;
import io.renren.common.to.SkuHasStockTo;
import io.renren.common.to.SkuReductionTo;
import io.renren.common.to.SpuBoundTo;
import io.renren.common.to.es.SkuEsModel;
import io.renren.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.SpuInfoDao;
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

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    EsFeignService esFeignService;

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

    @Override
    public void spuUp(Long spuId) {

        List<SkuEsModel> skuEsModelList = new ArrayList<>();

        //查询出当前sku的可检索的规格参数
        List<ProductAttrValueEntity> spuAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<SkuEsModel.Attrs> attrs = spuAttrs.stream().filter((attr -> {
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            return attrEntity != null && attrEntity.getSearchType() == 1;
        }))
            .map((productAttr -> {
                SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
                attr.setAttrId(productAttr.getAttrId());
                attr.setAttrName(productAttr.getAttrName());
                attr.setAttrValue(productAttr.getAttrValue());
                return attr;
            }))
            .collect(Collectors.toList());
        
        //查询出spu对应所有的sku信息
        List<SkuInfoEntity> skuInfoList = skuInfoService.getSkusBySpuId(spuId);

        //远程调用库存系统，判断是否有库存
        List<Long> skuIds =skuInfoList.stream().map((SkuInfoEntity::getSkuId)).collect(Collectors.toList());
        Map<Long, Boolean> hasStockMap = null;
        try{
            R r = wareFeignService.hasStock(skuIds);
            hasStockMap = r.getData("brands",new TypeReference<List<SkuHasStockTo>>(){}).stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("远程调用库存服务出现异常: "+e.getMessage());
        }
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        skuEsModelList = skuInfoList.stream().map((skuInfoEntity -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfoEntity,skuEsModel);
            skuEsModel.setSkuPrice(skuInfoEntity.getPrice());
            skuEsModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());

            //刚上架的热度为0
            skuEsModel.setHasScore(0L);
            //分别查询出品牌以及分类的名称
            SpuInfoEntity spuInfo = spuInfoService.getById(spuId);
            CategoryEntity category = categoryService.getById(spuInfo.getCatalogId());
            BrandEntity brand = brandService.getById(spuInfo.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setCatelogName(category.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            //设置sku的规格参数等
            skuEsModel.setAttrs(attrs);
            skuEsModel.setHasStock(finalHasStockMap==null?true:finalHasStockMap.get(skuInfoEntity.getSkuId()));
            return skuEsModel;
        })).collect(Collectors.toList());

        //远程调用es服务，上架商品
        R r = esFeignService.productSave(skuEsModelList);
        if(r.getCode()!=0){
            System.out.println("es服务远程调用失败");
        }
        //上架成功后修改spu的状态
        else {
            this.baseMapper.updateSpuStatus(spuId, ProductConst.ProductStatus.UP.getCode());
        }
    }

}