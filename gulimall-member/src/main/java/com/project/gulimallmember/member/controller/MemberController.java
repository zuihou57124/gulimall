package com.project.gulimallmember.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.project.gulimallmember.member.vo.LoginVo;
import com.project.gulimallmember.member.vo.SocialUserVo;
import io.renren.common.myconst.MyConst;
import com.project.gulimallmember.member.exception.HasPhoneException;
import com.project.gulimallmember.member.exception.HasUserNameException;
import com.project.gulimallmember.member.feign.CouponFeign;
import com.project.gulimallmember.member.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.project.gulimallmember.member.entity.MemberEntity;
import com.project.gulimallmember.member.service.MemberService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/**
 * 会员
 *
 * @author qcw
 * @email zuihou57124@gmail.com
 * @date 2020-04-29 17:20:27
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeign couponFeign;

    @RequestMapping("/coupon/list")
    public R memberCoupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("秦风");
        R coupons = couponFeign.memberCoupons();
        return R.ok().put("member",memberEntity).put("allCoupons",coupons.get("allCoupons"));

    }

    /**
     * 社交登录
     */
    @PostMapping("/auth/login")
    public R socialLogin(@RequestBody SocialUserVo socialUserVo, HttpServletResponse response){
        MemberEntity member = null;
        new Cookie("aa","aa").setDomain("");
        try {
            member = memberService.login(socialUserVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (member==null){
            System.out.println("用户名或密码错误");
            return R.error(MyConst.MemberEnum.USER_LOGIN_EXCEPTION.getCode(),MyConst.MemberEnum.USER_LOGIN_EXCEPTION.getMsg());
        }
        return R.ok().setData(member);
    }

    /**
     * 会员登录
     */
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        MemberEntity member = memberService.login(loginVo);
        if (member==null){
            System.out.println("用户名或密码错误");
            return R.error(MyConst.MemberEnum.USER_LOGIN_EXCEPTION.getCode(),MyConst.MemberEnum.USER_LOGIN_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    /**
     * 注册会员
     */
    @PostMapping("/regsiter")
    public R regsiter(@RequestBody RegisterVo registerVo){

        try {
            memberService.register(registerVo);
        } catch (HasPhoneException e) {
            System.out.println("手机已被注册");
            return R.error(MyConst.MemberEnum.HAS_PHONE_EXCEPTION.getCode(),MyConst.MemberEnum.HAS_PHONE_EXCEPTION.getMsg());

        }catch (HasUserNameException e){
            System.out.println("用户名已被注册");
            return R.error(MyConst.MemberEnum.HAS_USER_EXCEPTION.getCode(),MyConst.MemberEnum.HAS_USER_EXCEPTION.getMsg());

        }catch (Exception e){
            e.printStackTrace();
        }

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
