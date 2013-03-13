package com.enlightware.pixpack.admin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Server;
import com.laukien.exception.DatabaseException;

public class Referer {

	private HttpServletRequest gRequest;
	private int gId;
	private int gCount;
	private String gName;
	
	public Referer() {
		gId=-1;
		gCount=-1;
		gName=null;
	}

	public int getId() {
		return gId;
	}
	
	public int getCount() {
		return gCount;
	}
	
	public String getName() {
		return gName;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gRequest=pRequest;
	}
	
	public void update() {
		
	}
	
	public String getList() {
		StringBuffer sb=new StringBuffer();
		String id="";
		int count;
		int offset;
		try {
			offset=Integer.parseInt(gRequest.getParameter("offset"));
			gRequest.getSession().setAttribute("admin.referer.offset", new Integer(offset));
		} catch(Exception e) {
			try {
				offset=((Integer)gRequest.getSession().getAttribute("admin.referer.offset")).intValue();
			} catch(Exception e1) {
				offset=0;
			}
		}
		
		//order
		String order=gRequest.getParameter("order");
		boolean direction;
		try {
			direction=((Boolean)gRequest.getSession().getAttribute("admin.referer.direction")).booleanValue();
		} catch(Exception e) {
			direction=true;
		}
		if(com.laukien.string.String.isEmpty(order) || (!order.equals("id") && !order.equals("count") && !order.equals("name"))) {
			order=(String)gRequest.getSession().getAttribute("admin.referer.order");
			if(order==null) order="id";
		} else {
			if(order.equals((String)gRequest.getSession().getAttribute("admin.referer.order"))) {
				direction=!direction;
				gRequest.getSession().setAttribute("admin.referer.direction",new Boolean(direction));
			}
			gRequest.getSession().setAttribute("admin.referer.order",order);
		}

		
		String additional="ORDER BY "+order+" "+(direction ? "DESC" : "ASC") +" OFFSET "+offset+" LIMIT 20";

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Count referers
			ps=db.prepareStatement("SELECT count(id) AS count FROM referer");
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Referer.getList: Invalid Result");
			count=rs.getInt("count");

			//Remove fiels form the filesystem
			ps=db.prepareStatement("SELECT * FROM referer WHERE visible IS NULL "+additional);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Referer.getList: Invalid Result");

			while(rs.next()) {
				id+=rs.getInt("id")+" ";
				sb.append("<tr class=\"row"+((rs.getRow()%2)+1)+"\">");
				sb.append("<td>"+(rs.getRow()+offset)+"</td>");
				sb.append("<td>"+rs.getInt("id")+"</td>");
				sb.append("<td>"+rs.getInt("count")+"</td>");
				sb.append("<td><a target=\"_blank\" href=\"http://"+rs.getString("name")+"\">"+rs.getString("name")+"</a></td>");
				sb.append("<td>");
				sb.append("<a target=\"blank\" href=\"/admin.referer_show.html?id="+rs.getString("id")+"\"><img border=\"0\" src=\"/image/icon/show16.gif\" alt=\"\"/></a>");
				sb.append("</td>");
				sb.append("</tr>");
			}
		} catch(Exception e) {
			Log.write("Referer.getList: Database-Error\n"+e,Log.SYSTEM);
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
		sb.append("<tr><td colspan=\"5\">");
		sb.append("<input type=\"hidden\" name=\"id\" value=\""+id+"\"/>");
		sb.append(Lib.next(count, offset, 20, null, null));
		sb.append("</td></tr>");
		
		return sb.toString();
	}

	public void read(int id) {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Name referers
			ps=db.prepareStatement("SELECT name FROM referer WHERE id=?");
			ps.setInt(1,id);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Referer.read: Invalid Result");
			gName=rs.getString("name");

			//count
			ps=db.prepareStatement("SELECT count(*) FROM file WHERE key_referer=?");
			ps.setInt(1,id);
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Referer.read: Invalid Result");
			gCount=rs.getInt("count");
			
			//id
			gId=id;
		} catch(Exception e) {
			Log.write("Referer.read: Database-Error\n"+e,Log.SYSTEM);
			gId=-1;
			gCount=-1;
			gName=null;
			return;
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

	public void read(String name) {
		gName=name.toLowerCase();
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Name referers
			ps=db.prepareStatement("SELECT id FROM referer WHERE name=?");
			ps.setString(1,gName);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Referer.read: Invalid Result");
			gId=rs.getInt("id");

			//count
			ps=db.prepareStatement("SELECT count(*) FROM file WHERE key_referer=?");
			ps.setInt(1,gId);
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Referer.read: Invalid Result");
			gCount=rs.getInt("count");
		} catch(Exception e) {
			Log.write("Referer.read: Database-Error\n"+e,Log.SYSTEM);
			gId=-1;
			gCount=-1;
			gName=null;
			return;
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

	public void visible(int id, boolean status) {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Name referers
			ps=db.prepareStatement("UPDATE referer SET visible=? WHERE id=?");
			ps.setBoolean(1, status);
			ps.setInt(2,id);
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Referer.visible: Database-Error\n"+e,Log.SYSTEM);
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
	}
	
	public void clean() {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Name referers
			ps=db.prepareStatement("SELECT id FROM file WHERE key_user=(SELECT id FROM account WHERE name_lo='anonymous') AND key_referer IN (SELECT id FROM referer WHERE visible=false)");
			
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("Referer.clean: Invalid Result");
			
			while(rs.next()) {
				DBFSFile.delete(rs.getInt("id"));
			}
		} catch(Exception e) {
			Log.write("Referer.clean: Database-Error\n"+e,Log.SYSTEM);
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

	public String getGallery() {
		if(gId==-1) return "Invalid Gallery";
		
		StringBuffer sb=new StringBuffer();
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//Random referers
			ps=db.prepareStatement("SELECT *, extension.name AS extension FROM file, extension WHERE file.key_extension=extension.id AND key_referer=? ORDER BY random() LIMIT 24");
			ps.setInt(1, gId);
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("Referer.getGallery: Invalid Result");

			while(rs.next()) {
				sb.append("<img alt=\"\" src=\"http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\"/>");
			}
		} catch(Exception e) {
			Log.write("Referer.getGalelry: Database-Error\n"+e,Log.SYSTEM);
			return "Referer.getGallery: Database-Error\n"+e;
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

		return sb.toString();
	}
}
