package com.imeeting.mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.imeeting.framework.ContextLoader;
import com.imeeting.mvc.model.addressbook.AddressBookDAO;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.richitec.util.Pager;

@Controller
@RequestMapping(value = "/addressbook")
public class AddressbookController {
	private static Log log = LogFactory.getLog(AddressbookController.class);

	private AddressBookDAO abDao;
	private static final int PageSize = 50;

	@PostConstruct
	public void init() {
		abDao = ContextLoader.getAddressBookDAO();
	}

	@RequestMapping("/upload")
	public void upload(
			@RequestParam(value = "contacts", required = true) String contacts,
			@RequestParam(value = "groups", required = false) String groups,
			@RequestParam(value = "username", required = true) String userName,
			HttpServletResponse response) {
		BasicDBList contactsJsonArray = (BasicDBList) JSON.parse(contacts);
		List<DBObject> contactsList = new ArrayList<DBObject>();
		for (Object obj : contactsJsonArray) {
			contactsList.add((DBObject) obj);
		}

		List<DBObject> groupsList = null;
		if (groups != null) {
			BasicDBList groupsJsonArray = (BasicDBList) JSON.parse(groups);
			groupsList = new ArrayList<DBObject>();
			for (Object obj : groupsJsonArray) {
				groupsList.add((DBObject) obj);
			}
		}
		abDao.saveAddressBook(userName, contactsList, groupsList);
	}

	@RequestMapping("/contactList")
	public void contactList(
			HttpServletResponse response,
			@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
			@RequestParam(value = "username", required = true) String userName,
			@RequestParam(value = "groupId", required = false) String groupId)
			throws JSONException, IOException {
		long totalCount = abDao.getContactsCount(userName, groupId);
		List<DBObject> contactList = abDao.getContacts(userName, groupId,
				offset, PageSize);
		String contactsJson = contactList.toString();
		log.info("contact list: " + contactsJson);
		JSONArray contactsArray = new JSONArray(contactsJson);

		String url = "/addressbook/contactList" + "?";
		Pager pager = new Pager(offset, PageSize, (int) totalCount, url);

		JSONObject ret = new JSONObject();
		JSONObject jsonPager = new JSONObject();
		jsonPager.put("offset", pager.getOffset());
		jsonPager.put("pagenumber", pager.getPageNumber());
		jsonPager.put("hasPrevious", pager.getHasPrevious());
		jsonPager.put("hasNext", pager.getHasNext());
		jsonPager.put("previousPage", pager.getPreviousPage());
		jsonPager.put("nextPage", pager.getNextPage());
		jsonPager.put("count", pager.getSize());
		ret.put("pager", jsonPager);
		ret.put("list", contactsArray);

		response.getWriter().print(ret.toString());

	}

	@RequestMapping("/search")
	public void search(HttpServletResponse response,
			@RequestParam(value = "username", required = true) String userName,
			@RequestParam(value = "groupId", required = false) String groupId,
			@RequestParam String searchWord) throws IOException {
		List<DBObject> contactList = abDao.searchContact(userName, groupId, searchWord);
		response.getWriter().print(contactList.toString());
	}
}
