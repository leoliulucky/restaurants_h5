package com.benxiaopao.mobile.common.aspect;

import java.lang.annotation.*;

/**
 * 自定义注解：指定需要认证权限的请求方法注解
 *
 * Created by liupoyang
 * 2019-05-02
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IncludeAuthorize {
    String value() default "";
}
