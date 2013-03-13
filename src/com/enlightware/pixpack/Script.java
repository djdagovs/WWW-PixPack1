package com.enlightware.pixpack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enlightware.pixpack.Lib;
import com.laukien.net.JavaScript;
import com.laukien.string.Replace;
/**
 * Generates a javaScript-Document that is only build f�r this call.
 * 
 * @author Stephan Laukien
 */
public class Script extends HttpServlet {
	private static final long serialVersionUID = 20061025L;

	public static final int ERROR_UNKNOWN = -1;
	public static final int ERROR_PARAMETER = 0;
	public static final int ERROR_NOTFOUND = 1;

	private static final String CHARSET = "UTF-8";

	private static JavaScript gsScript;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		gsScript=new JavaScript(Lib.getPath()+File.separator+"script");
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		PrintWriter out=pResponse.getWriter();

		pResponse.setContentType("text/javascript");
		
		String charset=pRequest.getParameter("charset");
		if(com.laukien.string.String.isEmpty(charset)) charset=CHARSET;
		try {
			pRequest.setCharacterEncoding(charset);
			pResponse.setCharacterEncoding(charset);
		} catch(Exception e) {
			out.write("document.writeln('<div style=\"color: red; font-weight: bold;\">Invalid charset ("+charset+")<br/><br/>"+e+"</div>');')");
			out.flush();
			return;
		}
		
		String script=pRequest.getPathInfo();
		//invalid script-parameter
		if(script==null || script.length()<=4) {
			out.println(getError("Invalid parameter",ERROR_PARAMETER));
			return;
		}
		
		//get script from the buffer
		String content=gsScript.get(script);
		if(content==null) content=getError("Script not found\n"+script,ERROR_NOTFOUND);
		
/*		if(script.equals("/vote.js")) {
			Text i18n=Lib.getI18n(pRequest);
			if(i18n!=null) content=Replace.replace(content,"${text}",i18n.getText("vote.send"));
			else content=Replace.replace(content,"${text}","OK!");
		}
*/
		out.println(content);
	}

	
	private String getError(String pContent, int pError) {
		return "alert('Error: "+pError+"\\n"+Replace.replace(pContent,"\n","\\n")+"');";
	}
	
	public void destroy() {
		
	}
	

}
