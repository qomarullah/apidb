package org.api.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.codegen.bean.Property;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
public class App 
{
	private final static Logger log = LogManager.getLogger(App.class);
	public static Properties prop;
	public static String test="1";
	
    public static void main( String[] args )
    {
    	//setup properties
    	prop = new Properties();
    	if(args.length>0)
    		loadProperties(prop, args[0]);
    	else{
    		log.debug("properties not found");
    		System.exit(0);
    	}
    	
    	//setup log
        if(args.length>1)
    		BasicConfigurator.configure();
    	else
    		PropertyConfigurator.configure(args[0]);
    	
    	
    	//String logFile=prop.getProperty("logback.configurationFile","logback.xml");
    	//setup server
    	int port=Integer.parseInt(prop.getProperty("server.port","9001"));
    	int maxThreads = Integer.parseInt(prop.getProperty("server.maxthread","200"));
    	int minThreads = Integer.parseInt(prop.getProperty("server.minthread","30"));
    	int timeOutMillis = Integer.parseInt(prop.getProperty("server.timeout","20000"));
    	
    	//start server
    	new SparkServer(port,minThreads,maxThreads,timeOutMillis);
    	//new TomcatServer(port, minThreads, maxThreads, timeOutMillis);
    }
    
    public static void loadProperties(Properties prop, String filename){

    	InputStream input = null;
    	try {

    		input = new FileInputStream(filename);
    		// load a properties file
    		prop.load(input);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }
}
