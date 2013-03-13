package com.enlightware.pixpack;

import javax.servlet.http.HttpServletRequest;

public class Message {
	public static final int TYPE_NONE = -1;
	public static final int TYPE_INFORMATION = 1;
	public static final int TYPE_WARNING = 2;
	public static final int TYPE_ERROR = 3;

	private HttpServletRequest gRequest;

	public Message() {
		gRequest=null;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gRequest=pRequest;
	}

	public static void removeMessage(HttpServletRequest pRequest) {
		pRequest.getSession().removeAttribute("message.text");
		pRequest.getSession().removeAttribute("message.type");
	}

	public static void setMessage(HttpServletRequest pRequest, String pText, int pType) {
		pRequest.setAttribute("message.text", pText);
		pRequest.setAttribute("message.type", new Integer(pType));
	}
	
	public static String getMessage(HttpServletRequest pRequest) {
		return (String)pRequest.getAttribute("message.text");
	}

	public static int getMessageType(HttpServletRequest pRequest) {
		try {
			return ((Integer)pRequest.getAttribute("message.type")).intValue();
		} catch(Exception e) {
			return TYPE_NONE;
		}
	}
	
	/**
	 * Retruns the type of the message as a usable class-name.
	 * 
	 * @param pRequest request-variable
	 * @return class-descriptior
	 */
	public static String getMessageTypeAsClass(HttpServletRequest pRequest) {
		switch(getMessageType(pRequest)) {
		case TYPE_INFORMATION: return "message_information";
		case TYPE_WARNING: return "message_warning";
		case TYPE_ERROR: return "message_error";
		default: return "";
		}
	}
	
	/**
	 * Returns the current server-message.
	 * Is there are no message the result will be "&amp;#160;".
	 * 
	 * @return server-message
	 */
	public String getMessage() {
		if(gRequest==null) return "&#160;";
		String msg=getMessage(gRequest);
		
		return msg!=null ? msg:"&#160;";
	}
	
	public String getMessageTypeAsClass() {
		if(gRequest==null) return "";
		return getMessageTypeAsClass(gRequest);
	}	
}
