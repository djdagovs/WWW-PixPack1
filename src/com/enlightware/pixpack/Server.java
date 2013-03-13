package com.enlightware.pixpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Server;
import com.laukien.math.Random;

public class Server {
	private static String gsURL;
	private static String gsPath;
	private static String gsPrefix;
	private static String gsSuffix;
	private static int gsMin;
	private static int gsMax;
	private static Exception gsError;

	static {
		init();
	}
	
	private synchronized static void init() {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"server.properties"));
			synchronized(Server.class) {
				gsURL=prop.getProperty("url");
				gsPath=prop.getProperty("path");
				gsPrefix=prop.getProperty("prefix");
				gsSuffix=prop.getProperty("suffix");
				gsMin=Integer.parseInt(prop.getProperty("min"));
				gsMax=Integer.parseInt(prop.getProperty("max"));
				
				if(gsMax<gsMin || gsMax<1 || gsMin<0) throw new Exception("Server.init: Min/Max is invalid");
				
				//make directories
				for(int i=gsMin; i<=gsMax; i++) {
					if(!new File(gsPath+File.separator+buildName(i)).mkdirs()) throw new IOException("Server.init: Unable to create server-directory");
				}
				
				gsError=null;
			}
		} catch (Exception e) {
			synchronized(Server.class) {
				gsError=e;
			}
		}
	}
	
	public static void reload() {
		init();
	}

	public static Exception getError() {
		return gsError;
	}
	
	/**
	 * "3"
	 * 
	 * @return
	 */
	public static int getRandomNumber() {
		return Random.nextInt(gsMax-gsMin+1)+gsMin;
	}
	
	/**
	 * "srv003"
	 * 
	 * @return
	 */
	public static String getRandomName() {
		return gsPrefix+intToString(getRandomNumber())+gsSuffix;
	}

	/**
	 * "srv001"
	 * 
	 * @param pNumber
	 * @return
	 */
	public static String buildName(int pNumber) {
		return gsPrefix+intToString(pNumber)+gsSuffix;
	}

	/**
	 * "http://pixpack.net"
	 * 
	 * @return
	 */
	public static String getRoot() {
		return "http://"+gsURL;
	}

	/**
	 * "http://pixpack.net/image"
	 * 
	 * @return
	 */
	public static String getImageURI() {
		return getRoot()+"/image";
	}

	/**
	 * "pixpack.net"
	 * 
	 * @return
	 */
	public static String getURL() {
		return gsURL;
	}
	
	/**
	 * "/data/enlightware/pixpack"
	 * 
	 * @return
	 */
	public static String getPath() {
		return gsPath;
	}
	
	public static int getMin() {
		return gsMin;
	}
	
	public static int getMax() {
		return gsMax;
	}
	
	private static String intToString(int pServer) {
		if(pServer<10) return "00"+pServer;
		if(pServer<100) return "0"+pServer;
		
		return ""+pServer;
	}
}
