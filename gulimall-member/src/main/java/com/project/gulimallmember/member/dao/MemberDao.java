package com.project.gulimallmember.member.dao;

import com.project.gulimallmember.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:20:27
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
