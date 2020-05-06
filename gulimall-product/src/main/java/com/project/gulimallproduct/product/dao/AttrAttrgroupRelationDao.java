package com.project.gulimallproduct.product.dao;

import com.project.gulimallproduct.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.gulimallproduct.product.vo.AttrGroupRelationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteRelation(@Param("attrGroupRelation") List<AttrGroupRelationVo> attrGroupRelationVos);
}
