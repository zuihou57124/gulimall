package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.vo.AttrAndGroupVo;
import com.project.gulimallproduct.product.vo.AttrGroupRelationVo;
import com.project.gulimallproduct.product.vo.SkuItemVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params,Long catlogId);

    PageUtils getAttrRelation(Map<String, Object> params, Long attrgroupId);

    void removeAttrRelation(AttrGroupRelationVo[] asList);

    PageUtils getAttrNoRelation(Map<String, Object> params, Long attrgroupId);

    void saveRelation(List<AttrGroupRelationVo> attrVo);

    List<AttrAndGroupVo> getAttrList(Map<String, Object> params, Long catelogId);

    List<SkuItemVo.SpuItemBaseAttrVo> getGroupAttrsBySpuId(Long spuId,Long catelogId);
}

