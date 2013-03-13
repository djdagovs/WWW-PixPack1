package com.enlightware.pixpack;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.DBFS;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.DBFSFolder;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.exception.DatabaseException;
import com.laukien.io.file.Append;
import com.laukien.io.file.Delete;
import com.laukien.taglib.i18n.Text;

/**
 * FileMan handles the pictures and its parent-folders.
 * 
 * @author Stephan Laukien
 */
public class FileMan extends HttpServlet {
	private static final long serialVersionUID = 200608129L;

	static {
		initInternal();
	}
	
	private synchronized static void initInternal() {
	}
	
	public static void reload() {
		initInternal();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		pRequest.setCharacterEncoding("UTF-8");
		pResponse.setCharacterEncoding("UTF-8");
		pResponse.setContentType("text/html");
		
		Text i18n=Lib.getI18n(pRequest);

		Login login=new Login(pRequest);
		String username=login.getUsername();
		int status=login.getStatus();
		String type=pRequest.getParameter("type");
		String list=pRequest.getParameter("list");
		String source=pRequest.getParameter("content");
		int offset;
		try {
			offset=Integer.parseInt(pRequest.getParameter("offset"));
		} catch(Exception e) {
			offset=0;
		}
		
		if(i18n==null || username==null || (type==null && list==null)) return;
		
		pRequest.setAttribute("forward","true");
		
		try {
			if(list!=null) {
				pRequest.getSession().setAttribute("fileman.folder",source);
				
				String out;
				boolean isContent=!(source==null || source.length()==0);
				
				if(list.equals("folder")) {
					if(isContent) out="<div class=\"root\" onclick=\"fileman_folder('')\">/</div>";
					else out="<div class=\"root active\" onclick=\"fileman_folder('')\">/</div>";
					out+=folder(source, username);
				} else if(list.equals("file")) {
					out=file(source, username,offset);
				} else if(list.equals("restore")) {
					if(status==User.STATUS_FREE) {
						pRequest.getRequestDispatcher("/premium.jsp").forward(pRequest,pResponse);
						return;
					} else out=restore(source, username);
				} else out="ERROR: Invalid list-type";
				
				pResponse.getWriter().println(out);
			}
			else {
				if(type.equals("restore.delete")) {
					pResponse.getWriter().println(deleteRestore(username,i18n));
					return;
				}
				
				if(type.startsWith("folder")) {
					if(type.equals("folder")) pRequest.getRequestDispatcher("fileman/folder.jsp").forward(pRequest, pResponse);	//pResponse.getWriter().println(folder(pRequest, content, username));
					else if(type.equals("folder.add")) pRequest.getRequestDispatcher("fileman/folder.add.jsp").forward(pRequest, pResponse);
					else if(type.equals("folder.rename")) pRequest.getRequestDispatcher("fileman/folder.rename.jsp").forward(pRequest, pResponse);
					else if(type.equals("folder.move")) pRequest.getRequestDispatcher("fileman/folder.move.jsp").forward(pRequest, pResponse);
					else if(type.equals("folder.delete")) pRequest.getRequestDispatcher("fileman/folder.delete.jsp").forward(pRequest, pResponse);
					else if(type.equals("folder.status")) pRequest.getRequestDispatcher("fileman/folder.status.jsp").forward(pRequest, pResponse);
					else pResponse.getWriter().println(i18n.getText("fileman.error"));
				} else if(type.startsWith("file")) {
					if(type.equals("file")) {
						//last file
						pRequest.getSession().setAttribute("fileman.file",source);
						pRequest.getRequestDispatcher("fileman/file.jsp").forward(pRequest, pResponse);	//pResponse.getWriter().println(file(pRequest, content, username));
					}
					else if(type.equals("file.hotlink")) pRequest.getRequestDispatcher("fileman/file.hotlink.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.protect")) {
						if(status==User.STATUS_FREE) {
							pRequest.getRequestDispatcher("/premium.jsp").forward(pRequest,pResponse);
							return;
						} else pRequest.getRequestDispatcher("fileman/file.protect.jsp").forward(pRequest, pResponse);
						
					}
					else if(type.equals("file.download")) pRequest.getRequestDispatcher("fileman/file.download.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.upload")) pRequest.getRequestDispatcher("fileman/file.upload.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.rename")) pRequest.getRequestDispatcher("fileman/file.rename.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.move")) pRequest.getRequestDispatcher("fileman/file.move.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.desc")) pRequest.getRequestDispatcher("fileman/file.desc.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.status")) pRequest.getRequestDispatcher("fileman/file.status.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.delete")) pRequest.getRequestDispatcher("fileman/file.delete.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.trim")) pRequest.getRequestDispatcher("fileman/file.trim.jsp").forward(pRequest, pResponse);
					else if(type.equals("file.strip")) pRequest.getRequestDispatcher("fileman/file.strip.jsp").forward(pRequest, pResponse);
					else pResponse.getWriter().println(i18n.getText("fileman.error"));
				} else if(type.startsWith("filter")) {
					if(type.equals("filter.negate")) pRequest.getRequestDispatcher("fileman/filter.negate.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.equalize")) pRequest.getRequestDispatcher("fileman/filter.equalize.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.enhance")) pRequest.getRequestDispatcher("fileman/filter.enhance.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.despeckle")) pRequest.getRequestDispatcher("fileman/filter.despeckle.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.flip")) pRequest.getRequestDispatcher("fileman/filter.flip.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.flop")) pRequest.getRequestDispatcher("fileman/filter.flop.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.monochrome")) pRequest.getRequestDispatcher("fileman/filter.monochrome.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.normalize")) pRequest.getRequestDispatcher("fileman/filter.normalize.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.ping")) pRequest.getRequestDispatcher("fileman/filter.ping.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.transpose")) pRequest.getRequestDispatcher("fileman/filter.transpose.jsp").forward(pRequest, pResponse);
					else if(type.equals("filter.transverse")) pRequest.getRequestDispatcher("fileman/filter.transverse.jsp").forward(pRequest, pResponse);
				} else if(type.startsWith("info")) {
					if(type.equals("info.folder")) pRequest.getRequestDispatcher("fileman/info.folder.jsp").forward(pRequest, pResponse);
					else if(type.equals("info.file")) pRequest.getRequestDispatcher("fileman/info.file.jsp").forward(pRequest, pResponse);
					else if(type.equals("info.fileman")) pRequest.getRequestDispatcher("fileman/info.fileman.jsp").forward(pRequest, pResponse);
					else if(type.equals("info.about")) pRequest.getRequestDispatcher("fileman/info.about.jsp").forward(pRequest, pResponse);
					else pResponse.getWriter().println(i18n.getText("fileman.error"));
				} else pResponse.getWriter().println(i18n.getText("fileman.error"));
			}
		} catch(Exception e) {
			Log.write("FileMan.service: Unable to call sub-routine"+e, Log.SYSTEM);
			pResponse.getWriter().println(i18n.getText("fileman.error")+"\n"+e);
		}
	}

	public static int countFolders(String pUsername) throws DatabaseException {
		DBFSFolder folder=new DBFSFolder();
		folder.setUsername(pUsername);
		return folder.count();
	}

	public static int countFiles(String pUsername, String pFolder) throws DatabaseException {
		DBFSFile file=new DBFSFile();
		file.setUsername(pUsername);
		return file.count(pFolder);
	}

	public static String folder(String pFolder, String pUsername) throws DatabaseException {
		StringBuffer sb=new StringBuffer();
		//boolean isContent=!(pFolder==null || pFolder.length()==0);

		sb.append("<ul class=\"close\">");
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());

			db.connect();
			
			ps=db.prepareStatement("SELECT folder.name FROM folder, account WHERE folder.key_user=account.id AND account.name=? ORDER BY folder.name ASC");
			ps.setString(1, pUsername);
			
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("FileMan.folder: No result");
			
			String name;
			while(rs.next()) {
				name=rs.getString("name");
				if(name.equals(pFolder)) sb.append("<li class=\"open active\" onclick=\"fileman_folder('"+name+"')\">"+name+"</li>");
				else sb.append("<li class=\"close\" onclick=\"fileman_folder('"+name+"')\">"+name+"</li>");
			}
			
		} catch(Exception e) {
			throw new DatabaseException("FileMan.folder: Error while accessing the database\n"+e);
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

	public static String file(String pFolder, String pUsername, int pOffset) throws DatabaseException {
		StringBuffer sb=new StringBuffer();
		boolean isContent=!(pFolder==null || pFolder.length()==0);
		int count;

		sb.append("<br/><ul class=\"fileman_icon\">");
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			String queryCount;
			if(!isContent) queryCount="SELECT count(file.id) FROM file, account WHERE file.key_user=account.id AND account.name=? AND file.status>=? AND file.key_folder=?";
			else queryCount="SELECT count(file.id) FROM file, folder, account WHERE file.key_user=account.id AND account.name=? AND file.status>=? AND file.key_folder=folder.id AND folder.name=?";

			ps=db.prepareStatement(queryCount);
			ps.setString(1, pUsername);
			ps.setInt(2,DBFSFile.STATUS_DEFAULT);
			if(!isContent) ps.setInt(3,-1);
			else ps.setString(3,pFolder);

			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("FileMan.file: No result");
			count=rs.getInt("count");
			if(count==0) return "";
			
			String queryFile;
			if(!isContent) queryFile="SELECT file.server, file.name, file.timestamp, file.key, extension.name AS extension FROM file, account, extension WHERE file.key_user=account.id AND account.name=? AND file.status>=? AND file.key_folder=? AND file.key_extension=extension.id ORDER BY file.sort, file.name ASC OFFSET ? LIMIT 24";
			else queryFile="SELECT file.server, file.name, file.timestamp, file.key, extension.name AS extension FROM file, folder, account, extension WHERE file.key_user=account.id AND account.name=? AND file.status>=? AND file.key_folder=folder.id AND folder.name=? AND file.key_extension=extension.id ORDER BY file.sort, file.name ASC OFFSET ? LIMIT 24";

			ps=db.prepareStatement(queryFile);
			ps.setString(1, pUsername);
			ps.setInt(2,DBFSFile.STATUS_DEFAULT);
			if(!isContent) ps.setInt(3,-1);
			else ps.setString(3,pFolder);
			ps.setInt(4, pOffset);

			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("FileMan.file: No result");
			
			String name, thumbnail;
			while(rs.next()) {
				name=rs.getString("name");
				thumbnail="http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension");

				sb.append("<li><a href=\"javascript:fileman_file('"+rs.getString("timestamp")+"_"+rs.getString("key")+"')\">");
				sb.append("<img alt=\""+name+"\" title=\""+name+"\" src=\""+thumbnail+"\"/>");
				sb.append("<span>"+name+"</span></a></li>");
			}
			
		} catch(Exception e) {
			throw new DatabaseException("FileMan.file: Error while accessing the database\n"+e);
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
		sb.append("</ul><br/>"+Lib.next(count, pOffset, 24, "javascript:fileman_folder('"+pFolder+"',",")"));
		
		return sb.toString();
	}

	public static String restore(String pFile, String pUsername) throws DatabaseException {
		StringBuffer sb=new StringBuffer();
		boolean isRestore=!(pFile==null || pFile.length()==0);

		sb.append("<br/><ul class=\"fileman_icon\">");
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());

			db.connect();

			String query;
			if(isRestore) {
				DBFSFile file=new DBFSFile();
				file.setInternalFilename(pFile);
				query="UPDATE file SET status=? WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)";
				ps=db.prepareStatement(query);
				ps.setInt(1,DBFSFile.STATUS_DEFAULT);
				ps.setString(2,file.getTimestamp());
				ps.setString(3,file.getKey());
				ps.setString(4,pUsername);
				
				ps.executeUpdate();

			}
			query="SELECT file.server, file.name, file.timestamp, file.key, extension.name AS extension FROM file, account, extension WHERE file.key_user=account.id AND account.name=? AND file.status=? AND file.key_folder=? AND file.key_extension=extension.id ORDER BY file.sort, file.name ASC";
			
			
			ps=db.prepareStatement(query);
			ps.setString(1, pUsername);
			ps.setInt(2,DBFSFile.STATUS_DELETE);
			ps.setInt(3,-1);

			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("FileMan.restore: No result");
			
			String name, thumbnail;
			while(rs.next()) {
				name=rs.getString("name");
				thumbnail="http://"+Server.buildName(rs.getInt("server"))+'.'+Server.getURL()+"/_"+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension");

				sb.append("<li><a href=\"javascript:fileman_restore('"+rs.getString("timestamp")+"_"+rs.getString("key")+"')\">");
				sb.append("<img alt=\""+name+"\" title=\""+name+"\" src=\""+thumbnail+"\"/>");
				sb.append("<span>"+name+"</span></a></li>");
			}
			
		} catch(Exception e) {
			throw new DatabaseException("FileMan.restore: Error while accessing the database\n"+e);
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
		sb.append("</ul><br/>");
		
		return sb.toString();
	}

	public String deleteRestore(String pUsername, Text pI18n) {
		//remove the user from the database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			db.getConnection().setAutoCommit(false);
			
			//Remove fiels form the filesystem
			ps=db.prepareStatement("SELECT file.server, file.timestamp, file.key, extension.name AS extension FROM file, extension, account WHERE file.key_user=account.id AND account.name=? AND file.status=? AND file.key_extension=extension.id");
			ps.setString(1,pUsername);
			ps.setInt(2,DBFSFile.STATUS_DELETE);
			
			rs=ps.executeQuery();
			
			if(rs==null) throw new DatabaseException("FileMan.deleteRestore: Invalid Result");

			String tmpPath;
			while(rs.next()) {
				tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"));
				if(!Delete.delete(tmpPath+File.separator+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension"))
						|| !Delete.delete(tmpPath+File.separator+'_'+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")))
					
							//write file
							synchronized(DBFS.class) {
									Append.appendString(new File(tmpPath+".sh"),"rm "+tmpPath+File.separatorChar+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\nrm "+tmpPath+File.separatorChar+'_'+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")+"\n\n");
							}
					
							Log.write("Account.delete: DELETE - "+rs.getString("timestamp")+'_'+rs.getString("key")+'@'+rs.getInt("server"),Log.WARNING);
			}

			//delete db-entries
			ps=db.prepareStatement("DELETE FROM file WHERE status=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setInt(1,DBFSFile.STATUS_DELETE);
			ps.setString(2, pUsername);
			ps.executeUpdate();
			
			db.getConnection().commit();
			
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//no nothing
			}
			Log.write("FileMan.deleteRestore: Database-Error\n"+e,Log.SYSTEM);
			return pI18n.getText("fileman.file.restore.delete.error");
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
		
		return pI18n.getText("fileman.file.restore.delete.ok");
	}

	private String translate(HttpServletRequest pRequest, String pKey) {
		Text i18n=(Text)pRequest.getSession().getAttribute("i18n.i18n");
		return i18n==null ? pKey: i18n.getText(pKey);
	}

	public void destroy() {
		
	}
}
