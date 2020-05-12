package com.project.gulimallware.ware.dao;

import com.project.gulimallware.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:27:07
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Long getStockBySkuId(Long skuId);
}
