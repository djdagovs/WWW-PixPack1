package com.enlightware.pixpack.community;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.laukien.bean.feedreader.FeedReader;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Log;
import com.laukien.exception.XMLException;
import com.laukien.math.Random;
import com.laukien.string.Convert;
import com.laukien.string.Replace;
import com.laukien.xml.XML;

public class Communication {

	private static String gsNew;
	private static int gsNewTime;
	private static Hashtable gsText;
	private static int gsTextTime;
	
	private String gLanguage;

	static {
		init();
	}
	
	private synchronized static void init() {
		gsNew=null;
		gsNewTime=-1;
		gsText=new Hashtable();
		gsTextTime=-1;
	}
	
	public static void reload() {
		init();
		FeedReader.reload();
	}
	
	public Communication() {
		gLanguage="de";
	}
	
	
	public void setLanguage(String pLanguage) {
		gLanguage=pLanguage==null || !pLanguage.equals("de") ? "en" : "de";
	}

	public String getRandom() {
		String content=Lib.getContent("http://a-z.joetoe.com/api/random?count=5&type=html&except=pixpack&language="+gLanguage);
		
		int idx=content.indexOf("<enlightware project=\"a-z\"");
		if(idx==-1) return "";
		content=content.substring(content.indexOf('>', idx)+1,content.lastIndexOf('<'));
		
		try {
			if(System.getProperty("sun.jnu.encoding").toLowerCase().indexOf("utf")==-1)	content=Convert.latin1ToUTF8(content);
		} catch (Exception e) {
			//do nothing
		}
		
		content=Replace.replace(content,"<a ", "<a target=\"_blank\" ");
		return content;
	}

	public String getNew() {
		int day=new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
		if(day!=gsNewTime) {
			gsNewTime=day;
			
			String content=Lib.getContent("http://a-z.joetoe.com/api/new?count=1&type=html");
			
			int idx=content.indexOf("<enlightware project=\"a-z\"");
			if(idx==-1) return "";
			content=content.substring(content.indexOf('>', idx)+1,content.lastIndexOf('<'));

			try {
				if(System.getProperty("sun.jnu.encoding").toLowerCase().indexOf("utf")==-1)	content=Convert.latin1ToUTF8(content);
			} catch (Exception e) {
				//do nothing
			}
			content=Replace.replace(content,"<a ", "<a target=\"_blank\" ");
			
			synchronized(Communication.class) {
				gsNew=content;
			}
		}
		
		return gsNew;
	}
	
	public String getText() {
		int day=new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
		if(day!=gsTextTime) {
			gsTextTime=day;
			
			try {
				String quoteDE=loadText("afeni.net","Die gr&ouml;&szlig;te deutsche Zitatsammlung");
				String sayingDE=loadText("spruch.joetoe.com", "Spr&uuml;che f&uuml;r jede Gelegenheit");
				String jokeDE=loadText("witz.joetoe.com", "Eine riesige Sammlung an Witzen");
				String quoteEN=loadText("quote.joetoe.com", "The world largest collection of english quotes");
				String sayingEN=loadText("saying.joetoe.com","Proverbs for each opportunity");
				String jokeEN=loadText("joke.joetoe.com","A giant collection of jokes");

				synchronized(gsText) {
					gsText.clear();
					gsText.put("de0", quoteDE);
					gsText.put("de1", sayingDE);
					gsText.put("de2", jokeDE);
					gsText.put("en0", quoteEN);
					gsText.put("en1", sayingEN);
					gsText.put("en2", jokeEN);
				}
				
				Log.write("Communication - Load Text: Success",Log.INFORMATION);
			} catch(Exception e) {
				Log.write("Communication - Load Text: Error\n"+e,Log.SYSTEM);
			}
			
		}
		
		int rnd=Random.randomInt(0, 3);
		return (String)gsText.get(gLanguage+rnd);
	}

	private String loadText(String url, String desc) throws XMLException {
		String content=Replace.replace(Lib.getContent("http://"+url+"/api/random?_id=pixpack"),"&","&amp;");
		XML xml=new XML(content);
		String text=xml.getElementsByTagName("text").item(0).getFirstChild().getNodeValue();
		String link=xml.getElementsByTagName("url").item(0).getFirstChild().getNodeValue();
		
		//cut the text
		StringTokenizer st=new StringTokenizer(text,"\n");
		text="";
		for(int i=0; i<=3 && st.hasMoreTokens(); i++) {
			text+=st.nextToken()+"<br/>";
		}
		text=text.substring(0,text.length()-5);
		if(st.hasMoreTokens()) text+="...";
		
		//build result
		StringBuilder sb=new StringBuilder();
		sb.append("<div class=\"text\">");
		sb.append("<a href=\""+link+"\" target=\"_blank\">"+text+"</a>");
		sb.append("</div>");
		sb.append("<div class=\"link\">");
		sb.append("<a href=\"http://"+url+"\" onclick=\"return open_link('http://"+url+"')\"><span class=\"small\">"+desc+"</span>&nbsp;&nbsp;&nbsp;<b>"+url+"</b></a>");
		sb.append("</div>");
		
		return sb.toString();
	}
}