package org.api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MysqlQuery {

	private final static Logger log = LogManager.getLogger(MysqlQuery.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

public static String Singlesql(String prefix, String jndi, String sql) throws SQLException{
		
		String resp="null";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
				log.debug("DS = " + jndi +"="+ sql);
				conn = MysqlFacade.getConnection(jndi);
				if (conn == null) {
					throw new SQLException("Cannot get connection from datasource : " + jndi);
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String tempvalue="";

				if (rs.next()) {
					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, Object> map = new HashMap<String, Object>();

					ResultSetMetaData meta = rs.getMetaData();
					for (int i=0;i<meta.getColumnCount();i++) {
						String value = rs.getString(meta.getColumnName(i+1));
						String qkey = prefix + meta.getColumnName(i+1);
						if (value == null) {
							value = "null";
						}
						map.put(qkey, value);
						/*List<Object> myList = new ArrayList<Object>();
						myList.add("Jonh");
						myList.add("Jack");
						myList.add("James");
						mapObject.put("names", myList);*/
					}
					resp=objectMapper.writeValueAsString(map).toString();
						
				}
			} catch (Exception e) {
				log.error("SQL sql error = " + sql + ", jndi = " + jndi, e);
				resp = e.getMessage();
				return resp;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						log.error("SQL sql error-rs = " + sql + ", jndi = " + jndi, e);
							
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						log.error("SQL sql error-ps = " + sql + ", jndi = " + jndi, e);
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						log.error("SQL sql error-conn = " + sql + ", jndi = " + jndi, e);
					}
				}
			}
		return resp;
	}
	
public static String Updatesql(String jndi, String sql)throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		String resp="nok";
		try {
			conn = MysqlFacade.getConnection(jndi);
			if (conn == null) {
				throw new SQLException("Cannot get connection from datasource : " + jndi);
			}
			ps = conn.prepareStatement(sql);
			int affRow = ps.executeUpdate();
			resp="ok:"+affRow;
			
		} catch (Exception e) {
			resp = e.getMessage();
			return resp;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("SQL update error-ps = " + sql + ", jndi = " + jndi, e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("SQL update error-conn = " + sql + ", jndi = " + jndi, e);
				}
			}
		}
		return resp;
		
	}
	//end update method
}
