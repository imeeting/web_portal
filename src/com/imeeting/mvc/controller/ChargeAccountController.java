package com.imeeting.mvc.controller;

import java.io.IOException;
import java.sql.SQLException;
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
import org.json.JSONException;
import org.json.JSONObject;
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
import com.richitec.sms.client.SMSClient;
import com.richitec.ucenter.model.UserDAO;
import com.richitec.util.Pager;
import com.richitec.util.RandomString;
import com.richitec.vos.client.AccountInfo;
import com.richitec.vos.client.CurrentSuiteInfo;
import com.richitec.vos.client.VOSClient;
import com.richitec.vos.client.VOSHttpResponse;

@Controller
public class ChargeAccountController {
	private static Log log = LogFactory.getLog(ChargeAccountController.class);

	private VOSClient vosClient;
	private ChargeDAO chargeDao;
	private UserDAO userDao;
	private SMSClient smsClient;

	@PostConstruct
	public void init() {
		vosClient = ContextLoader.getVOSClient();
		chargeDao = ContextLoader.getChargeDAO();
		userDao = ContextLoader.getUserDAO();
		smsClient = ContextLoader.getSMSClient();
	}

	@RequestMapping(value = "/deposite", method = RequestMethod.GET)
	public ModelAndView deposite() {
		ModelAndView view = new ModelAndView();
		view.setViewName("deposite");
		view.addObject(WebConstants.page_name.name(), "deposite");
		return view;
	}

	/**
	 * 充值卡号前四位表示该卡的面额
	 * 
	 * @param response
	 * @param account
	 * @param pin
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	@RequestMapping(value = "/zhihuicard", method = RequestMethod.POST)
	public ModelAndView zhihuiCard(HttpServletResponse response,
			@RequestParam(value = "account_name") String account,
			@RequestParam(value = "pin") String pin,
			@RequestParam(value = "password") String password)
			throws IOException, SQLException {
		ModelAndView mv = new ModelAndView();
		boolean isExist = userDao.isExistsLoginName(account);
		if (!isExist) {
			mv.setViewName("accountcharge/invalidAccount");
			return mv;
		}

		if (pin.length() < 4) {
			mv.setViewName("accountcharge/invalidPin");
			return mv;
		}

		Double value = 0.0;
		String cardValue = pin.substring(0, 4);
		try {
			value = Double.parseDouble(cardValue);
		} catch (NumberFormatException e) {
			mv.setViewName("accountcharge/invalidPin");
			return mv;
		}

		String chargeId = pin + "_" + RandomString.genRandomChars(10);
		VOSHttpResponse vosResp = vosClient.depositeByCard(account, pin,
				password);
		if (vosResp.getHttpStatusCode() != 200 || !vosResp.isOperationSuccess()) {
			chargeDao.addChargeRecord(chargeId, account, value,
					ChargeStatus.vos_fail);
			log.error("\nCannot deposite to account <" + account
					+ "> with card <" + pin + ">" + "<" + password + ">"
					+ "\nVOS Http Response : " + vosResp.getHttpStatusCode()
					+ "\nVOS Status Code : " + vosResp.getVOSStatusCode()
					+ "\nVOS Response Info ：" + vosResp.getVOSResponseInfo());
		}

		mv.addObject("vosResponse", vosResp);
		if (vosResp.isOperationSuccess()) {
			/*
			 * log.info("VOS INFO : " + vosResp.getVOSResponseInfo());
			 * DepositeCardInfo info = new
			 * DepositeCardInfo(vosResp.getVOSResponseInfo());
			 * mv.addObject("despositeInfo", info);
			 */
			chargeDao.addChargeRecord(chargeId, account, value,
					ChargeStatus.success);
			smsClient.sendTextMessage(account, "您的智会账户已成功充值" + value + "元，谢谢！");
		}

		mv.setViewName("accountcharge/vosComplete");
		return mv;
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
		view.addObject(WebConstants.balance.name(), 
				vosClient.getAccountBalance(userBean.getUserName()));

		// get charge history list
		int total = chargeDao.getChargeListTotalCount(userBean.getUserName());
		int pageSize = 10;
		List<Map<String, Object>> chargeList = chargeDao.getChargeList(
				userBean.getUserName(), offset, pageSize);

		String url = "accountcharge?";
		Pager pager = new Pager(offset, pageSize, total, url);
		view.addObject(WebConstants.pager.name(), pager);
		view.addObject(WebConstants.charge_list.name(), chargeList);
		return view;
	}

	@RequestMapping(value = "/alipay", method = RequestMethod.POST)
	public ModelAndView aliPay(HttpSession session,
			@RequestParam(value = "account_name") String accountName,
			@RequestParam(value = "charge_amount") String chargeAmount)
			throws Exception {
		log.info("****** prepay alipay ******");
		boolean isExist = userDao.isExistsLoginName(accountName);
		ModelAndView mv = new ModelAndView();
		if (isExist) {
			mv.setViewName("accountcharge/alipay");
		} else {
			mv.setViewName("accountcharge/invalidAccount");
		}
		return mv;
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
				String accountName = ChargeUtil.finishCharge(order_no,
						total_fee);
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

	// API urls
	
	/**
	 * get account balance, used for API
	 * @param response
	 * @param userName
	 * @throws JSONException
	 * @throws IOException
	 */
	@RequestMapping("/accountBalance")
	public void accountBalance(HttpServletResponse response,
			@RequestParam(value = "username") String userName)
			throws JSONException, IOException {
		// get account balance
		JSONObject ret = new JSONObject();
		Double value = vosClient.getAccountBalance(userName);
		ret.put("result", null==value ? 0 : 1);
		if (null != value){
			ret.put(WebConstants.balance.name(), value);
		}
		response.getWriter().print(ret.toString());
	}

	/**
	 * charge with card, used for API
	 * @param response
	 * @param userName
	 * @param pin
	 * @param password
	 * @throws IOException
	 * @throws SQLException
	 */
	@RequestMapping(value = "/cardCharge", method = RequestMethod.POST)
	public void cardCharge(HttpServletResponse response,
			@RequestParam(value = "username") String userName,
			@RequestParam(value = "pin") String pin,
			@RequestParam(value = "password") String password)
			throws IOException, SQLException {
		boolean isExist = userDao.isExistsLoginName(userName);
		if (!isExist) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (pin.length() < 4) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Double value = 0.0;
		String cardValue = pin.substring(0, 4);
		try {
			value = Double.parseDouble(cardValue);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String chargeId = pin + "_" + RandomString.genRandomChars(10);
		VOSHttpResponse vosResp = vosClient.depositeByCard(userName, pin,
				password);
		if (vosResp.getHttpStatusCode() != 200 || !vosResp.isOperationSuccess()) {
			chargeDao.addChargeRecord(chargeId, userName, value,
					ChargeStatus.vos_fail);
			log.error("\nCannot deposite to account <" + userName
					+ "> with card <" + pin + ">" + "<" + password + ">"
					+ "\nVOS Http Response : " + vosResp.getHttpStatusCode()
					+ "\nVOS Status Code : " + vosResp.getVOSStatusCode()
					+ "\nVOS Response Info ：" + vosResp.getVOSResponseInfo());
		}

		if (vosResp.isOperationSuccess()) {
			chargeDao.addChargeRecord(chargeId, userName, value,
					ChargeStatus.success);
			smsClient
					.sendTextMessage(userName, "您的智会账户已成功充值" + value + "元，谢谢！");
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.sendError(HttpServletResponse.SC_CONFLICT);
		}

	}
}
