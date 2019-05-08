package com.benxiaopao.mobile.user.controller;

import com.benxiaopao.common.supers.BaseConstant;
import com.benxiaopao.common.supers.BaseController;
import com.benxiaopao.common.util.RSAUtil;
import com.benxiaopao.common.util.ThreadContent;
import com.benxiaopao.common.util.ViewResult;
import com.benxiaopao.mobile.common.constant.UserConstant;
import com.benxiaopao.mobile.user.service.UserService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap;
import java.util.regex.Pattern;

/**
 * 用户模块请求控制层
 *
 * Created by liupoyang
 * 2019-05-02
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/login")
    public ModelAndView preLogin(@RequestParam(value = "path", defaultValue = "") String redirectUrl) throws Exception {
        try{
            if(Strings.isNullOrEmpty(redirectUrl)){
                redirectUrl = ThreadContent.request().getHeader("Referer");
                if(!Strings.isNullOrEmpty(redirectUrl)){
                    java.net.URL url = new java.net.URL(redirectUrl);
                    if(url == null || !ThreadContent.request().getServerName().equals(url.getHost())){
                        redirectUrl = null;
                    }
                    else{
                        if(url.getPath().endsWith("/user/register")
                                || url.getPath().endsWith("/user/login")
                                || url.getPath().endsWith("/user/inputMobile4Register")
                                || url.getPath().endsWith("/user/preVerifyMobile4Register")
                                || url.getPath().endsWith("/user/verifySuccess4Register")
                                || url.getPath().endsWith("/user/inputMobile4ResetPwd")
                                || url.getPath().endsWith("/user/preVerifyMobile4ResetPwd")
                                || url.getPath().endsWith("/user/verifySuccess4ResetPwd")
                                || url.getPath().endsWith("/user/resetPwdSuccessByMobile")
                                ){
                            redirectUrl = null;
                        }else{
                            redirectUrl = java.net.URLEncoder.encode(redirectUrl, "UTF-8");
                        }
                    }
                }
            }
            AbstractMap.SimpleEntry<Integer, RSAPublicKey> entry = RSAUtil.publicKeyWithRandom();
            return ViewResult.newInstance()
                    .code(1).msg("进用户登录页成功")
                    .put("path", redirectUrl)
                    .put("module", entry.getValue().getModulus().toString(16))
                    .put("exponent", entry.getValue().getPublicExponent().toString(16))
                    .put("random", entry.getKey())
                    .view("user/login");
        } catch (Exception e) {
            log.info("#进用户登录页出错：{}", e.getMessage());
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 用户登录请求<br />异步请求
     * @return
     * @throws Exception
     */
    @PostMapping(value="/doLogin")
    @ResponseBody
    public String login(String account, String password, String captcha) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(account), "请输入账户");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "请输入密码");

            password = RSAUtil.decrypt(password);
            AbstractMap.SimpleEntry<Integer, String> result = userService.login(account, password, captcha, captcha());
            if(result.getKey()> 0){
                RSAUtil.removeRandom();
            }
            //当session中有字段表明是否需要显示验证码，对应ajax取返回的json里也设置是否需要显示验证码
            boolean isRequireValidateCode = (ThreadContent.request().getSession().getAttribute("requireValidateCode") != null);
            return ViewResult.newInstance().code(result.getKey()).msg(result.getValue()).
                    put("requireValidateCode", isRequireValidateCode)
                    .json();
        } catch (Exception e) {
            log.info("#用户登录出错：{}", e.getMessage());
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 用户注册页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/register")
    public ModelAndView preRegister() throws Exception {
        try{
            AbstractMap.SimpleEntry<Integer, RSAPublicKey> entry = RSAUtil.publicKeyWithRandom();
            return ViewResult.newInstance()
                    .code(1).msg("进用户注册页成功")
                    .put("module", entry.getValue().getModulus().toString(16))
                    .put("exponent", entry.getValue().getPublicExponent().toString(16))
                    .put("random", entry.getKey())
                    .view("user/register");
        } catch (Exception e) {
            log.error("#进用户注册页出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 用户注册请求<br />异步请求
     * @return
     * @throws Exception
     */
    @PostMapping(value="/doRegister")
    @ResponseBody
    public String register(String account, String password, String captcha) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(account), "请输入账号");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "请输入密码");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(captcha), "请输入正确的验证码");

            int nickLen = account.getBytes("GBK").length;
            Preconditions.checkArgument(nickLen >= 4 && nickLen <= 18, "用户名不可少于4位或多于18位，请重新输入");
            Preconditions.checkArgument(Pattern.compile("[^0-9](.)*").matcher(account).matches(), "用户名不可以数字开头，请重新输入");
            Preconditions.checkArgument(Pattern.compile("^[-_a-zA-Z0-9\u4e00-\u9fa5]+$").matcher(account).matches(), "用户名只能包括中文、英文字母、数字及中划线、下划线，请重新输入");
            Preconditions.checkArgument(!UserConstant.hitKeywordForNickname(account), "用户名不可被注册，请重新输入");
            password = RSAUtil.decrypt(password);
            Preconditions.checkArgument(password.length() >= 6 && password.length() <= 16, "密码长度不可少于6位或多于16位，请检查");
            Preconditions.checkArgument(captcha.equalsIgnoreCase(captcha()), "请输入正确的验证码");

            int userTempId = userService.register(account, password);
            RSAUtil.removeRandom();
            return ViewResult.newInstance().code(1).msg("注册成功").json();
        } catch (Exception e) {
            log.error("#用户注册出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 注册激活用户输入手机号页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/inputMobile4Register")
    public ModelAndView inputMobile4Register(String mobile) throws Exception {
        return ViewResult.newInstance()
                .code(1).msg("进注册激活用户输入手机号页成功")
                //手机号，该参数可能有，也可能没有
                .put("mobile", mobile)
                .view("user/active");
    }

    /**
     * 注册激活获取手机验证码请求<br />异步请求
     * @return
     * @throws Exception
     */
    @PostMapping(value="/getMobileCode4Register")
    @ResponseBody
    public String getMobileCode4Register(String mobile) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");

            long mobileAuthenId = userService.getMobileCode4Register(mobile);
            return ViewResult.newInstance()
                    .code(1).msg("注册激活获取手机验证码成功")
                    .put("mobileAuthenId", mobileAuthenId)
                    .put("mobile", mobile)
                    .put("verifyCode", ThreadContent.getData("verifyCode"))
                    .json();
        } catch (Exception e) {
            log.error("#请求注册激活获取手机验证码出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进注册激活手机号验证页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/preVerifyMobile4Register")
    public ModelAndView preVerifyMobile4Register(String mobile, @RequestParam(value = "mai", defaultValue = "0") long mobileAuthenId, String verifyCode) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");
            Preconditions.checkArgument(mobileAuthenId > 0, "请刷新页面后重新验证");

            return ViewResult.newInstance()
                    .code(1).msg("进注册激活手机号验证页成功")
                    .put("mobile", mobile)
                    .put("mobileAuthenId", mobileAuthenId)
                    .put("verifyCode", verifyCode)
                    .view("user/active2");
        } catch (Exception e) {
            log.error("#进注册激活手机号验证页出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 注册激活验证手机验证码请求<br />异步请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/verifyMobileCode4Register")
    @ResponseBody
    public String verifyMobileCode4Register(String mobile, String code, @RequestParam(value = "mai", defaultValue = "0") long mobileAuthenId) throws Exception {
        try{
            //检验数据
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(code), "请输入验证码");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");
            Preconditions.checkArgument(Pattern.compile("^\\d{6}$").matcher(code).matches(), "验证码输入错误，请检查");
            Preconditions.checkArgument(mobileAuthenId > 0, "请刷新页面后重新验证");

            userService.verifyMobileCode4Register(mobile, code, mobileAuthenId);
            return ViewResult.newInstance().code(1).msg("注册激活验证手机验证码成功").json();
        } catch (Exception e) {
            log.error("#注册激活验证手机验证码出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进注册激活验证手机号成功页
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/verifySuccess4Register")
    public ModelAndView verifySuccess4Register() throws Exception {
        return ViewResult.newInstance().code(1)
                .msg("进注册激活验证手机号成功页成功")
                .view("user/activeSuccess");
    }

    /**
     * 进入使用手机号重置密码页
     * @return
     * @throws Exception
     */
    @GetMapping(value="/inputMobile4ResetPwd")
    public ModelAndView inputMobile4ResetPwd(String mobile) throws Exception {
        return ViewResult.newInstance()
                .code(1).msg("进入使用手机号重置密码页成功")
                //手机号，该参数可能有，也可能没有
                .put("mobile", mobile)
                .view("user/resetPasswordByMobile");
    }

    /**
     * 找回密码获取手机验证码请求<br />异步请求
     * @return
     * @throws Exception
     */
    @PostMapping(value="/getMobileCode4ResetPwd")
    @ResponseBody
    public String getMobileCode4ResetPwd(String mobile, String captcha) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");

            //验证码，该参数在第一次输入手机号时有，在再次获取手机验证码时没有
            String realCaptcha = captcha();
            if(realCaptcha != null){
                Preconditions.checkArgument(!Strings.isNullOrEmpty(captcha), "请输入验证码");
                Preconditions.checkArgument(captcha.equalsIgnoreCase(realCaptcha), "验证码输入错误，请检查");
                ThreadContent.request().getSession().removeAttribute(BaseConstant.SESSION_CAPTCHA);
            }

            long mobileAuthenId = userService.getMobileCode4ResetPwd(mobile);
            return ViewResult.newInstance()
                    .code(1).msg("找回密码获取手机验证码成功")
                    .put("mobileAuthenId", mobileAuthenId)
                    .put("mobile", mobile)
                    .put("verifyCode", ThreadContent.getData("verifyCode"))
                    .json();
        } catch (Exception e) {
            log.error("#找回密码获取手机验证码出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进找回密码用户手机号验证页
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/preVerifyMobile4ResetPwd")
    public ModelAndView preVerifyMobile4ResetPwd(String mobile, @RequestParam(value = "mai", defaultValue = "0") int mobileAuthenId, String verifyCode) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");
            Preconditions.checkArgument(mobileAuthenId > 0, "请刷新页面后重新验证");

            return ViewResult.newInstance()
                    .code(1).msg("进找回密码用户手机号验证页成功")
                    .put("mobile", mobile)
                    .put("mobileAuthenId", mobileAuthenId)
                    .put("verifyCode", verifyCode)
                    .view("user/resetPasswordByMobile2");
        } catch (Exception e) {
            log.error("#进找回密码用户手机号验证页出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).view("error404");
        }
    }

    /**
     * 找回密码验证手机验证码请求<br />异步请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/verifyMobileCode4ResetPwd")
    @ResponseBody
    public String verifyMobileCode4ResetPwd(String mobile, String code, @RequestParam(value = "mai", defaultValue = "0") long mobileAuthenId) throws Exception {
        try{
            //检验数据
            Preconditions.checkArgument(!Strings.isNullOrEmpty(mobile), "请输入手机号");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(code), "请输入验证码");
            Preconditions.checkArgument(Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$").matcher(mobile).matches(), "手机号格式不正确，请检查");
            Preconditions.checkArgument(Pattern.compile("^\\d{6}$").matcher(code).matches(), "验证码输入错误，请检查");
            Preconditions.checkArgument(mobileAuthenId > 0, "请刷新页面后重新验证");

            userService.verifyMobileCode4ResetPwd(mobile, code, mobileAuthenId);
            return ViewResult.newInstance().code(1).msg("找回密码验证手机验证码成功").json();
        } catch (Exception e) {
            log.error("#找回密码验证手机验证码出错：", e);
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 进找回密码验证手机号成功页
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/verifySuccess4ResetPwd")
    public ModelAndView verifySuccess4ResetPwd() throws Exception {
        String nickname = (String) ThreadContent.request().getSession().getAttribute("temp_resetPwd_nickname");
        return ViewResult.newInstance()
                .code(1).msg("进找回密码验证手机号成功页成功")
                .put("nickname", nickname)
                .view("user/resetPassword");
    }

    /**
     * 重置密码请求<br />异步请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/resetPwd")
    @ResponseBody
    public String resetPwd(@RequestParam(value = "pwd", defaultValue = "") String password) throws Exception {
        try{
            Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "请输入密码");
            Preconditions.checkArgument(password.length() >= 6 && password.length() <= 16, "密码长度不符合要求，请检查");
            userService.resetPwd(password);
            return ViewResult.newInstance().code(1).msg("重置密码成功").json();
        } catch (Exception e) {
            log.info("#重置密码出错：" + e.getMessage());
            return ViewResult.newInstance().code(-1).msg(e.getMessage()).json();
        }
    }

    /**
     * 手机号重置密码成功页
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/resetPwdSuccessByMobile")
    public ModelAndView resetPwdSuccessByMobile() throws Exception {
        return ViewResult.newInstance()
                .code(1).msg("进手机号重置密码成功页成功")
                .view("user/resetSuccess");
    }

    /**
     * 用户登出
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/logout")
    public String logout() throws Exception {
        ThreadContent.request().getSession().invalidate();
        return ViewResult.newInstance().code(1).msg("用户登出成功").redirect("/global/index");
    }

}
