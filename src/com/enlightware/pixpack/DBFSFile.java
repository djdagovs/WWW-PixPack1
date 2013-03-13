package com.enlightware.pixpack;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.laukien.array.Add;
import com.laukien.bean.database.Database;
import com.laukien.bean.query.TSearch;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.Comment;
import com.enlightware.pixpack.DBFS;
import com.enlightware.pixpack.DBFSExtension;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.DBFSFolder;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.io.file.Append;
import com.laukien.io.file.Delete;
import com.laukien.string.Cut;
import com.laukien.string.HTML;
import com.laukien.string.Replace;

public class DBFSFile extends DBFS {
	public static final int STATUS_BAN = -2;
	public static final int STATUS_DELETE = -1;
	public static final int STATUS_DEFAULT = 0;
	public static final int STATUS_SAVE = 1;

	private int gServer;

	private int gId; 
	private String gName;
	private String gTimestamp;
	private String gKey;
	private DBFSExtension gExtension;
	private DBFSFolder gFolder;
	private String gUsername;
	private String gLast;
	private long gCount;
	private boolean gPublic;
	private boolean gAdult;
	private int gStatus;
	private String gDescription;
	private String gIp;
	private int gWidth;
	private int gHeight;
	private int gSize;
	private String gProtect;
	private boolean gIsThumbnail;
	private int gVote;
	private Comment[] gComments;
	
	public DBFSFile() {
		super();
		gServer=-1;
		gIsThumbnail=false;
		
		gId=-1; 
		gName=null;
		gTimestamp=null;
		gKey=null;
		gExtension=null;
		gFolder=null;
		gUsername=null;
		gLast=null;
		gCount=0;
		gPublic=true;
		gAdult=false;
		gStatus=0;
		gDescription=null;
		gIp="0.0.0.0";
		gWidth=0;
		gHeight=0;
		gSize=0;
		gProtect=null;
		gVote=-1;
		gComments=null;
	}
	
	public void setRequest(HttpServletRequest pRequest) {
		gIp=pRequest.getRemoteAddr();
	}
	
	/**
	 * Extracts and sets timestamp, key and the user (if given).
	 * 
	 * @param pInternal filename which is used internally
	 */
	public void setInternalFilename(String pInternal) {
		if(pInternal==null || pInternal.length()<28) throw new ParameterException("DBFSFile.setInternalFilename: Invalid filename\n(timestamp_key.ext)"+pInternal);
		//to lower case
		pInternal=pInternal.toLowerCase();
		
		int pos;
		
		//is user?
		pos=pInternal.indexOf(':');
		if(pos!=-1) {
			//mail!=username
			gUsername=pInternal.substring(0,pos);
			pInternal=pInternal.substring(pos+1);
		}
		
		//check if it is a thumbnail
		if(pInternal.charAt(0)=='_') {
			gIsThumbnail=true;
			pInternal=pInternal.substring(1);
		}
		
		pos=pInternal.indexOf('_');
		if(pos!=17) throw new ParameterException("DBFS: Invalid length (Timestamp)");
		gTimestamp=pInternal.substring(0,pos);
		gKey=pInternal.substring(pos+1,pos+11);
		
		//Extension
		pos=pInternal.length();
		if(pInternal.charAt(pos-4)!='.') gExtension=null;
		else gExtension=new DBFSExtension(pInternal.substring(pos-3,pos));
		
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
	
	public void setExtension(String pExtension) {
		gExtension=new DBFSExtension(pExtension);
	}
	
	public DBFSExtension getExtension() {
		return gExtension;
	}
	
	public void setFolder(DBFSFolder pFolder) {
		gFolder=pFolder;
	}
	
	public DBFSFolder getFolder() {
		return gFolder;
	}
	
	public void setUsername(String pUsername) {
		gUsername=pUsername;
	}
	
	
	/**
	 * Sets the server that is need by #insert().
	 * 
	 * @param pServer valid number of a server
	 */
	public void setServer(int pServer) {
		gServer=pServer;
	}
	
	/**
	 * Returns the Server which hosts the image.
	 * If the data had not been read from the database it will call #read().
	 * 
	 * @return number of the server or "-1" if there is no server or an database-error (read)
	 * @see #read()
	 */
	public int getServer() {
		try {
			if(gServer!=-1) return gServer;
			else return (gServer=read());
		} catch(Exception e) {
			return (gServer=-1);
		}
	}
	
	public void setDescription(String pDescription) {
		if(pDescription==null || (pDescription=pDescription.trim()).length()==0) gDescription=null;
		else gDescription=HTML.isValidText(pDescription) ? pDescription : null;
	}
	
	public String getDescription() {
		return gDescription;
	}
	
	public void setPublic(boolean pPublic) {
		gPublic=pPublic;
	}
	
	public boolean isPublic() {
		return gPublic;
	}
	
	public void setAdult(boolean pAdult) {
		gAdult=pAdult;
	}
	
	public boolean isAdult() {
		return gAdult;
	}
	
	public void setStatus(int pStatus) {
		if(pStatus<Short.MIN_VALUE || pStatus>Short.MAX_VALUE) throw new ParameterException("DBFSFile.setStatus: Invalid size");
	}
	
	public int getStatus() {
		return gStatus;
	}
	
	public long getCount() {
		return gCount;
	}

	public int getId() {
		return gId;
	}
	
	/**
	 * Sets the name of the image/file.
	 * It's only the free, database-holded name.
	 * 
	 * @param pName
	 */
	public void setName(String pName) {
		gName=Cut.length(Lib.deleteQuote(pName),128);
	}
	
	public String getName() {
		return gName;
	}

	public String getLast() {
		return gLast;
	}

	public void setWidth(int pWidth) {
		if(pWidth<Short.MIN_VALUE || pWidth>Short.MAX_VALUE) throw new ParameterException("DBFSFile.setWidth: Invalid size");
		
		gWidth=pWidth;
	}
	
	public int getWidth() {
		return gWidth;
	}
	
	public void setHeight(int pHeight) {
		if(pHeight<Short.MIN_VALUE || pHeight>Short.MAX_VALUE) throw new ParameterException("DBFSFile.setHeight: Invalid size");
		
		gHeight=pHeight;
	}
	
	public int getHeight() {
		return gHeight;
	}
	
	public void setSize(int pSize) {
		gSize=pSize;
	}
	
	public int getSize() {
		return gSize;
	}
	
	/**
	 * Sets the allowed referer.
	 * 
	 * @param pReferer referer which is the pattern
	 */
	public void setProtect(String pReferer) {
		gProtect=pReferer;
	}
	
	/**
	 * Returns the protection-string.
	 * if it has no characters inside it'll be <code>null</code>.
	 * 
	 * @return referer or <code>null</code> if there is no protection
	 */
	public String getProtect() {
		return com.laukien.string.String.isEmpty(gProtect) ? null : gProtect;
	}
	
	public boolean isThumbnail() {
		return gIsThumbnail;
	}

	protected void setVote(int pCount, int pSum) {
		gVote=pCount>0 ? pSum/pCount : -1;
	}
	
	public int getVote() {
		return gVote;
	}
	
	/**
	 * Returs the picture's comments as String.
	 * 
	 * @return
	 */
	public String getCommentsAsString() {
		if(gComments==null || gComments.length==0) return "";
		
		StringBuffer sb=new StringBuffer();

		for(int i=0; i<gComments.length; i++) {
			sb.append("<div class=\"comment\">"+Replace.replace(gComments[i].getText(),"\n","<br/>"));
			sb.append("<div class=\"author\">"+(gComments[i].getUser())+"</div></div>");
		}
		
		return sb.toString();
	}
	
	public void addComment(String pText) throws DatabaseException {
		if(gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.exists: Invalid parameter-set");
		 if(pText==null || (pText=pText.trim()).length()<5 || !HTML.isValidText(pText) || HTML.isTag(pText)) return;
		 
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			//check if the user exists
			ps=db.prepareStatement("INSERT INTO comment (text, timestamp, key_file, key_user) VALUES (?,?,(SELECT id FROM file WHERE timestamp=? AND key=?), (SELECT id FROM account WHERE name=?))");
			ps.setString(1,pText);
			ps.setString(2,DateTime.getTimestamp());
			ps.setString(3,gTimestamp);
			ps.setString(4,gKey);
			if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(5,Account.ANONYMOUS);
			else ps.setString(5,gUsername);
			
			ps.executeUpdate();
			
		} catch(Exception e) {
			//insert a text twice
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
	 * Checks if the Image exists in the database (FS).
	 * <p>
	 * 	Global Parameters:
	 * 	<br/>
	 * 	internalFilename
	 * 	(username)
	 * </p>
	 * 
	 * @return <code>true</code> if the file exists; otherwiese <code>false</code>
	 * @throws DatabaseException 
	 */
	public boolean exists() throws DatabaseException {
		if(gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.exists: Invalid parameter-set");
		
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="SELECT count(file.id) AS count FROM file, account WHERE file.status>=? AND file.name=? AND file.key=? AND file.key_user=account.id AND account.name=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setInt(1,STATUS_DEFAULT);
			ps.setString(2,gTimestamp);
			ps.setString(3,gKey);
			if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(4,Account.ANONYMOUS);
			else ps.setString(4,gUsername);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.exists: No result");
			
			return (rs.getInt("count")>0);
			
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.exists: Error while accessing the database\n"+e);
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

	public int count(String pFolder) throws DatabaseException {
		boolean isContent=!(pFolder==null || pFolder.length()==0);
		
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());

			String query;
			if(isContent) query="SELECT count(file.id) FROM file, folder, account WHERE file.status>=? AND file.key_user=account.id AND account.name=? AND file.key_folder=folder.id AND folder.name=?";
			else query="SELECT count(file.id) FROM file, account WHERE file.status>=? AND file.key_user=account.id AND account.name=? AND file.key_folder=?";
			
			db.connect();
			
			ps=db.prepareStatement(query);
			ps.setInt(1,STATUS_DEFAULT);
			if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(2,Account.ANONYMOUS);
			else ps.setString(2,gUsername);
			if(isContent) ps.setString(3, pFolder);
			else ps.setInt(3,-1);	//no folder
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("FileMan.count: No result");
			
			return rs.getInt("count");
		} catch(Exception e) {
			throw new DatabaseException("FileMan.count: Error while accessing the database\n"+e);
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
	 * Gets all information which are needed the show an image.
	 * "Read" increments/updates the image-entry.
	 * If the file doesn't exists <code>-1</code> will be returned.
	 * A selection of parameters will be set by "read".
	 * (server, name, count, public, adult, status, protect, extension-name, mimetype)
	 * 
	 * <p>
	 * 	Global Parameters:
	 * 	<br/>
	 * 	filename
	 * 	(username)
	 * </p>
	 * @retrun the server which hosts the image
	 * @throws DatabaseException 
	 */
	public int read() throws DatabaseException {
		if(gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.read: Invalid parameter-set");
		
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			//String query="SELECT file.id, file.server, file.count FROM file, account WHERE file.timestamp=? AND file.key=? AND file.key_user=account.id AND account.name=?";
			String query="SELECT " +
					"file.id, file.server, file.name, file.count, file.public, file.adult, file.status, file.protect, " +
					"extension.name AS extName, extension.mimetype AS extType " +
					"FROM file, extension " +
					"WHERE file.timestamp=? AND file.key=? AND file.key_extension=extension.id";

			db.connect();
			
			//get the server-id
			ps=db.prepareStatement(query);
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			//if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(3,Account.ANONYMOUS);
			//else ps.setString(3,gUsername);
			
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("DBFSFile.read: No result");
			if(!rs.next()) return -1;	//dosn't exists
			
			//Set the selection of read parameters
			gServer=rs.getInt("server");
			gName=rs.getString("name");
			gCount=rs.getLong("count");
			gPublic=rs.getBoolean("public");
			gAdult=rs.getBoolean("adult");
			gStatus=rs.getShort("status");
			gProtect=rs.getString("protect");
			gExtension=new DBFSExtension(rs.getString("extName"), rs.getString("extType"));
			
			if(!gIsThumbnail) {
				//update
				query="UPDATE file SET last=?, count=? WHERE file.id=?";
				ps=db.prepareStatement(query);
				ps.setString(1, DateTime.getTimestamp());
				ps.setLong(2, (rs.getLong("count"))+1);
				ps.setInt(3,rs.getInt("id"));
				
				ps.executeUpdate();
				
//				rs.updateString("last", DateTime.getTimestamp());
//				rs.updateLong("count", rs.getLong("count")+1);
//				rs.updateRow();
			}
			
			return gServer;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.read: Error while accessing the database\n"+e);
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
	 * Increments the counter and sets "last".
	 * 
	 * @throws DatabaseException
	 */
	public void increment() throws DatabaseException {
		if(gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.increment: Invalid parameter-set");
		
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			//String query="SELECT file.id, file.server, file.count FROM file, account WHERE file.timestamp=? AND file.key=? AND file.key_user=account.id AND account.name=?";
			String query="SELECT id, count FROM file WHERE file.timestamp=? AND file.key=?";

			db.connect();
			
			//get the server-id
			ps=db.prepareStatement(query);
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			//if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(3,Account.ANONYMOUS);
			//else ps.setString(3,gUsername);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.increment: No result");
			
			//update
			query="UPDATE file SET last=?, count=? WHERE id=?";
			ps=db.prepareStatement(query);
			ps.setString(1, DateTime.getTimestamp());
			ps.setLong(2, (rs.getLong("count"))+1);
			ps.setInt(3,rs.getInt("id"));
			
			ps.executeUpdate();
			
//			rs.updateString("last", DateTime.getTimestamp());
//			rs.updateLong("count", rs.getLong("count")+1);
//			rs.updateRow();
			
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.increment: Error while accessing the database\n"+e);
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
	 * Adds a new File to the DBFS.
	 * There are no checks if the file already exists.
	 * @throws DatabaseException 
	 *
	 */
	public void insert() throws DatabaseException {
		//check Parameters
		correct();
		
		//init Folder
		int folderId=-1;	//No folder
		if(gFolder!=null && (gFolder.id!=-1 || (gFolder.name!=null && gFolder.name.length()>0))) {
			gFolder.setUsername(gUsername);
			//get info and/or create the folder
			gFolder.read();
			folderId=gFolder.id;
		}
		
		//init User
		String userName=(gUsername!=null ? gUsername : Account.ANONYMOUS);	//Default "No-User"

		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;

		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			TSearch ts=new TSearch();
			String query="INSERT INTO file " +
					"(server, name, timestamp, key, last, key_user, key_folder, key_extension, description, public, adult, status, ip, width, height, size, protect, vector) " +
					" VALUES (?,?,?,?,?,(SELECT id FROM account WHERE name=?), ?,(SELECT id FROM extension WHERE name=?),?,?,?,?,?::inet,?,?,?,?,'"+ts.toVector(gName+(gDescription==null ? "" : " "+gDescription))+"'::tsvector)";
			
			db.connect();

			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setInt(1,gServer);
			ps.setString(2,gName);
			ps.setString(3,gTimestamp);
			ps.setString(4,gKey);
			ps.setString(5,gTimestamp);
			ps.setString(6,userName);
			ps.setInt(7,folderId);
			ps.setString(8,gExtension.name);
			ps.setString(9,gDescription);
			ps.setBoolean(10,gPublic);
			ps.setBoolean(11,gAdult);
			ps.setShort(12,(short)gStatus);
			ps.setString(13,gIp);
			ps.setShort(14,(short)gWidth);
			ps.setShort(15,(short)gHeight);
			ps.setInt(16,gSize);
			ps.setString(17,Cut.length(gProtect,250));
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.insert: Error while accessing the database\n"+e);
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
	 * Resds an image and fills all
	 * @throws DatabaseException 
	 *
	 */
	public void select() throws DatabaseException {
		if(gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.select: Invalid parameter-set");
		
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			String query="SELECT " +
				"file.id, file.server, file.name, file.timestamp, file.key, file.last, file.count, file.description, file.public, file.adult, file.status, file.ip, file.width, file.height, file.size, file.protect, file.key_folder AS folder_id, " +
				"file.vote_sum, file.vote_count, " +
				"extension.id AS extension_id, extension.name AS extension_name, extension.mimetype AS extension_mimetype, extension.description AS extension_description, extension.write AS extension_write, extension.web AS extension_web, " +
				"account.name AS account_name, account.password AS account_password, account.timestamp AS account_timestamp, account.status AS account_status " +
				"FROM file, extension, account " +
				"WHERE file.timestamp=? AND file.key=? AND " +
				"file.key_extension=extension.id AND " +
				"file.key_user=account.id";// AND account.name=?";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			//if(com.laukien.string.String.isEmpty(gUsername)) ps.setString(3,Account.ANONYMOUS);
			//else ps.setString(3,gUsername);
			
			rs=ps.executeQuery();
			
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.select: No result (file)");
			
			gServer=rs.getInt("server");
			gId=rs.getInt("id");
			gName=rs.getString("name");
			gExtension=new DBFSExtension(rs.getInt("extension_id"),rs.getString("extension_name"),rs.getString("extension_mimetype"),rs.getString("extension_description"), rs.getBoolean("extension_write"), rs.getBoolean("extension_web"));
			gUsername=rs.getString("account_name");
			gLast=rs.getString("last");
			gCount=rs.getInt("count");
			gPublic=rs.getBoolean("public");
			gAdult=rs.getBoolean("adult");
			gStatus=rs.getShort("status");
			gDescription=rs.getString("description");
			gIp=rs.getString("ip");
			gWidth=rs.getShort("width");
			gHeight=rs.getShort("height");
			gSize=rs.getInt("size");
			gProtect=rs.getString("protect");
			setVote(rs.getInt("vote_count"), rs.getInt("vote_sum"));

			//Read Folder-Information
			if(rs.getInt("folder_id")!=-1) {
				ps=db.prepareStatement("SELECT * from folder WHERE id=?");
				ps.setInt(1,rs.getInt("folder_id"));

				rs=ps.executeQuery();
				
				if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.select: No result (folder)");

				gFolder=new DBFSFolder(rs.getInt("id"),rs.getString("name"),gUsername);
			} else gFolder=new DBFSFolder(-1);

			ps=db.prepareStatement("SELECT " +
					"comment.id, comment.text, comment.timestamp, " +
					"CASE WHEN comment.key_user=NULL THEN NULL ELSE (SELECT name FROM account WHERE id=comment.key_user) END AS user " +
					"FROM comment " +
					"WHERE comment.key_file=? " +
					"ORDER by timestamp DESC");
			ps.setInt(1, gId);
			
			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("DBFSFile.select: No result (comment)");
			
			Comment comment;
			gComments=new Comment[0];
			
			while(rs.next()) {
				comment=new Comment(rs.getString("text"), new DateTime(rs.getString("timestamp")),rs.getString("user"));
				gComments=(Comment[])Add.value(Comment.class, gComments, comment);
			}
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.select: Error while accessing the database\n"+e);
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
	 * Updates the file-entry in the database.
	 * All variables have to be set.
	 * 
	 * @throws DatabaseException 
	 */
	public void update() throws DatabaseException {
		//check Parameters
		correct();
		
		//init Folder
		int folderId=-1;	//No folder
		if(gFolder!=null && (gFolder.id!=-1 || (gFolder.name!=null && gFolder.name.length()>0))) {
			gFolder.setUsername(gUsername);
			//get info and/or create the folder
			gFolder.read();
			folderId=gFolder.id;
		}
		
		//init User
		String userName=(gUsername!=null ? gUsername:Account.ANONYMOUS);	//Default "No-User"

		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;

		try {
			db.setParameter(Lib.getParameter());
			
			//build query
			TSearch ts=new TSearch();
			String query="UPDATE file SET " +
					"server=?, name=?, last=?, key_user=(SELECT id FROM account WHERE name=?), key_folder=?, key_extension=(SELECT id FROM extension WHERE name=?), description=?, public=?, adult=?, status=?, ip=?::inet, width=?, height=?, size=?, protect=?, vector='"+ts.toVector(gName+(gDescription==null ? "" : " "+gDescription))+"'::tsvector " +
					"WHERE timestamp=? AND key=?";
			
			db.connect();

			//set values of an existing file
			ps=db.prepareStatement(query);
			ps.setInt(1,gServer);
			ps.setString(2,gName);
			ps.setString(3,DateTime.getTimestamp());
			ps.setString(4,userName);
			ps.setInt(5,folderId);
			ps.setString(6,gExtension.name);
			ps.setString(7,gDescription);
			ps.setBoolean(8,gPublic);
			ps.setBoolean(9,gAdult);
			ps.setShort(10,(short)gStatus);
			ps.setString(11,gIp);
			ps.setShort(12,(short)gWidth);
			ps.setShort(13,(short)gHeight);
			ps.setInt(14,gSize);
			ps.setString(15,Cut.length(gProtect,250));
			ps.setString(16,gTimestamp);
			ps.setString(17,gKey);
			
			ps.executeUpdate();
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.update: Error while accessing the database\n"+e);
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
	
	public boolean rename(String pDest) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null || pDest==null ||(pDest=pDest.trim()).length()==0) throw new ParameterException("DBFSFile.rename: Invalid parameter-set");
		if(HTML.isTag(pDest)) return false;
		
		//correct name
		pDest=Cut.length(pDest,128);
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("SELECT id, description FROM file WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			ps.setString(3,gUsername);
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) return false;
			
			int id=rs.getInt("id");
			String description=rs.getString("description");
			
			TSearch ts=new TSearch();
			ps=db.prepareStatement("UPDATE file SET name=?, vector='"+ts.toVector(pDest+(description==null ? "" : " "+description))+"'::tsvector WHERE id=?");
			ps.setString(1,pDest);
			ps.setInt(2,id);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.rename: Error while accessing the database\n"+e);
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
	 * Local movement of the file.
	 * The file are moved into a local folder.
	 * If the given folder (<code>pDest</code>) dosn't exists, a DatabaseException will be thrown.
	 * 
	 * @param pDest
	 * @return
	 * @throws DatabaseException
	 */
	public boolean move(String pDest) throws DatabaseException {
		return move(pDest, gUsername);
	}

	/**
	 * Global movement of the file.
	 * The file are moved into the named folder of the named user ('anonymous' means global).
	 * If the given folder (<code>pDest</code>) dosn't exists, it throw a ParameterException.
	 * (A user dosn't allowed to create a global folder!!!) 
	 * 
	 * @param pDest
	 * @param pUsername
	 * @return
	 * @throws DatabaseException
	 */
	public boolean move(String pDest, String pUsername) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.move: Invalid parameter-set");
		if(HTML.isTag(pDest)) return false;
		
		boolean isGlobal=com.laukien.string.String.isEmpty(pUsername) || pUsername.equals(Account.ANONYMOUS);
		String user=isGlobal ? Account.ANONYMOUS : pUsername;
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			if(pDest==null ||(pDest=pDest.trim()).length()==0) {
				ps=db.prepareStatement("UPDATE file SET "+(isGlobal ? "protect=null, " : "")+"key_folder=?, key_user=(SELECT id FROM account WHERE name=?) " +
						"WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE account.name=?)");
				ps.setInt(1,-1);	//ROOT-Folder
				ps.setString(2,user);
				ps.setString(3,gTimestamp);
				ps.setString(4,gKey);
				ps.setString(5,gUsername);
			} else {
				ps=db.prepareStatement("UPDATE file SET "+(isGlobal ? "protect=null, " : "")+"key_folder=" +
						"(SELECT folder.id FROM folder, account WHERE folder.name=? AND folder.key_user=account.id AND account.name=?), " +
						"key_user=(SELECT id FROM account WHERE name=?) " +
						"WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE account.name=?)");
				ps.setString(1,pDest);
				ps.setString(2,user);
				ps.setString(3,user);
				ps.setString(4,gTimestamp);
				ps.setString(5,gKey);
				ps.setString(6,gUsername);
			}
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.move: Error while accessing the database\n"+e);
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
	 * Changes the description of the file.
	 * 
	 * @param pDest
	 * @return
	 * @throws DatabaseException
	 */
	public boolean description(String pDest) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null || pDest==null ||(pDest=pDest.trim()).length()==0) throw new ParameterException("DBFSFile.description: Invalid parameter-set");
		if(!HTML.isValidText(pDest)) return false;
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("SELECT id, name FROM file WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setString(1,gTimestamp);
			ps.setString(2,gKey);
			ps.setString(3,gUsername);
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) return false;

			int id=rs.getInt("id");
			String name=rs.getString("name");

			TSearch ts=new TSearch();
			ps=db.prepareStatement("UPDATE file SET description=?, vector='"+ts.toVector(name+(pDest==null ? "" : " "+pDest))+"'::tsvector WHERE id=?");
			ps.setString(1,pDest);
			ps.setInt(2,id);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.description: Error while accessing the database\n"+e);
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
	 * Sets the status of the file
	 * 
	 * @param pIsPublic
	 * @param pIsAdult
	 * @return
	 * @throws DatabaseException
	 */
	public boolean status(boolean pIsPublic, boolean pIsAdult) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.status: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE file SET public=?, adult=? WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setBoolean(1,pIsPublic);
			ps.setBoolean(2,pIsAdult);
			ps.setString(3,gTimestamp);
			ps.setString(4,gKey);
			ps.setString(5,gUsername);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.status: Error while accessing the database\n"+e);
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
	 * Sets the status of the file.
	 * 
	 * @param pStatus
	 * @param pIsAdult
	 * @return
	 * @throws DatabaseException
	 */
	public boolean status(int pStatus) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null || pStatus<Short.MIN_VALUE || pStatus>Short.MAX_VALUE) throw new ParameterException("DBFSFile.status: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE file SET status=? WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setShort(1,(short)pStatus);
			ps.setString(2,gTimestamp);
			ps.setString(3,gKey);
			ps.setString(4,gUsername);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.status: Error while accessing the database\n"+e);
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
	 * Deletes the File without checking if there any files in them.
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public boolean delete() throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.delete: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			db.getConnection().setAutoCommit(false);

			//get User-Details
			ps=db.prepareStatement("SELECT id, status FROM account WHERE name=?");
			ps.setString(1,gUsername);
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.delete: No result");

			int id=rs.getInt("id");
			
			if(rs.getInt("status")==User.STATUS_FREE) {	//delete file from filesystem and the database
				//get file
				ps=db.prepareStatement("SELECT file.id, file.server, extension.name AS extension FROM file, extension WHERE file.timestamp=? AND file.key=? AND file.key_user=? AND file.key_extension=extension.id");
				ps.setString(1, gTimestamp);
				ps.setString(2, gKey);
				ps.setInt(3, id);

				rs=ps.executeQuery();
				if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.delete: No result");
				
				//delete it form the filesystem
				String tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"));
				if(!Delete.delete(tmpPath+File.separator+gTimestamp+'_'+gKey+'.'+rs.getString("extension"))
						|| !Delete.delete(tmpPath+File.separator+'_'+gTimestamp+'_'+gKey+'.'+rs.getString("extension"))) {

					//write file
					synchronized(DBFS.class) {
						Append.appendString(new File(tmpPath+".sh"),"rm "+tmpPath+File.separatorChar+gTimestamp+'_'+gKey+'.'+rs.getString("extension")+"\nrm "+tmpPath+File.separatorChar+'_'+gTimestamp+'_'+gKey+'.'+rs.getString("extension")+"\n\n");
					}
					Log.write("DBFSFile.delete: DELETE - "+gTimestamp+'_'+gKey+'@'+rs.getInt("server"),Log.WARNING);
				}

				
				//delete comments
				ps=db.prepareStatement("DELETE FROM comment WHERE key_file=?");
				ps.setInt(1, id);

				//delete it from the database
				ps=db.prepareStatement("DELETE FROM file WHERE file.timestamp=? AND file.key=? AND file.key_user=?");
				ps.setString(1, gTimestamp);
				ps.setString(2, gKey);
				ps.setInt(3, id);
				
				ps.executeUpdate();
			} else {	//update database
				//move images to the ROOT-Folder and mark it as deleted
				ps=db.prepareStatement("UPDATE file set key_folder=?, status=? WHERE timestamp=? AND key=? AND key_user=?");
				ps.setInt(1, -1);	//ROOT-folder
				ps.setInt(2, DBFSFile.STATUS_DELETE);	//Status: deleted
				ps.setString(3, gTimestamp);
				ps.setString(4, gKey);
				ps.setInt(5, id);

				ps.executeUpdate();
			}
			
			db.getConnection().commit();
			return true;
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//do nothing
			}
			throw new DatabaseException("DBFSFile.delete: Error while accessing the database\n"+e);
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
	}	

	/**
	 * Deletes the File by its id.
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public static boolean delete(int pId) throws DatabaseException {
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;

		String timestamp, key;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//get file
			ps=db.prepareStatement("SELECT file.id, file.server, file.timestamp, file.key, extension.name AS extension FROM file, extension WHERE file.id=? AND file.key_extension=extension.id");
			ps.setInt(1, pId);

			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFile.delete: No result");

			timestamp=rs.getString("timestamp");
			key=rs.getString("key");
			
			//delete it form the filesystem
			String tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"));
			if(!Delete.delete(tmpPath+File.separator+timestamp+'_'+key+'.'+rs.getString("extension"))
					|| !Delete.delete(tmpPath+File.separator+'_'+timestamp+'_'+key+'.'+rs.getString("extension"))) {

				//write file
				synchronized(DBFS.class) {
					Append.appendString(new File(tmpPath+".sh"),"rm "+tmpPath+File.separatorChar+timestamp+'_'+key+'.'+rs.getString("extension")+"\r\n "+tmpPath+File.separatorChar+'_'+timestamp+'_'+key+'.'+rs.getString("extension")+"\r\n\r\n");
				}
				Log.write("DBFSFile.delete: DELETE - "+timestamp+'_'+key+'@'+rs.getInt("server"),Log.WARNING);
			}


			//delete comments
			ps=db.prepareStatement("DELETE FROM comment WHERE key_file=?");
			ps.setInt(1, pId);
			
			//delete it from the database
			ps=db.prepareStatement("DELETE FROM file WHERE id=?");
			ps.setInt(1, pId);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.delete: Error while accessing the database\n"+e);
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
	 * Sets the protection-serverlist for the file.
	 * The protection-String will be converted and corrected.
	 * 
	 * @param pServerList http-referer
	 * @return
	 * @throws DatabaseException
	 */
	public boolean protect(String pServerList) throws DatabaseException {
		//check Parameters
		if(gUsername==null || gTimestamp==null || gKey==null) throw new ParameterException("DBFSFile.status: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE file SET protect=? WHERE timestamp=? AND key=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setString(1,correctProtect(pServerList,'|'));
			ps.setString(2,gTimestamp);
			ps.setString(3,gKey);
			ps.setString(4,gUsername);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFile.protect: Error while accessing the database\n"+e);
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

	public String getInternalFilename() {
		return gTimestamp+'_'+gKey+'.'+gExtension.name;
	}

	public String getUsername() {
		return gUsername;
	}
	
	private void correct() {
		if(gServer<Server.getMin() || gServer>Server.getMax()) throw new ParameterException("DBFSFile.correct: Invalid Server");
		if(gTimestamp==null || gTimestamp.length()!=17 || gKey==null || gKey.length()!=10 || gExtension==null)  throw new ParameterException("DBFSFile.correct: Invalid internal filename");
		if(gIp==null || gIp.length()<7 || gIp.length()>15)  throw new ParameterException("DBFSFile.correct: Invalid IP (Request not set)");
		
		Cut.length(gName,128,"...");
	}

	/**
	 * Converts the Protection-String from '|'-separated to ' ' and back.
	 * In the case of '|': If the String is longer then 248 characters it will be cut.
	 * All servernames in the list will be "lowercased".
	 * 
	 * @param pProtect List of servers
	 * @param pSeparator separator (' ' &  '|')
	 * @return the converted String if there where any servers in the list; otherwise an epty String (NOT NULL)
	 */
	public static String correctProtect(String pProtect, char pSeparator) {
		if(pProtect==null || pProtect.length()==0) pProtect="";
		else {
			StringTokenizer st=new StringTokenizer(pProtect,"|, ");
			pProtect="";
			while(st.hasMoreTokens()) {
				pProtect+=st.nextToken()+pSeparator;
			}
			
			if(pProtect.length()>0) {
				if(pSeparator=='|') {
					Cut.length(pProtect,249);
					pProtect='|'+pProtect;
				} else pProtect=pProtect.trim();
				
				pProtect=pProtect.toLowerCase();
			}
		}
		return pProtect;
	}
}
