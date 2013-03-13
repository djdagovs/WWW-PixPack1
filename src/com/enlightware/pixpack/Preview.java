package com.enlightware.pixpack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.string.HTML;
import com.laukien.string.Random;
import com.laukien.taglib.i18n.Text;

public class Preview extends Root {
	private static final String ALLFOLDER=" OR (key_user IN (SELECT id FROM account WHERE key_parent=?))";
	private static final String SUBFOLDER=" OR (key_user IN (SELECT id FROM account WHERE key_parent=?) AND file.key_folder IN (SELECT id FROM FOLDER WHERE name=?))";
	
	public static final String LINK=Server.getRoot()+"/show.html?";
	public static final String DESCRIPTION="<a href=\""+Server.getRoot()+"\" title=\"PixPack - Image Hosting\">PixPack</a>";

	private int gId;
	private String gTimestamp;
	private String gKey;
	private String gUser;
	private String gFolder;
	private String gLink;
	private String gDescription;
	private int gCount;
	private boolean gIsAllfolder;
	private boolean gIsSubfolder;
	private boolean gIsWindow;
	private String gCSS;

	public Preview() {
		super();
		gId=-1;
		gTimestamp=null;
		gKey=null;
		gUser=null;
		gFolder=null;
		gLink=null;
		gDescription=null;
		gCount=-1;
		gIsAllfolder=false;
		gIsSubfolder=false;
		gIsWindow=false;
		gCSS=null;
	}
	
	/**
	 * Trys to get "i18n".
	 * 
	 * @return i18n-Object or <code>null</code>
	 */
	public Text getI18n() {
		if(gI18n==null) {
			if(gRequest==null) return null;
			gI18n=Lib.getI18n(gRequest);
		}
		return gI18n;
	}
	
	public int getId() {
		return gId;
	}
	
	public void setTimestamp(String pTimestamp) {
		gTimestamp=pTimestamp;
	}

	public String getTimestamp() {
		return gTimestamp;
	}
	
	public void setKey(String pKey) {
		gKey=pKey;
	}
	
	public String getKey() {
		return gKey;
	}
	
	public String getUser() {
		return gUser;
	}
	
	public void setFolder(String pFolder) {
		gFolder=pFolder;
	}
	
	public String getFolder() {
		return gFolder;
	}
	
	public void setLink(String pLink) {
		if(pLink==null || (pLink=pLink.trim()).length()==0) gLink=null;
		else gLink=HTML.isTag(pLink) ? null : pLink;
	}
	
	public String getLink() {
		return gLink;
	}
	
	public void setDescription(String pDescription) {
		if(pDescription==null || (pDescription=pDescription.trim()).length()==0) gDescription=null;
		else gDescription=HTML.isValidText(pDescription) ?  pDescription : null;
	}
	
	public String getDescription() {
		return gDescription;
	}
	
	public int getCount() {
		return gCount;
	}
	
	public void setAllfolder(boolean pInclude) {
		gIsAllfolder=pInclude;
	}
	
	public boolean isAllfolder() {
		return gIsAllfolder;
	}
	
	public void setSubfolder(boolean pInclude) {
		gIsSubfolder=pInclude;
	}
	
	public boolean isSubfolder() {
		return gIsSubfolder;
	}
	
	public void setWindow(boolean pInclude) {
		gIsWindow=pInclude;
	}
	
	public boolean isWindow() {
		return gIsWindow;
	}
	
	public void checkId() {
		if(gTimestamp==null || gTimestamp.length()!=17 || gKey==null || gKey.length()!=10) throw new ParameterException("Preview.checkId: Timestamp or/and Key not set");
	}
	
	public boolean getPreviewById(int pId) {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//get galelry from db
			ps=db.prepareStatement("SELECT preview.id, preview.link, preview.description, preview.timestamp, preview.key, folder.name AS folder, (SELECT name FROM account WHERE id=folder.key_user) AS folder_user, preview.count, preview.allfolder, preview.subfolder, preview.window " +
					"FROM preview, folder " +
					"WHERE preview.id=? AND preview.key_folder=folder.id");
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) return false;

			//set variables
			gId=rs.getInt("id");
			gLink=rs.getString("link");
			gDescription=rs.getString("description");
			gTimestamp=rs.getString("timestamp");
			gKey=rs.getString("key");
			gFolder=rs.getString("folder");
			gUser=rs.getString("folder_user");
			gCount=rs.getInt("count");
			gIsAllfolder=rs.getBoolean("allfolder");
			gIsSubfolder=rs.getBoolean("subfolder");
			gIsWindow=rs.getBoolean("window");
			
			return true;
		} catch(Exception e) {
			Log.write("Preview.getPreviewById: Database-Error\n"+e,Log.SYSTEM);
			return false;
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
	}
	
	public String getList() {
		checkRequest();
		StringBuffer sb=new StringBuffer();
		int count=-1;

		String query="SELECT preview.id, preview.link, preview.description, preview.timestamp, preview.key, preview.allfolder, folder.name AS folder, preview.count " +
				"FROM preview, folder " +
				"WHERE folder.key_user=? AND preview.key_folder=folder.id ";
		
		//extend the SQL-query
		int offset;
		try {
			offset=Integer.parseInt(gRequest.getParameter("offset"));
			gRequest.getSession().setAttribute("preview.offset", new Integer(offset));
		} catch(Exception e) {
			try {
				offset=((Integer)gRequest.getSession().getAttribute("preview.offset")).intValue();
			} catch(Exception e1) {
				offset=0;
			}
		}

		String order=gRequest.getParameter("order");
		boolean direction;
		try {
			direction=((Boolean)gRequest.getSession().getAttribute("preview.direction")).booleanValue();
		} catch(Exception e) {
			direction=false;
		}
		
		if(com.laukien.string.String.isEmpty(order) || (!order.equals("link") && !order.equals("folder") && !order.equals("count"))) {
			order=(String)gRequest.getSession().getAttribute("preview.order");
			if(order==null) order="timestamp";
		} else {
			if(order.equals((String)gRequest.getSession().getAttribute("preview.order"))) {
				direction=!direction;
				gRequest.getSession().setAttribute("preview.direction",new Boolean(direction));
			}
			gRequest.getSession().setAttribute("preview.order",order);
		}

		
		query+="ORDER BY "+order+" "+(direction ? "DESC" : "ASC") +" OFFSET "+offset+" LIMIT 20";

		//init login
		Login login=new Login();
		login.setRequest(gRequest);
		int userId=login.getUser().getId();

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(preview.id) AS count " +
					"FROM preview, folder " +
					"WHERE folder.key_user=? AND preview.key_folder=folder.id");
			ps.setInt(1,userId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Preview.getList: Invalid Result");
			count=rs.getInt("count");
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement(query);
			ps.setInt(1,userId);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Preview.getList: Invalid Result");
			
			int row=0;
			String id;
			while(rs.next()) {
				row++;
				id=rs.getString("timestamp")+"','"+rs.getString("key");
				sb.append("<tr class=\"row"+((row%2)+1)+"\">");
				sb.append("<td>"+rs.getString("timestamp")+"</td>");
				sb.append("<td>"+(rs.getBoolean("allfolder") ? "*" : rs.getString("folder"))+"</td>");
				sb.append("<td>"+rs.getString("count")+"</td>");
				sb.append("<td class=\"nobr\">");
				sb.append("<a href=\"javascript:preview_show('"+id+"')\"><img alt=\"show\" src=\"/image/icon/show16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"javascript:preview_hotlink('"+id+"')\"><img alt=\"hotlink\" src=\"/image/icon/hotlink16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"javascript:preview_inlet('"+id+"')\"><img alt=\"inlet\" src=\"/image/icon/inlet16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"preview.edit.html?id="+rs.getInt("id")+"\"><img alt=\"edit\" src=\"/image/icon/edit16.gif\" border=\"0\"/></a>&nbsp;");
				sb.append("<a href=\"preview.delete.html?id="+rs.getInt("id")+"\"><img alt=\"delete\" src=\"/image/icon/delete16.gif\" border=\"0\"/></a>");
				sb.append("</td></tr>");
			}
		} catch(Exception e) {
			Log.write("Preview.getList: Database-Error\n"+e,Log.SYSTEM);
			
			//delete variables
			gRequest.getSession().removeAttribute("preview.offset");
			gRequest.getSession().removeAttribute("preview.order");
			gRequest.getSession().removeAttribute("preview.direction");
			
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
			sb.append("<tr><td colspan=\"5\" style=\"text-align: center; margin-top: 20px;\">");
			for(int i=0; i<(count/20)+1; i++) {
				sb.append("<a href=\"?offset="+(i*20)+"\">&nbsp;"+(i+1)+"&nbsp;</a>");
			}
			sb.append("</td></tr>");
		}
		
		return sb.toString();
	}
	
	public boolean add() {
		//check parameters
		if(gFolder==null || (gFolder=gFolder.trim()).length()==0) return false;
		
		Login login=new Login(gRequest);
		int userId=login.getUser().getId();

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//add preview
			//Write intio the database - if there is any hack an exception will be thrown --> error-message
			ps=db.prepareStatement("INSERT INTO preview (timestamp, key, link, description, allfolder, subfolder, window, key_folder) VALUES (?,?,?,?,?,?,?,(SELECT id FROM folder WHERE name=? AND key_user=?))");
			ps.setString(1, DateTime.getTimestamp());	//timestamp
			ps.setString(2, Random.random(10,'a','z'));	//key
			ps.setString(3, gLink);
			ps.setString(4, gDescription);
			ps.setBoolean(5, gIsAllfolder);
			ps.setBoolean(6, gIsSubfolder);
			ps.setBoolean(7, gIsWindow);
			ps.setString(8, gFolder);
			ps.setInt(9, userId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Preview.add: Database-Error\n"+e,Log.SYSTEM);
			return false;
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
		
		return true;
	}
	
	public boolean edit() {
		if(gId==-1) return false;

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE preview set link=?, description=?, window=? WHERE id=?");
			ps.setString(1, gLink);
			ps.setString(2, gDescription);
			ps.setBoolean(3, gIsWindow);
			ps.setInt(4, gId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Previwe.edit: Database-Error\n"+e,Log.SYSTEM);
			return false;
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
		
		return true;
	}
	
	public boolean delete() {
		if(gId==-1) return false;
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("DELETE FROM preview WHERE id=?");
			ps.setInt(1, gId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Preview.delete: Database-Error\n"+e,Log.SYSTEM);
			return false;
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
		
		return true;
	}
	
	public String show() {
		checkId();
		StringBuffer sb=new StringBuffer();
		
		int folder;
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the preview exists and its permission
			ps=db.prepareStatement("SELECT " +
					"preview.id, preview.link, preview.description, preview.allfolder, preview.subfolder, preview.window, preview.last, preview.count, " +
					"preview.key_folder AS folder_id, folder.name AS folder_name, " +
					"account.id AS user_id, account.name AS user_name, account.status AS user_status " +
					"FROM preview, folder, account " +
					"WHERE preview.timestamp=? AND preview.key=? AND preview.key_folder=folder.id AND folder.key_user=account.id");
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) return "Invalid Preview";
			
			folder=rs.getInt("folder_id");

			//build common XML
			gLink=rs.getString("link");
			gDescription=rs.getString("description");
			gFolder=rs.getString("folder_name");
			gCount=rs.getInt("count");
			gIsAllfolder=rs.getBoolean("allfolder");
			gIsSubfolder=rs.getBoolean("subfolder");
			gIsWindow=rs.getBoolean("window");
			
			//get user information (id, name)
/*			ps=db.prepareStatement("SELECT account.id AS user_id, account.name AS user_name FROM account, folder WHERE folder.id=? AND folder.key_user=account.id LIMIT 1");
			ps.setInt(1, folder);
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) return "Invalid User";
*/			

			gUser=rs.getString("user_name");
			int userId=rs.getInt("user_id");
			int status=rs.getInt("user_status");

			//get images from folder
			if(gIsAllfolder) {
				ps=db.prepareStatement("SELECT timestamp, key, " +
						"(SELECT name FROM extension WHERE extension.id=file.key_extension) AS extension " +
						"FROM file " +
						"WHERE public=? AND status>=? AND (key_user=?"+ALLFOLDER+") " +
						"ORDER BY random() LIMIT 1");
				ps.setBoolean(1, true);
				ps.setInt(2, DBFSFile.STATUS_DEFAULT);
				ps.setInt(3, userId);
				ps.setInt(4, userId);
			} else {
				ps=db.prepareStatement("SELECT timestamp, key, " +
						"(SELECT name FROM extension WHERE extension.id=file.key_extension) AS extension " +
						"FROM file " +
						"WHERE public=? AND status>=? AND (key_folder=?"+(gIsSubfolder ? SUBFOLDER : "")+") " +
						"ORDER BY random() LIMIT 1");
				ps.setBoolean(1, true);
				ps.setInt(2, DBFSFile.STATUS_DEFAULT);
				ps.setInt(3, folder);
				if(gIsSubfolder) {
					ps.setInt(4, userId);
					ps.setString(5, gFolder);
				}
			}
			rs=ps.executeQuery();
			
			if(rs==null || !rs.next()) return "Invalid Preview";

			sb.append("<a href=\""+(gIsWindow ? "#\" onclick=\"return top.open('" :""));
			if(gLink==null || status<User.STATUS_PREMIUM) sb.append(LINK);
			else sb.append(gLink==null ? null : (gLink.indexOf('?')==-1 ? gLink+'?' : gLink+'&'));
			sb.append("timestamp="+rs.getString("timestamp")+"&key="+rs.getString("key"));
			sb.append((gIsWindow ? "','','')" : ""));
			sb.append("\">\n<img alt=\"PixPack\" src=\""+Server.getRoot()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\"/></a><br/>");
			if(gDescription==null || status<User.STATUS_PREMIUM) sb.append(DESCRIPTION);
			else sb.append(gDescription);

		} catch(Exception e) {
			Log.write("Preview.show: Database-Error\n"+e,Log.SYSTEM);
			return "Preview.show: Database-Error\n"+e;
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
		
		return "<div class=\"pixpack_preview\">\n"+sb.toString()+"</div>";
	}

	public void setCSS(String pColor) {
		gCSS=pColor;
	}
	
	public String getCSS() {
		return gCSS;
	}
}
