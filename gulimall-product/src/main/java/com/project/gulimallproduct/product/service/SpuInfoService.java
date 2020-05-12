package com.project.gulimallproduct.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallproduct.product.entity.SpuInfoDescEntity;
import com.project.gulimallproduct.product.vo.SpuSaveVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallproduct.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 14:45:07
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void saveSpuBaseInfo(SpuInfoEntity spuInfo);

    PageUtils spuInfoList(Map<String, Object> params);

    void spuUp(Long spuId);
}

