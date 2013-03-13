package com.enlightware.pixpack.community;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import com.enlightware.pixpack.Lib;
import com.laukien.datetime.Time;
import com.laukien.exception.XMLException;
import com.laukien.string.Convert;
import com.laukien.string.Replace;
import com.laukien.xml.XML;
import com.laukien.xml.XMLParameter;

public class Blog {
	private static final Object LOCK = new Object();

	private static int gsLast;
	private static Hashtable gsResult;
	private static File gsXSLFile;
	
	static {
		init();
	}
	
	private synchronized static void init() {
		gsResult=new Hashtable();
		gsXSLFile=getXSL();
	}
	
	public static String getNews(String pLanguage) throws XMLException {
		int now=new Time().getMinute();
		now=now%6;	//10 minutes
		if(gsLast!=now || gsLast==-1 || gsResult.get(pLanguage)==null) {
			synchronized(LOCK) {
				gsLast=now;
			}
		} else return (String)gsResult.get(pLanguage);
		
		XMLParameter param=new XMLParameter();
		param.setCache(false);
		String content=getContent("http://"+pLanguage+".blog.enlightware.com/feed/");
		try {
			content=Convert.UTF8ToLatin1(content);
		} catch(Exception e) {
			//do nothing
		}
		XML xml=new XML(Replace.replace(content,"&","&amp;"),param);
			
		xml.setResultType(XML.STRING);
		xml.setXSLParameter("language", pLanguage);
		xml.transform(getXSL());
		
		String result=xml.getResultString();
		gsResult.put(pLanguage,result);
		
		return result;
	}

	private static File getXSL() {
		if(gsXSLFile==null) {
			synchronized(LOCK) {
				gsXSLFile=new File(Lib.getPath()+File.separator+"WEB-INF"+File.separator+"community"+File.separatorChar+"news.xsl");
			}
		}
		return gsXSLFile;
	}

	private static String getContent(String pURL) {
		StringBuffer sb=new StringBuffer();
		InputStreamReader isr=null;
		BufferedReader utf8Reader = null;
		try {
			URLConnection con=new URL(pURL).openConnection();
			con.connect();
			isr = new InputStreamReader (con.getInputStream(),"UTF8");
			utf8Reader = new BufferedReader (isr);
			String line;
			while((line=utf8Reader.readLine())!=null) {
				sb.append(line);
			}
		} catch(Exception e) {
			sb.append("<xml></xml>");
			//do nothing
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
		
		
		try {
			return Convert.UTF8ToLatin1(sb.toString());
		} catch (UnsupportedEncodingException e) {
			return sb.toString();
		}
	}
}
