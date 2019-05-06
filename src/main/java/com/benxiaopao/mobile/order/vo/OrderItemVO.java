package com.benxiaopao.mobile.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细VO类
 *
 * Created by liupoyang
 * 2019-05-06
 */
@Data
public class OrderItemVO {
    private String itemId;
    private String orderId;
    private String productId;
    private String productName;
    private BigDecimal originPrice;
    private BigDecimal shopPrice;
    private Integer productCount;
    private String productIcon;
    private Date createTime;
    private Date updateTime;
}
