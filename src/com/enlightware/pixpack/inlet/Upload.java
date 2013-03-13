package com.enlightware.pixpack.inlet;

import javax.servlet.http.HttpServletRequest;

import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Server;
import com.laukien.string.Replace;

/**
 * 
 * @author Stephan Laukien
 * @deprecated
 */
public class Upload implements InletInterface {

	private static final String CHARSET = "UTF-8";

	private HttpServletRequest gRequest;
	private String gUser;
	private String gFolder;
	private String gWidth;
	private String gHeight;
	private String gMethod;
	private String gCharset;
	private String gLanguage;
	private String gCallback;
	private String gKey;
	private String gFilename;

	public Upload() {
		gRequest=null;
		gFilename=null;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gRequest=pRequest;
		initParameter();
		initLink();
	}
	
	private void initParameter() {
		gUser=gRequest.getParameter("user");
		if(isNull(gUser)) gUser=null;
		gFolder=gRequest.getParameter("folder");
		if(isNull(gFolder)) gFolder=null;
		gWidth=gRequest.getParameter("width");
		if(isNull(gWidth)) gWidth=null;
		gHeight=gRequest.getParameter("height");
		if(isNull(gHeight)) gHeight=null;
		gMethod=gRequest.getParameter("method");
		if(isNull(gMethod)) gMethod=null;
		gCharset=gRequest.getParameter("charset");
		if(isNull(gCharset)) gCharset=CHARSET;
		gLanguage=gRequest.getParameter("language");
		if(isNull(gLanguage)) gLanguage=null;
		gCallback=gRequest.getParameter("callback");
		if(isNull(gCallback)) gCallback=null;
		gKey=gRequest.getParameter("key");
		if(isNull(gKey)) gKey=null;
	}
	
	private void initLink() {
		if((gFilename=(String)gRequest.getAttribute("upload.file.name"))!=null) {
			//gFilename+=".img";	//call the image-redirector

			String user=com.enlightware.pixpack.Lib.getUsername(gRequest);
			if(user!=null) gFilename=user+':'+gFilename;
			gFilename=Server.getRoot()+'/'+gFilename;
		}
	}
		
	private boolean isNull(String pParam) {
		return (pParam==null || pParam.length()==0 || pParam.equals("undefined"));
	}
	
	public boolean isUploaded() {
		return gFilename!=null;
	}

	public String getKey() {
		return gKey;
	}

	public String getCallback() {
		return gCallback;
	}

	public String getCharset() {
		return gCharset;
	}

	public String getHeight() {
		return gHeight;
	}

	public String getWidth() {
		return gWidth;
	}

	public String getLanguage() {
		return gLanguage;
	}

	public String getMethod() {
		return gMethod;
	}

	public HttpServletRequest getRequest() {
		return gRequest;
	}

	public String getFolder() {
		return gFolder;
	}

	public String getUser() {
		return gUser;
	}

	public String getFilename() {
		return gFilename;
	}
	
	public void save() {
		gRequest.getSession().setAttribute("key", gKey);
		gRequest.getSession().setAttribute("charset", gCharset);
		gRequest.getSession().setAttribute("language", gLanguage);
		gRequest.getSession().setAttribute("callback", gCallback);
	}
	
	public void load() {
		gKey=(String)gRequest.getSession().getAttribute("key");
		gCharset=(String)gRequest.getSession().getAttribute("charset");
		gLanguage=(String)gRequest.getSession().getAttribute("language");
		gCallback=(String)gRequest.getSession().getAttribute("callback");
	}
	
	public void transfer() {
		//Download.put(gKey,gFilename);
	}

	public String getContent() {
		return null;
	}
	private String getInlet(String pContent) {
		pContent=Replace.replace(pContent,"${root}",Server.getRoot());
		pContent=Replace.replace(pContent,"${key}",Lib.getUniqueKey());
		
		return pContent;
	}

	public String getScript() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFile(String pContent) {
		// TODO Auto-generated method stub
		
	}
}
