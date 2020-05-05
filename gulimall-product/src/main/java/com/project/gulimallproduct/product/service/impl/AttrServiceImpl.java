package com.project.gulimallproduct.product.service.impl;

import com.project.gulimallproduct.product.dao.AttrAttrgroupRelationDao;
import com.project.gulimallproduct.product.dao.AttrGroupDao;
import com.project.gulimallproduct.product.dao.CategoryDao;
import com.project.gulimallproduct.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimallproduct.product.entity.AttrGroupEntity;
import com.project.gulimallproduct.product.entity.CategoryEntity;
import com.project.gulimallproduct.product.vo.AttrRespVo;
import com.project.gulimallproduct.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

import com.project.gulimallproduct.product.dao.AttrDao;
import com.project.gulimallproduct.product.entity.AttrEntity;
import com.project.gulimallproduct.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired(required = false)
    AttrAttrgroupRelationDao attrGroupRelationDao;

    @Autowired(required = false)
    AttrGroupDao attrGroupDao;

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }


    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        //保存属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.save(attrEntity);
        //保存属性与属性分组之间的联系
        AttrAttrgroupRelationEntity attrGroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrGroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrGroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrGroupRelationDao.insert(attrGroupRelationEntity);
    }

    @Override
    public PageUtils baseQueryPage(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.eq("attr_id",key)
                        .or()
                        .like("attr_name",key);
        }

        PageUtils pageUtils = null;
        IPage<AttrEntity> page = null;

        if(catelogId==0){
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper
            );
        }
        else {
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper.eq("catelog_id",catelogId)
            );
        }
        List<AttrEntity> attrEntityList = page.getRecords();
        List<AttrRespVo> attrRespVoList = attrEntityList.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //获取属性属于哪个属性分组id
            AttrAttrgroupRelationEntity attrGroupRelationEntity =
                    attrGroupRelationDao.selectOne
                            (new QueryWrapper<AttrAttrgroupRelationEntity>()
                                    .eq("attr_id", attrEntity.getAttrId())
                            );
            //获取属性分组名称
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelationEntity.getAttrGroupId());
            attrRespVo.setAttrGroupId(attrGroupRelationEntity.getAttrGroupId());
            attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());

            //获取属性的分类id
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            attrRespVo.setCatelogName(categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils = new PageUtils(page);
        pageUtils.setList(attrRespVoList);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrVo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        //获取属性属于哪个属性分组
        AttrAttrgroupRelationEntity attrGroupRelationEntity =
                attrGroupRelationDao.selectOne
                        (new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id",attrId)
                        );
        //获取属性分组名称
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelationEntity.getAttrGroupId());

        attrRespVo.setAttrGroupId(attrGroupRelationEntity.getAttrGroupId());
        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
        return attrRespVo;
    }

}