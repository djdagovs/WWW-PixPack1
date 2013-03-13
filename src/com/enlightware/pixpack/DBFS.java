package com.enlightware.pixpack;


public class DBFS {
	static {
		init();
	}
	
	private synchronized static void init() {
	}
	
	public static void reload() {
		init();
	}
}
