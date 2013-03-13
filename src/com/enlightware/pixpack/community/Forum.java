package com.enlightware.pixpack.community;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.laukien.bean.database.Database;
import com.laukien.bean.database.Parameter;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.User;
import com.laukien.crypt.Hash;
import com.laukien.exception.ParameterException;

/**
 * Grabs information from the PixPack-Forum.
 * 
 * @deprecated
 */
public class Forum {
	
	private static Parameter gsParameter;

	static {
		init();
	}

	
	private synchronized static void init() {
		gsParameter=new Parameter();
		try {
			synchronized(gsParameter) {
				gsParameter.load(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"forum.db");
			}
		} catch(Exception e) {
			Log.write("Forum.init: Unable to load database-parameter\n"+e,Log.SYSTEM);
			throw new ParameterException("Forum.init: Unable to load database-parameter\n"+e);
		}
	}
	
	public static void reload() {
		init();
	}
	
	/**
	 * Registered the user in the phpBB2-Forum.
	 * 
	 * @param pUser
	 */
	protected static boolean register(User pUser) {
		//(5, 1, 'test', '098f6bcd4621d373cade4e832627b4f6', 1162196093, -1, 1162195890, 1162195861, 0, 0, 0.00, 1, 'english', 'D M d, Y g:i a', 0, 0, 0, 0, 0, NULL, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, '', 0, 'test@mail.com', '', '', '', '', '', '', '', '', '', '', '', NULL);
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("INSERT INTO "+db.getParameter().getTable()+"users (user_id, username, user_password, user_email) VALUES (?,?,?,?)");
			ps.setInt(1,pUser.getId());
			ps.setString(2,pUser.getName());
			ps.setString(3,Hash.md5(pUser.getPassword()));
			ps.setString(4, pUser.getName());	//email
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Forum.register: Database-Error\n"+e,Log.SYSTEM);
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

	public static boolean delete(User pUser) {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("DELETE FROM "+db.getParameter().getTable()+"users WHERE user_id=?");
			ps.setInt(1,pUser.getId());
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Forum.delete: Database-Error\n"+e,Log.SYSTEM);
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

	public static boolean edit(User pUser) {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("UPDATE "+db.getParameter().getTable()+"users SET user_active=?, username=?, user_password=?, user_email=? WHERE user_id=?");
			ps.setInt(1,pUser.getStatus()==User.STATUS_DENIED ? 0 : 1);
			ps.setString(2,pUser.getName());
			ps.setString(3,Hash.md5(pUser.getPassword()));
			ps.setString(4,pUser.getMail());
			ps.setInt(5,pUser.getId());
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Forum.edit: Database-Error\n"+e+db+ps,Log.SYSTEM);
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
	
	protected static boolean password(User pUser) {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("UPDATE "+db.getParameter().getTable()+"users SET user_password=? WHERE user_id=?");
			ps.setString(1,Hash.md5(pUser.getPassword()));
			ps.setInt(2,pUser.getId());
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Forum.password: Database-Error\n"+e,Log.SYSTEM);
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
	
	/**
	 * Changes the username.
	 * 
	 * @return
	 */
	protected static boolean username(User pUser) {
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("UPDATE "+db.getParameter().getTable()+"users SET username=? WHERE user_id=?");
			ps.setString(1,pUser.getName());
			ps.setInt(2,pUser.getId());
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Forum.username: Database-Error\n"+e,Log.SYSTEM);
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
	
	public static String getNews(String pLanguage) {
		//SELECT post_id, post_subject, post_text FROM phpbb_posts_text WHERE post_id in (SELECT post_id FROM phpbb_posts WHERE forum_id = (SELECT forum_id from phpbb_forums WHERE forum_id=5) ORDER BY post_time ASC) LIMIT 3;
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		StringBuffer sb=new StringBuffer();
		sb.append("<ul class=\"comunity_news\">");

		try {
			db.setParameter(gsParameter);
			
			db.connect();

			//check if the user exists
			//ps=db.prepareStatement("SELECT phpbb_posts.topic_id, post_subject FROM phpbb_posts, phpbb_posts_text WHERE phpbb_posts.post_id=phpbb_posts_text.post_id AND phpbb_posts_text.post_id in (SELECT post_id FROM phpbb_posts WHERE forum_id = (SELECT forum_id from phpbb_forums WHERE forum_id=5)) ORDER BY phpbb_posts.topic_id DESC LIMIT 10");
			ps=db.prepareStatement("SELECT "+db.getParameter().getTable()+"posts.topic_id, post_subject FROM "+db.getParameter().getTable()+"posts, "+db.getParameter().getTable()+"posts_text WHERE "+db.getParameter().getTable()+"posts.post_id="+db.getParameter().getTable()+"posts_text.post_id AND "+db.getParameter().getTable()+"posts_text.post_id in (SELECT post_id FROM "+db.getParameter().getTable()+"posts WHERE forum_id = (SELECT forum_id from "+db.getParameter().getTable()+"forums WHERE forum_id=?)) ORDER BY "+db.getParameter().getTable()+"posts.topic_id DESC LIMIT 10");
			
			//'en' or 'de'
			if(pLanguage!=null && pLanguage.equals("de")) ps.setInt(1,5);
			else ps.setInt(1,1);
			
			rs=ps.executeQuery();
			
			if(rs==null) throw new RuntimeException("Forum.getNews: No result");
			while(rs.next()) {
				sb.append("<li><a href=\"http://forum.pixpack.net/viewtopic.php?t="+rs.getInt("topic_id")+"\">"+rs.getString("post_subject")+"</a></li>");
			}
		} catch(Exception e) {
			Log.write("Forum.getNews: Database-Error\n"+e,Log.SYSTEM);
			return "No news<br/>"+e;
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

		sb.append("</ul>");
		return sb.toString();
	}
}
