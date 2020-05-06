package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.vo.AttrRespVo;
import com.project.gulimallproduct.product.vo.AttrVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.AttrEntity;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils baseQueryPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrVo(Long attrId);

    void updateAttr(AttrVo attrVo);

    void removeAttr(List<Long> asList);

}

