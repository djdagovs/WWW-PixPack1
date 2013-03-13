package com.enlightware.pixpack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.Server;
import com.laukien.exception.DatabaseException;
import com.laukien.string.Cut;

public class Extra extends Root {

	public Extra() {
		super();
	}
	
	public String getRandomGallery() {
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
			ps=db.prepareStatement("SELECT * FROM view_randomgallery LIMIT 20");
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Extra.getRandomGallery: Invalid Result");
			int count=0;
			sb.append("<table class=\"gallery\">");
			sb.append("<tr>");
			while(rs.next()) {
				if(count%5==0) sb.append("</tr><tr>");
				count++;
				sb.append("<td>");
				sb.append("<a href=\""+Server.getRoot()+"/show.html?timestamp="+rs.getString("timestamp")+"&key="+rs.getString("key")+"\">");
				sb.append("<img src=\"http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\" alt=\""+rs.getString("name")+"\" title=\""+rs.getString("name")+"\"/>");
				sb.append("<br/>"+Cut.length(rs.getString("name"),10,"..."));
				sb.append("</a></td>");
			}
			sb.append("</tr></table>");
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//no nothing
			}
			Log.write("Extra.getRandomGallery: Database-Error\n"+e,Log.SYSTEM);
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
}
