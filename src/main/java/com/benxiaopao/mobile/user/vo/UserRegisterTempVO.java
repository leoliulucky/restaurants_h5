package com.benxiaopao.mobile.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户临时VO类
 *
 * Created by liupoyang
 * 2019-05-04
 */
@Data
public class UserRegisterTempVO {
    private Integer id;
    private String mobile;
    private String email;
    private String nickName;
    private String passwd;
    private Date registerTime;
    private String question;
    private String answer;
    private String weiboUId;
    private String weiboAccessToken;
    private String qqUId;
    private String qqAccessToken;
    private String registerChannelId;
    private String tempInfo;
}
