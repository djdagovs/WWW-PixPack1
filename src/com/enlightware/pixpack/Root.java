package com.enlightware.pixpack;

import javax.servlet.http.HttpServletRequest;

import com.laukien.exception.ParameterException;
import com.laukien.taglib.i18n.Text;

public abstract class Root {
	protected HttpServletRequest gRequest;
	protected Text gI18n;
	
	public Root() {
		gRequest=null;
		gI18n=null;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gRequest=pRequest;
	}
	
	public void setI18n(com.laukien.taglib.i18n.Text pI18n) {
		gI18n=pI18n;
	}
	
	public boolean isSubmitted() {
		if(gRequest==null) throw new ParameterException("Root.isSubmitted: Request-variable not set");
		return gRequest.getParameter("submit")!=null && gRequest.getAttribute("forward")==null;
	}
	
	public void checkRequest() {
		if(gRequest==null) throw new ParameterException("Root.checkRequest: Request-variable not set");
	}
	
	public void checkI18n() {
		if(gI18n==null) throw new ParameterException("Root.checkI18n: I18n-variable not set");
	}
	
	public void checkAll() {
		checkRequest();
		checkI18n();
	}
}
