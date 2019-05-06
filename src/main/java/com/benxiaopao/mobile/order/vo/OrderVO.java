package com.benxiaopao.mobile.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单VO类
 *
 * Created by liupoyang
 * 2019-05-06
 */
@Data
public class OrderVO {
    private String orderId;
    private Integer payType;
    private BigDecimal totalAmout;
    private BigDecimal realTotalAmout;
    private BigDecimal shipmentExpense;
    private Short orderStatus;
    private Byte orderType;
    private Byte orderFrom;
    private String pOrderId;
    private Integer buyerId;
    private String consignee;
    private Integer province;
    private Integer city;
    private Integer district;
    private String address;
    private String tel;
    private Date createTime;
    private Date updateTime;
}
