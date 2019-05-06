package com.benxiaopao.mobile.product.vo;

import lombok.Data;

import java.util.Date;

/**
 * 菜品VO类
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Data
public class ProductVO {
    private String productId;
    private String productName;
    private double price;
    private int stock;
    private String description;
    private String icon;
    private byte status;
    private int categoryType;
    private int restaurantId;
    private Date createTime;
    private Date updateTime;
    //用户选择后的数量（份数）
    private int productCount;
}
