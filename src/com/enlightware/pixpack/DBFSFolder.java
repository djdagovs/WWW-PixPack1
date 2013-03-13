package com.enlightware.pixpack;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFS;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.io.file.Delete;
import com.laukien.string.Cut;
import com.laukien.string.HTML;

public class DBFSFolder extends DBFS {

	protected int id;
	protected String name;
	private String gUsername;

	public DBFSFolder() {
		id=-1;
		name=null;
		gUsername=null;
	}
	
	public DBFSFolder(int pId) {
		this();
		id=pId;
	}

	public DBFSFolder(String pName) {
		this();
		name=Lib.deleteQuote(pName);
	}

	public DBFSFolder(int pId, String pName, String pUsername) {
		id=pId;
		name=Lib.deleteQuote(pName);
		gUsername=pUsername;
	}
	
	public void setUsername(String pUsername) {
		gUsername=pUsername;
	}

	public String getUsername() {
		return gUsername;
	}
	
	public int getId() {
		return id;
	}

	public void setName(String pName) {
		name=Lib.deleteQuote(pName);
	}
	public String getName() {
		return name;
	}

	/**
	 * Checks if the named folder exists.
	 * 
	 * @return <code>true</code> if the folder exists; otherwise <code>false</code>
	 * @throws DatabaseException 
	 */
	public boolean exists() throws DatabaseException {
		//check Parameters
		if(gUsername==null) throw new ParameterException("DBFSFolder.exists: Invalid parameter-set");
		if(name==null || (name=name.trim()).length()==0) return true;	//ROOT exists
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//build query
			ps=db.prepareStatement("SELECT count(folder.id) FROM folder, account WHERE folder.name=? AND folder.key_user=account.id AND account.name=?");
			ps.setString(1,name);
			ps.setString(2,gUsername);

			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFolder.exists: No Result");

			return (rs.getInt("count")>0);			
		} catch(Exception e) {
			throw new DatabaseException("DBFSFolder.exists: Error while accessing the database\n"+e);
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
	 * Counts the users folder.
	 * 
	 * @return "folder-count"
	 * @throws DatabaseException
	 */
	public int count() throws DatabaseException {
		//check Parameters
		if(gUsername==null) throw new ParameterException("DBFSFolder.count: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//build query
			ps=db.prepareStatement("SELECT count(folder.id) FROM folder, account WHERE folder.key_user=account.id AND account.name=?");
			ps.setString(1,gUsername);

			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFolder.count: No Result");

			return rs.getInt("count");			
		} catch(Exception e) {
			throw new DatabaseException("DBFSFolder.count: Error while accessing the database\n"+e);
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
	 * Returns the size of the folder.
	 * All filesizes are summerized.
	 * 
	 * @return returns the sum off all filesizes in <code>long</code>
	 * @throws DatabaseException
	 */
	public long size() throws DatabaseException {
		//check Parameters
		if(gUsername==null) throw new ParameterException("DBFSFolder.size: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//build query
			if(name==null || (name=name.trim()).length()==0) {
				ps=db.prepareStatement("SELECT sum(file.size) FROM file, account WHERE file.key_user=account.id AND account.name=?");
				ps.setString(1,gUsername);
			} else {
				ps=db.prepareStatement("SELECT sum(file.size) FROM file, account, folder WHERE file.key_user=account.id AND account.name=? AND file.key_folder=folder.id AND folder.name=?");
				ps.setString(1,gUsername);
				ps.setString(2,name);
			}

			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFolder.size: No Result");

			return rs.getLong("sum");			
		} catch(Exception e) {
			throw new DatabaseException("DBFSFolder.size: Error while accessing the database\n"+e);
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
	 * Reads and sets all data of the folder from the database.
	 * If the folder doesn't exists it will be created.
	 * 
	 * @throws DatabaseException
	 */
	public void read() throws DatabaseException {
		//check Parameters
		if(id==-1 && name==null) throw new ParameterException("DBFSFolder.read: Invalid parameter-set");
		
		String username=gUsername!=null ? gUsername:Account.ANONYMOUS;
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			//build query
			String query="SELECT * FROM folder WHERE ";
			//folder-name got the highest priority
			if(name!=null) {
				//!!!name+username
				query+="key_user=(SELECT id FROM account WHERE name=?) AND name=?";
				ps=db.prepareStatement(query);
				ps.setString(1,username);
				ps.setString(2,name);
			} else {
				query+="id=?";
				ps=db.prepareStatement(query);
				ps.setInt(1,id);
			}
			

			rs=ps.executeQuery();
			if(rs==null) throw new DatabaseException("DBFSFolder.read: No Result");
			if(!rs.next()) {
				//format
				String name=Cut.length(this.name,64);
				
				//Add a new Folder - but only if there is a name and an user
				if(gUsername==null || query.indexOf("id=?")!=-1) throw new ParameterException("DBFSFolder: Folder-Id dosn't exists\n"+id);
				ps=db.prepareStatement("INSERT INTO folder (name, key_user) VALUES(?,(SELECT id FROM account WHERE name=?))");
				ps.setString(1,name);
				ps.setString(2,username);
				
				ps.executeUpdate();
				
				//get the folder id
				ps=db.prepareStatement("SELECT folder.id, folder.name FROM folder, account WHERE name=? AND key_user=account.id AND account.name=?)");
				ps.setString(1,name);
				ps.setString(2,username);
				
				rs=ps.executeQuery();
				if(rs==null || !rs.next()) throw new DatabaseException("DBFSFolder.read: No Result (add new folder)");
			}
			
			id=rs.getInt("id");
			name=rs.getString("name");
		} catch(Exception e) {
			throw new DatabaseException("DBFSFolder.read: Error while accessing the database\n"+e);
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
	 * Creates a new Folder without checking it it already exists.
	 * 
	 * @return <code>true</code> if the folder was created successfully; otherwise <code>false</code>
	 * @throws DatabaseException 
	 */
	public boolean add() throws DatabaseException {
		//check Parameters
		if(gUsername==null || name==null ||(name=name.trim()).length()==0) throw new ParameterException("DBFSFolder.add: Invalid parameter-set");
		if(HTML.isTag(name)) return false;
		
		//correct name
		name=Cut.length(name,64);
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("INSERT INTO folder (name, key_user) VALUES (?, (SELECT id FROM account WHERE name=?))");
			ps.setString(1,name);
			ps.setString(2,gUsername);
			
			ps.executeUpdate();
			
			return true;
		} catch(Exception e) {
			throw new DatabaseException("DBFSFolder.create: Error while accessing the database\n"+e);
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
		if(gUsername==null || name==null ||(name=name.trim()).length()==0 || pDest==null ||(pDest=pDest.trim()).length()==0) throw new ParameterException("DBFSFolder.rename: Invalid parameter-set");
		if(HTML.isTag(pDest)) return false;
		
		//correct name
		pDest=Cut.length(pDest,64);
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();
			
			db.getConnection().setAutoCommit(false);
			
			//check if the folder already exists
			ps=db.prepareStatement("SELECT count(id) AS count FROM folder WHERE name=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setString(1, pDest);
			ps.setString(2, gUsername);
			rs=ps.executeQuery();
			if(rs==null || !rs.next() || rs.getInt("count")!=0) return false;

			//rename folder
			ps=db.prepareStatement("UPDATE folder SET name=? WHERE name=? AND key_user=(SELECT id FROM account WHERE name=?)");
			ps.setString(1,pDest);
			ps.setString(2,name);
			ps.setString(3,gUsername);
			
			ps.executeUpdate();

			db.getConnection().commit();
			
			return true;
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch(Exception e1) {
				//do nothing
			}
			Log.write("DBFSFolder.rename: Error while accessing the database\n"+e, Log.SYSTEM);
			throw new DatabaseException("DBFSFolder.rename: Error while accessing the database\n"+e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
			} catch (Exception e) {
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
	 * Local movement of the files.
	 * The files are moved into a local folder.
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
	 * Global movement of the files.
	 * The files are moved into the named folder of the named user ('anonymous' means global).
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
		if(gUsername==null || name==null ||(name=name.trim()).length()==0) throw new ParameterException("DBFSFolder.move: Invalid parameter-set");
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

			//move folder
			if(pDest==null ||(pDest=pDest.trim()).length()==0) {
				ps=db.prepareStatement("UPDATE file SET "+(isGlobal ? "protect=null, " : "")+"key_folder=?, key_user=(SELECT id FROM account WHERE name=?) " +
						"WHERE key_folder=(SELECT folder.id FROM folder, account WHERE folder.name=? AND folder.key_user=account.id AND account.name=?)");
				ps.setInt(1,-1);	//ROOT-Folder
				ps.setString(2,user);
				ps.setString(3,name);
				ps.setString(4,gUsername);
			} else {
				ps=db.prepareStatement("UPDATE file SET "+(isGlobal ? "protect=null, " : "")+"key_folder=" +
						"(SELECT folder.id FROM folder, account WHERE folder.name=? AND folder.key_user=account.id AND account.name=?), " +
						"key_user=(SELECT id FROM account WHERE name=?) " +
						"WHERE key_folder=" +
						"(SELECT folder.id FROM folder, account WHERE folder.name=? AND folder.key_user=account.id AND account.name=?)");
				ps.setString(1,pDest);
				ps.setString(2,user);
				ps.setString(3,user);
				ps.setString(4,name);
				ps.setString(5,gUsername);
			}
			
			ps.executeUpdate();
			
			ps.getConnection().commit();
			return true;
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch(Exception e1) {
				//do nothing
			}
			Log.write("DBFSFolder.move: Error while accessing the database\n"+e, Log.SYSTEM);
			throw new DatabaseException("DBFSFolder.move: Error while accessing the database\n"+e);
		} finally {
			try {
				db.getConnection().setAutoCommit(true);
			} catch (Exception e) {
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
	 * Deletes the Folder without checking if there any files in them.
	 * All files will be moved into the "ROOT"-folder and marked as deleted.
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public boolean delete() throws DatabaseException {
		//check Parameters
		if(gUsername==null || name==null ||(name=name.trim()).length()==0) throw new ParameterException("DBFSFolder.delete: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			db.getConnection().setAutoCommit(false);
			
			//delete gallery
			ps=db.prepareStatement("DELETE FROM gallery WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=(SELECT id FROM account WHERE name=?))");
			ps.setString(1, name);
			ps.setString(2, gUsername);
			ps.executeUpdate();

			//delete preview
			ps=db.prepareStatement("DELETE FROM preview WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=(SELECT id FROM account WHERE name=?))");
			ps.setString(1, name);
			ps.setString(2, gUsername);
			ps.executeUpdate();

			//get User-Details
			ps=db.prepareStatement("SELECT id, status FROM account WHERE name=?");
			ps.setString(1,gUsername);
			rs=ps.executeQuery();

			if(rs==null || !rs.next()) throw new DatabaseException("DBFSFolder.delete: No result");

			int id=rs.getInt("id");
			
			if(rs.getInt("status")==User.STATUS_FREE) {	//delete file from filesystem and the database
				//get file
				ps=db.prepareStatement("SELECT file.id, file.server, file.timestamp, file.key, extension.name AS extension FROM file, extension WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=?) AND file.key_extension=extension.id");
				ps.setString(1, name);
				ps.setInt(2, id);

				rs=ps.executeQuery();
				if(rs==null) throw new DatabaseException("DBFSFolder.delete: No result");
				
				//delete it form the filesystem
				while(rs.next()) {
					String tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"))+File.separator;
					tmpPath=Server.getPath()+File.separator+Server.buildName(rs.getInt("server"))+File.separator;
					if(!Delete.delete(tmpPath+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension"))
							|| !Delete.delete(tmpPath+'_'+rs.getString("timestamp")+'_'+rs.getString("key")+'.'+rs.getString("extension")))
								Log.write("DBFSFolder.delete: DELETE - "+rs.getString("timestamp")+'_'+rs.getString("key")+'@'+rs.getInt("server"),Log.WARNING);
				}

				
				//delete it from the database
				ps=db.prepareStatement("DELETE FROM file WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=?)");
				ps.setString(1, name);
				ps.setInt(2, id);
				
				ps.executeUpdate();
			} else {	//update database
				//move images to 'anonymous'
				ps=db.prepareStatement("UPDATE file set key_folder=?, status=? WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=?)");
				ps.setInt(1, -1);	//ROOT-folder
				ps.setInt(2, DBFSFile.STATUS_DELETE);	//Status: deleted
				ps.setString(3, name);
				ps.setInt(4, id);
				
				ps.executeUpdate();

			}

			ps=db.prepareStatement("DELETE FROM folder WHERE name=? AND key_user=?");
			ps.setString(1,name);
			ps.setInt(2,id);
			
			ps.executeUpdate();

			db.getConnection().commit();
			return true;
		} catch(Exception e) {
			try {
				db.getConnection().rollback();
			} catch (SQLException e1) {
				//do nothing
			}
			throw new DatabaseException("DBFSFolder.delete: Error while accessing the database\n"+e);
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
	 * Sets the status of the file
	 * 
	 * @param pIsPublic
	 * @param pIsAdult
	 * @return
	 * @throws DatabaseException
	 */
	public boolean status(boolean pIsPublic, boolean pIsAdult) throws DatabaseException {
		//check Parameters
		if(gUsername==null || name==null ||(name=name.trim()).length()==0) throw new ParameterException("DBFSFolder.delete: Invalid parameter-set");
		
		//init DB
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			
			db.connect();

			ps=db.prepareStatement("UPDATE file SET public=?, adult=? WHERE key_folder=(SELECT id FROM folder WHERE name=? AND key_user=(SELECT id FROM account WHERE name=?))");
			ps.setBoolean(1,pIsPublic);
			ps.setBoolean(2,pIsAdult);
			ps.setString(3,name);
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
}