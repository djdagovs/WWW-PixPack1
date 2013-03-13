package com.enlightware.pixpack;

import com.enlightware.pixpack.Root;

public class DBFSExtension extends Root {
	protected int id;
	protected String name;
	protected String mimetype;
	protected String description;
	protected boolean isWritable;
	protected boolean isWeb;

	public DBFSExtension() {
		id=-1;
		name=null;
		mimetype=null;
		description=null;
		isWritable=false;
		isWeb=false;
	}
	
	public DBFSExtension(String pName) {
		this();
		name=pName;
	}
	
	public DBFSExtension(int pId, String pName, String pMimetype, String pDescription, boolean pIsWritable, boolean pIsWeb) {
		id=pId;
		name=pName;
		mimetype=pMimetype;
		description=pDescription;
		isWritable=pIsWritable;
		isWeb=pIsWeb;
	}

	public DBFSExtension(String pName, String pMimetype) {
		this();
		name=pName;
		mimetype=pMimetype;
	}

	public String getDescription() {
		return description;
	}

	public String getMimetype() {
		return mimetype;
	}

	public String getName() {
		return name;
	}
	
	public boolean isWritable() {
		return isWritable;
	}
	
	public boolean isWeb() {
		return isWeb;
	}
}
