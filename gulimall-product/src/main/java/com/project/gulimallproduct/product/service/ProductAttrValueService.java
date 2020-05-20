package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.vo.BaseAttrs;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.ProductAttrValueEntity;
import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:07
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBaseAttrs(Long id, List<BaseAttrs> baseAttrs);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    void updateAttrListForSpu(Long spuId, List<ProductAttrValueEntity> list);

    ProductAttrValueEntity getAttrForSpuIdAndAttrId(Long spuId,Long attrId);
}

