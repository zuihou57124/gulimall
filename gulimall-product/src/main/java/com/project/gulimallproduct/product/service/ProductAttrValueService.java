package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.ProductAttrValueEntity;

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
}
