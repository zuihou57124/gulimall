package com.project.gulimallproduct.product.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.gulimallproduct.product.dao.AttrAttrgroupRelationDao;
import com.project.gulimallproduct.product.dao.AttrDao;
import com.project.gulimallproduct.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.util.StringUtils;


/**
 * @author qcw
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired(required = false)
    AttrAttrgroupRelationDao relationDao;

    @Autowired(required = false)
    AttrDao attrDao;

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
}