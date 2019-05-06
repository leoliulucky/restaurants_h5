package com.benxiaopao.mobile.restaurant.vo;

import lombok.Data;

import java.util.Date;

/**
 * 餐馆VO类
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Data
public class RestaurantVO {
    private int restaurantId;
    private String restaurantName;
    private String icon;
    private String address;
    private String tel;
    private String tags;
    private Date createTime;
    private Date updateTime;
}
