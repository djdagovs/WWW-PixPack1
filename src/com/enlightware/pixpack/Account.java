package com.enlightware.pixpack;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Message;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.io.file.Delete;
import com.laukien.io.file.Read;
import com.laukien.io.mail.SMTP;
import com.laukien.string.Convert;
import com.laukien.string.Random;
import com.laukien.string.Replace;
import com.laukien.taglib.i18n.Text;

public class Account extends Root {
	public static final String SYSTEM		= "system";
	public static final String ADMIN		= "admin";
	public static final String ANONYMOUS	= "anonymous";
	public static final String FREE			= "free";
	public static final String PREMIUM		= "premium";
	public static final String BUSINESS		= "business";
	
	public static final int SYSTEM_ID		= 1;
	public static final int ADMIN_ID		= 2;
	public static final int ANONYMOUS_ID	= 3;
	public static final int FREE_ID			= 4;
	public static final int PREMIUM_ID		= 5;
	public static final int BUSINESS_ID		= 6;
	
	private static final Pattern REGEXP_USERNAME=Pattern.compile("[A-Za-z0-9\\.\\-\\_\\@]{3,128}");
	
	private HttpServletResponse gResponse;

	public Account() {
		super();
	}
	
	public void setResponse(HttpServletResponse pResponse) {
		gResponse=pResponse;
	}
	
	public void checkResponse() {
		if(gResponse==null) throw new ParameterException("Account.checkResponse: Response-variable not set");
	}
	
	public boolean register() {
		checkAll();
		
		String mail=gRequest.getParameter("mail");
		if(mail==null || (mail=mail.trim()).length()<7 || mail.length()>128 || mail.indexOf('@')==-1 || mail.indexOf('.')==-1) {
			Message.setMessage(gRequest, gI18n.getText("register.error.mail"),Message.TYPE_ERROR);
			return false;
		}

		//get timestamp & password
		String timestamp=DateTime.getTimestamp();
		String password=Random.random(10,'a','z');

		//check if the user exists
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			db.getConnection().setAutoCommit(false);

			//check if the user exists
			ps=db.prepareStatement("SELECT count(id) AS count FROM account WHERE LOWER(mail)=?");
			ps.setString(1,mail);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Account.register: Invalid Result");
			
			if(rs.getInt("count")>0) {
				Message.setMessage(gRequest, gI18n.getText("register.error.user"),Message.TYPE_ERROR);
				return false;
			}
		
			//send register mail
			try {
				String lang=gI18n.getLanguage();
				if(!lang.equals("de")) lang="en";
				String text=Read.readString(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"mail"+File.separator+"register."+lang);
				//text=Replace.replace(text,"${user}",Convert.URLEncode(mail));
				text=Replace.replace(text,"${id}",mail+":"+timestamp);
				text=Replace.replace(text,"${password}",password);
			
				SMTP send=new SMTP();
				send.setTo(mail);
				send.setFrom("noreply@pixpack.net");
				send.setSubject(gI18n.getText("register.subject"));
				send.setText(text);
				send.send();
			} catch(Exception e) {
				Log.write("Account.register: Mail-Error\n"+e,Log.SYSTEM);
				Message.setMessage(gRequest, gI18n.getText("error.sendmail"),Message.TYPE_ERROR);
				return false;
			}

			//add user+timestamp
			ps=db.prepareStatement("INSERT INTO account (name, name_lo ,password, mail, timestamp) VALUES(?,?,?,?,?)");
			ps.setString(1, mail);
			ps.setString(2, mail.toLowerCase());
			ps.setString(3, password);
			ps.setString(4, mail);
			ps.setString(5, timestamp);
		
			ps.executeUpdate();
			db.getConnection().commit();
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//to nothing
			}
			Log.write("Account.register: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(gRequest, gI18n.getText("error.database"),Message.TYPE_ERROR);
			return false;
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				//do nothing
			}
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

		Message.setMessage(gRequest, gI18n.getText("register.sendmail"),Message.TYPE_INFORMATION);
		return true;
	}

	public boolean changePassword() {
		checkAll();

		User user=Login.getUser(gRequest);
		if(user==null) {
			Message.setMessage(gRequest,gI18n.getText("error.hack"),Message.TYPE_ERROR);
			return false;
		}
		

		String lOld=gRequest.getParameter("old");
		String lNew=gRequest.getParameter("new");
		String lRepeat=gRequest.getParameter("repeat");
		
		if(com.laukien.string.String.isEmpty(lOld) || 
				com.laukien.string.String.isEmpty(lNew) || 
				com.laukien.string.String.isEmpty(lRepeat) ||
				!lNew.equals(lRepeat) ||
				lNew.length()>64 ||
				user.getPassword()==null ||
				!user.getPassword().equals(lOld)) {
			Message.setMessage(gRequest,gI18n.getText("account.password.error"),Message.TYPE_WARNING);
			return false;
		}
		
		//update database
		Database db=new Database();
		PreparedStatement ps=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("UPDATE account SET password=? WHERE id=?");
			ps.setString(1,lNew);
			ps.setInt(2,user.getId());
			
			ps.executeUpdate();
		} catch(Exception e) {
			Log.write("Account.changePassword: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(gRequest, gI18n.getText("error.database"),Message.TYPE_ERROR);
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

		//update session
		user.setPassword(lNew);
		Login login=new Login();
		login.setRequest(gRequest);
		login.setUser(user);
		
		//set forum-password
		//String tmp=gRequest.getParameter("forum");
		//if(tmp!=null && tmp.equals("true"))	Forum.password(user);
		
		//message
		Message.setMessage(gRequest,gI18n.getText("account.ok"),Message.TYPE_INFORMATION);
		return true;
	}
	
	public boolean changeUsername() {
		checkAll();

		User user=Login.getUser(gRequest);
		if(user==null) {
			Message.setMessage(gRequest,gI18n.getText("error.hack"),Message.TYPE_ERROR);
			return false;
		}
		

		String lPassword=gRequest.getParameter("password");
		String lName=gRequest.getParameter("name");

		//check password, username, username!=another mail
		if(com.laukien.string.String.isEmpty(lPassword) || !user.getPassword().equals(lPassword)
				 || com.laukien.string.String.isEmpty(lName) || (lName=lName.trim()).equals(user.getName())
				 || !isValidUsername(lName) || (lName.indexOf('@')!=-1 && !lName.equalsIgnoreCase(user.getMail()))
				 || user.getId()<6) {
			Message.setMessage(gRequest,gI18n.getText("account.username.error"),Message.TYPE_WARNING);
			return false;
		}
		
		//update database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			db.setAutoCommit(false);
			
			//check if the user already exists
			ps=db.prepareStatement("SELECT count(id) FROM account WHERE name_lo=?");
			ps.setString(1,lName.toLowerCase());
			
			rs=ps.executeQuery();
			
			if(rs==null || !rs.next()) {
				Message.setMessage(gRequest, gI18n.getText("account.username.error")+user,Message.TYPE_WARNING);
				return false;
			}

			//set new username
			ps=db.prepareStatement("UPDATE account SET name=?, name_lo=? WHERE id=?");
			ps.setString(1,lName);
			ps.setString(2,lName.toLowerCase());
			ps.setInt(3,user.getId());
			
			ps.executeUpdate();
			
			
			db.commit();
		} catch(Exception e) {
			try {
				db.rollback();
			} catch (DatabaseException e1) {
				//do nothing
			}
			Log.write("Account.changePassword: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(gRequest, gI18n.getText("error.database"),Message.TYPE_ERROR);
			return false;
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				//do nothing
			}
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

		//update session
		user.setName(lName);
		Login login=new Login();
		login.setRequest(gRequest);
		login.setUser(user);
		
		//Set Forum-Username
		//Forum.username(user);
		
		//message
		Message.setMessage(gRequest,gI18n.getText("account.ok"),Message.TYPE_INFORMATION);
		return true;
	}
	
	public boolean sendPassword() {
		checkAll();

		String user=gRequest.getParameter("name");
		if(user==null || (user=user.trim().toLowerCase()).length()==0) return false;
		
		String query;
		if(user.indexOf('@')==-1) query="SELECT name, password, mail FROM account WHERE name_lo=?";
		else query="SELECT name, password, mail FROM account WHERE mail=?";
		
		//check if the user exists
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,user);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) {
				Message.setMessage(gRequest, gI18n.getText("login.lost.error")+user,Message.TYPE_WARNING);
				return false;
			}
			
			
			//send mail
			String lang=gI18n.getLanguage();
			if(!lang.equals("de")) lang="en";
			String text=Read.readString(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"mail"+File.separator+"password."+lang);
			text=Replace.replace(text,"${user}",Convert.URLEncode(rs.getString("name")));
			text=Replace.replace(text,"${password}",rs.getString("password"));

			SMTP send=new SMTP();
			send.setTo(rs.getString("mail"));
			send.setFrom("noreply@pixpack.net");
			send.setSubject(gI18n.getText("login.lost"));
			send.setText(text);
			send.send();
		} catch(Exception e) {
			Log.write("Account.sendPassword: Database-Error\n"+e,Log.SYSTEM);
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
		
		//message
		Message.setMessage(gRequest,gI18n.getText("login.lost.ok"),Message.TYPE_INFORMATION);
		return true;
	}
	
	public boolean autologin() {
		checkRequest();
		checkResponse();
		
		User user=Login.getUser(gRequest);
		if(user==null) return false;

		int time;
		String param=gRequest.getParameter("autologin");
		if(param==null || !param.equals("true")) time=0;
		else time=31536000;		//1 year

		Cookie cookie;
		cookie=new Cookie("autologin.user",user.getName());
		cookie.setMaxAge(time);
		gResponse.addCookie(cookie);
		cookie=new Cookie("autologin.key",generateKey(user.getPassword()));
		cookie.setMaxAge(time);
		gResponse.addCookie(cookie);
		
		return true;
	}
	
	public boolean isAutoLogin() {
		checkRequest();
		Cookie[] cookies=gRequest.getCookies();
		if(cookies==null) return false;
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals("autologin.user")) return true;
		}
		
		return false;
	}

	/**
	 * Creates the "checked"-Attribute for the checkbox.
	 * 
	 * @return String for the checkbox
	 */
	public String getAutoLoginAttribute() {
		return isAutoLogin() ? "checked=\"checked\"" : "";
	}
	
	/**
	 * checks if the user is to login via cookie
	 * 
	 * @param pRequest
	 */
	public static void checkAutologin(HttpServletRequest pRequest, Text pI18n) {
		//if loggedin - return
		if(Login.getUser(pRequest)!=null || !pRequest.getSession().isNew()) return;
		Cookie[] cookies=pRequest.getCookies();
		if(cookies==null) return;
		
		String name=null;
		String key=null;
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals("autologin.user")) name=cookies[i].getValue();
			else if(cookies[i].getName().equals("autologin.key")) key=cookies[i].getValue();
		}
		
		if(name==null || key==null) return;
		
		//get user-data from the database & check the key
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement("SELECT id, name, password, mail, timestamp, last, status, key_parent FROM account WHERE name_lo=? AND status>?");
			ps.setString(1,name.toLowerCase());
			ps.setInt(2,User.STATUS_NONE);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) {
				Message.setMessage(pRequest, pI18n.getText("login.error"),Message.TYPE_ERROR);
				return;
			}

			User user;
			if(generateKey(rs.getString("password")).equals(key)) {
				//set user
				user=new User();
				user.setId(rs.getInt("id"));
				user.setParentId(rs.getInt("key_parent"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
				user.setMail(rs.getString("mail"));
				user.setTimestamp(rs.getString("timestamp"));
				user.setLast(rs.getString("last"));
				user.setStatus(rs.getInt("status"));
				
				//write last
//				ps=db.prepareStatement("UPDATE account SET last=? WHERE id=?");
//				ps.setString(1, DateTime.getTimestamp());
//				ps.setInt(2, user.getId());
//				ps.executeUpdate();
				
				rs.updateString("last", DateTime.getTimestamp());
				rs.updateRow();
				
				//set user active
				Login login=new Login();
				login.setRequest(pRequest);
				login.setUser(user);
			} else {
				user=null;
				Message.setMessage(pRequest, pI18n.getText("login.error"),Message.TYPE_ERROR);
			}
		} catch(Exception e) {
			Log.write("Account.checkAutologin: Database-Error\n"+e,Log.SYSTEM);
			Message.setMessage(pRequest, pI18n.getText("error.database"),Message.TYPE_ERROR);
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

	/**
	 * Delete the "ID"entifed user
	 * 
	 * @param pId UserId
	 * @throws DatabaseException
	 */
	public static void delete(int pId) throws DatabaseException {
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			db.setAutoCommit(false);

			//delete all user-galleries
			ps=db.prepareStatement("DELETE FROM gallery WHERE key_folder IN (SELECT id from folder where key_user=?)");
			ps.setInt(1, pId);
			ps.executeUpdate();
			
			//delete all user-galleries
			ps=db.prepareStatement("DELETE FROM preview WHERE key_folder IN (SELECT id from folder where key_user=?)");
			ps.setInt(1, pId);
			ps.executeUpdate();
			
			//delete all user-comments
			ps=db.prepareStatement("DELETE FROM comment WHERE key_user=?");
			ps.setInt(1, pId);
			ps.executeUpdate();
			
			//Remove files form the filesystem
			ps=db.prepareStatement("SELECT file.server, file.timestamp, file.key, extension.name AS extension FROM file, extension WHERE file.key_user=? AND file.key_extension=extension.id");
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			
			if(rs==null) throw new DatabaseException("Account.delete: Invalid Result");

			String tmpPath;
			while(rs.next()) {
				tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"))+File.separator;
				if(!Delete.delete(tmpPath+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension"))
						|| !Delete.delete(tmpPath+'_'+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")))
							Log.write("Account.delete: DELETE - "+rs.getString("timestamp")+'_'+rs.getString("key")+'@'+rs.getInt("server"),Log.WARNING);
			}

			//delete db-entries
			ps=db.prepareStatement("DELETE FROM file WHERE key_user=?");
			ps.setInt(1, pId);
			ps.executeUpdate();
			
			//delete folders of the user
			ps=db.prepareStatement("DELETE FROM folder WHERE key_user=?");
			ps.setInt(1, pId);
			ps.executeUpdate();
			
			//delete user from database
			ps=db.prepareStatement("DELETE FROM account WHERE id=?");
			ps.setInt(1,pId);
			
			ps.executeUpdate();

			db.commit();
			
		} catch(Exception e) {
			try {
				db.rollback();
			} catch (DatabaseException e1) {
				//no nothing
			}
			Log.write("Account.delete: Database-Error\n"+e,Log.SYSTEM);
			throw new DatabaseException("Account.delete: Database-Error\n"+e);
		} finally {
			try {
				db.setAutoCommit(true);
			} catch (DatabaseException e) {
				//do nothing
			}
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
	
	public static void edit(User pUser) throws DatabaseException {
		Database db=new Database();
		PreparedStatement ps=null;

		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			ps=db.prepareStatement("UPDATE account SET name=?, password=?, mail=?, status=?, key_parent=? WHERE id=?");
			ps.setString(1, pUser.getName());
			ps.setString(2, pUser.getPassword());
			ps.setString(3, pUser.getMail());
			ps.setInt(4,pUser.getStatus());
			ps.setInt(5,pUser.getParentId());
			ps.setInt(6,pUser.getId());
			
			ps.executeUpdate();
			
		} catch(Exception e) {
			Log.write("Account.edit: Database-Error\n"+e,Log.SYSTEM);
			throw new DatabaseException("Account.edit: Database-Error\n"+e);
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
	
	/**
	 * Deletes the current user, its galleries, folders and files.
	 * The user is identified by its session.
	 * 
	 * @return
	 */
	public boolean delete() {
		checkRequest();
		
		//init Login
		Login login=new Login();
		login.setRequest(gRequest);
		
		//check if the user/password is valid
		User user=login.getUser();
		if(user==null || !user.getPassword().equals(gRequest.getParameter("password"))) return false;
		
		//delete files, folders an the user itself
		try {
			delete(user.getId());
		} catch (DatabaseException e) {
			return false;
		}
		
		//Remove user from the session
		login.removeUser();
		
		//remove forum-user and return
		return true;//Forum.delete(user);
	}
	
	/**
	 * Checks if the current user as permission to edit or delete the user which is identified by its "id".
	 * 
	 * @param pId user to edit/delete
	 * @return
	 * @throws DatabaseException
	 */
	public boolean isUserPermission(int pId) throws DatabaseException {
		int parent=-1;
		
		//check if the user exists
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//check if the user exists
			ps=db.prepareStatement("SELECT key_parent FROM account WHERE id=?");
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Account.getUserById: Invalid Result");

			parent=rs.getInt("key_parent");
		} catch(Exception e) {
			Log.write("Account.getUserById: Database-Error\n"+e,Log.SYSTEM);
			throw new DatabaseException("Account.getUserById: Unable to get the data from the database\n"+e);
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

		Login login=new Login(gRequest);
		int userId=login.getUser().getId();
		int userParentId=login.getUser().getParentId();
		
		//security - system user
		if(pId<=6 || pId<userId) return false;
		
		//own account
		if(pId==userId) return true;
		
		//system
		if(userId==SYSTEM_ID) return true;
		
		//admin
		if(userId==ADMIN_ID && (parent==ADMIN_ID || parent==FREE_ID || parent==PREMIUM_ID)) return true;
		if(userParentId==ADMIN_ID && (parent==FREE_ID || parent==PREMIUM_ID)) return true;
		if(userId==FREE_ID && parent==FREE_ID) return true;
		if(userId==PREMIUM_ID && parent==PREMIUM_ID) return true;
		if(userId==BUSINESS_ID && parent==BUSINESS_ID) return true;
		//free, premium, business
		if(userId==parent) return true;

		return false;
	}

	/**
	 * Returns the "UserInformation" from the database.
	 * 
	 * @param id id if the user
	 * @return
	 * @throws DatabaseException 
	 */
	public static User getUserById(int pId) throws DatabaseException {
		User user=null;
		
		//check if the user exists
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//check if the user exists
			ps=db.prepareStatement("SELECT * FROM account WHERE id=?");
			ps.setInt(1,pId);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Account.getUserById: Invalid Result");

			//set user
			user=new User();
			user.setId(rs.getInt("id"));
			user.setParentId(rs.getInt("key_parent"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setMail(rs.getString("mail"));
			user.setTimestamp(rs.getString("timestamp"));
			user.setLast(rs.getString("last"));
			user.setStatus(rs.getInt("status"));
			
		} catch(Exception e) {
			Log.write("Account.getUserById: Database-Error\n"+e,Log.SYSTEM);
			throw new DatabaseException("Account.getUserById: Unable to get the data from the database\n"+e);
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

		return user;
	}

	private static boolean isValidUsername(String lName) {
		return REGEXP_USERNAME.matcher(lName).matches();
	}

	private static String generateKey(String pPlain) {
		MessageDigest md=null;
		try {
			md=MessageDigest.getInstance("SHA");
			md.update(pPlain.getBytes());
		} catch(NoSuchAlgorithmException e) {
			Log.write("Account.generateKey: System-Error\n"+e,Log.SYSTEM);
			return null;
		}

		byte[] digest=md.digest();
		StringBuffer lStr=new StringBuffer();
		for(int i=0; i<digest.length; i++) {
			String s=Integer.toHexString(digest[i]&0xFF);
			if(s.length()==1) lStr.append("0");
			lStr.append(s);
		}
		return lStr.toString();
	}
}
