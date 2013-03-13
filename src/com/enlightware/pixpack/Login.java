package com.enlightware.pixpack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.enlightware.pixpack.community.Forum;
import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Message;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;

public class Login extends Root {
	
	public Login() {
		super();
	}
	
	public Login(HttpServletRequest pRequest) {
		gRequest=pRequest;
	}
	
	/**
	 * Checks if the "user-passward-combination" is correct
	 * The password is the length of the name
	 * 
	 * No "database-commit" 'cause it is not important.
	 * 
	 * @return <code>true</code> if the login is correct; otherwise <code>false</code>
	 */
	public boolean access() {
		if(gRequest==null) throw new ParameterException("Login.isCorrect: Request-variable not set");
		if(gI18n==null) throw new ParameterException("Login.isCorrect: I18n-variable not set");

		//logout
		gRequest.getSession().removeAttribute("login.user");
		
		String name;
		String password;
		try {
			name=gRequest.getParameter("name").trim().toLowerCase();
			password=gRequest.getParameter("password").trim();
		} catch(Exception e) {
			return false;
		}
		
		//set query
		String query;
		if(name.indexOf('@')!=-1) query="SELECT id, name, mail, timestamp, last, status, key_parent FROM account WHERE LOWER(mail)=? AND password=? AND status>?";
		else query="SELECT id, name, mail, timestamp, last, status, key_parent FROM account WHERE LOWER(name)=? AND password=? AND status>?";
		
		//Init user
		User user=new User();
		user.setPassword(password);

		//check if the user exists
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,name);
			ps.setString(2,password);
			ps.setInt(3,User.STATUS_NONE);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) {
				Message.setMessage(gRequest, gI18n.getText("login.error"),Message.TYPE_ERROR);
				return false;
			}

			//set user
			user.setId(rs.getInt("id"));
			user.setParentId(rs.getInt("key_parent"));
			user.setName(rs.getString("name"));
			user.setMail(rs.getString("mail"));
			user.setTimestamp(rs.getString("timestamp"));
			user.setLast(rs.getString("last"));
			user.setStatus(rs.getInt("status"));
			
			//write last
			ps=db.prepareStatement("UPDATE account SET last=? WHERE id=?");
			ps.setString(1, DateTime.getTimestamp());
			ps.setInt(2, user.getId());
			ps.executeUpdate();
			
		} catch(Exception e) {
			Log.write("Login.isCorrect: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(gRequest, gI18n.getText("error.database"),Message.TYPE_ERROR);
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
		
		//set session-user
		setUser(user);

		//message
		Message.setMessage(gRequest, gI18n.getText("login.ok"),Message.TYPE_INFORMATION);
		return true;
	}
	
	
	public boolean isRegisterLink() {
		if(gRequest==null) throw new ParameterException("Login.isCorrect: Request-variable not set");
		return (gRequest.getParameter("id")!=null);
	}
	
	public boolean registerAndLogin() {
		if(gRequest==null) throw new ParameterException("Login.isCorrect: Request-variable not set");
		String id=gRequest.getParameter("id");
		int sep;
		if(id==null || (sep=id.lastIndexOf(':'))==-1) return false;
		
		String mail=id.substring(0,sep).toLowerCase();
		id=id.substring(sep+1);

		String password;
		
		//check if the user exists + status update
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("SELECT id, password, key_parent FROM account WHERE LOWER(mail)=? AND timestamp=? AND status=?");
			ps.setString(1,mail);
			ps.setString(2,id);
			ps.setInt(3,User.STATUS_NONE);
			
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("Login: isRegistered: Invalid Result");
			
			//get password
			if(!rs.next()) {
				Message.setMessage(gRequest, gI18n.getText("register.error.link"),Message.TYPE_ERROR);
				return false;
			}
		
			password=rs.getString("password");
			
			//add user+timestamp
			ps=db.prepareStatement("UPDATE account SET status=1 WHERE id=?");
			ps.setInt(1,rs.getInt("id"));
		
			ps.executeUpdate();

			//login & success message
			User user=new User();
			user.setId(rs.getInt("id"));
			user.setParentId(rs.getInt("key_parent"));
			user.setName(mail);	//mail==>name
			user.setMail(mail);
			user.setPassword(password);
			user.setTimestamp(id);
			user.setLast(DateTime.getTimestamp());
			user.setStatus(User.STATUS_FREE);
			setUser(user);
			
			//write last
			ps=db.prepareStatement("UPDATE account SET last=? WHERE id=?");
			ps.setString(1, DateTime.getTimestamp());
			ps.setInt(2, user.getId());
			ps.executeUpdate();

			//if(!Forum.register(user)) {
				//Message.setMessage(gRequest, gI18n.getText("register.error.forum"),Message.TYPE_ERROR);
				//return false;
			//}
		} catch(Exception e) {
			Log.write("Login.registerAndLogin: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(gRequest, gI18n.getText("error.database"),Message.TYPE_ERROR);
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
		
		Message.setMessage(gRequest, gI18n.getText("register.link"),Message.TYPE_INFORMATION);
		return true;
	}
	
	public void removeUser() {
		checkRequest();
		gRequest.getSession().removeAttribute("login.user");
		
		//remove image saved properties
		gRequest.getSession().removeAttribute("upload.key");
		gRequest.getSession().removeAttribute("folder.select");
		gRequest.getSession().removeAttribute("upload.file");
		gRequest.getSession().removeAttribute("upload.detail.public");
		gRequest.getSession().removeAttribute("upload.detail.adult");
		gRequest.getSession().removeAttribute("upload.image.size");
		gRequest.getSession().removeAttribute("upload.thumbnail.size");
		gRequest.getSession().removeAttribute("upload.thumbnail.option");
		gRequest.getSession().removeAttribute("upload.image.optimize");
	}
	
	
	public void setUser(User pUser) {
		checkRequest();
		if(pUser==null) removeUser();
		else gRequest.getSession().setAttribute("login.user",pUser);
	}
	
	/**
	 * Returns the current &amp; active user.
	 * 
	 * @return the user or <code>null</code> if the user has not logged in.
	 */
	public static User getUser(HttpServletRequest pRequest) {
		if(pRequest==null) throw new ParameterException("Login.getUser: Request-variable not set");
		
		 return (User)pRequest.getSession().getAttribute("login.user");
	}
	
	/**
	 * Returns the current &amp; active user.
	 * 
	 * @return the user or <code>null</code> if the user has not logged in.
	 */
	public User getUser() {
		checkRequest();
		return getUser(gRequest);
	}
	
	public static String getUsername(HttpServletRequest pRequest) {
		User user=getUser(pRequest);
		return user==null ? null : user.getName();
	}
	/**
	 * Returns the current &amp; active user NAME.
	 * 
	 * @return the user or <code>null</code> if the user has not logged in.
	 */
	public String getUsername() {
		return getUsername(gRequest);
	}
	
	/**
	 * Sets the given "Username" as current username &amp; as active user.
	 * If <code>pUsername</code> is NULL the current user will be removed.
	 */
	/*public void setUsername(String pUsername) {
		if(gRequest==null) throw new ParameterException("Login.setUsername: Request-variable not set");
		
		if(pUsername!=null) gRequest.getSession().setAttribute("login.user.name",pUsername.trim().toLowerCase());
		else gRequest.getSession().removeAttribute("login.user.name");
	}*/
	
	public boolean isPermission() {
		return getUser()!=null;
	}
	
	public int getStatus() {
		if(getUser()==null) return -1;
		else return getUser().getStatus();
	}
	
	public int getParentId() {
		if(getUser()==null) return -1;
		else return getUser().getParentId();
	}
	
	public void dummy() {
		User user=new User();
		user.setId(7);
		user.setParentId(Account.ADMIN_ID);
		user.setName("Stephan");
		user.setMail("stephan@laukien.com");
		user.setPassword("123");
		user.setTimestamp("20060822142410679");
		user.setLast("20060822142410679");
		user.setStatus(User.STATUS_FREE);
		setUser(user);
	}
}
