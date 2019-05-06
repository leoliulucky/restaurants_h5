package com.benxiaopao.mobile.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户VO类
 *
 * Created by liupoyang
 * 2019-05-02
 */
@Data
public class UserVO {
    private Integer userId;
    private String email;
    private String nickName;
    private String realName;
    private String displayName;
    private String password;
    private String mobile;
    private String city;
    private Date registerTime;
    private Date activeTime;
    private Short status;
    private Short forbidStatus;
    private Short securityLevel;
    private String weiboUId;
    private String weiboAccessToken;
    private String qqUId;
    private String qqAccessToken;
    private String registerChannelId;
}
