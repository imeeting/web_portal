package com.imeeting.mvc.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imeeting.constants.WebConstants;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.charge.ChargeDAO;
import com.imeeting.web.user.UserBean;
import com.richitec.util.Pager;
import com.richitec.vos.client.AccountInfo;
import com.richitec.vos.client.CurrentSuiteInfo;
import com.richitec.vos.client.VOSClient;

@Controller
public class ChargeAccountController {
	private static Log log = LogFactory.getLog(ChargeAccountController.class);
	
	private VOSClient vosClient;

	@PostConstruct
	public void init() {
		vosClient = ContextLoader.getVOSClient();
	}

	@RequestMapping(value = "/deposite", method = RequestMethod.GET)
	public ModelAndView deposite() {
		ModelAndView view = new ModelAndView();
		view.setViewName("deposite");
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
		ChargeDAO chargeDao = ContextLoader.getChargeDAO();
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
}
