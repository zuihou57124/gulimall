package com.project.gulimallcoupon.coupon.dao;

import com.project.gulimallcoupon.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:12:21
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
