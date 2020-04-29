package com.project.gulimallproduct.product.dao;

import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
