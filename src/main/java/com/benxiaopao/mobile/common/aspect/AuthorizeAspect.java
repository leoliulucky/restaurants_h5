package com.benxiaopao.mobile.common.aspect;

import com.benxiaopao.common.supers.BaseService;
import com.benxiaopao.common.util.ThreadContent;
import com.benxiaopao.mobile.user.vo.UserVO;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 权限处理
 *
 * Created by liupoyang
 * 2019-05-02
 */
@Aspect
@Component
@Slf4j
public class AuthorizeAspect extends BaseService {

    /**
     * 定义切点
     */
    @Pointcut("execution(public * com.benxiaopao.mobile.*.controller.*Controller.*(..))")
    public void authorize() {
    }

    /**
     * 权限环绕通知
     * @param proceedingJoinPoint
     */
    @Around("authorize()")
    public Object doAuthorize(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        loadRequestAndResponse();

        //判断用户请求是否是可放行通过的特殊请求
        if (isInclude(proceedingJoinPoint)) {
            UserVO user = (UserVO) currentUser();
            if(user == null){
                HttpServletRequest request = ThreadContent.request();
                HttpServletResponse response = ThreadContent.response();

                String parameters = request.getQueryString();
                String url = request.getRequestURL().toString();
                url = Strings.isNullOrEmpty(parameters) ? url : (url + "?" +  parameters);
                log.info("#用户未登录，请求的url={}", url);
                String redirectUrl = java.net.URLEncoder.encode(url, "UTF-8");
                response.sendRedirect(basePath(request) + "/user/login.do?path=" + redirectUrl);
                return null;
            }



        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * 判断用户请求是否是可放行通过的特殊请求<br />私有方法
     * @param proceedingJoinPoint
     * @return
     */
    private boolean isInclude(ProceedingJoinPoint proceedingJoinPoint) {
        //获取访问目标方法
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        return targetMethod.isAnnotationPresent(IncludeAuthorize.class);
    }

    private void loadRequestAndResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        ThreadContent.set(request, response);
    }

    /**
     * 获取项目访问基路径<br />私有方法
     * @param request HttpServletRequest对象
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private String basePath(HttpServletRequest request) throws MalformedURLException, URISyntaxException {
        //访问基路径
        String basePath = "http" + "://"+ request.getServerName() + ":"+ request.getServerPort() + request.getContextPath();
        URL urlObj = new URL(basePath);
        if (urlObj.getPort() == urlObj.getDefaultPort()) {
            urlObj = new URL(urlObj.getProtocol(), urlObj.getHost(), -1, urlObj.getPath());
        }
        basePath = urlObj.toURI().toString();
        return basePath;
    }

}
