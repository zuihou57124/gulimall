package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.entity.SpuInfoEntity;
import com.project.gulimallproduct.product.vo.SkuItemVo;
import com.project.gulimallproduct.product.vo.Skus;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:08
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkusInfo(SpuInfoEntity spuInfo, List<Skus> skus);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo getItem(Long skuId);
}

