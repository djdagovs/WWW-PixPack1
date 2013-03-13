package com.enlightware.pixpack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Root;
import com.laukien.exception.DatabaseException;

public class Statistic extends Root {
		
	private static final Object SYNC = new Object();
	private static final String FILE = Lib.getPath()+File.separator+"WEB-INF"+File.separator+"statistic.counter"; 
	
	private static long gsView;
	private static long gsSession;
	
	static {
		init();
	}
	
	private synchronized static void init() {
		read();
	}
	
	public static void reload() {
		init();
	}
	
	private synchronized static void read() {
		//read counter
		BufferedReader br=null;
		try {
			br=new BufferedReader(new FileReader(FILE));
			gsView=Long.parseLong(br.readLine());
			gsSession=Long.parseLong(br.readLine());
		} catch (Exception e) {
			gsView=gsSession=0;
			Log.write("Statistic.read: Unable to read counter\n"+e, Log.WARNING);
		} finally {
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					//do nothing;
				}
		}
	}
	private synchronized static void write() {
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(FILE));
			bw.write(gsView+"\n");
			bw.write(gsSession+"\n");
			bw.flush();
		} catch (Exception e) {
			Log.write("Statistic.write: Unable to write counter\n"+e, Log.SYSTEM);
		} finally {
			if(bw!=null)
				try {
					bw.close();
				} catch (IOException e) {
					//do nothing;
				}
		}
	}
	
	public Statistic() {
		super();
	}
	
	public int getFileCount() {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(id) AS count FROM file WHERE status>?");
			ps.setInt(1, DBFSFile.STATUS_DELETE);
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Statistic.getFileCount: Invalid Result");
			return rs.getInt("count");
		} catch(Exception e) {
			Log.write("Statistic.getFileCount: Database-Error\n"+e,Log.SYSTEM);
			return -1;
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

	public int getFolderCount() {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(id) AS count FROM folder");
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Statistic.getFolderCount: Invalid Result");
			return rs.getInt("count");
		} catch(Exception e) {
			Log.write("Statistic.getFolderCount: Database-Error\n"+e,Log.SYSTEM);
			return -1;
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

	public int getUserCount() {
		//database
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			db.setParameter(Lib.getParameter());
			db.connect();

			//Count users
			ps=db.prepareStatement("SELECT count(id) AS count FROM account");
			
			rs=ps.executeQuery();
			if(rs==null || !rs.next()) throw new DatabaseException("Statistic.getUserCount: Invalid Result");
			return rs.getInt("count")-6;
		} catch(Exception e) {
			Log.write("Statistic.getUserCount: Database-Error\n"+e,Log.SYSTEM);
			return -1;
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
	
	public long getViewCount() {
		return gsView;
	}
	
	public long getSessionCount() {
		return gsSession;
	}
	
	public static void incView() {
		synchronized(SYNC) {
			gsView++;
		}
		
		if(gsView%100==0) write();
	}

	public static void incSession() {
		synchronized(SYNC) {
			gsSession++;
		}
	}
}
