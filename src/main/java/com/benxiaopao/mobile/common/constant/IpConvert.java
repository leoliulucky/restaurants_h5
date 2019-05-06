package com.benxiaopao.mobile.common.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP转换Long型数组进行比较工具
 * 
 */

public class IpConvert
{

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(IpConvert.class);

	/**
	 * 获取客户端浏览器的ip地址，目前只支持IPV4
	 */
	public static String getIpAddr(HttpServletRequest request)
	{
		List<String> IPs = new ArrayList<String>();
		String ip = request.getHeader("x-forwarded-for");

		logger.debug("获取客户端浏览器ip地址 start：");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.indexOf(":") >= 0)
		{ // 判断是否为IPV6地址
			ip = "127.0.0.1";
		}
		Matcher matcher = Pattern.compile("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)"+
					"(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}")
					.matcher(ip);
		while (matcher.find()) {
			String tmp = matcher.group();
			IPs.add(tmp);
			logger.info("请求中的IP地址" + tmp);
		}
		logger.debug("获取客户端浏览器ip地址 end：IP="+ip);
		
		return (IPs != null && !IPs.isEmpty()) ? IPs.get(0) : null;
	}

	// 將IP地址转换成10进制整数
	public static long IpToLong(String strIp) throws ParseException
	{
		if (strIp == null || strIp.equals(""))
			throw new ParseException("null parameters when parsing ip address",
					0);
		logger.debug("Ip地址转换成10进制整数 start：");
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		String[] slots = strIp.split("\\.");
		// 将每个.之间的字符串转换成整型
		if (slots.length != 4)
			throw new ParseException("wrong ip address string", 0);
		for (int i = 0; i < 4; i++) {
			try {
				ip[i] = Long.parseLong(slots[i]);
			} catch (NumberFormatException e) {
				throw new ParseException("wrong ip address slot format"
						+ slots[i], i);
			}
		}
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将十进制整数形式转换成127.0.0.1形式的ip地址
	public static String LongToIP(Long longIp)
	{
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	public static void main(String args[])
	{
		String ip = new String("192.168.1.1");
		Long ipint = null;
		try {
			ipint = IpConvert.IpToLong(ip);
		}catch (Exception e) {
			logger.error("ip错误", e);
		}
		logger.info("The number str =" + ipint);
		String ipstr = IpConvert.LongToIP(ipint);
		logger.info("The number Long =" + ipstr);

	}

}
