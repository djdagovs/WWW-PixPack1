package com.enlightware.pixpack;

import java.sql.PreparedStatement;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Message;
import com.enlightware.pixpack.Root;
import com.laukien.datetime.DateTime;
import com.laukien.string.Cut;
import com.laukien.string.HTML;

public class Contact extends Root {
	private String gScope;
	private String gName;
	private String gMail;
	private String gSubject;
	private String gMessage;
	private short gStatus;

	public Contact() {
		super();
		
		gScope=gName=gMail=gSubject=gMessage=null;
		gStatus=0;
	}
	
	public String getMail() {
		return gMail;
	}

	public void setMail(String pMail) {
		gMail = pMail;
	}

	public String getMessage() {
		return gMessage;
	}

	public void setMessage(String pMessage) {
		gMessage = pMessage;
	}

	public String getName() {
		return gName;
	}

	public void setName(String pName) {
		gName = pName;
	}

	public String getScope() {
		return gScope;
	}

	public void setScope(String pScope) {
		gScope = pScope;
	}

	public int getStatus() {
		return (int)gStatus;
	}

	public void setStatus(int pStatus) {
		gStatus = (short)pStatus;
	}

	public String getSubject() {
		return gSubject;
	}

	public void setSubject(String pSubject) {
		gSubject = pSubject;
	}

	public boolean check() {
		gScope=gRequest.getParameter("scope");
		gName=gRequest.getParameter("name");
		gMail=gRequest.getParameter("mail");
		gSubject=gRequest.getParameter("subject");
		gMessage=gRequest.getParameter("message");
		
		if(gScope==null || gName==null || gMail==null || gSubject==null || gMessage==null) return false;
		Cut.length(gScope=gScope.trim(),32);
		Cut.length(gName=gName.trim(),128);
		Cut.length(gMail=gMail.trim(),128);
		Cut.length(gSubject.trim(),128);
		gMessage=gMessage.trim();
		
		return !(gScope.length()<3 || gName.length()<3 || gMail.length()<8 || gMail.indexOf('@')<1 || gMessage.length()<20 || HTML.isTag(gScope+gName+gMail+gSubject+gMessage));
	}

	public void write() {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="INSERT INTO contact (scope, name, mail, subject, message, username, timestamp, language, status) VALUES(?,?,?,?,?,?,?,?,?)";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,gScope);
			ps.setString(2,gName);
			ps.setString(3,gMail);
			ps.setString(4,gSubject.length()==0 ? Cut.length(gMessage,64,"...") : gSubject);
			ps.setString(5,gMessage);
			ps.setString(6,Login.getUsername(gRequest));
			ps.setString(7,DateTime.getTimestamp());
			ps.setString(8,gI18n.getLanguage());
			ps.setShort(9,gStatus);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Message.setMessage(gRequest,gI18n.getText("contact.error"),Message.TYPE_ERROR);
			Log.write("Contact.write: Error while accessing the database\n"+e,Log.SYSTEM);
		} finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}

		Message.setMessage(gRequest,gI18n.getText("contact.ok"),Message.TYPE_INFORMATION);
	}

}
