package org.api.db;

/**
 * Bismillahirrahmanirrahim
 * @author Qomarullah
 * @time 9:27:44 PM
 */

import static spark.Spark.*;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;


public class SparkServer {

	private final static Logger log = LogManager.getLogger(SparkServer.class);
	private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	static {
		corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		corsHeaders.put("Access-Control-Allow-Origin", "*");
		corsHeaders.put("Access-Control-Allow-Headers",
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
		corsHeaders.put("Access-Control-Allow-Credentials", "true");
	}
	public final static void apply() {
		Filter filter = new Filter() {
			@Override
			public void handle(Request request, Response response) throws Exception {
				corsHeaders.forEach((key, value) -> {
					response.header(key, value);
				});
				response.type("application/json");
			}
		};
		Spark.after(filter);
	}
	

	public SparkServer(int port, int minThreads, int maxThreads, int timeOutMillis){
		
		Spark.port(port);
		threadPool(maxThreads, minThreads, timeOutMillis);
		Spark.get("/hello", (req, res) -> "Bismillahirrahmanirrahim");
		
		Spark.get("/query", (request, response) -> {
			long start=System.currentTimeMillis();
			
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = singleSql("", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		   
		    long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		
		Spark.post("/query", (request, response) -> {
			long start=System.currentTimeMillis();
			
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = singleSql("", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		   
		    long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
			
		    return resp;
		});
		//////////////////////////////////////////////////////

		Spark.get("/querylist", (request, response) -> {
			long start=System.currentTimeMillis();
			
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
		
			long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		
		Spark.post("/querylist", (request, response) -> {
			
			long start=System.currentTimeMillis();
			String resp="";
			Result result = new Result();
			
			String body=request.body();
			Map<String, String> map = new HashMap<String, String>();
			String jndi="";
			String sql="";
			try {

				ObjectMapper mapper = new ObjectMapper();
				map = mapper.readValue(body, new TypeReference<Map<String, String>>(){});
				
				jndi=map.get("jndi");
				sql=map.get("sql");
				
				try {
					result = queryList(result, "", "", jndi, sql);	
				} catch (Exception e) {
					// TODO: handle exception
					resp = e.getMessage();
				}
				
			} catch (JsonGenerationException e) {
				e.printStackTrace();
				resp = e.getMessage();
				
			} catch (JsonMappingException e) {
				e.printStackTrace();
				resp = e.getMessage();
				
			} catch (IOException e) {
				e.printStackTrace();
				resp = e.getMessage();
				
			}
			
			
			ObjectMapper objectMapper = new ObjectMapper();
			resp=objectMapper.writeValueAsString(result).toString();
			
			long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		//////////////////////////////////////////////////////
		Spark.get("/update", (request, response) -> {
			long start=System.currentTimeMillis();
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = updateSql(jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    
		    long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		
		Spark.post("/update", (request, response) -> {
			long start=System.currentTimeMillis();
			
			String resp="";
			String jndi=request.queryParams("jndi");
			String sql=request.queryParams("sql");
		    try {
		    	 resp = updateSql(jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				resp = e.getMessage();
			}
		    response.type("application/json");
		    response.header("Access-Control-Allow-Origin", "*");
			
		    long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		
		//////////////////////////////////
		Spark.get("/getlist", (request, response) -> {
			
			long start=System.currentTimeMillis();
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
			long rt=start-System.currentTimeMillis();
			String tdr=rt+"|"+jndi+"|"+sql+"|"+resp;
			LogTDR.info(tdr);
		    return resp;
		});
		
		apply();
		
		
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

				int j=0;
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					
					ResultSetMetaData meta = rs.getMetaData();
					for (int i=0;i<meta.getColumnCount();i++) {
						String value = rs.getString(meta.getColumnName(i+1));
						String qkey = prefix + meta.getColumnName(i+1);
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