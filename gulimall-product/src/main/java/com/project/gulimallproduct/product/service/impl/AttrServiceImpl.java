package com.project.gulimallproduct.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

import java.util.*;
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


/**
 * @author qcw
 */
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

    @Transactional(rollbackFor = {})
    @Override
    public void saveAttr(AttrVo attrVo) {
        //保存属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.save(attrEntity);
        //保存属性与属性分组之间的联系(销售属性不用保存)
        if (attrVo.getSearchType()==1){
            if(attrVo.getAttrGroupId()!=null){
                AttrAttrgroupRelationEntity attrGroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrGroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
                attrGroupRelationEntity.setAttrId(attrEntity.getAttrId());
                attrGroupRelationDao.insert(attrGroupRelationEntity);
            }

        }

    }

    @Override
    public PageUtils baseQueryPage(Map<String, Object> params, Long catelogId,String attrType) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

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
                    queryWrapper.eq("attr_type","base".equalsIgnoreCase(attrType)?1:0)
            );
        }
        else {
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    queryWrapper.eq("catelog_id",catelogId)
                                .eq("attr_type","base".equalsIgnoreCase(attrType)?1:0)
            );
        }

        List<AttrRespVo> attrRespVoList;
        List<AttrEntity> attrEntityList = page.getRecords();;

        attrRespVoList = attrEntityList.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //销售属性不需要分组
            if("base".equalsIgnoreCase(attrType)){
                //获取属性属于哪个属性分组id
                AttrAttrgroupRelationEntity attrGroupRelationEntity =
                        attrGroupRelationDao.selectOne
                                (new QueryWrapper<AttrAttrgroupRelationEntity>()
                                        .eq("attr_id", attrEntity.getAttrId())
                                );
                //获取属性分组名称
                if(attrGroupRelationEntity!=null&&attrGroupRelationEntity.getAttrGroupId()!=null&&attrGroupRelationEntity.getAttrGroupId()!=0){
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelationEntity.getAttrGroupId());
                    attrRespVo.setAttrGroupId(attrGroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

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

        //获取属性属于哪个属性分组(销售属性不用)
        if(attrRespVo.getSearchType()==1){
            AttrAttrgroupRelationEntity attrGroupRelationEntity =
                    attrGroupRelationDao.selectOne
                            (new QueryWrapper<AttrAttrgroupRelationEntity>()
                                    .eq("attr_id",attrId)
                            );
            //获取属性分组名称
            if(attrGroupRelationEntity!=null&&attrGroupRelationEntity.getAttrGroupId()!=null&&attrGroupRelationEntity.getAttrGroupId()!=0){
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupRelationEntity.getAttrGroupId());
                attrRespVo.setAttrGroupId(attrGroupRelationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        //获取属性的分类id
        List<Long> catelogPath = new ArrayList<>();
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        attrRespVo.setCatelogName(categoryEntity.getName());
        attrRespVo.setCatelogId(categoryEntity.getCatId());
        //获取分类id的完整路径
        catelogPath.add(categoryEntity.getCatId());
        categoryEntity = categoryDao.selectById(categoryEntity.getParentCid());
        catelogPath.add(categoryEntity.getCatId());
        categoryEntity = categoryDao.selectById(categoryEntity.getParentCid());
        catelogPath.add(categoryEntity.getCatId());
        Collections.reverse(catelogPath);
        attrRespVo.setCatelogPath(catelogPath);

        return attrRespVo;
    }

    @Transactional(rollbackFor = {})
    @Override
    public void updateAttr(AttrVo attrVo) {

        //更新属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        this.updateById(attrEntity);

        //更新属性与属性分组之间的联系(销售属性不用更新)
        if(attrVo.getSearchType()==1){
            AttrAttrgroupRelationEntity attrGroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrGroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrGroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());

            // 如果在属性分组信息中单方面删除关联信息，修改关联信息时需要重新插入关联信息
            AttrAttrgroupRelationEntity relationEntity = attrGroupRelationDao.selectOne(new UpdateWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id",attrEntity.getAttrId()));

            if(relationEntity!=null){
                //如果修改前分组属性存在，修改后分组属性不存在，就删除分组关联
                //,进而防止错误关联
                if(attrGroupRelationEntity.getAttrGroupId()==null){
                    //attrGroupRelationEntity.setAttrGroupId((long) 0);
                    attrGroupRelationDao.delete(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrGroupRelationEntity.getAttrId()));
                }
                attrGroupRelationDao.update(attrGroupRelationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id",attrEntity.getAttrId()));
            }else {
                //如果修改前属性未被关联，判断修改后是否被关联，如果是，创建关联关系
                if(attrGroupRelationEntity.getAttrGroupId()!=null){
                    attrGroupRelationDao.insert(attrGroupRelationEntity);
                }

            }
        }
    }

    /**
     * @param asList 需要删除的属性id列表
     * 如果是基本属性还要连带属性分组关联信息一起删除
     */
    @Transactional(rollbackFor = {})
    @Override
    public void removeAttr(List<Long> asList) {
        this.removeByIds(asList);
        attrGroupRelationDao.delete(new QueryWrapper<AttrAttrgroupRelationEntity>()
            .in("attr_id",asList));
    }

}