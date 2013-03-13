package com.enlightware.pixpack.inlet;

import javax.servlet.http.HttpServletRequest;

public interface InletInterface {

	public void setRequest(HttpServletRequest pRequest);
	public void setFile(String pContent);
	public String getScript();
	public String getContent();
}
