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
 * @time 3:07:03 AM
 */
public class TomcatServlet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	private final static Logger log = LogManager.getLogger(SparkServer.class);
	
	
	public TomcatServlet(int port, int minThreads, int maxThreads, int timeOutMillis){
	
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.getConnector().setAttribute("maxThreads", maxThreads);
		tomcat.getConnector().setAttribute("connectionTimeout", timeOutMillis);
		log.debug("Listener:" + maxThreads);

		
		String webappDirLocation = "src/main/webapp/";
        tomcat.setPort(port);
        StandardContext ctx=null;
		try {
			ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

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
		String response="ok";
		//req.getParameter(")
		
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
