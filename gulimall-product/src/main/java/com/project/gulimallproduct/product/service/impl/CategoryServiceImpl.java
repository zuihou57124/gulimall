package com.project.gulimallproduct.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.project.gulimallproduct.product.dao.CategoryBrandRelationDao;
import com.project.gulimallproduct.product.entity.CategoryBrandRelationEntity;
import com.project.gulimallproduct.product.service.CategoryBrandRelationService;
import com.project.gulimallproduct.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author qcw
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Autowired(required = false)
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


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


    @Transactional(rollbackFor = {})
    @Override
    public void updateDetail(CategoryEntity category) {

        this.updateById(category);
        //分类名不为空说明分类名也要修改，其他关联表也要同步修改
        if(!StringUtils.isEmpty(category.getName())){
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setCatelogId(category.getCatId());
            categoryBrandRelationEntity.setCatelogName(category.getName());
            categoryBrandRelationDao.update(categoryBrandRelationEntity
                    ,new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id",category.getCatId())
            );
        }

    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatelog2Json() {

        Map<String, List<Catelog2Vo>> catelog2JsonMap = new HashMap<>();
        String catelog2Json = stringRedisTemplate.opsForValue().get("catelog2Json");
        if(StringUtils.isEmpty(catelog2Json)){
            catelog2JsonMap = getCatelog2JsonFromdb();
            stringRedisTemplate.opsForValue().set("catelog2Json", JSON.toJSONString(catelog2JsonMap));
        }else {
            catelog2JsonMap = JSON.parseObject(catelog2Json,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        }
        return catelog2JsonMap;
    }

    public Map<String, List<Catelog2Vo>> getCatelog2JsonFromdb() {

        //直接查询所有分类，然后从流中筛选，避免频繁操作数据库
        List<CategoryEntity> allCategoryList = this.list(null);

        List<CategoryEntity> category1List = getChilds(allCategoryList,0L);
        Map<String, List<Catelog2Vo>> catelogListMap = category1List.stream().collect(Collectors.toMap((categoryEntity -> categoryEntity.getCatId().toString()), (categoryEntity -> {
            //设置二级分类
            List<CategoryEntity> catelog2List = getChilds(allCategoryList,categoryEntity.getCatId());
            List<Catelog2Vo> catelog2VoList = null;
            if (catelog2List != null) {
                catelog2VoList = catelog2List.stream().map((item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setCatelog1Id(categoryEntity.getCatId().toString());
                    catelog2Vo.setId(item.getCatId().toString());
                    catelog2Vo.setName(item.getName());
                    //设置三级分类
                    List<CategoryEntity> category3List = getChilds(allCategoryList,item.getCatId());
                    List<Catelog2Vo.Catalog3> catelog3List = null;
                    if (category3List != null) {
                        catelog3List = category3List.stream().map((category3 -> {
                            Catelog2Vo.Catalog3 catelog3 = new Catelog2Vo.Catalog3();
                            catelog3.setCatalog2Id(item.getCatId().toString());
                            catelog3.setId(category3.getCatId().toString());
                            catelog3.setName(category3.getName());
                            return catelog3;
                        })).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3List);

                    return catelog2Vo;
                })).collect(Collectors.toList());
            }
            return catelog2VoList;
        })));

        return catelogListMap;
    }

    //获取所有子分类
    private List<CategoryEntity> getChilds(List<CategoryEntity> list,Long parent_cid) {
        list = list.stream().filter((item-> item.getParentCid()==parent_cid)).collect(Collectors.toList());
        return list;
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