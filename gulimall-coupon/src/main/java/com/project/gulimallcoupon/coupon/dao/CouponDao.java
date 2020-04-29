package com.project.gulimallcoupon.coupon.dao;

import com.project.gulimallcoupon.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:12:22
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
