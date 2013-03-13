package com.enlightware.pixpack;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.laukien.bean.database.Database;
import com.laukien.bean.database.Parameter;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Root;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.string.Cut;

public class Log extends Root {
	public static final int INFORMATION=1;
	public static final int WARNING=2;
	public static final int USER=3;
	public static final int SYSTEM=4;

	private static boolean gsIsDatabase;
	
	static {
		init();
	}
	
	private synchronized static void init() {
		
/*		Parameter param;
		try {
			param=new Parameter();
			param.load(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"log.properties");
			com.laukien.bean.log.Log.setDatabase(param);
			gsIsDatabase=true;
		} catch(Exception e) {
			gsIsDatabase=false;
			write("Log.init: Unable to init database", com.laukien.bean.log.Status.FATAL);
		}

		com.laukien.bean.log.Log.setScope("pixpack");
*/
		//init
		com.laukien.bean.log.Lib.getScope();
		gsIsDatabase=true;
	}
	
	public static void reload() {
		init();
	}
	
	public Log() {
		super();
	}
	
	public String getLogList() {
		checkRequest();
		StringBuffer sb=new StringBuffer();
		
		//extend the SQL-query
		int offset;
		try {
			offset=Integer.parseInt(gRequest.getParameter("offset"));
			gRequest.getSession().setAttribute("admin.log.offset", new Integer(offset));
		} catch(Exception e) {
			try {
				offset=((Integer)gRequest.getSession().getAttribute("admin.log.offset")).intValue();
			} catch(Exception e1) {
				offset=0;
			}
		}

		String order=gRequest.getParameter("order");
		boolean direction;
		try {
			direction=((Boolean)gRequest.getSession().getAttribute("admin.log.direction")).booleanValue();
		} catch(Exception e) {
			direction=true;
		}
		
		if(com.laukien.string.String.isEmpty(order) || (!order.equals("id") && !order.equals("timestamp") && !order.equals("message") && !order.equals("status"))) {
			order=(String)gRequest.getSession().getAttribute("admin.log.order");
			if(order==null) order="id";
		} else {
			if(order.equals((String)gRequest.getSession().getAttribute("admin.log.order"))) {
				direction=!direction;
				gRequest.getSession().setAttribute("admin.log.direction",new Boolean(direction));
			}
			gRequest.getSession().setAttribute("admin.log.order",order);
		}

		
		String query="SELECT * FROM log ORDER BY "+order+" "+(direction ? "DESC" : "ASC") +" OFFSET "+offset+" LIMIT 20";
		int count;

		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(id) AS count FROM log");
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Log.getUserList: Invalid Result");
			count=rs.getInt("count");
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement(query);
			
			rs=ps.executeQuery();

			if(rs==null) throw new DatabaseException("Log.getUserList: Invalid Result");
			
			int row=0;
			while(rs.next()) {
				row++;
				sb.append("<tr class=\"row"+((row%2)+1)+"\">");
				sb.append("<td>"+rs.getInt("id")+"</td>");
				sb.append("<td>"+rs.getString("timestamp")+"</td>");
				sb.append("<td>"+Cut.length(rs.getString("message"),50,"...")+"</td>");
				sb.append("<td>"+rs.getString("status")+"</td>");
				sb.append("</tr>");
			}
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//no nothing
			}
			Log.write("Log.getLogList: Database-Error\n"+e,Log.SYSTEM);
			
			//delete variables
			gRequest.getSession().removeAttribute("admin.log.offset");
			gRequest.getSession().removeAttribute("admin.log.order");
			
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
		
		//Navigation
		if(count>20) {
			sb.append("<tr><td colspan=\"4\" style=\"text-align: center; margin-top: 20px;\">");
			for(int i=0; i<(count/20)+1; i++) {
				sb.append("<a href=\"?offset="+(i*20)+"\">&nbsp;"+(i+1)+"&nbsp;</a>");
			}
			sb.append("</td></tr>");
		}
		
		return sb.toString();
	}
	
	public void clean() {
		checkRequest();
		if(gRequest.getParameter("clean")==null) return;
		
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement("DELETE FROM log WHERE id<=(SELECT id FROM log ORDER BY id DESC LIMIT 1)-10");
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException("Log.clean: Error while accessing the database\n"+e);
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
	
	public static void write(String pMessage, int pStatus) {
		if(pStatus<Short.MIN_VALUE || pStatus>Short.MAX_VALUE) throw new ParameterException("Log.write: Invalid status");

		if(gsIsDatabase) com.laukien.bean.log.Log.write(pMessage, pStatus);
		else {
			System.out.println("["+pStatus+"] "+pMessage);
		}
		
/*		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="INSERT INTO log (timestamp, message, status, last) VALUES(?,?,?,?)";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			String timestamp=DateTime.getTimestamp();
			ps.setString(1,timestamp);
			ps.setString(2,pMessage);
			ps.setShort(3,(short)pStatus);
			ps.setString(4,timestamp);
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException("Log.write: Error while accessing the database\n"+e);
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
*/	}
	
	public static LogObject read(int pId) {
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="SELECT * FROM log WHERE id=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new RuntimeException("Log.read: No result");

			LogObject log=new Log().new LogObject();
			log.id=rs.getInt("id");
			log.timestamp=rs.getString("timestamp");
			log.message=rs.getString("message");
			log.status=rs.getInt("status");
			log.comment=rs.getString("comment");
			log.last=rs.getString("last");
			
			return log;
		} catch(Exception e) {
			throw new RuntimeException("Log.read: Error while accessing the database\n"+e);
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
	
	/**
	 * Resds all database-log-entries into a list.
	 * 
	 * @param pOnlyNew if <code>true</code> only the unseen, new entries will be returned
	 * @return a list of the logs
	 */
	public static Vector readAll(boolean pOnlyNew) {
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query;
			if(pOnlyNew) query="SELECT status, timestamp, message FROM log WHERE timestamp=last ORDER BY timestamp DESC;";
				query="SELECT status, timestamp, message FROM log ORDER BY timestamp DESC;";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			
			rs=ps.executeQuery();
			if(rs==null) throw new RuntimeException("Log.readAll: No result");

			LogObject log;
			Vector list=new Vector();
			while(rs.next()) {
				log=new Log().new LogObject();
				log.id=rs.getInt("id");
				log.timestamp=rs.getString("timestamp");
				log.message=rs.getString("message");
				log.status=rs.getInt("status");
				log.comment=rs.getString("comment");
				log.last=rs.getString("last");
				list.add(log);
			}
			
			return list;
		} catch(Exception e) {
			throw new RuntimeException("Log.readAll: Error while accessing the database\n"+e);
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
	
	public static void updateLast(int pId) {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="UPDATE log SET last=? WHERE id=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,DateTime.getTimestamp());
			ps.setInt(2,pId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException("Log.updateLast: Error while accessing the database\n"+e);
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
	
	public static void updateStatus(int pId, int pStatus) {
		if(pStatus<Short.MIN_VALUE || pStatus>Short.MAX_VALUE) throw new ParameterException("Log.updateStatus: Invalid status");

		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="UPDATE log SET status=?, last=? WHERE id=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setInt(1,pStatus);
			ps.setString(2,DateTime.getTimestamp());
			ps.setInt(3,pId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException("Log.updateStatus: Error while accessing the database\n"+e);
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
	
	public static void updateComment(int pId, String pComment) {
		if(pComment==null) throw new ParameterException("Log.updateComment: Invalid status");
		pComment=Cut.length(pComment,250);

		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="UPDATE log SET comment=?, last=? WHERE id=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,pComment);
			ps.setString(2,DateTime.getTimestamp());
			ps.setInt(3,pId);
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new RuntimeException("Log.updateComment: Error while accessing the database\n"+e);
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
	
	class LogObject {
		public int id;
		public int status;
		public String timestamp;
		public String message;
		public String last;
		public String comment;
	}
}
