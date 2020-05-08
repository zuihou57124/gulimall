package com.project.gulimallcoupon.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.to.SkuReductionTo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallcoupon.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:12:20
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReductionInfo(SkuReductionTo skuReductionTo);
}

