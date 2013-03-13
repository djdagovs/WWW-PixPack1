package com.enlightware.pixpack;

import com.enlightware.pixpack.Account;
import com.enlightware.pixpack.DBFS;
import com.laukien.exception.ParameterException;

/**
 * Read Disk-Operations.
 * 
 * @author Stephan Laukien
 * @deprecated
 */
public class DBFSDisk extends DBFS {
	
	private static final int NONE = -1;
	private static final int FOLDER = 1;
	private static final int FILE = 2;

	private int gScope;
	protected String name;
	protected String username;
	
	public DBFSDisk() {
		gScope=NONE;
		name=null;
		username=null;
	}
	
	/**
	 * Moves the users content to the global filesystem.
	 * 
	 * @return
	 */
	public boolean move() {
		return move(null);
	}
	
	/**
	 * Moves the users content to an other users content.
	 * @param pUsername destination-user; <code>null</code> means "global"
	 * @return
	 */
	public boolean move(String pUsername) {
		if(gScope==NONE || username==null) throw new ParameterException("DBFSDisk.move: Incalid Parameter-Set");
		
		//is global
		boolean isGlobal=(com.laukien.string.String.isEmpty(pUsername) || pUsername.equals(Account.ANONYMOUS));
		
		//same user
		if(!isGlobal && pUsername.equals(username)) return false;
		
		return true;
	}

	/**
	 * Deletes the users content.
	 * 
	 * @return
	 */
	public boolean delete() {
		if(gScope==NONE || username==null) throw new ParameterException("DBFSDisk.move: Incalid Parameter-Set");
		return true;
	}

	public void setFolder(String pName) {
		gScope=FOLDER;
		name=pName;
	}
	public void setFile(String pName) {
		gScope = FILE;
		name=pName;
	}
	
	public void setUsername(String pUsername) {
		username=pUsername;
	}
}
