<%@page import="com.imeeting.framework.ContextLoader"%>
<%@page import="com.imeeting.constants.WebConstants"%>
<%@page import="com.imeeting.mvc.model.charge.ChargeUtil"%>
<%
	/* *
	 *功能：即时到帐接口接入页
	 *版本：3.2
	 *日期：2011-03-17
	 *说明：
	 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
	 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

	 *************************注意*****************
	 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
	 *1、商户服务中心（https://b.alipay.com/support/helperApply.htm?action=consultationApply），提交申请集成协助，我们会有专业的技术工程师主动联系您协助解决
	 *2、商户帮助中心（http://help.alipay.com/support/232511-16307/0-16307.htm?sh=Y&info_type=9）
	 *3、支付宝论坛（http://club.alipay.com/read-htm-tid-8681712.html）
	 *如果不想使用扩展功能请把扩展功能参数赋空值。
	 **********************************************
	 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.alipay.services.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支付宝即时到帐接口</title>
</head>
<%
	String accountName = request.getParameter("account_name");
	//请与贵网站订单系统中的唯一订单号匹配
	String out_trade_no = ChargeUtil.getOrderNumber("alipay", accountName);
	//订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
	String body = "智会账户充值";
	//订单总金额，显示在支付宝收银台里的“应付总额”里
	String total_fee = request.getParameter("charge_amount");
	
	ContextLoader.getChargeDAO().addChargeRecord(out_trade_no, accountName, Float.valueOf(total_fee));
	
	//把请求参数打包成数组
	Map<String, String> sParaTemp = new HashMap<String, String>();
	sParaTemp.put("payment_type", "1");
	sParaTemp.put("out_trade_no", out_trade_no);
	sParaTemp.put("subject", out_trade_no);
	sParaTemp.put("body", body);
	sParaTemp.put("total_fee", total_fee);

	//构造函数，生成请求URL
	String sHtmlText = AlipayService
			.create_direct_pay_by_user(sParaTemp);
	out.println(sHtmlText);
%>
<body>
</body>
</html>
