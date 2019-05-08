package com.benxiaopao.mobile.restaurant.controller;

import com.benxiaopao.common.supers.BaseController;
import com.benxiaopao.common.util.ViewResult;
import com.benxiaopao.mobile.restaurant.service.RestaurantService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 餐馆模块请求控制层
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Controller
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController extends BaseController {
    @Autowired
    private RestaurantService restaurantService;

    /**
     * 餐馆详情
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/detail")
    public ModelAndView getRestaurant(@RequestParam(name="i") int restaurantId) throws Exception {
        Preconditions.checkArgument(restaurantId > 0, "数据错误，请刷新页面重试");
        Map<String, Object> detail = restaurantService.getRestaurantById(restaurantId);
        return ViewResult.newInstance()
                .code(1).msg("进餐馆详情页成功")
                .put("detail", detail)
                .view("restaurant/showRestaurant");
    }

}
