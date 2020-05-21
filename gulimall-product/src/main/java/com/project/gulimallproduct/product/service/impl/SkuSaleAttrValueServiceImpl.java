package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.entity.SkuInfoEntity;
import com.project.gulimallproduct.product.service.AttrService;
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

    @Autowired
    AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * @param spuId spuid
     */
    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId, Long catelogid) {
        List<SkuItemVo.SkuItemSaleAttrVo> skuItemSaleAttrVos;

        //查找出spu所有的销售属性
        List<AttrEntity> saleAttrs = attrService.list(new QueryWrapper<AttrEntity>().eq("catelog_id", catelogid).eq("attr_type",0));
        skuItemSaleAttrVos = saleAttrs.stream().map((attrEntity -> {
            //设置销售属性的id以及名称
            SkuItemVo.SkuItemSaleAttrVo skuItemSaleAttrVo = new SkuItemVo.SkuItemSaleAttrVo();
            skuItemSaleAttrVo.setAttrId(attrEntity.getAttrId());
            skuItemSaleAttrVo.setAttrName(attrEntity.getAttrName());
            //设置销售属性的值(列表)
            return skuItemSaleAttrVo;
        })).collect(Collectors.toList());

        //首先根据spuid查找出所有sku
        List<SkuInfoEntity> skuList = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        List<SkuSaleAttrValueEntity> skuSaleAttrValueList = new ArrayList<>();
        //收集每种属性存在的属性值
        skuItemSaleAttrVos = skuItemSaleAttrVos.stream().map((saleAttr -> {
            List<SkuItemVo.SaleAttrWithSkuId> attrValues = new ArrayList<>();
            for (SkuInfoEntity skuInfoEntity : skuList) {
                SkuItemVo.SaleAttrWithSkuId saleAttrWithSkuId = new SkuItemVo.SaleAttrWithSkuId();
                SkuSaleAttrValueEntity skuSaleAttrValue = this.getOne(new QueryWrapper<SkuSaleAttrValueEntity>().eq("attr_id", saleAttr.getAttrId()).eq("sku_id", skuInfoEntity.getSkuId()));
                if(skuSaleAttrValue!=null){
                    skuSaleAttrValueList.add(skuSaleAttrValue);
                    saleAttrWithSkuId.setAttrValue(skuSaleAttrValue.getAttrValue());
                    attrValues.add(saleAttrWithSkuId);
                }
                System.out.println(saleAttr.getAttrName()+"："+attrValues);
            }
            attrValues = attrValues.stream().distinct().collect(Collectors.toList());
            saleAttr.setAttrValues2(attrValues);

            return saleAttr;
        })).collect(Collectors.toList());



        //收集每种属性的值存在于多少种sku中
        for (int i = 0; i < skuItemSaleAttrVos.size(); i++) {
            SkuItemVo.SkuItemSaleAttrVo saleAttrVo = skuItemSaleAttrVos.get(i);
            List<SkuItemVo.SaleAttrWithSkuId> attrValues2 = saleAttrVo.getAttrValues2();

            for (int k = 0; k < attrValues2.size(); k++) {
                SkuItemVo.SaleAttrWithSkuId saleAttrWithSkuId = attrValues2.get(k);

                for (int j = 0; j < skuSaleAttrValueList.size(); j++) {
                    SkuSaleAttrValueEntity skuSaleAttrValue = skuSaleAttrValueList.get(j);
                    if(skuSaleAttrValue.getAttrId().equals(saleAttrVo.getAttrId())&&skuSaleAttrValue.getAttrValue().equals(saleAttrWithSkuId.getAttrValue())){
                        if("".equals(saleAttrWithSkuId.getSkuIds())){
                            saleAttrWithSkuId.setSkuIds(skuSaleAttrValue.getSkuId().toString());
                        }
                        else {
                            saleAttrWithSkuId.setSkuIds(saleAttrWithSkuId.getSkuIds()+","+skuSaleAttrValue.getSkuId());
                        }

                    }
                }
            attrValues2.set(k,saleAttrWithSkuId);
            }
            saleAttrVo.setAttrValues2(attrValues2);
            skuItemSaleAttrVos.set(i,saleAttrVo);
        }



        return skuItemSaleAttrVos;
        
/*        skuList.stream().map((skuInfoEntity -> {
            //查找出sku所有的销售属性
            List<SkuSaleAttrValueEntity> skuSaleAttrValueList = this.list(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuInfoEntity.getSkuId()));
            skuSaleAttrValueList.stream().map((skuSaleAttr -> {
                return skuSaleAttr.getAttrValue();
            })).collect(Collectors.toList());
            return null;
        })).collect(Collectors.toList());*/

    }
}