package com.benxiaopao.mobile.global.controller;

import com.benxiaopao.common.supers.BaseConstant;
import com.benxiaopao.common.supers.BaseController;
import com.benxiaopao.common.util.CaptchaUtil;
import com.benxiaopao.common.util.RSAUtil;
import com.benxiaopao.common.util.ThreadContent;
import com.benxiaopao.common.util.ViewResult;
import com.benxiaopao.mobile.common.constant.GlobalConstant;
import com.benxiaopao.mobile.global.service.GlobalService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap;
import java.util.Map;

/**
 * 全局模块请求控制层
 *
 * Created by liupoyang
 * 2019-05-03
 */
@Controller
@RequestMapping("/global")
@Slf4j
public class GlobalController extends BaseController {
    @Autowired
    private GlobalService globalService;

    /**
     * 网站首页
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/index")
    public ModelAndView index(@RequestParam(defaultValue="1")String page) throws Exception {
        int pageNum = Integer.parseInt(page);
        Map<String, Object> home = globalService.getIndexData(pageNum, GlobalConstant.DEFAULT_PAGE_SIZE);
        return ViewResult.newInstance()
                .code(1).msg("进网站首页成功")
                .put("home", home)
                .view("home/index");
    }

    /**
     * 验证码
     * @return
     * @throws Exception
     */
    @GetMapping(value="/captcha")
    public String getCaptcha() throws Exception {
        HttpServletRequest request = ThreadContent.request();
        HttpServletResponse response = ThreadContent.response();

        AbstractMap.SimpleEntry<String, byte[]> entry = CaptchaUtil.generate();

        request.getSession().setAttribute(BaseConstant.SESSION_CAPTCHA, entry.getKey());
        response.setContentType("image/" + CaptchaUtil.format());
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        OutputStream stream = response.getOutputStream();
        stream.write(entry.getValue(), 0, entry.getValue().length);
        stream.flush();
        stream.close();
        //JSP容器在处理完成请求后会调用releasePageConter方法释放所有的PageContestObject，
        //并且同时调用getWriter方法。
        //由于getWriter方法与在JSP页面中使用流相关的getOutputStream方法冲突，就会造成这种异常。
        //pushBody()的作用是保存当前的out对象，并更新PageContext中Page范围内Out对象。解决异常问题
//        out.clear();
//        out = pageContext.pushBody();
        return null;
    }

}
