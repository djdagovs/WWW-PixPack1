package com.enlightware.pixpack.admin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFS;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Server;
import com.laukien.exception.DatabaseException;
import com.laukien.string.Cut;

public class Image {
	
	private boolean gIsAnonymous;
	private boolean gIsEdit;
	private HttpServletRequest gRequest;

	
	public Image() {
		gRequest=null;
		gIsAnonymous=false;
		gIsEdit=false;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gRequest=pRequest;
	}
	
	public void setAnonymous(boolean pIsAnonymous) {
		gIsAnonymous=pIsAnonymous;
	}
	
	public boolean isAnonymous() {
		return gIsAnonymous;
	}

	public void setEdit(boolean pIsEdit) {
		gIsEdit=pIsEdit;
	}
	
	public boolean isEdit() {
		return gIsEdit;
	}
	
	public String getList() {
		StringBuffer sb=new StringBuffer();
		sb.append("<div>");
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement("SELECT * FROM view_last WHERE status=? AND key_user=(SELECT id FROM account WHERE name=?) LIMIT 10");
			ps.setInt(1, DBFSFile.STATUS_DEFAULT);
			ps.setString(2, Account.ANONYMOUS);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Image.getLast: Invalid Result");
			int count=0;
			sb.append("<table class=\"gallery\">");
			sb.append("<tr>");
			while(rs.next()) {
				if(count%5==0) sb.append("</tr><tr>");
				count++;
				sb.append("<td>");
				sb.append("<img src=\"http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\" alt=\""+rs.getString("name")+"\" title=\""+rs.getString("name")+"\"/ style=\"width: 120px\" onclick=\"image_open('http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+'/'+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"',"+rs.getInt("width")+','+rs.getInt("height")+")\">");
				sb.append("<br/>");
				sb.append("<input title=\"Update\" type=\"radio\" name=\"_"+rs.getString("timestamp")+'_'+rs.getString("key")+"\" checked=\"checked\" onchange=\"admin_image_status('"+rs.getString("timestamp")+'_'+rs.getString("key")+"','update')\"/>U");
				sb.append("<input title=\"Delete\" type=\"radio\" name=\"_"+rs.getString("timestamp")+'_'+rs.getString("key")+"\" onchange=\"admin_image_status('"+rs.getString("timestamp")+'_'+rs.getString("key")+"','delete')\"/>D");
				sb.append("<input title=\"GagPack\" type=\"radio\" name=\"_"+rs.getString("timestamp")+'_'+rs.getString("key")+"\" onchange=\"admin_image_status('"+rs.getString("timestamp")+'_'+rs.getString("key")+"','gagpack')\"/>G");
				sb.append("<input title=\"Sex\" type=\"radio\" name=\"_"+rs.getString("timestamp")+'_'+rs.getString("key")+"\" onchange=\"admin_image_status('"+rs.getString("timestamp")+'_'+rs.getString("key")+"','sex')\"/>S");
				sb.append("<input id=\""+rs.getString("timestamp")+'_'+rs.getString("key")+"\" name=\""+rs.getString("timestamp")+'_'+rs.getString("key")+"\" type=\"hidden\" value=\"update\"/>");
				sb.append("</td>");
			}
			sb.append("</tr></table>");
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//no nothing
			}
			Log.write("Image.getLast: Database-Error\n"+e,Log.SYSTEM);
			return "<tr><td colspan=\"4\">Permission-Database<br/>"+e+"</td></tr>";
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(db!=null) db.close();
		}
		sb.append("</div>");
		return sb.toString();
	}
	
	public void update() {
		String key;
		Enumeration num=gRequest.getParameterNames();
		//if(true) throw new RuntimeException("K:20070201073503517_snhuoavavb ::"+gRequest.getParameter("20070201073503517_snhuoavavb"));
		while(num.hasMoreElements()) {
			key=(String)num.nextElement();
			if(key.length()!=28) continue;

			if(gRequest.getParameter(key).equals("update")) update(key);
			else if(gRequest.getParameter(key).equals("delete")) delete(key);
			else if(gRequest.getParameter(key).equals("gagpack")) gagpack(key);
			else if(gRequest.getParameter(key).equals("sex")) sex(key);
			else break;
			
		}
	}
	
	private void update(String pKey) {
		DBFSFile file=new DBFSFile();
		file.setTimestamp(pKey.substring(0,17));
		file.setKey(pKey.substring(18));
		
		try {
			file.read();
			file.setUsername(Account.ANONYMOUS);
			file.status(DBFSFile.STATUS_SAVE);
		} catch (DatabaseException e) {
			//do nothing
			throw new RuntimeException(e);
		}
	}
	
	private void delete(String pKey) {
		DBFSFile file=new DBFSFile();
		file.setTimestamp(pKey.substring(0,17));
		file.setKey(pKey.substring(18));
		
		try {
			file.read();
			file.setUsername(Account.ANONYMOUS);
			file.delete();
		} catch (DatabaseException e) {
			//do nothing
			throw new RuntimeException(e);
		}
	}

	private void gagpack(String pKey) {
		DBFSFile file=new DBFSFile();
		file.setTimestamp(pKey.substring(0,17));
		file.setKey(pKey.substring(18));
		
		try {
			file.read();
			file.setUsername(Account.ANONYMOUS);
			file.move(null, "GagPack");	//root folder
			file.setUsername("GagPack");
			file.status(DBFSFile.STATUS_SAVE);
			file.status(true, false);
		} catch (DatabaseException e) {
			//do nothing
			throw new RuntimeException(e);
		}
	}

	private void sex(String pKey) {
		DBFSFile file=new DBFSFile();
		file.setTimestamp(pKey.substring(0,17));
		file.setKey(pKey.substring(18));
		
		try {
			file.read();
			file.setUsername(Account.ANONYMOUS);
			//file.move(null, "Sex");	//root folder
			//file.setUsername("Sex");
			file.status(DBFSFile.STATUS_BAN);
			file.status(false,true);	//public & adult
		} catch (DatabaseException e) {
			//do nothing
			throw new RuntimeException(e);
		}
	}
}
