package com.imeeting.mvc.model.charge;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imeeting.constants.ChargeStatus;
import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.controller.ChargeAccountController;
import com.richitec.util.RandomString;
import com.richitec.vos.client.VOSClient;
import com.richitec.vos.client.VOSHttpResponse;

public class ChargeUtil {
	private static Log log = LogFactory.getLog(ChargeUtil.class);
	/**
	 * 得到订单号
	 * 
	 * @param type - pay type (alipay, netbank, card)
	 * @param accountName - account to charge
	 * @return
	 */
	public static String getOrderNumber(String type, String accountName) {
		Date currTime = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("_yyyyMMdd_HHmmss_",
				Locale.US);
		return type + sf.format(currTime) + accountName + "_"
				+ RandomString.validateCode();
	}
	
	public static String finishCharge(String chargeId, String money) {
		ChargeDAO chargeDao = ContextLoader.getChargeDAO();
		Map<String, Object> chargeInfo = chargeDao.getChargeInfoById(chargeId);
		if (chargeInfo == null) {
			return null;
		}
		String userName = (String) chargeInfo.get("username");
		if (userName == null) {
			return null;
		}
		String status = (String) chargeInfo.get("status");
		if (ChargeStatus.success.name().equals(status)) {
			return userName;
		}
		
		Double amount = Double.valueOf(money);
		VOSClient vosClient = ContextLoader.getVOSClient();
		VOSHttpResponse response = vosClient.deposite(userName, amount);
		if (response.isOperationSuccess()) {
			log.info("vos deposite success");
			chargeDao.updateChargeRecord(chargeId, ChargeStatus.success);
			return userName;
		} else {
			log.info("vos deposite fail");
			chargeDao.updateChargeRecord(chargeId, ChargeStatus.vos_fail);
			return null;
		}
	}
}
