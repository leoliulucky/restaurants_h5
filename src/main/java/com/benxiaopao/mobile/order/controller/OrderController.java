package com.benxiaopao.mobile.order.controller;

import com.benxiaopao.common.supers.BaseConstant;
import com.benxiaopao.common.supers.BaseController;
import com.benxiaopao.common.util.RSAUtil;
import com.benxiaopao.common.util.ThreadContent;
import com.benxiaopao.common.util.ViewResult;
import com.benxiaopao.mobile.common.aspect.IncludeAuthorize;
import com.benxiaopao.mobile.common.constant.UserConstant;
import com.benxiaopao.mobile.order.service.OrderService;
import com.benxiaopao.mobile.product.vo.ProductVO;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 订单模块请求控制层
 *
 * Created by liupoyang
 * 2019-05-06
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    /**
     * 进用户选好菜品页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/goods")
    @IncludeAuthorize
    public ModelAndView chooseGoods(@RequestParam(value = "pid", defaultValue = "") String productIds, @RequestParam(value = "num", defaultValue = "") String productCounts) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(productIds), "没有选好菜单，请刷新页面重试");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(productCounts), "没有选好菜单，请刷新页面重试");

            List<ProductVO> productVOList = orderService.chooseGoods(productIds, productCounts);

            return ViewResult.newInstance()
                    .code(1).msg("进用户选好菜品页成功")
                    .put("productVOList", productVOList)
                    .put("productIds", productIds)
                    .put("productCounts", productCounts)
                    .view("order/showGoods");
        } catch (Exception e) {
            log.error("#进用户选好菜品页出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 确认下单请求<br />异步请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/create")
    @ResponseBody
    @IncludeAuthorize
    public String createOrder(@RequestParam(value = "pid", defaultValue = "") String productIds, @RequestParam(value = "num", defaultValue = "") String productCounts) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(productIds), "没有选好菜单，请刷新页面重试");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(productCounts), "没有选好菜单，请刷新页面重试");

            String orderId = orderService.createOrder(productIds, productCounts);
            return ViewResult.newInstance().code(1).msg("确认下单成功")
                    .put("orderId", orderId)
                    .json();
        } catch (Exception e) {
            log.info("#确认下单出错：" + e.getMessage());
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进支付页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/money")
    @IncludeAuthorize
    public ModelAndView getOrderById(@RequestParam(value = "oid", defaultValue = "") String orderId) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(orderId), "数据错误，请刷新页面重试");

            Map<String, Object> orderMap = orderService.getOrderById(orderId);

            return ViewResult.newInstance()
                    .code(1).msg("进支付页成功")
                    .put("orderMap", orderMap)
                    .view("order/showOrder");
        } catch (Exception e) {
            log.error("#进支付页出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 支付请求<br />异步请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/pay")
    @ResponseBody
    @IncludeAuthorize
    public String payOrder(@RequestParam(value = "oid", defaultValue = "") String orderId) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(orderId), "参数错误，请刷新页面重试");

            orderService.payOrder(orderId);
            return ViewResult.newInstance().code(1).msg("支付成功")
                    .json();
        } catch (Exception e) {
            log.info("#支付出错：" + e.getMessage());
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进支付成功页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/paySuccess")
    @IncludeAuthorize
    public ModelAndView paySuccess() throws Exception {
        return ViewResult.newInstance()
                .code(1).msg("进支付成功页成功")
                .view("order/paySuccess");
    }
}
