package com.enlightware.pixpack.inlet;

import javax.servlet.http.HttpServletRequest;

public class Null implements InletInterface {

	private String gFilename;

	public void setRequest(HttpServletRequest pRequest) {
		gFilename=pRequest.getPathInfo();
		if(com.laukien.string.String.isNotEmpty(gFilename)) gFilename=gFilename.substring(1);
	}

	public String getContent() {
		return "Error - No valid Inlet\n"+gFilename;
	}

	public String getScript() {
		return "Error - No valid Inlet\n"+gFilename;
	}

	public void setFile(String pContent) {
	}
}
