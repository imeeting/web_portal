package com.imeeting.mvc.model.charge;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.imeeting.constants.ChargeStatus;

public class ChargeDAO {
	private static Log log = LogFactory.getLog(ChargeDAO.class);
	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}

	public int getChargeListTotalCount(String userName)
			throws DataAccessException {
		String sql = "SELECT count(*) FROM im_charge_history WHERE username = ? AND status = ? ORDER BY time DESC";
		return jdbc.queryForInt(sql, userName, ChargeStatus.success.name());
	}

	public List<Map<String, Object>> getChargeList(String userName, int offset,
			int pageSize) {
		String sql = "SELECT chargeId, money, DATE_FORMAT(time, '%Y-%m-%d %H:%i') as charge_time " +
				"FROM im_charge_history WHERE username = ? AND status = ? " +
				"ORDER BY time DESC LIMIT ?, ?";
		int startIndex = (offset - 1) * pageSize;
		List<Map<String, Object>> list = null;
		try {
			list = jdbc.queryForList(sql, userName,
					ChargeStatus.success.name(), startIndex, pageSize);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void addChargeRecord(String chargeId, String userName, Double money, ChargeStatus status) {
		String sql = "INSERT INTO im_charge_history(chargeId, username, money, status) VALUES(?, ?, ?, ?)";
		jdbc.update(sql, chargeId, userName, money, status.name());
	}	

	public void addChargeRecord(String chargeId, String userName, Double money) {
		String sql = "INSERT INTO im_charge_history(chargeId, username, money) VALUES(?, ?, ?)";
		jdbc.update(sql, chargeId, userName, money);
	}

	public void updateChargeRecord(String chargeId, ChargeStatus status) {
		String sql = "UPDATE im_charge_history SET status = ? WHERE chargeId = ?";
		jdbc.update(sql, status.name(), chargeId);
	}

	public Map<String, Object> getChargeInfoById(String chargeId) {
		String sql = "SELECT * FROM im_charge_history WHERE chargeId = ?";
		Map<String, Object> info = null;
		try {
			info = jdbc.queryForMap(sql, chargeId);
		} catch (DataAccessException e) {
			log.info(e.getMessage());
		}
		return info;
	}
}