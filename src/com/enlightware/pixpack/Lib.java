package com.enlightware.pixpack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.laukien.array.Add;
import com.laukien.bean.database.Database;
import com.laukien.bean.database.Parameter;
import com.laukien.bean.magick.Config;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Login;
import com.enlightware.pixpack.Root;
import com.enlightware.pixpack.User;
import com.laukien.datetime.DateTime;
import com.laukien.exception.ParameterException;
import com.laukien.string.Random;
import com.laukien.string.Remove;
import com.laukien.string.Replace;
import com.laukien.taglib.i18n.Text;

public class Lib extends Root {
	public static final int MESSAGE_TYPE_NONE = -1;
	public static final int MESSAGE_TYPE_INFORMATION = 1;
	public static final int MESSAGE_TYPE_WARNING = 2;
	public static final int MESSAGE_TYPE_ERROR = 3;
	private static String gsPath;

	static String[] gsExtensions;
	private static String gsUploadFormat;
	private static Parameter gsParameter;
	
	static {
		init();
	}
	
	private synchronized static void init() {
		//Path
		String path=Lib.class.getResource("").toExternalForm();

		if(path.startsWith("file:")) path=path.substring(5);

		int pos=path.indexOf("/WEB-INF");
		if(pos==-1) throw new RuntimeException("Lib.init: Unable to get the path\n"+path);
		gsPath=path.substring(0,pos);

		//Magick
		try {
			Config.load(new File(gsPath+File.separator+"WEB-INF"+File.separator+"magick.properties"));
		} catch (Exception e) {
			throw new ParameterException("Lib.init: Unable to load Magick-Config\n"+e);
		}
		
		//Extension and "upload.format"
		String[] extensions=new String[]{};
		StringBuffer sb=new StringBuffer();
		
		Database db=new Database();
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try {
			gsParameter=new Parameter();
			synchronized(gsParameter) {
				gsParameter.load(gsPath+File.separator+"WEB-INF"+File.separator+"pixpack.db");
			}

			db.setParameter(gsParameter);
			
			//build query
			String query="SELECT name, mimetype, description FROM extension ORDER BY name ASC";
			db.connect();

			
			//check if the user exists
			ps=db.prepareStatement(query);
			rs=ps.executeQuery();
			if(rs==null) throw new ParameterException("Lib.init: No result");
			
			while(rs.next()) {
				//extension-array
				extensions=(String[])Add.value(String.class,extensions,rs.getString("name"));
				//upload.format
				sb.append("<span title=\""+rs.getString("description")+" ("+rs.getString("mimetype")+")\">"+rs.getString("name")+"</span>, ");
			}
			
		} catch(Exception e) {
			throw new RuntimeException("Lib.init: Error while accessing the database\n"+e);
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
		
		synchronized(Lib.class) {
			gsExtensions=extensions;
			gsUploadFormat=sb.substring(0,sb.length()-2);	//remove the last commata
		}
	}
	
	public Lib() {
		super();
	}
	
	/**
	 * Returns the database-parameters for the common database-connection.
	 * 
	 * @return
	 */
	public static Parameter getParameter() {
		return gsParameter;
	}
	
	/**
	 * Gets the users name or <code>null</code> if there is no loged in user.
	 * 
	 * @param request request-parameter
	 * @return name of the user
	 */
	public static String getUsername(HttpServletRequest pRequest) {
		Login login=new Login();
		login.setRequest(pRequest);
		return login.getUsername();
	}
	
	public String getUsername() {
		return getUsername(gRequest);
	}
	
	/**
	 * Gets the users unique mailaddress or <code>null</code> if there is no loged in user.
	 * 
	 * @param request request-parameter
	 * @return mail of the user
	 */
	public static String getUsermail(HttpServletRequest pRequest) {
		Login login=new Login();
		login.setRequest(pRequest);
		User user=login.getUser();
		if(user==null) return null;
		
		return user.getMail();
	}
	
	public String getUsermail() {
		return getUsername(gRequest);
	}
	
	public static final String getPath() {
		return gsPath;
	}
	
	public static String getUniqueKey() {
		return DateTime.getTimestamp()+'_'+Random.random(10,'a','z');
	}

	/**
	 * Returns a list of all supported formats.
	 * 
	 * @return HTML-List
	 */
	public static String getUploadFormat() {
		return gsUploadFormat;
	}
	/**
	 * Checks if the file is supported by the libraries.
	 * 
	 * @param pExtension only the extension of the file
	 * @return <code>true</code> if the extension is supported; otherwise <code>false</code>
	 */
	public static boolean isValidExtension(String pExtension) {
		//most common files
		if(isWebExtension(pExtension)) return true;
		
		try {
			//only 3 letters
			pExtension=pExtension.substring(0,3);
		} catch(Exception e) {
			//if the extension is NULL or has not 3 characters
			return false;
		}
		int length=gsExtensions.length;
		for(int i=0; i<length; i++) {
			if(gsExtensions[i].equals(pExtension)) return true;
		}
		
		return false;
	}
	
	/**
	 * Checks the file-extension is showable via a standard webbrowser.
	 * 
	 * @param pExtension only the extension of the filename
	 * @return <code>true</code> if the extension is supported; otherwise <code>false</code>
	 */
	public static boolean isWebExtension(String pExtension) {
		return (pExtension!=null && (
				pExtension.equals("jpg") ||
				pExtension.equals("png") ||
				pExtension.equals("gif")));
	}
	
	/**
	 * Returns the extension of the filename.
	 * 
	 * @param pFilename complete filename
	 * @return the extension or <code>null</code> if there is no extension
	 */
	public static String getExtension(String pFilename) {
		int dot=pFilename.lastIndexOf('.');
		return dot>0 ? pFilename.substring(dot+1).toLowerCase() : null;
	}

	/**
	 * Removes the extension (like .jpg)
	 * 
	 * @param pFilename complete filename
	 * @return filename without its extension
	 */
	public static String removeExtension(String pFilename) {
		int pos=pFilename.lastIndexOf('.');
		if(pos==-1) return pFilename;
		return pFilename.substring(0,pos);
	}

	public static String extractFilename(String pFile) {
		int pos=pFile.lastIndexOf('/');
		if(pos!=-1) pFile.substring(pos+1);
		else pos=pFile.lastIndexOf('\\');
		if(pos!=-1) pFile.substring(pos+1);

		return pFile;
	}

	public static String[] keys2Array(String pKeys) {
		StringTokenizer st=new StringTokenizer(pKeys,"|");
		int count=st.countTokens();
		String[] array=new String[count];
		
		for(int i=0; i<count; i++) {
			array[0]=st.nextToken();			
		}
		return array;
	}
	
	public static String removeQuote(String pString) {
		return Replace.replace(pString, "\"", "&quot;");
	}

	public static String addQuote(String pString) {
		return Replace.replace(pString, "&quot;", "\"");
	}
	
	public static String deleteQuote(String pString) {
		return Remove.deleteChar(pString, '"');
	}

	public static Text getI18n(HttpServletRequest pRequest) {
		return (Text)pRequest.getSession().getAttribute("i18n.i18n");
	}

	public static void setI18n(HttpServletRequest pRequest, Text pI18n) {
		pRequest.getSession().setAttribute("i18n.i18n",pI18n);
	}

	public static String getContent(String pURL) {
		StringBuffer sb=new StringBuffer();
		InputStreamReader isr=null;
		BufferedReader utf8Reader = null;
		try {
			URLConnection con=new URL(pURL).openConnection();
			con.connect();
			isr = new InputStreamReader (con.getInputStream(),System.getProperty("sun.jnu.encoding"));
			utf8Reader = new BufferedReader (isr);
			String line;
			while((line=utf8Reader.readLine())!=null) {
				sb.append(line+'\n');
			}
		} catch(Exception e) {
			return null;
		} finally {
			if(utf8Reader!=null)
				try {
					utf8Reader.close();
				} catch (IOException e) {
					//do nothing
				}
			if(isr!=null)
				try {
					isr.close();
				} catch (IOException e) {
					//do nothing
				}
		}
		
		//Convert for fucking Linux-plattforms
		
		return sb.toString();
	}
	
	public static String next(int pCount, int pOffset, int pLimit, String pPrefix, String pSuffix) {
		if(pCount<=pLimit) return "";

		if(pPrefix==null) pPrefix="?offset=";
		if(pSuffix==null) pSuffix="";
		StringBuilder sb=new StringBuilder();
		sb.append("<div class=\"next\">");
		
		if(pOffset>=pLimit) sb.append("<a href=\""+pPrefix+(pOffset-pLimit)+pSuffix+"\"><b>&lt;&lt;</b></a>&#160;");
		int start=(pOffset/pLimit)+1;
		int count=pCount/pLimit;
		//if more than count*limit e.g 12
		if(pCount%pLimit!=0) count++;

		int i=1;
		if(start>5) i=start-5;
		if(start<count+5 && count>start+5) count=start+5;
		
		while(i<=count) {
			if(start!=i) sb.append("<a href=\""+pPrefix+(i*pLimit-pLimit)+pSuffix+"\">"+i+"</a>&#160;");
			else sb.append("<b>"+i+"</b>&#160;");
			i++;
		}
		
		if((pOffset+pLimit)<=pCount) sb.append("<a href=\""+pPrefix+(pOffset+pLimit)+pSuffix+"\"><b>&gt;&gt;</b></a>");
		sb.append("</div>");
		
		return sb.toString();
	}
}
