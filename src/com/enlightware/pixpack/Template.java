package com.enlightware.pixpack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Root;
import com.laukien.exception.DatabaseException;

public class Template extends Root {

	public static final int TYPE_ALL		= 0;
	public static final int TYPE_GALLERY	= 1;
	public static final int TYPE_HOMEPAGE	= 2;

	public Template() {
		super();
	}
	
	public String getGalleryList() {
		checkRequest();
	
		StringBuffer sb=new StringBuffer();
		
		Login login=new Login(gRequest);
		int parentId=login.getParentId();
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//get templates (depending on the user)
			if(parentId<Account.BUSINESS_ID) ps=db.prepareStatement("SELECT * from template ORDER BY name");
			else {
				ps=db.prepareStatement("SELECT * from template WHERE key_parent=1 OR key_parent=? AND (type=0 OR type=?) ORDER by name");
				ps.setInt(1,parentId);
				ps.setInt(2,TYPE_GALLERY);
			}
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Template.getGalleryList: Invalid Result");
			
			int row=0;

			StringTokenizer st;
			String tempName, tempTitle, tempStyle;
			while(rs.next()) {
				row++;
				tempName=rs.getString("name");
				tempTitle=rs.getString("title");
				
				if(rs.getString("styles")!=null) {
					sb.append("<optgroup label=\""+tempName+"\" class=\"row"+((row%2)+1)+"\">");

					st=new StringTokenizer(rs.getString("styles"),"|");
					int count=st.countTokens();
					for(int i=0; i<count; i++) {
						tempStyle=st.nextToken();
						sb.append("<option value=\""+tempName+'.'+tempStyle+"\">"+tempStyle+"</option>");			
					}
					
					sb.append("</optgroup>");
				}
				else sb.append("<option value=\""+tempName+"\" class=\"row"+((row%2)+1)+"\">"+tempTitle+"</option>");
			}
		} catch(Exception e) {
			Log.write("Template.getGalleryList: Database-Error\n"+e,Log.SYSTEM);
			return "Permission-Database<br/>"+e;
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
		
		return "<select name=\"template\" class=\"field\">"+sb.toString()+"</select>";
	}
}