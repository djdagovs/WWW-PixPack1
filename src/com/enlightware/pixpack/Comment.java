package com.enlightware.pixpack;

import com.laukien.datetime.DateTime;

public class Comment {
	private String gText;
	private DateTime gTimestamp;
	private String gUser;
	
	public Comment() {
		gText=null;
		gTimestamp=null;
		gUser=null;
	}
	
	public Comment(String pText, DateTime pTimestamp, String pUser) {
		gText=pText;
		gTimestamp=pTimestamp;
		gUser=pUser;
	}
	
	public String getText() {
		return gText;
	}
	public void setText(String text) {
		this.gText = text;
	}
	public DateTime getTimestamp() {
		return gTimestamp;
	}
	public void setTimestamp(DateTime timestamp) {
		this.gTimestamp = timestamp;
	}
	public String getUser() {
		return gUser;
	}
	public void setUser(String user) {
		this.gUser = user;
	}
}
