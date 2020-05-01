package com.project.gulimallproduct.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;


/**
 * @author qcw
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> allCategory = categoryDao.selectList(null);

        List<CategoryEntity> categoryEntityList = categoryDao.selectList(null);

        categoryEntityList = categoryEntityList.stream()
           .filter(category->category.getParentCid()==0)
           .map(categoryEntity ->{
               categoryEntity.setChildCate(getChild(categoryEntity,allCategory));
               return categoryEntity;
           })
           .sorted((o1,o2)->o1.getSort()-o2.getSort())
           .collect(Collectors.toList());
        return categoryEntityList;
    }


    /**
     * @return 返回菜单的所有子菜单
     */
    public List<CategoryEntity> getChild(CategoryEntity root,List<CategoryEntity> allCate){

          List<CategoryEntity> child = allCate.stream()
                .filter(categoryEntity ->
                categoryEntity.getParentCid()==root.getCatId())
                .map(categoryEntity -> {
                     categoryEntity.setChildCate(getChild(categoryEntity,allCate));
                     return categoryEntity;
                })
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
          return child;
    }

}