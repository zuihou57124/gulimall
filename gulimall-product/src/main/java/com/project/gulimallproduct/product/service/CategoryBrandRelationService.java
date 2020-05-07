package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.entity.BrandEntity;
import com.project.gulimallproduct.product.vo.BrandVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:08
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandEntity> getBrandRelationList(Long catlogId);
}

