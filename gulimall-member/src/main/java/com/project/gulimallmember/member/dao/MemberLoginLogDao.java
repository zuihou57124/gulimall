package com.project.gulimallmember.member.dao;

import com.project.gulimallmember.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:20:26
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
