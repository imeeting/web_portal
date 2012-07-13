package com.richitec.ucenter.model;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.imeeting.framework.ContextLoader;
import com.richitec.sms.client.SMSHttpResponse;
import com.richitec.util.MD5Util;
import com.richitec.util.RandomString;
import com.richitec.util.ValidatePattern;

public class UserDAO {
	private static Log log = LogFactory.getLog(UserDAO.class);
	public static final String SESSION_BEAN = "userBean";
	public static final String PASSWORD_STR = "huuguanghui";
	
	private JdbcTemplate jdbc;
	
	public void setDataSource(DataSource ds){
		jdbc = new JdbcTemplate(ds);
	}

	/**
	 * 获得手机验证码
	 * 
	 * @param session
	 * @param phone
	 * @param phoneCode
	 * @return
	 */
	public String getPhoneCode(HttpSession session, String phone) {
		String result = "0";
		String phoneCode = RandomString.validateCode();
		log.info("phone code: " + phoneCode);
		try {
			session.setAttribute("phonenumber", phone);
			session.setAttribute("phonecode", phoneCode);
			String content = "验证码：" + phoneCode + " [iMeeting]";
			SMSHttpResponse response = ContextLoader.getSMSClient()
					.sendTextMessage(phone, content);
			log.info("sms return: " + response.getCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 注册
	 * 
	 * @param session
	 * @param phone
	 * @param password
	 * @param password1
	 * @param code
	 * @return
	 */
	public String regUser(String phone, String password, String password1) {
		String result = checkRegisterUser(phone, password, password1);
		if (result.equals("0")) {
			String userkey = MD5Util.md5(phone + password);
			String sql = "INSERT INTO im_user(username, password, userkey) VALUE (?,?,?)";
			Object[] params = new Object[] { Long.parseLong(phone),
					MD5Util.md5(password), userkey };
			int resultCount = jdbc.update(sql, params);
			result = resultCount > 0 ? "0" : "1001";
		}
		return result;
	}

	/**
	 * 登录
	 * 
	 * @param session
	 * @param loginName
	 * @param loginPwd
	 * @return
	 * @throws JSONException
	 */
	public JSONObject login(String loginName, final String loginPwd) throws DataAccessException, JSONException {
		String sql = "SELECT userkey FROM im_user WHERE username=? AND password=?";
		Object[] params = new Object[] { loginName, loginPwd };
		String result = null;
		JSONObject ret = new JSONObject();
		try {
			String userkey = jdbc.queryForObject(sql, params, String.class);
			if (null != userkey){
				result = "0";
				ret.put("result", result);
				ret.put("userkey", userkey);
			}
		} catch (EmptyResultDataAccessException e) {
			result = "1";
			ret.put("result", result);
		} catch (DataAccessException e) {
			throw e;
		}
		return ret;
	}

	/**
	 * record the device info of login user
	 * 
	 * @param username
	 * @param brand
	 * @param model
	 * @param release
	 * @param sdk
	 * @param width
	 * @param height
	 */
	public void recodeDeviceInfo(String username, String brand,
			String model, String release, String sdk, String width,
			String height) {
		log.info("record device info - username:  " + username + " brand: "
				+ brand);

		String sql = "SELECT count(username) FROM fy_device_info WHERE username = ?";
		int count = jdbc.queryForInt(sql, username);
		if (count > 0){
			jdbc.update("UPDATE fy_device_info SET brand=?, model=?, " +
					"release_ver=?, sdk=?, width=?, height=? WHERE username = ?",
					brand, model, release, sdk, width, height, username);
		} else {
			jdbc.update("NSERT INTO fy_device_info VALUES(?,?,?,?,?,?,?)",
					 username, brand, model, release, sdk, width, height );
		}
	}

	public String checkPhoneCode(HttpSession session, String code) {
		if (code.equals("")) {
			return "1"; // 验证码必�?
		} else if (!code.equals(session.getAttribute("phonecode"))) {
			return "2"; // 验证码错�?
		} else {
			return "0";
		}
	}

	/**
	 * 判断用户注册信息是否正确
	 * 
	 * @param session
	 * @param phone
	 * @param password
	 * @param password1
	 * @param code
	 * @return
	 */
	public String checkRegisterUser(String phone, String password,
			String password1) {
		try {
			if (phone.equals("")) {
				return "1"; // 手机号码必填
			} else if (!ValidatePattern.isValidMobilePhone(phone)) {
				return "2"; // 手机号码格式错误
			} else if (isExistsLoginName(phone)) {
				return "3"; // 手机号码已存�?
			} else if (password.equals("")) {
				return "4"; // 密码必填
			} else if (!password.equals(password1)) {
				return "5"; // 两次密码输入不一�?
			} else {
				return "0";
			}
		} catch (Exception e) {
			return "1001";
		}

	}

	/**
	 * 判断手机号码是否正确
	 * 
	 * @param phone
	 * @return
	 */
	public String checkRegisterPhone(String phone) {
		try {
			if (phone.equals("")) {
				return "1"; // 手机号码必填
			} else if (!ValidatePattern.isValidMobilePhone(phone)) {
				return "2"; // 手机号码格式错误
			} else if (isExistsLoginName(phone)) {
				return "3"; // 手机号码已存�?
			} else {
				return "0";
			}
		} catch (Exception e) {
			return "1001";
		}
	}

	/**
	 * 判断该用户名是否存在
	 * 
	 * @param loginName
	 * @return
	 * @throws SQLException
	 */
	private boolean isExistsLoginName(String loginName)
			throws SQLException {
		String sql = "SELECT count(username) FROM im_user WHERE username = ?";
		Object[] params = new Object[] { loginName };
		return jdbc.queryForInt(sql, params) > 0;
	}

	/**
	 * 获得userkey
	 * 
	 * @param phone
	 * @return
	 */
	public String getUserKey(String phone) {
		String sql = "SELECT userkey FROM im_user WHERE username = ?";
		Object[] params = new Object[] { phone };
		return jdbc.queryForObject(sql, params, String.class);
	}

}
