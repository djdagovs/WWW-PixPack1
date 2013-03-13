package com.enlightware.pixpack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.laukien.array.Add;
import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFSFolder;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Root;
import com.laukien.exception.DatabaseException;

public class DBFSPresentation extends Root {
	private static String[] gsFolders;
	private String gSelectionId;
	private String gSelectionName;
	private String gSelectionMark;
	private String gSelectionException;
	private String gUsername;


	static {
		init();
	}
	
	private synchronized static void init() {
		//load if there are no languages
		String[] folders=new String[]{};
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("SELECT folder.name FROM folder, account WHERE key_user=account.id AND account.name=? ORDER BY folder.name ASC");
			ps.setString(1, Account.ANONYMOUS);
			rs=ps.executeQuery();
			
			if(rs==null) throw new DatabaseException("DBFSFolder.read: No Result");

			//fill
			while(rs.next()) {
				folders=(String[])Add.value(String.class,folders, rs.getString("name"));
			}
		} catch(Exception e) {
			throw new RuntimeException("DBFSFolder.read: Error while accessing the database\n"+e);
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
		
		synchronized(DBFSFolder.class) {
			gsFolders=folders;
		}
	}
	
	public static void reload() {
		init();
	}

	public DBFSPresentation() {
		gSelectionId=null;
		gSelectionName=null;
		gSelectionMark=null;
		gSelectionException=null;
		gUsername=null;
	}
	
	public void setUsername(String pUsername) {
		gUsername=pUsername;
	}

	/**
	 * Sets the TagId of the Folder-Selection
	 * 
	 * @param pId name of the tag
	 */
	public void setSelectionId(String pId) {
		if(com.laukien.string.String.isEmpty(pId)) return;
		gSelectionId=pId;
	}
	
	/**
	 * Sets the TagName of the Folder-Selection
	 * 
	 * @param pName name of the tag
	 */
	public void setSelectionName(String pName) {
		if(com.laukien.string.String.isEmpty(pName)) return;
		gSelectionName=pName;
	}
	
	/**
	 * Sets a pre-selection.
	 * 
	 * @param pName name of the tag
	 */
	public void setSelectionMark(String pMark) {
		gSelectionMark=pMark;
	}
	
	/**
	 * Sets the TagName of the Folder-Selection which don't be shown
	 * 
	 * @param pName name of the tag
	 */
	public void setSelectionException(String pException) {
		gSelectionException=pException;
	}
	
	public String getFolders() {
		checkAll();
		
		StringBuffer sb=new StringBuffer();
		sb.append("<select "+(gSelectionId==null ? "" : "id=\""+gSelectionId+"\" ")+(gSelectionName==null ? "" : "name=\""+gSelectionName+"\" ")+"class=\"field\" onchange=\"if(document.getElementById('upload_form')) document.getElementById('upload_form').description.focus()\">");
		//ROOT
		if(gSelectionException==null || !gSelectionException.equals("/")) sb.append("<option value=\"\">/</option><optgroup>");

		if(gUsername!=null) {
			//init DB
			Database db=new Database();
			PreparedStatement ps=null;
			ResultSet rs=null;
			
			try {
				db.setParameter(Lib.getParameter());
				
				db.connect();

				ps=db.prepareStatement("SELECT folder.name FROM folder, account WHERE key_user=account.id AND account.name=? ORDER BY folder.name ASC");
				ps.setString(1, gUsername);
				rs=ps.executeQuery();
				
				if(rs==null) throw new DatabaseException("DBFSPresentation.getFolders: No Result");

				//fill
				while(rs.next()) {
					if(gSelectionException==null || !rs.getString("name").equals(gSelectionException)) {
						if(gSelectionMark==null || !gSelectionMark.equals(rs.getString("name"))) sb.append("<option>"+rs.getString("name")+"</option>");
						else sb.append("<option selected=\"selected\">"+rs.getString("name")+"</option>");
					}
				}
			} catch(Exception e) {
				throw new RuntimeException("DBFSPresentation.getFolders: Error while accessing the database\n"+e);
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
		} else {
			for(int i=0; i<gsFolders.length; i++) {
				sb.append("<option value=\""+gsFolders[i]+"\">"+gI18n.getText("folder."+gsFolders[i])+"</option>");
			}
		}
		
		//ROOT
		if(gSelectionException==null || !gSelectionException.equals("/")) sb.append("</optgroup>");
		
		sb.append("</select>");
		
		return sb.toString();
	}
	
	public String getWritableFormats() {
		StringBuffer sb=new StringBuffer();
		sb.append("<select "+(gSelectionId==null ? "" : "id=\""+gSelectionId+"\" ")+(gSelectionName==null ? "" : "name=\""+gSelectionName+"\" ")+"class=\"field\">");

		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("SELECT name, description FROM extension WHERE write=? ORDER BY name ASC");
			ps.setBoolean(1, true);
			rs=ps.executeQuery();
			
			if(rs==null) throw new DatabaseException("DBFSPresentation.getWritableFormats: No Result");

			//fill
			while(rs.next()) {
				if(gSelectionException==null || !rs.getString("name").equals(gSelectionException)) {
					if(gSelectionMark!=null && gSelectionMark.equals(rs.getString("name"))) sb.append("<option value=\""+rs.getString("name")+"\" selected=\"selected\">["+rs.getString("name")+"] "+rs.getString("description")+"</option>");
					else sb.append("<option value=\""+rs.getString("name")+"\">["+rs.getString("name")+"] "+rs.getString("description")+"</option>");
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("DBFSPresentation.getWritable: Error while accessing the database\n"+e);
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

		sb.append("</select>");
		
		return sb.toString();
	}
}
