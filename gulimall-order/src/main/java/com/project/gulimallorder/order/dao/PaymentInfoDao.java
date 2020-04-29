package com.project.gulimallorder.order.dao;

import com.project.gulimallorder.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:16:41
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
