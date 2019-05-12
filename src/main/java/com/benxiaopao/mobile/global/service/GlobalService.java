package com.benxiaopao.mobile.global.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.benxiaopao.common.exception.BizException;
import com.benxiaopao.common.supers.BaseService;
import com.benxiaopao.common.util.HttpClientUtil;
import com.benxiaopao.common.util.Pagination;
import com.benxiaopao.mobile.common.constant.GlobalConstant;
import com.benxiaopao.mobile.restaurant.vo.RestaurantVO;
import com.benxiaopao.mobile.user.vo.UserVO;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 全局业务逻辑服务处理类
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Service
@Slf4j
public class GlobalService extends BaseService {

    /**
     * 查询用户首页展示数据
     */
    public Map<String, Object> getIndexData(int pageNum, int pageSize) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        Pagination pagination = Pagination.currentPagination(pageNum, pageSize);
        UserVO user = (UserVO) currentUser();

        Map<String, Object> params = Maps.newHashMap();
        params.put("pageNum", 1);
        params.put("pageSize", 10);
        params.put("userId", user == null? 0 : user.getUserId());
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/restaurant/list", params);
        log.info("# restaurant list response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用餐馆列表API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("调用餐馆列表API接口出错");
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
        pagination.setTotalCount(dataObj.getInteger("totalCount"));

        JSONArray restaurantArray = JSON.parseArray(dataObj.get("restaurantList").toString());
        List<RestaurantVO> restaurantVOList =  FluentIterable.from(restaurantArray).transform(new Function<Object, RestaurantVO>() {
            @Override
            public RestaurantVO apply(Object obj) {
                JSONObject restaurantObj = (JSONObject)obj;
                RestaurantVO restaurantVO = new RestaurantVO();
                //BeanUtils.copyProperties(restaurantObj, restaurantVO);
                restaurantVO.setRestaurantId(restaurantObj.getInteger("restaurantId"));
                restaurantVO.setRestaurantName(restaurantObj.getString("restaurantName"));
                restaurantVO.setIcon(restaurantObj.getString("icon"));
                restaurantVO.setAddress(restaurantObj.getString("address"));
                restaurantVO.setTel(restaurantObj.getString("tel"));
                restaurantVO.setTags(restaurantObj.getString("tags"));

                return restaurantVO;
            }
        }).toList();

        result.put("restaurantVOList", restaurantVOList);

        JSONArray recommendDataArray = JSON.parseArray(dataObj.get("recommendDataList").toString());
        if(recommendDataArray != null && recommendDataArray.size() > 0){
            List<String> recommendDataList = Lists.newArrayList();
            for(int i = 0; i < recommendDataArray.size(); i++){
                recommendDataList.add(recommendDataArray.get(i).toString());
            }
            result.put("recommendDataList", recommendDataList);
        }

        return result;
    }
}
