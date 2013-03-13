package com.enlightware.pixpack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.laukien.bean.magick.Magick;
import com.laukien.bean.magick.Quality;
import com.laukien.bean.magick.Scale;
import com.laukien.bean.magick.Strip;
import com.laukien.bean.magick.Trim;
import com.laukien.bean.magick.template.Thumbnail;
import com.enlightware.pixpack.DBFSFile;
import com.enlightware.pixpack.DBFSFolder;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Message;
import com.enlightware.pixpack.Server;
import com.enlightware.pixpack.Upload;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.DatabaseException;
import com.laukien.exception.ParameterException;
import com.laukien.information.http.ClientInformation;
import com.laukien.io.file.Copy;
import com.laukien.io.file.Delete;
import com.laukien.string.HTML;
import com.laukien.string.Random;
import com.laukien.taglib.i18n.Text;

public class Upload extends HttpServlet {
	private static final long serialVersionUID = 20060814L;
	
	public static final int MAXSIZE = 2024;	//1024kB
	//public static final String PATH = "/data/enlightware/pixpack";
	public static final String ENCODING = "UTF-8";
	public static final String DEFAULTUSER = ".";

	public static int gsMaxsize;
	public static String gsEncoding;
	public static String gsDefaultuser;

	private static Exception gsError;

	static {
		internalInit();
	}
	
	private synchronized static void internalInit() {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"upload.properties"));
			synchronized(Upload.class) {
				gsMaxsize=Integer.parseInt(prop.getProperty("maxsize"));
				gsEncoding=prop.getProperty("encoding");
				gsDefaultuser=prop.getProperty("defaultuser");
				
				gsError=null;
			}
		} catch (Exception e) {
			synchronized(Upload.class) {
				gsMaxsize=MAXSIZE;
				gsEncoding=ENCODING;
				gsDefaultuser=DEFAULTUSER;
				
				gsError=e;
			}
		}
	}
	
	public static void reload() {
		internalInit();
	}

	public static Exception getError() {
		return gsError;
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		if(Lib.getUsername(pRequest)==null) return;
		
		//last page
		String last=(String)pRequest.getSession().getAttribute("last");
		pRequest.getSession().setAttribute("last", "upload");

		Text text=(Text)pRequest.getSession().getAttribute("i18n.i18n");
		if(text==null) {
			forward(pRequest, pResponse, "Security Logout - Session reloaded", Lib.MESSAGE_TYPE_WARNING, null);
			return;
		}

		//check referer
		String referer=new ClientInformation(pRequest).getReferer();
		if(referer==null || referer.indexOf(pRequest.getServerName())==-1) {
			if(last==null || last.equals("upload")) {
				Log.write("Service - referer: ("+Lib.getUsername(pRequest)+") "+new ClientInformation(pRequest).getRemoteHost(),Log.USER);
				forward(pRequest, pResponse, text.getText("error.hack"),Lib.MESSAGE_TYPE_ERROR,null);
				return;
			}
		}

		//user
		User user=new Login(pRequest).getUser();

		//Anonymous: 2MB; Free: 4MB; Premium: 8MB
		int maxsize=user==null ? gsMaxsize : user.getStatus()==User.STATUS_FREE ? gsMaxsize*4 : gsMaxsize*8;
		
		//init upload
		com.laukien.io.upload.Upload upload=new com.laukien.io.upload.Upload(pRequest);
		upload.setMaxSize(maxsize);
		upload.setMaxCount(1);
		//upload.setPath(getPath(Lib.getUsername(pRequest)));
		upload.setEncoding(gsEncoding);
		try {
			upload.fetch();
		} catch (Exception e) {
			Log.write("Service - upload.fetch: ("+Lib.getUsername(pRequest)+") "+e,Log.SYSTEM);
			forward(pRequest, pResponse, text.getText("upload.error.submit"), Lib.MESSAGE_TYPE_ERROR, null);
			return;
		}
		
		
		//reload-block
		String reload=upload.getParameter("key");
		if(reload==null || reload.length()!=28) {
			Log.write("Service - key: ("+Lib.getUsername(pRequest)+") "+new ClientInformation(pRequest).getRemoteHost(),Log.USER);
			forward(pRequest, pResponse, text.getText("error.hack"),Lib.MESSAGE_TYPE_ERROR, upload.getParameter("forward"));
			return;
		}
		if(reload.equals(pRequest.getSession().getAttribute("upload.key"))) {
			forward(pRequest, pResponse, null,Lib.MESSAGE_TYPE_NONE, upload.getParameter("forward"));
			return;
		} else pRequest.getSession().setAttribute("upload.key",reload);

		//check if there is any HTML-tag
		// +"" prevents NullPointerException
		if(HTML.isTag(upload.getParameter("filename")+upload.getParameter("folder")) || !HTML.isValidText(upload.getParameter("description")+"")) {
			forward(pRequest, pResponse, text.getText("upload.html"), Lib.MESSAGE_TYPE_ERROR, null);
			return;
		}
		
		//save last selected folder
		if(upload.getParameter("folder")!=null && Lib.getUsername(pRequest)!=null) pRequest.getSession().setAttribute("folder.select",upload.getParameter("folder"));
		
		//File or URL
		String type=upload.getParameter("type");
		try {
			if(type!=null && type.equals("url")) uploadURL(pRequest, pResponse, upload, text);
			else uploadFile(pRequest, pResponse, upload, text);
		} catch(Exception e) {
			Log.write("Service - uploadFile/URL: ("+Lib.getUsername(pRequest)+") "+e,Log.SYSTEM);
			forward(pRequest, pResponse, text.getText("error.database"),Lib.MESSAGE_TYPE_NONE, upload.getParameter("forward"));
			return;
		}
	}

	private static void uploadURL(HttpServletRequest pRequest, HttpServletResponse pResponse, com.laukien.io.upload.Upload pUpload, Text pText) throws ServletException, IOException, DatabaseException {
		HttpSession session=pRequest.getSession();
		session.setAttribute("upload.file", "url");
		
		//Inlet
		String forward=pUpload.getParameter("forward");
		String user=pUpload.getParameter("user");
		String filename=pUpload.getParameter("filename");
		String folder=pUpload.getParameter("folder");
		String description=pUpload.getParameter("description");
		
		String tmp=pUpload.getParameter("public");
		boolean isPublic=(tmp!=null && tmp.equals("true"));
		if(isPublic) session.removeAttribute("upload.detail.public");
		else session.setAttribute("upload.detail.public", "false");
		
		tmp=pUpload.getParameter("adult");
		boolean isAdult=(tmp!=null && tmp.equals("true"));
		if(isAdult) session.setAttribute("upload.detail.adult", "true");
		else session.removeAttribute("upload.detail.adult");

		String file=pUpload.getParameter("url");
		//check if the field exists
		if(file==null || (!file.startsWith("http://") && !file.startsWith("ftp://"))) {
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
		}
		
		//if upload is called directly
		String clientFile=Lib.extractFilename(file);
		String extension=Lib.getExtension(clientFile);
		if(extension==null) {
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
			
		}

		//check if the filetype is valid
		if(!Lib.isValidExtension(extension)) {
			//tmpFile doesn't exists
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
		}
		
		if(user==null || user.length()==0) user=Lib.getUsername(pRequest);
		String internalFilename=getInternalFilename(clientFile);
		if(filename==null || filename.length()==0) filename=Lib.removeExtension(clientFile);

		//get Server-number
		int server=Server.getRandomNumber();
		
		//check if the file already exists
		DBFSFile dbfs=new DBFSFile();
		dbfs.setRequest(pRequest);
		dbfs.setUsername(user);
		dbfs.setFolder(new DBFSFolder(folder));
		dbfs.setInternalFilename(internalFilename);
		dbfs.setExtension(extension);
		dbfs.setName(filename);
		dbfs.setServer(server);
		dbfs.setDescription(description);
		dbfs.setPublic(user==null ? (session.isNew() ? false : true) : isPublic);
		dbfs.setAdult(isAdult);
		if(session.isNew()) dbfs.setStatus(DBFSFile.STATUS_BAN);
		
		if(dbfs.exists()) {
			//tmpFile doesn't exists
			Log.write("UploadURL - file.exists: "+new ClientInformation(pRequest).getRemoteHost(),Log.USER);
			forward(pRequest, pResponse, pText.getText("error.hack"),Lib.MESSAGE_TYPE_ERROR, forward);
			return;
		}
		
		
		URL url=new URL(file);
		URLConnection conn=url.openConnection();
		InputStream is=null;
		BufferedInputStream bis=null;
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;
		
		byte[] gLine=new byte[128];
		int i;
		int count=0;	//read the filesize
		int max=pUpload.getMaxSize()*1024;	//kb-->*1024=Byte
		File tmpFile=java.io.File.createTempFile(internalFilename, ".tmp", new File(System.getProperty("java.io.tmpdir")));
		
		//getPath(server)+File.separator+internalFilename;
		//Download
		try {
			fos=new FileOutputStream(tmpFile);
			bos=new BufferedOutputStream(fos);
			conn.connect();
			is=conn.getInputStream();
			bis=new BufferedInputStream(is);
			while((i=bis.read(gLine))!=-1) {
				count+=i;
				if(count>max) {
					forward(pRequest, pResponse, pText.getText("upload.error.maxsize"),Lib.MESSAGE_TYPE_WARNING, forward);
					return;
				}
				bos.write(gLine,0,i);
			}
		} catch(Exception e) {
			//remove source-file
			deleteFile(tmpFile);
			
			Log.write("UploadURL - upload: "+e,Log.SYSTEM);
			forward(pRequest, pResponse, pText.getText("upload.error.connection"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
		} finally {
			if(fos!=null && bos!=null) {
				fos.flush();
				bos.flush();
				bos.close();
				fos.close();
			}
			
			if(bis!=null) bis.close();
			if(is!=null) is.close();
		}
		
		try {
			setImage(pUpload, dbfs, tmpFile, internalFilename, extension, session);
		} catch(Exception e) {
			Log.write("Upload.File: Unable to save file\n"+e, Log.SYSTEM);
			forward(pRequest, pResponse, pText.getText("upload.error.file"),Lib.MESSAGE_TYPE_ERROR, forward);
			return;
		} finally {
			deleteFile(tmpFile);
		}

		//hotlinks
		pRequest.setAttribute("upload.file.name",internalFilename);

		forward(pRequest, pResponse, pText.getText("upload.success"),Lib.MESSAGE_TYPE_INFORMATION, forward);
	}
	
	private static void uploadFile(HttpServletRequest pRequest, HttpServletResponse pResponse, com.laukien.io.upload.Upload pUpload, Text pText) throws ServletException, IOException, DatabaseException {
		HttpSession session=pRequest.getSession();
		session.removeAttribute("upload.file");
		
		//Inlet
		String forward=pUpload.getParameter("forward");
		String user=pUpload.getParameter("user");
		String filename=pUpload.getParameter("filename");
		String folder=pUpload.getParameter("folder");
		String description=pUpload.getParameter("description");
		
		String tmp=pUpload.getParameter("public");
		boolean isPublic=(tmp!=null && tmp.equals("true"));
		if(isPublic) session.removeAttribute("upload.detail.public");
		else session.setAttribute("upload.detail.public", "false");
		
		tmp=pUpload.getParameter("adult");
		boolean isAdult=(tmp!=null && tmp.equals("true"));
		if(isAdult) session.setAttribute("upload.detail.adult", "true");
		else session.removeAttribute("upload.detail.adult");

		com.laukien.io.upload.File file=pUpload.getFile("file");
		
		//check if the field exists
		if(file==null) {
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
		}
		
		//check the size and if the file is "a real file"
		if(!file.isValid()) {
			//remove source-file
			deleteFile(file.getServerFile());
			forward(pRequest, pResponse, pText.getText("upload.error.maxsize"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
		}

		String clientFile=file.getClientFilename();
		String extension=Lib.getExtension(clientFile);
		if(extension==null) {
			//remove source-file
			deleteFile(file.getServerFile());
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_WARNING, forward);
			return;
			
		}
		boolean isZip=(extension.equals("zip") && new Login(pRequest).getUser()!=null);

		//check the filetypes
		if(!isZip && !Lib.isValidExtension(extension)) {
			//remove source-file
			deleteFile(file.getServerFile());
			forward(pRequest, pResponse, pText.getText("upload.error.type"),Lib.MESSAGE_TYPE_ERROR, forward);
			return;
		}
		
		//if upload is called directly
		if(user==null || user.length()==0) user=Lib.getUsername(pRequest);
		String internalFilename;
		
		//check if the file already exists
		DBFSFile dbfs=new DBFSFile();
		dbfs.setFolder(new DBFSFolder(folder));
		dbfs.setRequest(pRequest);
		dbfs.setUsername(user);
		dbfs.setDescription(description);
		dbfs.setPublic(user==null ? (session.isNew() ? false : true) : isPublic);
		dbfs.setAdult(isAdult);
		if(session.isNew()) dbfs.setStatus(DBFSFile.STATUS_BAN);

		//ZIP
		if(isZip) {
			if(filename==null) filename="";
			String name;
			File tmpFile, tmpDir=new File(System.getProperty("java.io.tmpdir"));
			InputStream in=null;
			OutputStream out=null;
			byte[] buffer = new byte[1024];
			int bufferLen;
			ZipEntry entry;
			ZipFile zip=new ZipFile(file.getServerFile());
			Enumeration entries=zip.entries();
			try {
				while(entries.hasMoreElements()) {
					entry=(ZipEntry)entries.nextElement();
					if(entry.isDirectory()) continue;

					name=filename+new File(entry.getName()).getName();
					extension=Lib.getExtension(name);
					if(!Lib.isValidExtension(extension)) continue;
					internalFilename=DateTime.getTimestamp()+'_'+Random.random(10,'a','z')+'.'+extension;
					tmpFile=java.io.File.createTempFile("upload@"+System.currentTimeMillis()+'_', ".tmp", tmpDir);

					//copy from zip to tmp
					try {
						in=zip.getInputStream(entry);
						out=new BufferedOutputStream(new FileOutputStream(tmpFile));
						
						while((bufferLen=in.read(buffer))>=0) out.write(buffer, 0, bufferLen);
						
					} catch(Exception e) {
						//next image
						continue;
					} finally {
					    in.close();
					    out.close();
					}


					
					dbfs.setInternalFilename(internalFilename);
					dbfs.setExtension(extension);
					dbfs.setName(Lib.removeExtension(name));

					//dbfs.exists() - not possible
					
					try {
						setImage(pUpload, dbfs, tmpFile, internalFilename, extension, session);
					} catch(Exception e) {
						//next image
					}
				}
			} catch(Exception e) {
				Log.write("Upload.File: Unable to save file\n"+e, Log.SYSTEM);
				forward(pRequest, pResponse, pText.getText("upload.error.file"),Lib.MESSAGE_TYPE_ERROR, forward);
				return;
			} finally {
				zip.close();

				//remove source-file
				deleteFile(file.getServerFile());
			}
		} else {
			internalFilename=getInternalFilename(clientFile);
			if(filename==null || filename.length()==0) filename=Lib.removeExtension(clientFile);
			dbfs.setInternalFilename(internalFilename);
			dbfs.setExtension(extension);
			dbfs.setName(filename);

			if(dbfs.exists()) {
				//remove source-file
				deleteFile(file.getServerFile());
				Log.write("UploadFile - file.exists: "+new ClientInformation(pRequest).getRemoteHost(),Log.USER);
				forward(pRequest, pResponse, pText.getText("error.hack"),Lib.MESSAGE_TYPE_ERROR, forward);
				return;
			}

			try {
				setImage(pUpload, dbfs, file.getServerFile(), internalFilename, extension, session);
			} catch(Exception e) {
				Log.write("Upload.File: Unable to save file\n"+e, Log.SYSTEM);
				forward(pRequest, pResponse, pText.getText("upload.error.file"),Lib.MESSAGE_TYPE_ERROR, forward);
				return;
			} finally {
				deleteFile(file.getServerFile());
			}
			
			//hotlinks
			pRequest.setAttribute("upload.file.name",internalFilename);
		}

		
		forward(pRequest, pResponse, pText.getText("upload.success"),Lib.MESSAGE_TYPE_INFORMATION, forward);
	}
	
	/**
	 * 
	 * @param pUpload for parameters only
	 * @param pDbfs	File-Object
	 * @param pTempFile temporary file
	 * @param pInternalFilename filename (timestamp_key)
	 * @param pExtension extension of the file
	 * @param pSession Session-Object to write parameters for the presentation
	 * @throws ParameterException
	 * @throws DatabaseException
	 */
	private static void setImage(com.laukien.io.upload.Upload pUpload, DBFSFile pDbfs, File pTempFile, String pInternalFilename, String pExtension, HttpSession pSession) throws ParameterException, DatabaseException {
		//get Server-number
		int server=Server.getRandomNumber();
		pDbfs.setServer(server);
		
		//Image-Management
		String imageGeometry=pUpload.getParameter("image.size");
		pSession.setAttribute("upload.image.size", imageGeometry);
		String thumbGeometry=pUpload.getParameter("thumbnail.size");
		pSession.setAttribute("upload.thumbnail.size", thumbGeometry);
		int thumbOption;
		String tmp=pUpload.getParameter("thumbnail.option");
		if(tmp==null || tmp.equals("cut")) thumbOption=Thumbnail.OPTION_CUT;
		else if(tmp.equals("width")) thumbOption=Thumbnail.OPTION_WIDTH;
		else if(tmp.equals("height")) thumbOption=Thumbnail.OPTION_HEIGHT;
		else thumbOption=Thumbnail.OPTION_AUTO;
		pSession.setAttribute("upload.thumbnail.option",tmp);
		
		Magick magick=new Magick();
		magick.setInputFile(pTempFile);
		if(!pExtension.equals("gif")) magick.setImageIndex(0);
		
		//If the image is not a web-grafic if'll be converted to jpeg
		if(!Lib.isWebExtension(pExtension)) {
			pInternalFilename=Lib.removeExtension(pInternalFilename)+".jpg";
			//change db-filename
			pDbfs.setInternalFilename(pInternalFilename);
		}

		File outFile=new File(getPath(server)+File.separator+pInternalFilename);
		magick.setOutputFile(outFile);
		
		
		if(pUpload.getParameter("image.optimize")!=null && pUpload.getParameter("image.optimize").equals("true")) {
			pSession.removeAttribute("upload.image.optimize");
			
			magick.add(new Trim());
			magick.add(new Strip());
			
			Quality quality=new Quality();
			quality.setImage(75);
			magick.add(quality);
		} else pSession.setAttribute("upload.image.optimize","false");
		
		
		if(com.laukien.string.String.isNotEmpty(imageGeometry)) {
			//if options --> resize NOT scale
			Scale scale=new Scale();
			try {
				scale.setGeometry(imageGeometry);
			} catch(ParameterException e) {
				//invalid parameter
				Log.write("Upload.setImage: Image- Scale: "+e,Log.USER);
				throw new ParameterException("Upload.setImage - Scale");
			}
			magick.add(scale);
		}
		
		magick.execute();
		if(magick.isError() && !outFile.exists()) {
			Log.write("Upload.setImage - Convert: "+magick.getRuntimeCommand()+'\n'+magick.getResult(),Log.SYSTEM);
			throw new ParameterException("Upload.setImage - Convert");
		}

		//get ImageInfo
		com.laukien.bean.magick.Image info=magick.getImage();

		
		//Build thumbnail via the thumbnail-template
		outFile=new File(getPath(server)+File.separator+'_'+pInternalFilename);
		
		Thumbnail thumbnail=new Thumbnail();
		if(info!=null) {
			if(info.getHeight()>info.getWidth()) thumbnail.setAlignment(Thumbnail.ALIGN_HEIGHT);
			else thumbnail.setAlignment(Thumbnail.ALIGN_WIDTH);
		}
		thumbnail.setInputFile(pTempFile);
		thumbnail.setOutputFile(outFile);
		thumbnail.setOption(thumbOption);
		
		if(com.laukien.string.String.isNotEmpty(thumbGeometry)) {
			try {
				thumbnail.setGeometry(thumbGeometry);
			} catch(Exception e) {
				//invalid parameter
				Log.write("Upload.setImage: Thumbnail->Scale: "+e,Log.USER);
				throw new ParameterException("Upload.setImage - Scale");
			}
		}

		thumbnail.execute();
		if(thumbnail.isError() && !outFile.exists()) {
			Log.write("Upload.setImage - Thumbnail: "+thumbnail.getRuntimeCommand()+'\n'+thumbnail.getResult(),Log.SYSTEM);
			throw new ParameterException("Upload.setImage - Thumbnail");
		}


		//add to database
		if(info!=null) {
			pDbfs.setWidth(info.getWidth());
			pDbfs.setHeight(info.getHeight());
			pDbfs.setSize(info.getSize());
		}
		pDbfs.insert();
	}
	
	/**
	 * Returns the name of the file.
	 * 
	 * @param pUser current user
	 * @return Filename of the destination
	 */
	private static String getInternalFilename(String pFilename) {
		String extension=Lib.getExtension(pFilename);
		return extension!=null ? (DateTime.getTimestamp()+'_'+Random.random(10,'a','z')+'.'+extension) : null;		
	}

	/**
	 * Moves a file from <code>pSrc</code> to <code>pDest</code>.
	 * 
	 * @param pSrc source-file
	 * @param pDest	destination-file
	 * @return <code>true</code> if the operation was successfull; otherwise <code>false</code>
	 */
	private static boolean moveFile(File pSrc, File pDest) {
		return (Copy.copy(pSrc,pDest) && Delete.delete(pSrc)); 
	}
	
	/**
	 * Deletes a file from the filesystem.
	 * 
	 * @param pDest file to delete
	 * @return <code>true</code> if the operation was successfull; otherwise <code>false</code>
	 */
	private static boolean deleteFile(File pDest) {
		return Delete.delete(pDest); 
	}
	
	/**
	 * Returns the path of the file.
	 * 
	 * @param pUser current user
	 * @return Path of the destination
	 */
	/*private static String getPath(int pServer, String pUser) {
		if(com.laukien.string.String.isEmpty(pUser)) pUser=gsDefaultuser;
		String path=Server.getPath()+File.separator+Server.buildName(pServer)+File.separator+pUser;
		//remove '/.' from the end if teh defaultuser ist '.'
		if(path.charAt(path.length()-1)=='.') path=path.substring(0,path.length()-2);

		new File(path).mkdirs();
		
		return path;
	}*/
	private static String getPath(int pServer) {
		return Server.getPath()+File.separator+Server.buildName(pServer);
	}

	private static void forward(HttpServletRequest pRequest, HttpServletResponse pResponse, String pMessage, int pType, String pForward) throws ServletException, IOException {
		if(com.laukien.string.String.isEmpty(pMessage) || pType==Message.TYPE_NONE) Message.removeMessage(pRequest);
		else Message.setMessage(pRequest,pMessage, pType);
		if(pForward==null) pForward="";
		
		pRequest.getRequestDispatcher("/"+pForward).forward(pRequest,pResponse);
	}
	
}
