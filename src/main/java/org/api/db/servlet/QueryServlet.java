package org.api.db.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.db.MysqlQuery;

@WebServlet(
        name = "query",
        urlPatterns = {"/query"}
    )
public class QueryServlet extends HttpServlet {

	private final static Logger log = LogManager.getLogger(QueryServlet.class);
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
    	String result="";
		String jndi=req.getParameter("jndi");
		String sql=req.getParameter("sql");
	    try {
	    	result = MysqlQuery.Singlesql("", jndi, sql);	
		} catch (Exception e) {
			// TODO: handle exception
			result = e.getMessage();
		}
	    log.info("query:"+jndi+"|"+sql+"|"+resp);
	    
        ServletOutputStream out = resp.getOutputStream();
        
        result="test";
        out.write(result.getBytes());
        out.flush();
        out.close();
    }

}