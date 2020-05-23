package com.project.gulimallmember.member.service.impl;

import com.project.gulimallmember.member.entity.MemberLevelEntity;
import com.project.gulimallmember.member.exception.HasPhoneException;
import com.project.gulimallmember.member.exception.HasUserNameException;
import com.project.gulimallmember.member.service.MemberLevelService;
import com.project.gulimallmember.member.vo.LoginVo;
import com.project.gulimallmember.member.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import com.project.gulimallmember.member.dao.MemberDao;
import com.project.gulimallmember.member.entity.MemberEntity;
import com.project.gulimallmember.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean checkUserNameUnique(String userName) {
        MemberEntity member = this.getOne(new QueryWrapper<MemberEntity>().eq("username", userName));
        return member == null;
    }

    @Override
    public Boolean checkPhoneUnique(String phone) {
        MemberEntity member = this.getOne(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        return member == null;
    }

    @Override
    public MemberEntity login(LoginVo loginVo) {

        //对密码进行加密处理
        String userName = loginVo.getUserName();
        String password = loginVo.getPassword();
        //先根据用户名或者手机号查询出用户
        MemberEntity member = this.getOne(new QueryWrapper<MemberEntity>()
                .eq("username", userName).or()
                .eq("mobile",userName));

        if(member!=null){
            //查询出用户后，将页面提交的密码与数据库加密后的密码对比
            BCryptPasswordEncoder md5 = new BCryptPasswordEncoder();
            boolean matches = md5.matches(password, member.getPassword());
            if(matches){
                return member;
            }
        }
        return null;
    }

    @Override
    public void register(RegisterVo registerVo) throws Exception{
        MemberEntity memberEntity = new MemberEntity();
        //保存之前先判断用户名和手机是否唯一
        Boolean userNameCheck = this.checkUserNameUnique(registerVo.getUserName());
        Boolean phoneCheck = this.checkPhoneUnique(registerVo.getPhone());
        if(!userNameCheck){
            throw new HasUserNameException();
        }
        if(!phoneCheck){
            throw new HasPhoneException();
        }
        memberEntity.setUsername(registerVo.getUserName());
        memberEntity.setMobile(registerVo.getPhone());
        //MD5加密算法
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePwd = bCryptPasswordEncoder.encode(registerVo.getPassword());
        memberEntity.setPassword(encodePwd);
        //查询出默认的会员等级
        MemberLevelEntity defaultLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(defaultLevel.getId());
        this.save(memberEntity);

    }

}