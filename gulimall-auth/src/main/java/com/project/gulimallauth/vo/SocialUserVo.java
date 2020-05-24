package com.project.gulimallauth.vo;

import lombok.Data;

/**
 * @author qcw
 * 微博社交登录访问令牌Vo
 */
@Data
public class SocialUserVo {

    private String accessToken;

    private String remindIn;

    private long expiresIn;

    private String uid;

    private String isRealName;

}
