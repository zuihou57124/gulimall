package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.BrandEntity;
import java.util.Map;

/**
 * 品牌
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:09
 */

public interface BrandService extends IService<BrandEntity> {

    /**
     * @param params 参数
     * @return  列表
     */
    PageUtils queryPage(Map<String, Object> params);

    void updateRelation(BrandEntity brand);
}

