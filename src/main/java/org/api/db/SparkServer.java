package org.api.db;

/**
 * Bismillahirrahmanirrahim
 * @author Qomarullah
 * @time 9:27:44 PM
 */

import static spark.Spark.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.db.modal.Result;

import com.fasterxml.jackson.databind.ObjectMapper;


public class SparkServer {

	private final static Logger log = LogManager.getLogger(SparkServer.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	public SparkServer(int port, int minThreads, int maxThreads, int timeOutMillis){
		
		// Setup JMX
		/*JettyServerFactory js= new JettyServerFactory() {
			
			@Override
			public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public Handler
		};
		js.create(maxThreads, minThreads, threadTimeoutMillis);
		*/
		
		port(port);
		threadPool(maxThreads, minThreads, timeOutMillis);
		get("/hello", (req, res) -> "Bismillahirrahmanirrahim");
		
		get("/query", (request, response) -> {
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = singleSql("", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		
		post("/query", (request, response) -> {
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = singleSql("", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		//////////////////////////////////////////////////////

		get("/querylist", (request, response) -> {
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
			String array=request.queryParams("array");
			
			String resp="";
			Result result = new Result();
		
			try {
				result = queryList(result, array, "", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
			ObjectMapper objectMapper = new ObjectMapper();
			resp=objectMapper.writeValueAsString(result).toString();
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		
		post("/querylist", (request, response) -> {
			
			String resp="";
			Result result = new Result();
			
			String jndi=request.params("jndi");
			String sql=request.params("sql");
			String array=request.params("array");
			try {
				result = queryList(result, array, "", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
			ObjectMapper objectMapper = new ObjectMapper();
			resp=objectMapper.writeValueAsString(result).toString();
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		//////////////////////////////////////////////////////
		get("/update", (request, response) -> {
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = updateSql(jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		
		post("/update", (request, response) -> {
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = updateSql(jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		
		//////////////////////////////////
		get("/getlist", (request, response) -> {
			
			HashMap<String, String> myparam = new HashMap<String, String>();
			Set params =  request.queryParams();
			Iterator<String> itr = params.iterator();
			String jndi=null;
		 	String sql=null;
			// traversing over HashSet
			while(itr.hasNext()){
			  String key=itr.next();
			  myparam.put(key, request.queryParams(key));
			 
			  //replace all parameter
			  if(key.equals("sqlid")){
				 sql=App.prop.getProperty("sqlid."+myparam.get("sqlid"),"null");
			  }
			  if(sql!=null){
				  sql=sql.replaceAll("\\["+key+"\\]", request.queryParams(key));
			  }
			}
			
			String resp="";
			Result result = new Result();
			ObjectMapper objectMapper = new ObjectMapper();
			jndi = myparam.get("jndi");
			
			
			if(jndi==null || sql==null){
				result.status="failed";
				result.desc="wrong parameter";
				
			}else{
			
				try {
					result = getList(result, jndi, sql);	
				} catch (Exception e) {
					// TODO: handle exception
					resp = e.getMessage();
					result.desc = e.getMessage();
				}
			}
			
			resp=objectMapper.writeValueAsString(result).toString();
		    log.info("query:"+jndi+"|"+sql+"|"+resp);
		    return resp;
		});
		
		
	}
	
	

public Result getList(Result result, String jndi, String sql) throws SQLException{
		
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

				int j=0;
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					
					ResultSetMetaData meta = rs.getMetaData();
					for (int i=0;i<meta.getColumnCount();i++) {
						String value = rs.getString(meta.getColumnName(i+1));
						String qkey = meta.getColumnName(i+1);
						if (value == null) {
							value = "null";
						}
						map.put(qkey, value);
					}
					//add multiple
					result.results.add(map);
					j++;
				}
				result.status="success";
				result.count=j;
				result.count_total=j;
				result.pages=1;
				
			} catch (Exception e) {
				log.error("SQL sql error = " + sql + ", jndi = " + jndi, e);
				result.status="failed";
				result.desc=""+e.getMessage();
				return result;
				
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						log.error("SQL sql error-rs = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
							
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						log.error("SQL sql error-ps = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
						
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						log.error("SQL sql error-conn = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
						
					}
				}
			}
		return result;
	}
	
public Result queryList(Result result, String array, String prefix, String jndi, String sql) throws SQLException{
		
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

				Map<String, Object> map = new HashMap<String, Object>();
				int j=0;
				while (rs.next()) {
				
					ResultSetMetaData meta = rs.getMetaData();
					for (int i=0;i<meta.getColumnCount();i++) {
						String value = rs.getString(meta.getColumnName(i+1));
						String qkey = prefix + meta.getColumnName(i+1);
						if (value == null) {
							value = "null";
						}
						System.out.println(qkey+"-"+value);
						map.put(qkey, value);
					}
					//add multiple
					result.results.add(map);
					j++;
				}
				result.count=j;
				result.count_total=j;
				result.pages=1;
				
			} catch (Exception e) {
				log.error("SQL sql error = " + sql + ", jndi = " + jndi, e);
				result.status="failed";
				result.desc=""+e.getMessage();
				return result;
				
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						log.error("SQL sql error-rs = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
							
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						log.error("SQL sql error-ps = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
						
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						log.error("SQL sql error-conn = " + sql + ", jndi = " + jndi, e);
						result.status="failed";
						result.desc=""+e.getMessage();
						
					}
				}
			}
		return result;
	}
	
	
	public String singleSql(String prefix, String jndi, String sql) throws SQLException{
		
		String resp="nok";
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
	
	public String updateSql(String jndi, String sql)throws SQLException{
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