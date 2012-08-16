package com.imeeting.mvc.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.util.AlipayNotify;
import com.imeeting.constants.ChargeStatus;
import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.charge.ChargeDAO;
import com.imeeting.mvc.model.charge.ChargeUtil;
import com.imeeting.web.user.UserBean;
import com.richitec.util.Pager;
import com.richitec.vos.client.AccountInfo;
import com.richitec.vos.client.CurrentSuiteInfo;
import com.richitec.vos.client.VOSClient;

@Controller
public class ChargeAccountController {
	private static Log log = LogFactory.getLog(ChargeAccountController.class);
	
	private VOSClient vosClient;
	private ChargeDAO chargeDao;
	
	@PostConstruct
	public void init() {
		vosClient = ContextLoader.getVOSClient();
		chargeDao = ContextLoader.getChargeDAO();
	}

	@RequestMapping(value = "/deposite", method = RequestMethod.GET)
	public ModelAndView deposite() {
		ModelAndView view = new ModelAndView();
		view.setViewName("deposite");
		view.addObject(WebConstants.page_name.name(), "deposite");
		return view;
	}

	@RequestMapping(value = "/accountcharge", method = RequestMethod.GET)
	public ModelAndView accountCharge(
			HttpSession session,
			@RequestParam(value = "offset", required = false, defaultValue = "1") int offset) {
		ModelAndView view = new ModelAndView();
		view.setViewName("accountcharge");
		view.addObject(WebConstants.page_name.name(), "accountcharge");
		// get account
		UserBean userBean = (UserBean) session
				.getAttribute(UserBean.SESSION_BEAN);

		// get account balance
		AccountInfo accountInfo = vosClient.getAccountInfo(userBean.getName());
		CurrentSuiteInfo suiteInfo = vosClient.getCurrentSuite(userBean
				.getName());
		if (accountInfo != null && suiteInfo != null) {
			Double balance = accountInfo.getBalance()
					+ suiteInfo.getGiftBalance();
			view.addObject(WebConstants.balance.name(), balance);
		} else {
			view.addObject(WebConstants.balance.name(), new Double(-1));
		}

		// get charge history list
		int total = chargeDao.getChargeListTotalCount(userBean.getName());
		int pageSize = 10;
		List<Map<String, Object>> chargeList = chargeDao.getChargeList(
				userBean.getName(), offset, pageSize);

		String url = "accountcharge?";
		Pager pager = new Pager(offset, pageSize, total, url);
		view.addObject(WebConstants.pager.name(), pager);
		view.addObject(WebConstants.charge_list.name(), chargeList);
		return view;
	}
	
	@RequestMapping(value = "/alipay", method = RequestMethod.POST)
	public String aliPay(HttpSession session) throws Exception {
		log.info("****** prepay alipay ******");
		return "accountcharge/alipay";
	}
	
	/**
	 * 支付宝异步返回URL
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/alipayComplete")
	public @ResponseBody
	String aliPayComplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("****** alipay complete ******");
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}

		String order_no = request.getParameter("out_trade_no"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		String trade_status = request.getParameter("trade_status"); // 交易状态

		log.info("trade_status: " + trade_status);
		if (AlipayNotify.verify(params)) {
			if (trade_status.equals("TRADE_FINISHED")
					|| trade_status.equals("TRADE_SUCCESS")) {
				ChargeUtil.finishCharge(order_no, total_fee);
			} else {
				chargeDao.updateChargeRecord(order_no, ChargeStatus.fail);
			}
			return "success";
		} else {
			chargeDao.updateChargeRecord(order_no, ChargeStatus.fail);
			return "fail";
		}
	}
	
	/**
	 * 支付宝同步返回URL
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/alipayReturn")
	public ModelAndView aliPayReturn(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		log.info("****** alipay return ******");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("accountcharge/receive");
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		String order_no = request.getParameter("out_trade_no"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		String trade_status = request.getParameter("trade_status"); // 交易状态

		if (AlipayNotify.verify(params)) {
			if (trade_status.equals("TRADE_FINISHED")
					|| trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理（可参考“集成教程”中“3.4返回数据处理”）
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				String accountName = ChargeUtil.finishCharge(order_no, total_fee);
				mv.addObject("result", "0");
				mv.addObject(WebConstants.charge_money.name(), total_fee);
				mv.addObject(WebConstants.pay_account_name.name(), accountName);
			} else {
				mv.addObject("result", "1");
			}
		} else {
			// 该页面可做页面美工编辑
			mv.addObject("result", "1");
		}
		return mv;
	}
	
}
