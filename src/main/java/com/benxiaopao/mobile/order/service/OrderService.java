package com.benxiaopao.mobile.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.benxiaopao.common.exception.BizException;
import com.benxiaopao.common.supers.BaseService;
import com.benxiaopao.common.util.HttpClientUtil;
import com.benxiaopao.mobile.common.constant.GlobalConstant;
import com.benxiaopao.mobile.order.vo.OrderItemVO;
import com.benxiaopao.mobile.order.vo.OrderVO;
import com.benxiaopao.mobile.product.vo.ProductVO;
import com.benxiaopao.mobile.user.vo.UserVO;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务逻辑服务处理类
 *
 * Created by liupoyang
 * 2019-05-06
 */
@Service
@Slf4j
public class OrderService extends BaseService {
    /**
     * 选好商品展示
     * @param productIds
     * @param productCounts
     */
    public List<ProductVO> chooseGoods(String productIds, String productCounts) throws Exception {
        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("productIds", productIds);
        params.put("productCounts", productCounts);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/order/goods", params);
        log.info("# pre order goods response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用选好商品展示API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("选好商品展示API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        // 菜品
        JSONArray productArray = JSON.parseArray(dataObj.get("productList").toString());
        List<ProductVO> productVOList =  FluentIterable.from(productArray).transform(new Function<Object, ProductVO>() {
            @Override
            public ProductVO apply(Object obj) {
                JSONObject productObj = (JSONObject)obj;
                ProductVO productVO = new ProductVO();
                productVO.setProductId(productObj.getString("productId"));
                productVO.setProductName(productObj.getString("productName"));
                productVO.setPrice(productObj.getDouble("price"));
                productVO.setIcon(productObj.getString("icon"));
                productVO.setProductCount(productObj.getInteger("stock"));

                return productVO;
            }
        }).toList();

        return productVOList;
    }

    /**
     * 创建订单
     * @param productIds
     * @param productCounts
     */
    public String createOrder(String productIds, String productCounts) throws Exception {
        UserVO user = (UserVO) currentUser();

        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("productIds", productIds);
        params.put("productCounts", productCounts);
        params.put("userId", user.getUserId());
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/order/create", params);
        log.info("# order create response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用创建订单API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("创建订单API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
        String orderId = dataObj.getString("orderId");
        return orderId;
    }

    /**
     * 获取订单
     * @param orderId
     */
    public Map<String, Object> getOrderById(String orderId) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        UserVO user = (UserVO) currentUser();

        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("orderId", orderId);
        params.put("userId", user.getUserId());
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/order/get", params);
        log.info("# order get response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用获取订单API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("获取订单API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        // 订单详情
        JSONObject orderObj = JSON.parseObject(dataObj.get("order").toString());
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderObj.getString("orderId"));
        orderVO.setPayType(orderObj.getInteger("payType"));
        orderVO.setTotalAmout(orderObj.getBigDecimal("totalAmout"));
        orderVO.setRealTotalAmout(orderObj.getBigDecimal("realTotalAmout"));
        orderVO.setShipmentExpense(orderObj.getBigDecimal("shipmentExpense"));
        orderVO.setOrderStatus(orderObj.getShort("orderStatus"));
        orderVO.setTel(orderObj.getString("tel"));
        //...
        result.put("orderVO", orderVO);

        // 订单明细列表
        JSONArray orderItemArray = JSON.parseArray(dataObj.get("orderItemList").toString());
        List<OrderItemVO> orderItemVOList =  FluentIterable.from(orderItemArray).transform(new Function<Object, OrderItemVO>() {
            @Override
            public OrderItemVO apply(Object obj) {
                JSONObject orderItemObj = (JSONObject)obj;
                OrderItemVO orderItemVO = new OrderItemVO();
                orderItemVO.setItemId(orderItemObj.getString("itemId"));
                orderItemVO.setOrderId(orderItemObj.getString("orderId"));
                orderItemVO.setProductIcon(orderItemObj.getString("productIcon"));
                orderItemVO.setProductId(orderItemObj.getString("productId"));
                orderItemVO.setProductName(orderItemObj.getString("productName"));
                orderItemVO.setShopPrice(orderItemObj.getBigDecimal("shopPrice"));
                orderItemVO.setProductCount(orderItemObj.getInteger("productCount"));
                //...

                return orderItemVO;
            }
        }).toList();
        result.put("orderItemVOList", orderItemVOList);

        return result;
    }

    /**
     * 支付订单
     * @param orderId
     */
    public void payOrder(String orderId) throws Exception {
        UserVO user = (UserVO) currentUser();

        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("orderId", orderId);
        params.put("userId", user.getUserId());
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/order/pay", params);
        log.info("# order pay response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用支付订单API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("支付订单API接口出错: " + responseObj.getString("msg"));
        }
//        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
    }

}
