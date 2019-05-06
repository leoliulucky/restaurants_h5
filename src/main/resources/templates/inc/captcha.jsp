<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page language="java" import="java.util.AbstractMap.SimpleEntry"%>
<%@ page language="java" import="com.netro.web.common.supers.BaseConstant"%>
<%@ page language="java" import="com.netro.web.common.util.CaptchaUtil"%>
<%@ page language="java" import="java.io.OutputStream" %>
<%
	/**
	 * 验证码请求jsp页面， 用法说明(注：使用中有任何问题请联系 liupoyang)：
	 *		1、页面中img标签只要引入该文件，即可显示验证码图片；代码如下：
	 *			<img src="${basePath}/views/global/captcha.jsp" />
	 *		2、请在Controller中调用 captcha() 方法获取验证码的值，用作校验处理
	 *
	 */
	SimpleEntry<String, byte[]> entry = CaptchaUtil.generate();
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
	out.clear(); 
	out = pageContext.pushBody(); 
%>
