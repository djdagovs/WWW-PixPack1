package com.enlightware.pixpack.inlet;


import javax.servlet.http.HttpServletRequest;

import com.laukien.string.Remove;
import com.laukien.string.Replace;

public class Preview implements InletInterface {
	private String gTimestamp;
	private String gKey;

	public Preview() {
		super();
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gTimestamp=pRequest.getParameter("timestamp");
		gKey=pRequest.getParameter("key");
	}

	public String getScript() {
		if(gTimestamp==null || gKey==null && (gTimestamp+gKey).length()!=27) return Inlet.getCopyright();
		com.enlightware.pixpack.Preview preview=new com.enlightware.pixpack.Preview();
		preview.setTimestamp(gTimestamp);
		preview.setKey(gKey);
		String result=preview.show();
		result=Replace.replace(result,"\'","\\'");
		result=Replace.replace(result,"\n","\\n");
		result=Remove.deleteChar(result,'\n');
		//!!!
		result=Remove.deleteChar(result,'\r');
		return "document.write('"+result+"');";
	}
	
	public String getContent() {
		return null;
	}

	public void setFile(String pContent) {
		//no js-file		
	}

}
