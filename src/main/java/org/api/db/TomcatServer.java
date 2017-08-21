package org.api.db;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bismillahirrahmanirrahim
 * @author Qomarullah
 * time 3:07:03 AM
 */
public class TomcatServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private final static Logger log = LogManager.getLogger(SparkServer.class);
	
	
	public TomcatServer(int port, int minThreads, int maxThreads, int timeOutMillis){
	
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.getConnector().setAttribute("maxThreads", maxThreads);
		tomcat.getConnector().setAttribute("connectionTimeout", timeOutMillis);
		log.debug("Listener:" + maxThreads);

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

		Tomcat.addServlet(ctx, "apidb", new HttpServlet() {
			protected void service(HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {
				handle(req, resp);
			}
		});
		ctx.addServletMapping("/*", "apidb");

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        tomcat.getServer().await();
		
		
	}
	public void handle(HttpServletRequest req, HttpServletResponse resp) {

		long start = System.currentTimeMillis();
		
		// print response
		PrintWriter out = null;
		String response="nok";
		String uri=req.getRequestURI();
		
		if(uri.equals("/query")){
			String jndi=req.getParameter("jndi");
			String sql=req.getParameter("sql");
		    try {
		    	response = MysqlQuery.Singlesql("", jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				response = e.getMessage();
			}
		    log.info("query:"+jndi+"|"+sql+"|"+response);
		    
		}
		if(uri.equals("/update")){
			String jndi=req.getParameter("jndi");
			String sql=req.getParameter("sql");
		    try {
		    	response = MysqlQuery.Updatesql(jndi, sql);	
			} catch (Exception e) {
				// TODO: handle exception
				response = e.getMessage();
			}
		    log.info("update:"+jndi+"|"+sql+"|"+response);
		    //System.out.println("update:"+jndi+"|"+sql+"|"+response);
		    
		}
		try {
			out = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setHeader("Server", "ApiDB/1.0");
			
			out.write(response);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
		long time = System.currentTimeMillis() - start;

	}
}
