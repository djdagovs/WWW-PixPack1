package com.enlightware.pixpack.admin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.laukien.exception.DatabaseException;

public class Account extends com.enlightware.pixpack.Account {
	/**
	 * Generates a user-list which depends on the current/active user
	 * 
	 * @return HTML-Table-Inlet
	 */
	public String getList() {
		checkRequest();
		StringBuffer sb=new StringBuffer();
		int count=-1;
		//init login
		Login login=new Login();
		login.setRequest(gRequest);
		int parent=login.getParentId();
		
		String condition="";
		switch(parent) {
		case SYSTEM_ID:
			switch(login.getUser().getId()) {
			case SYSTEM_ID:
				//the query is ok
				break;
			case ADMIN_ID:
				condition+="WHERE key_parent="+ADMIN_ID;
				break;
			case ANONYMOUS_ID:
				condition+="WHERE key_parent="+ANONYMOUS_ID;
				break;
			case FREE_ID:
				condition+="WHERE key_parent="+FREE_ID;
				break;
			case PREMIUM_ID:
				condition+="WHERE key_parent="+PREMIUM_ID;
				break;
			case BUSINESS_ID:
				condition+="WHERE key_parent="+BUSINESS_ID;
				break;
			default:
				return "<tr><td colspan=\"5\">Permission-Error (parent)</td></tr>";
			}
			break;
		case ADMIN_ID:
			condition+="WHERE key_parent="+FREE_ID+" OR key_parent="+PREMIUM_ID;
			break;
		case BUSINESS_ID:
			condition+="WHERE key_parent=(SELECT id FROM account WHERE name='"+login.getUsername()+"')";
			break;
		default:
			return "<tr><td colspan=\"5\">Permission-Error (id)</td></tr>";
		}
		
		//extend the SQL-query

		int offset;
		try {
			offset=Integer.parseInt(gRequest.getParameter("offset"));
			gRequest.getSession().setAttribute("admin.account.offset", new Integer(offset));
		} catch(Exception e) {
			try {
				offset=((Integer)gRequest.getSession().getAttribute("admin.account.offset")).intValue();
			} catch(Exception e1) {
				offset=0;
			}
		}

		String order=gRequest.getParameter("order");
		boolean direction;
		try {
			direction=((Boolean)gRequest.getSession().getAttribute("admin.account.direction")).booleanValue();
		} catch(Exception e) {
			direction=false;
		}
		
		if(com.laukien.string.String.isEmpty(order) || (!order.equals("id") && !order.equals("name") && !order.equals("last") && !order.equals("status"))) {
			order=(String)gRequest.getSession().getAttribute("admin.account.order");
			if(order==null) order="id";
		} else {
			if(order.equals((String)gRequest.getSession().getAttribute("admin.account.order"))) {
				direction=!direction;
				gRequest.getSession().setAttribute("admin.account.direction",new Boolean(direction));
			}
			gRequest.getSession().setAttribute("admin.account.order",order);
		}

		
		String additional="ORDER BY "+order+" "+(direction ? "DESC" : "ASC") +" OFFSET "+offset+" LIMIT 20";

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(id) AS count FROM account "+condition);
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Account.getUserList: Invalid Result");
			count=rs.getInt("count");
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement("SELECT id, name, mail, last, status FROM account "+condition+' '+additional);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Account.getUserList: Invalid Result");
			
			int row=0;
			while(rs.next()) {
				row++;
				sb.append("<tr class=\"row"+((row%2)+1)+"\">");
				sb.append("<td>"+rs.getInt("id")+"</td>");
				sb.append("<td><a href=\"mailto:"+rs.getString("mail")+"\">"+rs.getString("name")+"</a></td>");
				sb.append("<td>"+rs.getString("last")+"</td>");
				sb.append("<td>"+rs.getString("status")+"</td>");
				sb.append("<td class=\"nobr\">");
				sb.append("<a href=\"admin.account_edit.html?id="+rs.getInt("id")+"\"><img alt=\"edit\" src=\"/image/icon/edit16.gif\" border=\"0\"/></a>&nbsp;");
				//sb.append("<a href=\"admin.account_image.html?id="+rs.getInt("id")+"\"><img alt=\"image\" src=\"/image/icon/image16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"admin.account_delete.html?id="+rs.getInt("id")+"\"><img alt=\"delete\" src=\"/image/icon/delete16.gif\" border=\"0\"/></a>");
				sb.append("</td></tr>");
			}
		} catch(Exception e) {
			Log.write("Account.getUserList: Database-Error\n"+e,Log.SYSTEM);
			
			//delete variables
			gRequest.getSession().removeAttribute("admin.account.offset");
			gRequest.getSession().removeAttribute("admin.account.order");
			gRequest.getSession().removeAttribute("admin.account.direction");
			
			return "<tr><td colspan=\"5\">Permission-Database<br/>"+e+"</td></tr>";
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
		
		//Navigation
		if(count>20) {
			sb.append("<tr><td colspan=\"5\">");
			sb.append(Lib.next(count, offset, 20, null, null));
			sb.append("</td></tr>");
		}
		
		return sb.toString();
	}
}
