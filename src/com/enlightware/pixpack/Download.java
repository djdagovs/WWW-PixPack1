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

import com.laukien.bean.magick.Magick;
import com.laukien.bean.magick.Resize;
import com.laukien.bean.magick.template.Thumbnail;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.Image;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.User;
import com.laukien.exception.DatabaseException;
import com.laukien.io.file.Delete;
/**
 * Convertes an image and manages its download and its lifetime on the filsystem.
 * The image will be converted into the destination-format, the mimetype will be set and after thet, it will be streamed directly to the client.
 * Download only wors with a valid and active session-user.
 * The "Taler"-Account will be set too.
 * 
 * @author Stephan Laukien
 */
public class Download extends HttpServlet {
	private static final long serialVersionUID = 200610019L;
	
	private static final int NONE=-1;
	private static final int UNKNOWN=0;
	private static final int IMAGE=1;
	private static final int DATABASE=2;
	private static final int CONVERT=3;

	private static final int RESIZE_OPTION_NONE=-1;
	private static final int RESIZE_OPTION_ADAPTIVE=0;
	private static final int RESIZE_OPTION_FORCE=1;		//!
	private static final int RESIZE_OPTION_PERCENTAGE=2;	//%
	private static final int RESIZE_OPTION_SMALLER=3;		//<
	private static final int RESIZE_OPTION_BIGGER=4;		//>
	private static final int RESIZE_OPTION_PIXEL=5;		//@

	private static final int LINK_NONE=-1;
	private static final int LINK_DEFAULT=0;		//http://pixpack.net/ts_key.ext
	private static final int LINK_FILESYSTEM=1;	///data/enlightware/pixpack/srv001/ts_key.ext
	private static final int LINK_DIRECT=2;		//http://srv001.pixpack.net/ts_key.ext
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
//		Login l=new Login();
//		l.setRequest(pRequest);		
//		l.dummy();
		
		User user=Login.getUser(pRequest);
		if(user==null) {
			Image.sendImage("access.gif", pResponse);
			return;
		}
		
		//Check User by its referer
		
		//Check account (Taler)
		
		String filename=pRequest.getPathInfo();
		if(filename==null || filename.length()<29) {
			Image.sendImage("error.gif", pResponse);
			return;
		} else filename=filename.substring(1);	//remove the slash '/'
		
		//Options
		String tmp=pRequest.getQueryString();
		boolean isOption=(tmp!=null && tmp.indexOf('=')!=-1);

		/**
		 * Save the rendered image.
		 */
		boolean oSave=false;
		
		/**
		 * Return the link of the rendered image.
		 * It depends on 'Save=true'.
		 */
		int oLink=LINK_NONE;
		
		boolean isResize=false;
		String oResizeWidth=null;
		String oResizeHeight=null;
		int oResizeOption=-1;
		boolean oThumbnail=false;

		/**
		 * Image format (jpg, gif, png, psd, ...)
		 */
		String oFormat=null;
		
		/**
		 * Name of the image.
		 * It could be used for the FileMan or the download-name.
		 */
		String oName=null;
		boolean oShow=false;

		if(isOption) {
			//resize
			oResizeWidth=pRequest.getParameter("resize.width");
			oResizeHeight=pRequest.getParameter("resize.height");
			tmp=pRequest.getParameter("resize.option");
			if(tmp!=null) {
				if(tmp.equalsIgnoreCase("force")) oResizeOption=RESIZE_OPTION_FORCE;
				else if(tmp.equalsIgnoreCase("precent") || tmp.equalsIgnoreCase("percentage")) oResizeOption=RESIZE_OPTION_PERCENTAGE;
				else if(tmp.equalsIgnoreCase("smaller")) oResizeOption=RESIZE_OPTION_SMALLER;
				else if(tmp.equalsIgnoreCase("bigger")) oResizeOption=RESIZE_OPTION_BIGGER;
				else if(tmp.equalsIgnoreCase("pixel")) oResizeOption=RESIZE_OPTION_PIXEL;
				else oResizeOption=RESIZE_OPTION_ADAPTIVE;
			} else oResizeOption=RESIZE_OPTION_NONE;
			
			tmp=pRequest.getParameter("thumbnail");
			oThumbnail=!(tmp==null || tmp.length()==0 || tmp.charAt(0)=='0' || tmp.charAt(0)=='f'|| tmp.charAt(0)=='F');
			
			isResize=(oResizeWidth!=null || oResizeHeight!=null || oResizeOption!=RESIZE_OPTION_NONE);
			
			//save
			tmp=pRequest.getParameter("save");
			oSave=!(tmp==null || tmp.length()==0 || tmp.charAt(0)=='0' || tmp.charAt(0)=='f' || tmp.charAt(0)=='F');

			//link
			tmp=pRequest.getParameter("link");
			if(tmp==null || tmp.length()==0) oLink=NONE;
			else {
				tmp=tmp.toLowerCase();
				if(tmp.charAt(0)=='d') oLink=LINK_DIRECT;
				else if(tmp.charAt(0)=='f') oLink=LINK_FILESYSTEM;
				else oLink=LINK_DEFAULT;
			}

			//format
			oFormat=pRequest.getParameter("format");
			if(oFormat!=null) oFormat=oFormat.toLowerCase();
			oName=pRequest.getParameter("name");
			tmp=pRequest.getParameter("show");
			oShow=!(tmp==null || tmp.length()==0 || tmp.charAt(0)=='0' || tmp.charAt(0)=='f' || tmp.charAt(0)=='F');
		}
		
		DBFSFile file=new DBFSFile();
		try {
			file.setInternalFilename(filename);
			file.setUsername(user.getName());
			
			file.select();
		} catch(Exception e) {
			message(pResponse, IMAGE, "Invalid parameter");
			return;
		}
		
		//SourceFile
		String sourceFile=Server.getPath()+File.separator+Server.buildName(file.getServer())+File.separator+file.getTimestamp()+'_'+file.getKey()+'.'+file.getExtension().getName();
		
		//set the system-name
		if(com.laukien.string.String.isEmpty(oName)) {
			oName=file.getName();
			if(com.laukien.string.String.isEmpty(oName)) oName=file.getTimestamp()+'_'+file.getKey();
		}
		
		//Stream directly
		if(!isResize && !oThumbnail && (oFormat==null || oFormat.length()==0)) {
			if(oLink!=LINK_NONE) {
				String internalFilename=file.getTimestamp()+'_'+file.getKey()+'.'+file.getExtension().getName();
				String linkFile;
				switch(oLink) {
				case LINK_DIRECT:		linkFile="http://"+Server.buildName(file.getServer())+'.'+Server.getURL()+'/'+internalFilename; break;
				case LINK_FILESYSTEM:	linkFile=Server.getPath()+File.separator+Server.buildName(file.getServer())+File.separator+internalFilename; break;
				default:				linkFile=Server.getRoot()+'/'+internalFilename;
				}
				
				message(pResponse, NONE, linkFile);
				return;
			}

			pResponse.setHeader("Content-Disposition",(oShow ? "inline" : "attachment")+"; filename=\""+oName+'.'+file.getExtension().getName()+"\""); 
			stream(pResponse,file.getExtension().getMimetype(), sourceFile);
			return;			
		}
		
		//Convert
		Magick magick=new Magick();
		magick.setImageIndex(0);
		
		if(isResize) {
			int width, height;
			Resize resize=new Resize();
			try {
				width=Integer.parseInt(oResizeWidth);
			} catch(Exception e) {
				width=-1;
			}
			
			try {
				height=Integer.parseInt(oResizeHeight);
			} catch(Exception e) {
				height=-1;
			}
			
			if(width>0 || height>0) {
				resize.setWidth(width);
				resize.setHeight(height);
				switch(oResizeOption) {
				case RESIZE_OPTION_FORCE:		resize.setOption(Resize.FORCE); break;
				case RESIZE_OPTION_PERCENTAGE:	resize.setOption(Resize.PERCENTAGE); break;
				case RESIZE_OPTION_SMALLER:		resize.setOption(Resize.SMALLER); break;
				case RESIZE_OPTION_BIGGER:		resize.setOption(Resize.BIGGER); break;
				case RESIZE_OPTION_PIXEL:		resize.setOption(Resize.PIXEL); break;
				default:						resize.setOption(Resize.ADAPTIVE); break;
				}
				magick.add(resize);
			}
		} else if(oThumbnail) {
			Thumbnail thumbnail=new Thumbnail();
			thumbnail.setAlignment(Thumbnail.ALIGN_AUTO);
			thumbnail.setInputFile(new File(sourceFile));
			magick.add(thumbnail);
		}
		
		if(oFormat!=null && (oFormat=oFormat.trim().toLowerCase()).length()>0) {
			//change the format if the image is to save in pixpack
			if(!Lib.isWebExtension(oFormat)) oSave=false;
		} else oFormat=file.getExtension().getName();

		//build outputfile
		String internalFilename=Lib.getUniqueKey();
		int internalServer=Server.getRandomNumber();
		String internalPath=(oSave ? Server.getPath()+File.separator+Server.buildName(internalServer)+File.separator : System.getProperty("java.io.tmpdir"));
		if(internalPath.charAt(internalPath.length()-1)!=File.separatorChar) internalPath+=File.separator;
		String destFile=internalPath+internalFilename+'.'+oFormat;

		magick.setInputFile(new File(sourceFile));
		magick.setOutputFile(new File(destFile));

		magick.execute();
		
		if(magick.isError() && !new File(destFile).exists()) {
			Log.write("Download - Convert: "+magick.getRuntimeCommand()+'\n'+magick.getResult(),Log.SYSTEM);
			message(pResponse, CONVERT, "Unable to convert");
			return;			
		}
		
		//get ImageInfo
		com.laukien.bean.magick.Image info=magick.getImage();

		//register the image to pixpack
		if(oSave) {
			//generate a thumbnail
			File thumbFile=new File(internalPath+'_'+internalFilename+'.'+oFormat);
			Thumbnail thumbnail=new Thumbnail();
			if(info!=null && info.getHeight()>info.getWidth()) thumbnail.setAlignment(Thumbnail.ALIGN_HEIGHT);
			thumbnail.setInputFile(new File(destFile));
			thumbnail.setOutputFile(thumbFile);
			thumbnail.execute();
			if(thumbnail.isError() && !thumbFile.exists()) {
				//remove source-file
				Delete.delete(destFile);
				Log.write("Download - Thumbnail: "+thumbnail.getRuntimeCommand()+'\n'+thumbnail.getResult(),Log.SYSTEM);
				message(pResponse, CONVERT, "Unable to build a thumbnail");
				return;
			}
			
			//write the image into the database
			file.setRequest(pRequest);
			file.setInternalFilename(internalFilename);
			file.setExtension(oFormat);
			if(oName!=null) file.setName(oName);
			file.setServer(internalServer);
			
			if(info!=null) {
				file.setWidth(info.getWidth());
				file.setHeight(info.getHeight());
				file.setSize(info.getSize());
			}
			
			try {
				message(pResponse,DATABASE,file.getUsername()+file.getFolder());
				if(true) return;
				file.insert();
			} catch (DatabaseException e) {
				Log.write("Download - Database: "+e,Log.SYSTEM);
				Delete.delete(destFile);
				Delete.delete(thumbFile);
				message(pResponse, DATABASE, "Unable to ad the file to PixPack");
				return;
			}

			if(oLink!=LINK_NONE) {
				String linkFile;
				switch(oLink) {
				case LINK_DIRECT:		linkFile="http://"+Server.buildName(internalServer)+'.'+Server.getURL()+'/'+internalFilename+'.'+oFormat; break;
				case LINK_FILESYSTEM:	linkFile=destFile; break;
				default:				linkFile=Server.getRoot()+'/'+internalFilename+'.'+oFormat;
				}
				
				message(pResponse, NONE, linkFile);
				return;
			}
			
		}

		pResponse.setHeader("Content-Disposition",(oShow ? "inline" : "attachment")+"; filename=\""+oName+'.'+oFormat+"\""); 
		//Mimetype should be resolved via the database (extension)
		stream(pResponse,"image/"+oFormat, destFile);

		//delete the tmp file - if it is not saved
		if(!oSave) Delete.delete(destFile);
	}
		
	public void destroy() {
		
	}
	
	private static void message(HttpServletResponse pResponse, int pNumber, String pMessage) throws IOException {
		pResponse.setContentType("text/plain");
		pResponse.getWriter().println((pNumber==NONE ? "" : pNumber+" ")+pMessage);
		pResponse.flushBuffer();
	}
	
	private static void stream(HttpServletResponse pResponse, String pMimetype, String pImage) throws IOException {
		pResponse.setContentType(pMimetype);

		FileInputStream fis=null;
		BufferedInputStream bis=null;
		ServletOutputStream sos=null;
		try {
			fis=new FileInputStream(pImage);
			bis=new BufferedInputStream(fis);
			sos=pResponse.getOutputStream();
			int i;
			while((i=bis.read())!=-1) sos.write(i);
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
		pResponse.flushBuffer();
	}
}
