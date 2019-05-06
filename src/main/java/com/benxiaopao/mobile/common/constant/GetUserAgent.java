package com.benxiaopao.mobile.common.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户端浏览器类型、操作系统类型判断
 */
public class GetUserAgent {

	private static final long serialVersionUID = 3451599798241628607L;

	private final static String IE9 = "MSIE 9.0";
	private final static String IE8 = "MSIE 8.0";
	private final static String IE7 = "MSIE 7.0";
	private final static String IE6 = "MSIE 6.0";
	private final static String MAXTHON = "Maxthon";
	private final static String QQ = "QQBrowser";
	private final static String GREEN = "GreenBrowser";
	private final static String SE360 = "360SE";
	private final static String FIREFOX = "Firefox";
	private final static String OPERA = "Opera";
	private final static String CHROME = "Chrome";
	private final static String SAFARI = "Safari";
	private final static String SOGOU = "SE 2.X";
	
	/**
     * 浏览器类型
     */
	private static final short BROWSER_TYPE_IE6 = 100;
	private static final short BROWSER_TYPE_IE7 = 110;
	private static final short BROWSER_TYPE_IE8 = 120;
	private static final short BROWSER_TYPE_IE9 = 130;
	private static final short BROWSER_TYPE_IE10 = 140;
	private static final short BROWSER_TYPE_IE11 = 150;
	private static final short BROWSER_TYPE_IE12 = 160;
	private static final short BROWSER_TYPE_FIREFOX = 200;
	private static final short BROWSER_TYPE_CHROME = 300;
	private static final short BROWSER_TYPE_OPERA = 400;
	private static final short BROWSER_TYPE_360 = 500;
	private static final short BROWSER_TYPE_MAXTHON = 600;
	private static final short BROWSER_TYPE_QQ = 610;
	private static final short BROWSER_TYPE_GREEN = 620;
	private static final short BROWSER_TYPE_SAFARI = 630;
	private static final short BROWSER_TYPE_SOGOU = 640;
	private static final short BROWSER_TYPE_OTHER = 999;

    /**
     * 操作系统类型
     */
	private static final short OS_TYPE_WIN95 = 100;
	private static final short OS_TYPE_WIN98 = 110;
	private static final short OS_TYPE_WINME = 120;
	private static final int OS_TYPE_WINNT4 = 130;
	private static final short OS_TYPE_WIN2K = 140;
	private static final short OS_TYPE_WINXP = 150;
	private static final short OS_TYPE_WIN7 = 160;
	private static final short OS_TYPE_WIN8 = 170;
	private static final short OS_TYPE_WIN9 = 180;
	private static final short OS_TYPE_WIN2003 = 200;
	private static final short OS_TYPE_WIN2008 = 210;
	private static final short OS_TYPE_LINUX = 300;
	private static final short OS_TYPE_MAC = 400;
	private static final short OS_TYPE_UNIX = 500;
	private static final short OS_TYPE_IOS = 600;
	private static final short OS_TYPE_ANDROID = 700;
	private static final short OS_TYPE_SUNOS = 800;
	private static final short OS_TYPE_OTHER = 900;
	
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(GetUserAgent.class);
	
	/**
	 * 判断客户端浏览器类型
	 * @param userAgent 客户端浏览器 返回数值得含义参见com.fuscent.core.consts.user.UserAgentType
	 */
	public static short checkBrowser(String userAgent) {

		logger.debug("浏览器类型判断 start："+userAgent);
		if (regex(IE9, userAgent))
			return BROWSER_TYPE_IE9;
		if (regex(IE8, userAgent))
			return BROWSER_TYPE_IE8;
		if (regex(IE7, userAgent))
			return BROWSER_TYPE_IE7;
		if (regex(IE6, userAgent))
			return BROWSER_TYPE_IE6;
		if (regex(MAXTHON, userAgent))
			return BROWSER_TYPE_MAXTHON;
		if (regex(QQ, userAgent))
			return BROWSER_TYPE_QQ;
		if (regex(GREEN, userAgent))
			return BROWSER_TYPE_GREEN;
		if (regex(SE360, userAgent))
			return BROWSER_TYPE_360;
		if (regex(FIREFOX, userAgent))
			return BROWSER_TYPE_FIREFOX;
		if (regex(OPERA, userAgent))
			return BROWSER_TYPE_OPERA;
		if (regex(CHROME, userAgent))
			return BROWSER_TYPE_CHROME;
		if (regex(SAFARI, userAgent))
			return BROWSER_TYPE_SAFARI;
		if(regex(SOGOU,userAgent))
			return BROWSER_TYPE_SOGOU;
		else
			return 0;
	}

	/**
	 * 根据正则表达式匹配字符串
	 * @param regex 正则表达式字符串
	 * @param str 浏览器字符形参
	 * @return 返回浏览器判断boolean值结果
	 */
	public static boolean regex(String regex, String str) {
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 判断客户端操作系统类型
	 * @param userAgent 客户端操作系统 返回数值得含义参见com.fuscent.core.consts.user.GetOsVersionType;
	 * @return
	 */
	public static short GetOSNameByUserAgent(String userAgent) {
		
		logger.debug("操作系统类型判断 start："+userAgent);
		if (userAgent.contains("NT 6.2")) {
			return OS_TYPE_WIN8;  // Windows 8/Windows Server 2012
		} else if (userAgent.contains("NT 6.1")) {
			return OS_TYPE_WIN7;  // Windows 7/Windows Server 2008 R2
		} else if (userAgent.contains("NT 6.0")) {
			return OS_TYPE_WIN2008; // Windows Vista/Server 2008  
		} else if (userAgent.contains("NT 5.2")) {
			return OS_TYPE_WIN2003; // Windows Server 2003 
		} else if (userAgent.contains("NT 5.1")) {
			return OS_TYPE_WINXP; // Windows XP 
		} else if (userAgent.contains("NT 5")) {
			return OS_TYPE_WIN2K; // Windows 2000 
		} else if (userAgent.contains("NT 4")) {
			return OS_TYPE_WINNT4; // Windows NT4 
		} else if (userAgent.contains("Me")) {
			return OS_TYPE_WINME; // Windows Me 
		} else if (userAgent.contains("98")) {
			return OS_TYPE_WIN98; // Windows 98 
		} else if (userAgent.contains("95")) {
			return OS_TYPE_WIN95; // Windows 95 
		} else if (userAgent.contains("Mac")) {
			return OS_TYPE_MAC; // Mac 
		} else if (userAgent.contains("Unix")) {
			return OS_TYPE_UNIX; // UNIX 
		} else if (userAgent.contains("Linux")) {
			return OS_TYPE_LINUX; // Linux 
		} else if (userAgent.contains("SunOS")) {
			return OS_TYPE_SUNOS; //SunOS 
		} else 
			return 0;
	}

}
