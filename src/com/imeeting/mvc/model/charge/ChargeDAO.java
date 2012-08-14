package com.imeeting.mvc.model.charge;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ChargeDAO {
	private JdbcTemplate jdbc;
	
	public void setDataSource(DataSource ds){
		jdbc = new JdbcTemplate(ds);
	}
	
	public int getChargeListTotalCount(String userName) throws DataAccessException {
		String sql = "SELECT count(*) FROM im_charge_history WHERE username=? ORDER BY time DESC";
		return jdbc.queryForInt(sql, userName);
	}

	public List<Map<String, Object>> getChargeList(String userName, int offset, int pageSize) {
		String sql = "SELECT money, DATE_FORMAT(time, '%Y-%m-%d %H:%i') as charge_time FROM im_charge_history WHERE username = ? ORDER BY time DESC LIMIT ?, ?";
		int startIndex = (offset - 1) * pageSize;
		List<Map<String, Object>> list = null;
		try {
			list = jdbc.queryForList(sql, userName, startIndex, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}