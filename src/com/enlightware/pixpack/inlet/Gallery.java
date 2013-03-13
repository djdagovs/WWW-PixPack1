package com.enlightware.pixpack.inlet;

import javax.servlet.http.HttpServletRequest;

import com.enlightware.pixpack.Server;

public class Gallery implements InletInterface {
	private static final String WIDTH = "500px";
	private static final String HEIGHT = "320px";
	private static final String COLOR = "#FFFFFF";
	
	private String gCSS;
	private String gWidth;
	private String gHeight;
	private String gTimestamp;
	private String gKey;
	private int gPosition;

	public Gallery() {
		super();
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gWidth=pRequest.getParameter("width");
		if(gWidth==null) gWidth=WIDTH;
		gHeight=pRequest.getParameter("height");
		if(gHeight==null) gHeight=HEIGHT;
		gCSS=pRequest.getParameter("css");
		if(gCSS!=null && gCSS.length()==0) gCSS=null;
		gTimestamp=pRequest.getParameter("timestamp");
		gKey=pRequest.getParameter("key");
		try {
			gPosition=Integer.parseInt(pRequest.getParameter("position"));
		} catch(Exception e) {
			gPosition=1;
		}
	}

	public String getScript() {
		if(gTimestamp==null || gKey==null && (gTimestamp+gKey).length()!=27) return "document.write('"+Inlet.getCopyright()+"')";
		else return "document.write('<iframe src=\""+Server.getRoot()+"/gallery.inlet?timestamp="+gTimestamp+"&key="+gKey+(gCSS==null ? "" : "&css="+gCSS)+"\" width=\""+gWidth+"\" height=\""+gHeight+"\" frameborder=\"0\" scrolling=\"auto\">"+Inlet.getCopyright()+"</iframe>');";
	}
	
	public String getContent() {
		if(gTimestamp==null || gKey==null && (gTimestamp+gKey).length()!=27) return Inlet.getCopyright();
		com.enlightware.pixpack.Gallery gallery=new com.enlightware.pixpack.Gallery();
		gallery.setTimestamp(gTimestamp);
		gallery.setKey(gKey);
		gallery.setPosition(gPosition);
		gallery.setCSS(gCSS);
		try {
			return gallery.show();
		} catch(Exception e) {
			return Inlet.getCopyright();
		}
	}

	public void setFile(String pContent) {
		//no js-file		
	}

}
