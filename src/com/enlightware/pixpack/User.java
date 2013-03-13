package com.enlightware.pixpack;

import com.laukien.datetime.DateTime;
import com.laukien.string.Cut;

public class User {

	/**
	 * Marks the status of an user.
	 * A user could be a child of a business-account an have its own status.
	 */
	public static final int STATUS_DENIED	= -1;
	public static final int STATUS_NONE		= 0;
	public static final int STATUS_FREE		= 1;
	public static final int STATUS_PREMIUM	= 2;

	
/**
	public static final int STATUS_BUSINESS	= 3;
	public static final int STATUS_ADMIN	= 8;
	public static final int STATUS_SYSTEM	= 9;
**/
	private int gId;
	private String gName;
	private String gPassword;
	private String gMail;
	private DateTime gTimestamp;
	private DateTime gLast;
	private int gStatus;
	private int gParentId;

	public User() {
		gId=gStatus=gParentId=-1;
		gName=gPassword=gMail=null;
		gTimestamp=gLast=null;
	}
	
	protected void setId(int pId) {
		gId=pId;
	}
	
	public int getId() {
		return gId;
	}

	public void setName(String pName) {
		gName=pName;
		if(gName!=null) gName=Cut.length(gName,128);
	}
	
	public String getName() {
		return gName;
	}

	public void setMail(String pMail) {
		gMail=pMail;
		if(gMail!=null) gMail=Cut.length(gMail,128);
	}
	
	public String getMail() {
		return gMail;
	}

	public void setPassword(String pPassword) {
		gPassword=pPassword;
		if(gPassword!=null) gPassword=Cut.length(gPassword,128);
	}

	public String getPassword() {
		return gPassword;
	}
	
	public void setTimestamp(DateTime pTimestamp) {
		gTimestamp=pTimestamp;
	}
	
	public void setTimestamp(String pTimestamp) {
		gTimestamp=new DateTime(pTimestamp);
	}

	public DateTime getTimestamp() {
		return gTimestamp;
	}
	
	public void setLast(DateTime pLast) {
		gLast=pLast;
	}
	
	public void setLast(String pLast) {
		gLast=new DateTime(pLast);
	}

	public DateTime getLast() {
		return gLast;
	}
	
	public void setStatus(int pStatus) {
		gStatus=pStatus;
		if(gStatus<STATUS_DENIED || gStatus>STATUS_PREMIUM) gStatus=STATUS_NONE;
	}
	
	public int getStatus() {
		return gStatus;
	}
	
	public void setParentId(int pParentId) {
		gParentId=pParentId;
	}
	
	public int getParentId() {
		return gParentId;
	}
}
