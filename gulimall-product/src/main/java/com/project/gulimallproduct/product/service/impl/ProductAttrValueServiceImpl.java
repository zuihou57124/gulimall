package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.service.AttrService;
import com.project.gulimallproduct.product.vo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.ProductAttrValueDao;
import com.project.gulimallproduct.product.entity.ProductAttrValueEntity;
import com.project.gulimallproduct.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存spu的规格参数(基本属性)
     * @param id spuId
     * @param baseAttrs 基本属性列表
     */
    @Override
    public void saveBaseAttrs(Long id, List<BaseAttrs> baseAttrs) {

        List<ProductAttrValueEntity> spuAttrVlues = baseAttrs.stream().map((attr -> {
            ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
            productAttrValue.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValue.setAttrName(attrEntity.getAttrName());
            productAttrValue.setAttrValue(attr.getAttrValues());
            productAttrValue.setSpuId(id);
            productAttrValue.setQuickShow(attr.getShowDesc());
            return productAttrValue;
        })).collect(Collectors.toList());

        this.saveBatch(spuAttrVlues);

    }

}