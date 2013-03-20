package com.imeeting.mvc.model.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactDAO {
	
	private static Log log = LogFactory.getLog(ContactDAO.class);
	
	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}
	
	public void saveJSONContact(String owner, JSONArray jsonArray) throws JSONException{
		List<ContactBean> contactList = getContactList(owner);

		for(int i=0; i<jsonArray.length(); i++){
			JSONObject contact = jsonArray.getJSONObject(i);
			String email = contact.getString("email");
			String phone = contact.getString("phone");
			String nickname = contact.getString("nickname");
			boolean isNewContact = true;
			for(ContactBean c : contactList){
				if( (email != null && email.length()>0 && email.equals(c.getEmail()) ) || 
					(phone != null && phone.length()>0 && phone.equals(c.getPhone()) ) )
				{
					updateContactCount(c);
					isNewContact = false;
					break;
				}
			}
			
			if (isNewContact){
				insertContact(owner, nickname, email, phone);
			}
		}
	}	
	
	private int insertContact(String owner, String nickname, String email, String phone){
		String sql = "INSERT INTO im_contact (owner, nickname, email, phone) VALUES (?,?,?,?)";
		return jdbc.update(sql, owner, nickname, email, phone);
	}
	
	private int updateContactCount(ContactBean contact){
		String sql = "UPDATE im_contact SET count=? WHERE id=?";
		return jdbc.update(sql, contact.getCount()+1, contact.getId());
	}	
	
	public List<ContactBean> getContactList(String owner){
		return getContactList(owner, null);
	}
	
	public List<ContactBean> getContactList(String owner, String word){
		String sql = "SELECT id, nickname, email, phone, count FROM im_contact WHERE owner = '" + owner + "'";
		if (null != word && word.length()>0){
			sql = sql + " AND ( " +
				"nickname LIKE '%" + word + "%' OR email LIKE '%" + word + "%' OR phone LIKE '%" + word + "%' )";;
		}
		sql = sql + " ORDER BY count DESC";
		log.info(sql);
		List<ContactBean> contactList = jdbc.query(sql, new RowMapper<ContactBean>(){
			@Override
			public ContactBean mapRow(ResultSet rs, int arg1) throws SQLException {
				ContactBean contact = new ContactBean();
				contact.setId(rs.getInt("id"));
				contact.setNickName(rs.getString("nickname"));
				contact.setEmail(rs.getString("email"));
				contact.setPhone(rs.getString("phone"));
				contact.setCount(rs.getInt("count"));
				return contact;
			}});
		
		return contactList;
	}
}
