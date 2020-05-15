package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.vo.Catelog2Vo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void updateDetail(CategoryEntity category);

    Map<String,List<Catelog2Vo>> getCatelog2Json();

    List<CategoryEntity> getCategorys1();
}

