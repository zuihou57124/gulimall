package com.project.gulimallorder.order.dao;

import com.project.gulimallorder.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:16:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
