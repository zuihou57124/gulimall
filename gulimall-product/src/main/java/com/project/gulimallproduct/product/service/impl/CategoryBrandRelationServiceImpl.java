package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.dao.BrandDao;
import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.service.BrandService;
import com.project.gulimallproduct.product.vo.BrandVo;
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

import com.project.gulimallproduct.product.dao.CategoryBrandRelationDao;
import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;
import com.project.gulimallproduct.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired(required = false)
    BrandDao brandDao;

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        String brandName = brandDao.selectById(categoryBrandRelation.getBrandId()).getName();
        String catelogName = categoryDao.selectById(categoryBrandRelation.getCatelogId()).getName();

        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(catelogName);
        this.save(categoryBrandRelation);

    }

    /**
     * @param catlogId 分类id
     * 获取某个分类下的所有品牌
     */
    @Override
    public List<BrandEntity> getBrandRelationList(Long catlogId) {

        List<CategoryBrandRelationEntity> relationList = this.baseMapper.selectList(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catlogId));
        List<BrandEntity> brandList = relationList.stream().map(
                (relation -> {
                    BrandEntity brand = brandService.getById(relation.getBrandId());
                    return brand;
                })
        ).collect(Collectors.toList());

        return brandList;
    }


}