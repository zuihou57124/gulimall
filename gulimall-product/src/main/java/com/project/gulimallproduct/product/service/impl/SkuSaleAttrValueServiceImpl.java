package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.SkuInfoEntity;
import com.project.gulimallproduct.product.service.SkuInfoService;
import com.project.gulimallproduct.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.SkuSaleAttrValueDao;
import com.project.gulimallproduct.product.entity.SkuSaleAttrValueEntity;
import com.project.gulimallproduct.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    SkuInfoService skuInfoService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @param id
     * @param spuId spuid
     */
    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long id, Long spuId) {
        List<SkuItemVo.SkuItemSaleAttrVo> skuItemSaleAttrVos = new ArrayList<>();
        //首先根据spuid查找出所有sku
        List<SkuInfoEntity> skuList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        skuList.stream().map((skuInfoEntity -> {

          SkuItemVo.SkuItemSaleAttrVo skuItemSaleAttrVo = new SkuItemVo.SkuItemSaleAttrVo();
          //查找出sku所有的销售属性
          List<SkuSaleAttrValueEntity> skuSaleAttrValueList = this.list(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuInfoEntity.getSkuId()));

          return skuItemSaleAttrVo;
        })).collect(Collectors.toList());



        return skuItemSaleAttrVos;
    }

}