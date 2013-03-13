package com.enlightware.pixpack.community;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.enlightware.pixpack.Lib;
import com.laukien.string.Convert;

public class News {

	private static String gsNew;
	private static int gsNewTime;

	private String gLanguage;

	static {
		init();
	}
	
	private synchronized static void init() {
		gsNew=null;
		gsNewTime=-1;
	}
	
	public static void reload() {
		init();
	}

	
	public News() {
		gLanguage=Locale.US.getLanguage();
	}
	
	public void setLanguage(String pLanguage) {
		gLanguage=pLanguage==null ? Locale.US.getLanguage() : pLanguage;
	}
	
	public String getTip() {
		int day=new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
		if(day!=gsNewTime) {
			gsNewTime=day;
			
			String content=Lib.getContent("http://a-z.joetoe.com/api/new?count=1&type=html&language="+gLanguage);
			
			int idx=content.indexOf("<enlightware project=\"a-z\"");
			if(idx==-1) return "";
			content=content.substring(content.indexOf('>', idx)+1,content.lastIndexOf('<'));

			try {
				if(System.getProperty("sun.jnu.encoding").toLowerCase().indexOf("utf")==-1)	content=Convert.latin1ToUTF8(content);
			} catch (Exception e) {
				//do nothing
			}
			
			//add target
			content="<a target=\"_blank\""+content.substring(2);
			
			synchronized(News.class) {
				gsNew=content;
			}
		}
		
		return gsNew;
	}
}
