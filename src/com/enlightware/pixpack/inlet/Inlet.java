package com.enlightware.pixpack.inlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Server;
import com.laukien.net.JavaScript;
/**
 * Generates a javaScript-Document that is only build f�r this call.
 * 
 * @author Stephan Laukien
 */
public class Inlet extends HttpServlet {
	private static final long serialVersionUID = 20061025L;

	public static final int ERROR_UNKNOWN = -1;
	public static final int ERROR_PARAMETER = 0;
	public static final int ERROR_NOTFOUND = 1;

	private static final String ID = "pixpack";
	private static final String CHARSET = "UTF-8";


	private static JavaScript gsScript;

	private boolean isScript;
	private boolean isContent;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		gsScript=new JavaScript(Lib.getPath()+File.separator+"inlet");
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		PrintWriter out=pResponse.getWriter();

		pResponse.setContentType("text/html");
		
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

		String name=pRequest.getPathInfo();
		//invalid script-parameter
		if(name==null || name.length()<=4) {
			//content (*.inlet)
			name=pRequest.getRequestURI();
			name=name.substring(1,name.length()-6);
			isScript=false;
			isContent=true;
		} else {
			//script (/inlet*.js)
			name=name.substring(1,name.length()-3);
			isScript=true;
			isContent=false;
		}

		//output
		InletInterface inlet;
		if(name.equals("gallery")) inlet=new Gallery();
		else if(name.equals("preview")) inlet=new Preview();
		else inlet=new Null();
		
		inlet.setRequest(pRequest);
		inlet.setFile(gsScript.get(name));
		
		if(isScript) {
			out.println(inlet.getScript());
		} else if(isContent) {
			out.println(inlet.getContent());
		}
	}
	
	private String getError(String pContent, int pError) {
		return "alert('Error: "+pError+"\\n"+pContent+"');";
	}
	
	public void destroy() {
		
	}
	

	private String getLanguage(HttpServletRequest pRequest) {
		String lang=pRequest.getParameter("language");
		if(lang==null) return "en";
		lang=lang.trim().toLowerCase();
		if(lang.length()!=2 || !lang.equals("de")) lang="en";
		
		return lang;
	}

	public static String getCopyright() {
		return "<h3>PixPack - Inlet</h3><a href=\""+Server.getRoot()+"\" title=\"PixPack\">PixPack</a><p><b>PixPack - Free Image Hosting.</b></p>";
	}

//	private String getText(String pKey, String pLanguage) {
//		Dictionary dict=(Dictionary)gsScripts.get(pLanguage);
//		if(dict==null) return pKey;
//		String value=dict.getWord(pKey);
//		
//		return value!=null ? value: pKey;
//	}
//
//	class Dictionary {
//		private String gLanguage;
//		private Hashtable gDictionary;
//		private Dictionary(String pLanguage) {
//			gLanguage=pLanguage;
//			gDictionary=new Hashtable();
//		}
//		
//		public void setWord(String pKey, String pValue) {
//			gDictionary.put(pKey, pValue);
//		}
//		
//		public String getWord(String pKey) {
//			return (String)gDictionary.get(pKey);
//		}
//	}
	
}
