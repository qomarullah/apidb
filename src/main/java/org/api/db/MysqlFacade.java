package org.api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import com.mchange.v2.c3p0.DataSources;

/*
 * - A connection facade to MySQL Database
 * - Utilize apache tomcat datasource connection, or it also can be used as standalone
 * - Use dbutil package (from apache commons) to simplify query execution
 */

public class MysqlFacade {
	
	private DataSource ds;
	private static MysqlFacade instance;
	private int connectionTimeout = 4000;
	private static Object lock = new Object();
	private static HashMap<String, MysqlFacade> instances = new HashMap<String, MysqlFacade>();
	private DataSource cp;
	private final static Logger log = LogManager.getLogger(MysqlFacade.class);
	
	private static String samplejndi="jdbc:mysql://xxxx:3306/xxxx|root|null;5;5;3;12000";
	
	private MysqlFacade(String myjndi) throws SQLException {
		//get jndi
		String jndi = App.prop.getProperty("jndi."+myjndi,"undef");
		
		log.debug("get-jndi:"+myjndi+"=>"+jndi);
		if(jndi.equals("undef"))
			throw new SQLException("not found jndi");
		
		//start connect
		int maxPool = 15;
		int maxConn = 30;
		int initial = 5;
		int expiry = 10000;
		String driver="com.mysql.cj.jdbc.Driver";
		
		String[] p = jndi.split(";");
		try {
			maxPool = Integer.parseInt(p[1]);
			maxConn = Integer.parseInt(p[2]);
			initial = Integer.parseInt(p[3]);
			expiry = Integer.parseInt(p[4]);
			
			
		} catch (Exception e1) {
			log.debug("MysqlFacade|"+ jndi + "maxPool / maxConn / initial / expiry not found,using default values");
		}

		p = p[0].split("\\|");
		//driver
		driver = p[3];
		
		try {
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName(driver);
			System.out.println("====================="+driver);
		} catch (ClassNotFoundException e1) {
			log.error("Error getting database driver : "+ "com.mysql.jdbc.Driver");
		}

		if(p[2].equals("null"))p[2]="";
		DataSource unpooled = DataSources.unpooledDataSource(p[0], p[1], p[2]);
		
		Map overrides = new HashMap();
		overrides.put("maxPoolSize", maxConn); // "boxed primitives" also work
		overrides.put("initialPoolSize", initial);
		overrides.put("maxIdleTime", (expiry / 1000));
		overrides.put("maxIdleTimeExcessConnections", (expiry / 1000));
		
		cp = DataSources.pooledDataSource(unpooled, overrides);

		log.info("MysqlFacade|" + jndi + " pool set-up, URL = " + p[0]
				+ ", maxPool = " + maxPool + ", maxConn = " + maxConn
				+ ", initial = " + initial + ", expiry = " + expiry);
	}

	public static void resetConnection(String jndi) {
		instances.remove(jndi);
		log.info("MysqlFacade|ReloadingDatasource|" + jndi);
	}

	public static Connection getConnection(String jndi) throws Exception {
		MysqlFacade instance = instances.get(jndi);
		if (instance == null) {
			synchronized (lock) {
				if (instances.get(jndi) == null) {
					instance = new MysqlFacade(jndi);
					instances.put(jndi, instance);
				} else {
					instance = instances.get(jndi);
				}
			}
		}
		if (instance.cp == null) {
			log.warn("getConnection() " + jndi + " = null");
			return null;
		}
		long ts = System.currentTimeMillis();
		Connection conn = instance.cp.getConnection();
		log.debug("Thread " + Thread.currentThread().getId()
				+ " getConnection() " + jndi + " = "
				+ (System.currentTimeMillis() - ts) + " ms");
		return conn;
	}



}
