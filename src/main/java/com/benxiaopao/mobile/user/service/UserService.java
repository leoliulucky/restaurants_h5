package com.benxiaopao.mobile.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.benxiaopao.common.exception.BizException;
import com.benxiaopao.common.supers.BaseService;
import com.benxiaopao.common.util.DateUtil;
import com.benxiaopao.common.util.HttpClientUtil;
import com.benxiaopao.common.util.Pagination;
import com.benxiaopao.common.util.ThreadContent;
import com.benxiaopao.mobile.common.constant.GetUserAgent;
import com.benxiaopao.mobile.common.constant.GlobalConstant;
import com.benxiaopao.mobile.common.constant.IpConvert;
import com.benxiaopao.mobile.product.vo.ProductCategoryVO;
import com.benxiaopao.mobile.product.vo.ProductVO;
import com.benxiaopao.mobile.restaurant.vo.RestaurantVO;
import com.benxiaopao.mobile.user.vo.UserRegisterTempVO;
import com.benxiaopao.mobile.user.vo.UserVO;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * 用户业务逻辑服务处理类
 *
 * Created by liupoyang
 * 2019-05-02
 */
@Service
@Slf4j
public class UserService extends BaseService {

    /**
     * 用户登录
     * @param account 账户
     * @param password 密码
     * @param inputCaptcha 用户输入的验证码
     * @param realCaptcha 真实的验证码
     * @return SimpleEntry<Integer, String>
     */
    public AbstractMap.SimpleEntry<Integer, String> login(String account, String password, String inputCaptcha, String realCaptcha) throws Exception {
        log.info("#用户开始登录，其中 account={}", account);

        // 获取客户端浏览器类型
        String userAgent = ThreadContent.request().getHeader("User-Agent");
        short browserType = GetUserAgent.checkBrowser(userAgent);
        // 获取客户端操作系统类型
        short osType = GetUserAgent.GetOSNameByUserAgent(userAgent);

        // 获取客户端IP地址，并将字符串IP转换成数字型
        long loginIp = 0;
        try {
            loginIp = IpConvert.IpToLong(IpConvert.getIpAddr(ThreadContent.request()));
        } catch (ParseException e) {
            log.error("#IP地址转换异常", e);
        }
        log.debug("#浏览器类型 browerType=" + browserType + " 操作系统类型osType=" + osType + " 登录IP地址 ipToLong=" + loginIp);

        //调用登录接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("loginId", account);
        params.put("password", password);
        params.put("loginIP", loginIp);
        params.put("browserType", browserType);
        params.put("osType", osType);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/login", params);
        log.info("# user login response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用用户登录API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("登录API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        UserVO user = null;
        UserRegisterTempVO userRegisterTemp = null;

        JSONObject userInfoMapObj = JSON.parseObject(dataObj.get("userInfoMap").toString());
        // 用户主表
        if(userInfoMapObj.get("user") != null){
            JSONObject userObj = JSON.parseObject(userInfoMapObj.get("user").toString());
            user = new UserVO();
            user.setUserId(userObj.getInteger("userId"));
            user.setEmail(userObj.getString("email"));
            user.setNickName(userObj.getString("nickName"));
            user.setRealName(userObj.getString("realName"));
            user.setDisplayName(userObj.getString("displayName"));
            user.setMobile(userObj.getString("mobile"));
        }

        // 用户临时表
        if(userInfoMapObj.get("userRegisterTemp") != null){
            JSONObject userRegisterTempObj = JSON.parseObject(userInfoMapObj.get("userRegisterTemp").toString());
            userRegisterTemp = new UserRegisterTempVO();
            userRegisterTemp.setId(userRegisterTempObj.getInteger("id"));
            userRegisterTemp.setMobile(userRegisterTempObj.getString("mobile"));
            userRegisterTemp.setEmail(userRegisterTempObj.getString("email"));
            userRegisterTemp.setNickName(userRegisterTempObj.getString("nickName"));
        }

        if(userRegisterTemp != null){
            //使用临时用户登录成功
            if (userRegisterTemp.getId() > 0) {
                //将用户注册信息放入Session
                ThreadContent.request().getSession().setAttribute("tempUserId", userRegisterTemp.getId());
                ThreadContent.request().getSession().setAttribute("nickname", userRegisterTemp.getNickName());

                return new AbstractMap.SimpleEntry<Integer, String>(2, "登录成功");
            } else if (userRegisterTemp.getId() == -3) {
                //临时表查无此人
                return new AbstractMap.SimpleEntry<Integer, String>(-3, "您输入的用户名不存在");
            } else if (userRegisterTemp.getId() == -4) {
                //临时用户密码错误
                return new AbstractMap.SimpleEntry<Integer, String>(-4, "您输入的用户名或密码有误");
            }
        }

        //要求验证码
        boolean requireValidateCode = false;
        if (user.getUserId() > 0) {
            requireValidateCode = dataObj.getBoolean("requireValidateCode");
        }
        if (requireValidateCode) {
            if (Strings.isNullOrEmpty(inputCaptcha) || !inputCaptcha.equalsIgnoreCase(realCaptcha)) {
                log.info("#用户登录需要验证码，但验证码错误");
                return new AbstractMap.SimpleEntry<Integer, String>(-88, "请输入正确的验证码");
            }
        }

        if(user.getUserId() > 0){
            //使之前的匿名session失效
            ThreadContent.request().getSession().invalidate();
            //将用户对象放入Session
            loadUser2Session(user);
            return new AbstractMap.SimpleEntry<Integer, String>(1, "登录成功");
        }
        else if(user.getUserId() == -1){
            log.info("#登录错误： 邮箱 {} 不存在", account);
            return new AbstractMap.SimpleEntry<Integer, String>(-1, "您输入的邮箱尚未注册");
        }
        else if(user.getUserId() == -2){
            log.info("#登录错误： 手机号 {} 不存在", account);
            return new AbstractMap.SimpleEntry<Integer, String>(-2, "您输入的手机号尚未注册");
        }
        else if(user.getUserId() == -3){
            log.info("#登录错误： 昵称 {} 不存在", account);
            return new AbstractMap.SimpleEntry<Integer, String>(-3, "您输入的用户名不存在");
        }
        else if(user.getUserId() == -4){
            log.info("#登录错误： 昵称 {} 密码错误", account);
            requireValidateCode = dataObj.getBoolean("requireValidateCode");
            if(requireValidateCode) {
                //该Session会在登录页面中用到，以显示验证码判断
                ThreadContent.request().getSession().setAttribute("requireValidateCode", true);
            }
            return new AbstractMap.SimpleEntry<Integer, String>(-4, "您输入的用户名或密码有误");
        }else if (user.getUserId() == -98) {
            log.info("#登录错误： 昵称 {} 为借款人账号，禁止登录", account);
            //该Session会在登录页面中用到，以显示验证码判断
            return new AbstractMap.SimpleEntry<Integer, String>(-98, "您输入的用户名为借款人角色，禁止登录");
        }
        else if (user.getUserId() == -99) {
            log.info("#登录错误： 昵称 {} 账户被锁定", account);
            //该Session会在登录页面中用到，以显示验证码判断
            ThreadContent.request().getSession().setAttribute("requireValidateCode", true);
            return new AbstractMap.SimpleEntry<Integer, String>(-99, "为保证账户资金安全，此账号已被临时锁定，24小时后解锁");
        }
        else if (user.getUserId() == -100){
            log.info("#登录错误： 登录失败次数大于等于3，禁止昵称登录");
            //该Session会在登录页面中用到，以显示验证码判断
            ThreadContent.request().getSession().setAttribute("requireValidateCode", true);
            return new AbstractMap.SimpleEntry<Integer, String>(-100, "为保证账户资金安全，请使用您的激活邮箱或手机号登录");
        }
        else {
            log.info("#登录错误： 未知的错误 昵称：{}", account);
            return new AbstractMap.SimpleEntry<Integer, String>(-5, "您输入的用户名不存在");
        }
    }

    /**
     * 用户注册
     * @param account
     * @param password
     * @return int 注册用户id
     */
    public int register(String account, String password) throws Exception {
        log.info("#用户开始注册，其中 nickname={}", account);

        //调用注册接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("account", account);
        params.put("password", password);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/register", params);
        log.info("# user register response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用用户注册API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("注册API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
        int userRegisterTempId = dataObj.getInteger("userRegisterTempId");

        ThreadContent.request().getSession().setAttribute("tempUserId", userRegisterTempId);
        ThreadContent.request().getSession().setAttribute("nickname", account);
        return userRegisterTempId;
    }

    /**
     * 注册激活获取手机验证码
     * @param mobile 手机号
     * @return int 手机认证id
     */
    public long getMobileCode4Register(String mobile) throws Exception {
        int tempUserId = (Integer) ThreadContent.request().getSession().getAttribute("tempUserId");
        Long lastSendTime = (Long) ThreadContent.request().getSession().getAttribute("lastSendTime");
        Preconditions.checkArgument(lastSendTime == null || DateUtil.now().getTime() > lastSendTime + 1 * 60 * 1000, "一分钟只能获取一次验证码");

        //调用注册接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("mobile", mobile);
        params.put("userRegisterTempId", tempUserId);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/register/fetchcode", params);
        log.info("# user fetch code response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用注册激活获取手机验证码API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("注册激活获取手机验证码API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        long mobileAuthenId = dataObj.getLong("mobileAuthenId");
        String verifyCode = dataObj.getString("verifyCode");

        log.info("#开始发送注册验证手机号短信验证码，其中 mobile={}, verifyCode={}", mobile, verifyCode);
        //这里调用第三方发短信 这里不调用，直接回显在页面 假装发了短信
//        smsService.sendRegister(mobile, verifyCode);
        ThreadContent.addData("verifyCode", verifyCode);

        //将发送时间存入Session，以做时间间隔检验
        ThreadContent.request().getSession().setAttribute("lastSendTime", DateUtil.now().getTime());
        return mobileAuthenId;
    }

    /**
     * 注册激活验证手机验证码
     * @param mobile 手机号
     * @param verifyCode 验证码
     * @param mobileAuthenId 手机认证id
     * @return
     */
    public void verifyMobileCode4Register(String mobile, String verifyCode, long mobileAuthenId) throws Exception {
        int tempUserId = (Integer) ThreadContent.request().getSession().getAttribute("tempUserId");

        //调用注册激活验证手机验证码接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("mobile", mobile);
        params.put("verifyCode", verifyCode);
        params.put("mobileAuthenId", mobileAuthenId);
        params.put("userRegisterTempId", tempUserId);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/register/verifycode", params);
        log.info("# user verify code response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用注册激活验证手机验证码API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("注册激活验证手机验证码API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
        JSONObject userObj = JSON.parseObject(dataObj.get("user").toString());
        UserVO user = new UserVO();
        user.setUserId(userObj.getInteger("userId"));
        user.setEmail(userObj.getString("email"));
        user.setNickName(userObj.getString("nickName"));
        user.setRealName(userObj.getString("realName"));
        user.setDisplayName(userObj.getString("displayName"));
        user.setMobile(userObj.getString("mobile"));

        ThreadContent.request().getSession().invalidate();
        loadUser2Session(user);
    }

    /**
     * 找回密码获取手机验证码
     * @param mobile 手机号
     * @return int 手机认证id
     */
    public long getMobileCode4ResetPwd(String mobile) throws Exception {
        Long lastSendTime = (Long) ThreadContent.request().getSession().getAttribute("lastSendTime");
        Preconditions.checkArgument(lastSendTime == null || DateUtil.now().getTime() > lastSendTime + 1 * 60 * 1000, "一分钟只能获取一次验证码");

        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("mobile", mobile);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/resetpwd/fetchcode", params);
        log.info("# user reset pwd code response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用找回密码获取手机验证码API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("找回密码获取手机验证码API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        long mobileAuthenId = dataObj.getLong("mobileAuthenId");
        String verifyCode = dataObj.getString("verifyCode");

        log.info("#开始发送找回密码验证手机号短信验证码，其中 mobile={}, verifyCode={}", mobile, verifyCode);
        //这里调用第三方发短信 这里不调用，直接回显在页面 假装发了短信
//        smsService.sendVerificationCode(mobile, mobileCode, MessageDef.SMS_SEND_RESET_PASSWORD);
        ThreadContent.addData("verifyCode", verifyCode);

        //将发送时间存入Session，以做时间间隔检验
        ThreadContent.request().getSession().setAttribute("lastSendTime", DateUtil.now().getTime());
        return mobileAuthenId;
    }

    /**
     * 找回密码验证手机验证码
     * @param mobile 手机号
     * @param verifyCode 验证码
     * @param mobileAuthenId 手机认证id
     */
    public void verifyMobileCode4ResetPwd(String mobile, String verifyCode, long mobileAuthenId) throws Exception {
        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("mobile", mobile);
        params.put("verifyCode", verifyCode);
        params.put("mobileAuthenId", mobileAuthenId);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/resetpwd/verifycode", params);
        log.info("# user verify code response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用找回密码验证手机验证码API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("找回密码验证手机验证码API接口出错: " + responseObj.getString("msg"));
        }
        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());
        JSONObject userObj = JSON.parseObject(dataObj.get("user").toString());
        UserVO user = new UserVO();
        int userId = userObj.getInteger("userId");
        String nickname = userObj.getString("nickName");

        //清除手机验证码发送时间
        ThreadContent.request().getSession().removeAttribute("lastSendTime");
        //将用户对象放入Session，以在重置密码时取数据做安全检验及重置操作
        ThreadContent.request().getSession().setAttribute("temp_resetPwd_userId", userId);
        ThreadContent.request().getSession().setAttribute("temp_resetPwd_nickname", nickname);
    }

    /**
     * 重置密码
     * @param password 新密码
     */
    public void resetPwd(String password) throws Exception {
        int userId = (Integer) ThreadContent.request().getSession().getAttribute("temp_resetPwd_userId");

        //调用接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("password", password);
        params.put("userId", userId);
        String response = HttpClientUtil.doPost(GlobalConstant.API_URL + "/api/user/resetpwd", params);
        log.info("# user reset pwd response = {}", response);
        JSONObject responseObj = JSON.parseObject(response);
        int code = responseObj.getInteger("code");
        if(code <= 0){
            log.info("# 调用重置密码API接口出错：{}", responseObj.getString("msg"));
            throw new BizException("重置密码API接口出错: " + responseObj.getString("msg"));
        }
//        JSONObject dataObj = JSON.parseObject(responseObj.get("data").toString());

        //清除Session中重置密码时用户信息
        ThreadContent.request().getSession().removeAttribute("temp_resetPwd_userId");
        ThreadContent.request().getSession().removeAttribute("temp_resetPwd_nickname");
    }

}
