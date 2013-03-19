package com.imeeting.mvc.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.addressbook.ContactBean;
import com.imeeting.mvc.model.addressbook.ContactDAO;
import com.imeeting.web.user.UserBean;

@Controller
@RequestMapping(value = "/contact")
public class ContactController {
	
	private ContactDAO contactDao;
	
	@PostConstruct
	public void init() {
		contactDao = ContextLoader.getContactDAO();
	}

	@RequestMapping(value="/search")
	public void search(
			HttpServletResponse response,
			HttpSession session,
			@RequestParam String searchWord )
			throws JSONException, IOException {
		UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
		List<ContactBean> contactList = contactDao.getContactList(user.getUserName(), searchWord);
		JSONArray array = new JSONArray();
		for (ContactBean c : contactList){
			JSONObject jo = new JSONObject();
			jo.put("nickname", c.getNickName());
			jo.put("phone", c.getPhone());
			jo.put("email", c.getEmail());
			array.put(jo);
		}
		response.getWriter().print(array.toString());
	}
}
