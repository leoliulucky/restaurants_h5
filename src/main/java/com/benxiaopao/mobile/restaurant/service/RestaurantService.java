package com.benxiaopao.mobile.restaurant.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.benxiaopao.common.exception.BizException;
import com.benxiaopao.common.supers.BaseService;
import com.benxiaopao.common.util.HttpClientUtil;
import com.benxiaopao.mobile.common.constant.GlobalConstant;
import com.benxiaopao.mobile.product.vo.ProductCategoryVO;
import com.benxiaopao.mobile.product.vo.ProductVO;
import com.benxiaopao.mobile.restaurant.vo.RestaurantVO;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 餐馆业务逻辑服务处理类
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Service
@Slf4j
public class RestaurantService extends BaseService {

    /**
     * 获取餐馆详情
     */
    public Map<String, Object> getRestaurantById(int restaurantId) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();


        Map<String, Object> params = Maps.newHashMap();
        params.put("restaurantId", restaurantId);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/restaurant/detail", params);
        log.info("# restaurant detail response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用餐馆详情API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("调用餐馆详情API接口出错");
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        // 餐馆详情
        JSONObject restaurantObj = JSON.parseObject(dataObj.get("restaurant").toString());
        RestaurantVO restaurantVO = new RestaurantVO();
        restaurantVO.setRestaurantId(restaurantObj.getInteger("restaurantId"));
        restaurantVO.setRestaurantName(restaurantObj.getString("restaurantName"));
        restaurantVO.setIcon(restaurantObj.getString("icon"));
        restaurantVO.setAddress(restaurantObj.getString("address"));
        restaurantVO.setTel(restaurantObj.getString("tel"));
        restaurantVO.setTags(restaurantObj.getString("tags"));
        result.put("restaurantVO", restaurantVO);

        // 品类列表
        JSONArray productCategoryArray = JSON.parseArray(dataObj.get("productCategoryList").toString());
        List<ProductCategoryVO> productCategoryVOList =  FluentIterable.from(productCategoryArray).transform(new Function<Object, ProductCategoryVO>() {
            @Override
            public ProductCategoryVO apply(Object obj) {
                JSONObject productCategoryObj = (JSONObject)obj;
                ProductCategoryVO productCategoryVO = new ProductCategoryVO();
                productCategoryVO.setCategoryId(productCategoryObj.getInteger("categoryId"));
                productCategoryVO.setCategoryName(productCategoryObj.getString("categoryName"));
                productCategoryVO.setCategoryType(productCategoryObj.getInteger("categoryType"));

                return productCategoryVO;
            }
        }).toList();
        result.put("productCategoryVOList", productCategoryVOList);

        // 菜品
        JSONObject productMapObj = JSON.parseObject(dataObj.get("productMap").toString());
        Map<Integer, List<ProductVO>> productVOMap = Maps.newHashMap();
        List<List<ProductVO>> productVOListList = Lists.newArrayList();
        for(ProductCategoryVO item : productCategoryVOList){
            JSONArray productArray = JSON.parseArray(productMapObj.get(item.getCategoryId()).toString());

            List<ProductVO> productVOList = FluentIterable.from(productArray).transform(new Function<Object, ProductVO>() {
                @Override
                public ProductVO apply(@Nullable Object obj) {
                    JSONObject productObj = (JSONObject)obj;
                    ProductVO productVO = new ProductVO();
                    productVO.setProductId(productObj.getString("productId"));
                    productVO.setProductName(productObj.getString("productName"));
                    productVO.setPrice(productObj.getDouble("price"));
                    productVO.setStock(productObj.getInteger("stock"));
                    productVO.setDescription(productObj.getString("description"));
                    productVO.setIcon(productObj.getString("icon"));
                    productVO.setCategoryType(productObj.getInteger("categoryType"));
                    productVO.setRestaurantId(productObj.getInteger("restaurantId"));

                    return productVO;
                }
            }).toList();
            productVOMap.put(Integer.valueOf(item.getCategoryId()), productVOList);
            productVOListList.add(productVOList);
        }
        result.put("productVOMap", productVOMap);
        result.put("productVOListList", productVOListList);

        return result;
    }
}
