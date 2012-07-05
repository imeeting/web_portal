package com.richitec.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBHelper {
	/**
	 * 
	 * �?��数据库操作接�?
	 * 
	 * @author ruanzhiyong6496
	 * @version 1.0
	 */
	
	private String dialect = "mysql";
	
	private ComboPooledDataSource dataSource;
	
	public DBHelper(){
		//do nothing
	}
	
	public void setDataSource(ComboPooledDataSource ds){
		this.dataSource = ds;
	}
	
	public void closeAll(){
		dataSource.close();
	} 

	/**
	 * 获得数据库连�?
	 * 
	 * @return 数据库连�?
	 * @throws SQLException 
	 */
	public Connection getConn() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * 关闭�?��对对�?归还链接到连接池
	 * 
	 * @param rs
	 *            数据集对�?
	 * @param ps
	 *            命令对象
	 */	
	public void closeAll(ResultSet rs, PreparedStatement ps, Connection conn) {
		if (null != rs) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != ps) {
			try {
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != conn){
			try{
				conn.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}	

	/**
	 * 无事务更�?不带参数)
	 * 
	 * @param sql
	 *            SQL语句 �?update table set price=200.00"
	 * @return 受影响行�?
	 * @throws SQLException
	 */
	public int update(String sql) throws Exception {
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(null, ps, conn);
		}
		return result;
	}

	/**
	 * 无事务更�?带参�?
	 * 
	 * @param sql
	 *            SQL语句 :"delete from table where id=?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return 受影响行�?
	 * @throws SQLException
	 */
	public int update(String sql, Object[] params) throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			result = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(null, ps, conn);
		}
		return result;
	}

	/**
	 * 非事务下批量更新
	 * 
	 * @param sql
	 *            SQL语句 :"insert into table(p1,p2,p3) values(?,?,?)"
	 * @param params
	 *            参数集合List<Object[]> 其中new Object[]{参数1,参数2,...}
	 * @return 受影响行�?
	 * @throws SQLException
	 */
	public int[] batchUpdate(String sql, List<Object[]> params)
			throws SQLException {
		int[] result = new int[params.size()];
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (Object[] objects : params) {
				for (int i = 0; i < objects.length; i++) {
					ps.setObject(i + 1, objects[i]);
				}
				ps.addBatch();
			}
			result = ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(null, ps, conn);
		}
		return result;
	}

	/**
	 * 查询(不带参数)返回List<Map<k,v>>其中k:字段名小�?v:字段�?
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @return 集合List<Map<k,v>> 其中k:字段名小�?v:字段�?
	 * @throws SQLException
	 */
	public List<Map<String, Object>> query(String sql) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < colCount; i++) {
					String key = rsmd.getColumnName(i + 1).toLowerCase();
					Object val = rs.getObject(i + 1) != null ? rs
							.getObject(i + 1) : "";
					map.put(key, val);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(rs, ps, conn);
		}
		return list;
	}

	/**
	 * 查询(不带参数)返回XML格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @return XML格式数据
	 * @throws SQLException
	 */
	public String queryXML(String sql) throws SQLException {
		List<Map<String, Object>> list = query(sql);
		return List2Xml(list.size(), list);
	}

	/**
	 * 查询(不带参数)返回JSON格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @return JSON格式数据
	 * @throws SQLException
	 */
	public String queryJSON(String sql) throws SQLException {
		List<Map<String, Object>> list = query(sql);
		return List2Json(list.size(), list);
	}

	/**
	 * 分页查询(不带参数)返回List<Map<k,v>>其中k:字段名小�?v:字段�?
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return 集合List<Map<k,v>> 其中k:字段名小�?v:字段�?
	 * @throws SQLException
	 */
	public List<Map<String, Object>> queryPager(String sql, int currPage,
			int pageSize) throws SQLException {
		String pageSql = getPageSql(sql, currPage, pageSize);
		return query(pageSql);
	}

	/**
	 * 分页查询(不带参数)返回XML格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return XML格式数据
	 * @throws SQLException
	 */
	public String queryXMLPager(String sql, int currPage, int pageSize)
			throws SQLException {
		int count = total(sql);
		String pageSql = getPageSql(sql, currPage, pageSize);
		List<Map<String, Object>> list = query(pageSql);
		return List2Xml(count, list);
	}

	/**
	 * 分页查询(不带参数)返回JSON格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table"
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return JSON格式数据
	 * @throws SQLException
	 */
	public String queryJSONPager(String sql, int currPage, int pageSize)
			throws SQLException {
		int count = total(sql);
		String pageSql = getPageSql(sql, currPage, pageSize);
		List<Map<String, Object>> list = query(pageSql);
		return List2Json(count, list);
	}

	/**
	 * 查询(带参�?返回List<Map<k,v>>其中k:字段名小�?v:字段�?
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return 集合List<Map<k,v>> 其中k:字段名小�?v:字段�?
	 * @throws SQLException
	 */
	public List<Map<String, Object>> query(String sql, Object[] params)
			throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < colCount; i++) {
					String key = rsmd.getColumnName(i + 1).toLowerCase();
					Object val = rs.getObject(i + 1) != null ? rs
							.getObject(i + 1) : "";
					map.put(key, val);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(rs, ps, conn);
		}
		return list;
	}

	/**
	 * 查询(带参�?返回XML格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return XML格式数据
	 * @throws SQLException
	 */
	public String queryXML(String sql, Object[] params) throws SQLException {
		List<Map<String, Object>> list = query(sql, params);
		return List2Xml(list.size(), list);
	}

	/**
	 * 查询(带参�?返回XML格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return XML格式数据
	 * @throws SQLException
	 */
	public String queryJSON(String sql, Object[] params) throws SQLException {
		List<Map<String, Object>> list = query(sql, params);
		return List2Json(list.size(), list);
	}

	/**
	 * 分页查询(带参�?返回List<Map<k,v>>其中k:字段名小�?v:字段�?
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return 集合List<Map<k,v>> 其中k:字段名小�?v:字段�?
	 * @throws SQLException
	 */
	public List<Map<String, Object>> queryPager(String sql, Object[] params,
			int currPage, int pageSize) throws SQLException {
		String pageSql = getPageSql(sql, currPage, pageSize);
		return query(pageSql, params);
	}

	/**
	 * 分页查询(带参�?返回XML格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return XML格式数据
	 * @throws SQLException
	 */
	public String queryXMLPager(String sql, Object[] params, int currPage,
			int pageSize) throws SQLException {
		int count = total(sql, params);
		String pageSql = getPageSql(sql, currPage, pageSize);
		List<Map<String, Object>> list = query(pageSql, params);
		return List2Xml(count, list);
	}

	/**
	 * 分页查询(带参�?返回JSON格式数据
	 * 
	 * @param sql
	 *            SQL语句 :"select * from table where id = ?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @param currPage
	 *            第几�?
	 * @param pageSize
	 *            每页记录�?
	 * @return JSON格式数据
	 * @throws SQLException
	 */
	public String queryJSONPager(String sql, Object[] params, int currPage,
			int pageSize) throws SQLException {
		int count = total(sql, params);
		String pageSql = getPageSql(sql, currPage, pageSize);
		List<Map<String, Object>> list = query(pageSql, params);
		return List2Json(count, list);
	}

	/**
	 * 单�?查询(不带参数)
	 * 
	 * @param sql
	 *            SQL语句 :"select name from table"
	 * @return 单�?
	 * @throws SQLException
	 */
	public String scalar(String sql) throws SQLException {
		String res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getObject(1).toString();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(rs, ps, conn);
		}
		return res;
	}

	/**
	 * 查询总记录数(不带参数)
	 * 
	 * @param sql
	 *            SQL语句 :"select count(*) from table"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return 总记录数
	 * @throws SQLException
	 */
	public int count(String sql) throws SQLException {
		int count = Integer.valueOf(scalar(sql));
		return count;
	}

	/**
	 * 单�?查询(带参)
	 * 
	 * @param sql
	 *            SQL语句 :"select name from table where id=?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return 单�?
	 * @throws SQLException
	 */
	public String scalar(String sql, Object[] params) throws SQLException {
		String res = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConn();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getObject(1).toString();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(rs, ps, conn);
		}
		return res;
	}

	/**
	 * 查询总记录数(带参�?
	 * 
	 * @param sql
	 *            SQL语句 :"select count(*) from table where id=?"
	 * @param params
	 *            参数对象数组 :new Object[]{参数1,参数2,...}
	 * @return 总记录数
	 * @throws SQLException
	 */
	public int count(String sql, Object[] params) throws SQLException {
		int count = Integer.valueOf(scalar(sql, params));
		return count;
	}

	/**
	 * 执行存储过程
	 * 
	 * @param spName
	 *            存储过程
	 * @param params
	 *            存储过程参数对象数组 :new Object[]{参数1,参数2,...}
	 * @param outParamNum
	 *            输出参数个数
	 * @return
	 * @throws SQLException
	 */
	public String[] execProc(String procName, Object[] params, int outParamNum)
			throws SQLException {
		String[] ret = new String[outParamNum];
		Connection conn = null;
		CallableStatement cs = null;
		try {
			conn = getConn();
			int inParamNum = (null != params) ? params.length : 0;
			String pSql = getPSql(procName, inParamNum, outParamNum);
			cs = (CallableStatement) conn.prepareCall(pSql);
			for (int i = 0; i < inParamNum; i++) {
				cs.setObject(i + 1, params[i]);
			}
			for (int k = 1; k <= outParamNum; k++) {
				cs.registerOutParameter(inParamNum + k, Types.VARCHAR);
			}
			cs.executeQuery();
			for (int k = 1; k <= outParamNum; k++) {
				ret[k - 1] = cs.getString(inParamNum + k);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeAll(null, cs, conn);
		}
		return ret;
	}

	/**
	 * 得到存储过程语句
	 * 
	 * @param procName
	 *            存储过程
	 * @param inParamNum
	 *            输入参数个数
	 * @param outParamNum
	 *            输出参数个数
	 * @return
	 * @throws SQLException
	 */
	private String getPSql(String procName, int inParamNum, int outParamNum)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("{call ").append(procName);
		int paramCount = inParamNum + outParamNum;
		if (paramCount > 0) {
			sb.append("(");
			for (int i = 0; i < paramCount; i++) {
				sb.append("?");
				if (i != paramCount - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append("}");
		return sb.toString();
	}

	private String List2Json(int count, List<Map<String, Object>> list)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		if (count > 0) {
			int len1 = list.size();
			sb.append("{total:").append(count).append(",root:[");
			for (int i = 0; i < len1; i++) {
				sb.append("{");
				Map<String, Object> map = list.get(i);
				String[] keys = map.keySet().toArray(new String[0]);
				int len2 = keys.length;
				for (int k = 0; k < len2; k++) {
					sb.append(keys[k]).append(":").append("'")
							.append(map.get(keys[k])).append("'");
					if (k != len2 - 1) {
						sb.append(",");
					}
				}
				sb.append("}");
				if (i != len1 - 1) {
					sb.append(",");
				}
			}
			sb.append("]}");
		}
		return sb.toString();
	}

	private String List2Xml(int count, List<Map<String, Object>> list)
			throws SQLException {
		StringBuffer sb = new StringBuffer(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<list>");
		if (count > 0) {
			sb.append("<total>").append(count).append("</total>");
			for (Map<String, Object> map : list) {
				sb.append("<item>");
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					sb.append("<").append(entry.getKey()).append(">");
					sb.append(entry.getValue());
					sb.append("</").append(entry.getKey()).append(">");
				}
				sb.append("</item>");
			}
		}
		sb.append("</list>");
		return sb.toString();
	}

	private int total(String sql) throws NumberFormatException, SQLException {
		StringBuffer countSql = new StringBuffer(0);
		int fromIndex = sql.toLowerCase().indexOf("from");
		countSql.append("select count(*) ").append(sql.substring(fromIndex));
		return Integer.valueOf(scalar(countSql.toString()));
	}

	private int total(String sql, Object[] params)
			throws NumberFormatException, SQLException {
		StringBuffer countSql = new StringBuffer(0);
		int fromIndex = sql.toLowerCase().indexOf("from");
		countSql.append("select count(*) ").append(sql.substring(fromIndex));
		return Integer.valueOf(scalar(countSql.toString(), params));
	}

	private String getPageSql(String sql, int currPage, int pageSize) {
		StringBuffer pageSql = new StringBuffer(0);
		if ("oracle".equalsIgnoreCase(dialect)) {
			pageSql.append("SELECT * FROM(SELECT FA.*, ROWNUM RN FROM (");
			pageSql.append(sql).append(") FA WHERE ROWNUM <= ");
			pageSql.append(currPage * pageSize).append(")WHERE RN >= ")
					.append((currPage - 1) * pageSize + 1);
		}
		if ("mysql".equalsIgnoreCase(dialect)) {
			pageSql.append(sql).append(" limit ")
					.append((currPage - 1) * pageSize).append(",")
					.append(pageSize);
		}
		return pageSql.toString();
	}

	/**
	 * 得到某表标识的下�?��
	 * 
	 * @param tabName
	 *            表名
	 * @param flag
	 *            表标识字�?
	 * @param defVal
	 *            默认�?
	 * @return 标识的下�?��
	 */
	public String nextVal(String tabName, String flag, String defVal) {
		String sql = "select max(" + flag + ") from " + tabName;
		String maxid = null;
		try {
			maxid = String.valueOf(scalar(sql));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (null == maxid) {
			return defVal;
		}
		int size = maxid.length();
		for (int i = 0; i < size; i++) {
			if (maxid.charAt(i) != '0') {
				int tid = Integer.valueOf(maxid.substring(i)) + 1;
				maxid = StringUtils.leftPad(String.valueOf(tid), size);
				break;
			}
		}
		return maxid;
	}

}
