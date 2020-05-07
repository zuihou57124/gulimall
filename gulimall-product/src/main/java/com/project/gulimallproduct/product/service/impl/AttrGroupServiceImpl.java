package com.project.gulimallproduct.product.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.gulimallproduct.product.dao.AttrAttrgroupRelationDao;
import com.project.gulimallproduct.product.dao.AttrDao;
import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.service.AttrAttrgroupRelationService;
import com.project.gulimallproduct.product.service.AttrService;
import com.project.gulimallproduct.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import com.project.gulimallproduct.product.dao.AttrGroupDao;
import com.project.gulimallproduct.product.entity.AttrGroupEntity;
import com.project.gulimallproduct.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * @author qcw
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired(required = false)
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrAttrgroupRelationService relationService;

    @Autowired(required = false)
    AttrDao attrDao;

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Autowired(required = false)
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((obj)->{
                obj.eq("attr_group_id",key)
                        .or()
                        .like("attr_group_name",key);
            });
        }

        if(catelogId==0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);
            return new PageUtils(page);
        }
        else {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper.eq("catelog_id",catelogId));
            return new PageUtils(page);

        }
    }

    @Override
    public PageUtils getAttrRelation(Map<String, Object> params, Long attrgroupId) {

        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id",attrgroupId);
        List<AttrAttrgroupRelationEntity> attrGroupEntityList = relationDao.selectList(queryWrapper);

        List<Long> attrIdList = attrGroupEntityList.stream().map(
                (AttrAttrgroupRelationEntity::getAttrId)
        ).collect(Collectors.toList());

        IPage<AttrEntity> page;

        if(attrIdList.size() > 0){
            page = attrDao.selectPage(
                    new Query<AttrEntity>().getPage(params),
                    new QueryWrapper<AttrEntity>().in("attr_id",attrIdList));
        }else {
            page = new Page<>();
            page.setRecords(null);
        }

        return new PageUtils(page);
    }


    /**
     * 移除关联信息
     */
    @Transactional
    @Override
    public void removeAttrRelation(AttrGroupRelationVo[] vos) {

        List<AttrGroupRelationVo> attrGroupRelationVos = Arrays.asList(vos);

        relationDao.deleteRelation(attrGroupRelationVos);
    }


    /**
     * 获取未关联的属性列表
     * 1. 没有被关联
     * 2. 属于当前商品分类
     */
    @Override
    public PageUtils getAttrNoRelation(Map<String, Object> params, Long attrgroupId) {

        AttrGroupEntity attrGroup = attrGroupDao.selectById(attrgroupId);
        IPage<AttrEntity> page = new Page<>();
        if(!ObjectUtils.isEmpty(attrGroup)){
            CategoryEntity category = categoryDao.selectById(attrGroup.getCatelogId());
            if(!ObjectUtils.isEmpty(category)){
                //第一步，获取当前商品分类下的全部属性(必须是规格参数，不能是销售属性)

                //然后 找出未关联的属性
                //有两种方法，1.一个个去数据库对比，
                // 2.直接从数据库查出所有，然后进行对比
                // 选择2,减少数据库的压力
                //获取所有已经关联的属性id
                List<Long> relationAttrList = relationDao.selectList(null)
                        .stream()
                        .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

                //List<AttrEntity> attrList = attrDao.selectList(new QueryWrapper<AttrEntity>().eq("catelog_id",category.getCatId())
                     //   .eq("search_type",1).notIn("attr_id",relationAttrList));

                QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id",category.getCatId())
                        .eq("search_type",1);

                if(relationAttrList.size() > 0){
                    wrapper.notIn("attr_id",relationAttrList);
                }
                String key = (String) params.get("key");
                if(!StringUtils.isEmpty(key)){
                    wrapper.and((obj->{
                        obj.eq("attr_id",key)
                           .or()
                           .like("attr_name",key);
                    }));
                }
                page = attrService.page(new Query<AttrEntity>().getPage(params), wrapper);
            }
        }

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = {})
    @Override
    public void saveRelation(List<AttrGroupRelationVo> attrVo) {


        List<AttrAttrgroupRelationEntity> relationEntityList = new ArrayList<>();
        for (AttrGroupRelationVo relationVo:attrVo) {
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(relationVo.getAttrId());
            relation.setAttrGroupId(relationVo.getAttrGroupId());
            relationEntityList.add(relation);
        }
        relationService.saveBatch(relationEntityList);

    }
}