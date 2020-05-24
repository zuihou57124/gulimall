package com.project.gulimallmember.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.gulimallmember.member.vo.LoginVo;
import com.project.gulimallmember.member.vo.RegisterVo;
import com.project.gulimallmember.member.vo.SocialUserVo;
import io.renren.common.utils.PageUtils;
import com.project.gulimallmember.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:20:27
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(RegisterVo registerVo) throws Exception;

    Boolean checkUserNameUnique(String userName);

    Boolean checkPhoneUnique(String phone);

    MemberEntity login(LoginVo loginVo);

    MemberEntity login(SocialUserVo socialUserVo) throws Exception;
}

