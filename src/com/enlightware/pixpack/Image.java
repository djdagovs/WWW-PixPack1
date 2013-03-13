package com.enlightware.pixpack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Server;
import com.laukien.information.http.ServerInformation;
/**
 * Redirecter - Will be used if PixPack got a database backend.
 * 
 * @author Stephan Laukien
 */
public class Image extends HttpServlet {
	private static final long serialVersionUID = 20060712L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		ServerInformation info=new ServerInformation(pRequest);
		String filename=info.getServletPath().substring(1);
		
		//check direct-connect
		if(filename.startsWith("image/")) {
			sendImage(filename.substring(6),pResponse);	// image without path
			return;
		} else if(filename.startsWith("banner/")) {
			sendImage(filename,pResponse);	// image without path
			return;
		}
		
		DBFSFile dbfs=new DBFSFile();
		try {
			dbfs.setInternalFilename(filename);
		} catch(Exception e) {
			Log.write("Image: "+e,Log.USER);
			//invalid image
			sendImage("error.gif",pResponse);
			return;
		}
		
		//getServer and do update
		int server;
		try {
			server=dbfs.read();	//read();
		} catch(Exception e) {
			throw new RuntimeException("Image.service: Unable to read image-information\n"+e);
		}
		
		//Status
		if(dbfs.getStatus()<DBFSFile.STATUS_DEFAULT) {
			if(dbfs.getStatus()==DBFSFile.STATUS_DELETE) {
				sendImage("delete.gif",pResponse);
				return;
			} if(dbfs.getStatus()==DBFSFile.STATUS_BAN) {
				sendImage("ban.gif",pResponse);
				return;
			} else {
				sendImage("error.gif",pResponse);
				return;
			}
		}
		
		//Protection-check
		String protect=dbfs.getProtect();
		if(com.laukien.string.String.isNotEmpty(protect)) {
			String serverName=pRequest.getServerName().toLowerCase();
			if(!serverName.equals(Server.getURL()) && protect.indexOf('|'+serverName+'|')==-1) {
				sendImage("protect.gif",pResponse);
				return;
			}
		}
		
		
		//if(server<Server.getMin() || server>Server.getMax()) sendImage("error.gif",pResponse);
		//else pResponse.sendRedirect("http://"+Server.buildName(server)+'.'+Server.getURL()+'/'+(dbfs.getUsername()!=null ? dbfs.getUsername()+'/':"")+dbfs.getInternalFilename());
		//else {
			pResponse.setContentType(dbfs.getExtension().getMimetype());
			pResponse.sendRedirect("http://"+Server.buildName(server)+'.'+Server.getURL()+'/'+(dbfs.isThumbnail() ? "_" : "")+dbfs.getInternalFilename());
		//}
	}

	protected static void sendImage(String pImage, HttpServletResponse pResponse) throws ServletException {
		FileInputStream fis=null;
		BufferedInputStream bis=null;
		ServletOutputStream sos=null;
		try {
			fis=new FileInputStream(Lib.getPath()+File.separator+"image"+File.separator+pImage);
			bis=new BufferedInputStream(fis);
			sos=pResponse.getOutputStream();
			pResponse.setContentType("image/"+Lib.getExtension(pImage));
			int i;
			while((i=bis.read())!=-1) sos.write(i);
		} catch(Exception e) {
			throw new ServletException("Image.sendImage: Unable to write image to outputstream\n"+e);
		} finally {
			if(sos!=null) {
				try {
					sos.flush();
					sos.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(bis!=null) {
				try {
					bis.close();
				} catch(Exception e) {
					//do nothing
				}
			}
			if(fis!=null) {
				try {
					fis.close();
				} catch(Exception e) {
					//do nothing
				}
			}
		}
	}

	public void destroy() {
		
	}
}
