package com.enlightware.pixpack.xsl;

import javax.servlet.http.HttpServletRequest;

import com.enlightware.pixpack.Server;
import com.laukien.taglib.i18n.Text;

public class Gallery {
	private static final String PREFIX=Server.getRoot()+"/gallery.inlet?";
	
	public static String getURL() {
		return Server.getURL();
	}
	
	public static String getTimestamp(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getTimestamp();
	}

	public static String getKey(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getKey();
	}
	
	public static int getPosition(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getPosition();
	}
	
	public static int getLast(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getLast();
	}
	
	public static String getTemplate(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getTemplate();
	}
	
	public static String getStyle(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getStyle();
	}
	
	public static String getUser(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getUser();
	}
	
	public static String getFolder(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getFolder();
	}
	
	public static String getTitle(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getTitle();
	}
	
	public static String getDescription(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getDescription();
	}
	
	public static int getCount(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getCount();
	}

	public static int getCol(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getCol();
	}
	
	public static int getRow(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getRow();
	}
	
	
	public static String getCSS(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getCSS();
	}

	public static boolean isAdvertisement(Object pGallery) {
		return ((com.enlightware.pixpack.Gallery)pGallery).getRequest()==null;
	}

	public static String page(Object pGallery) {
		int col=getCol(pGallery);
		int row=getRow(pGallery);
		if(col==-1 || row==-1) return "";	//no further page
		
		int max=col*row;
		int position=getPosition(pGallery);
		int last=getLast(pGallery);
		if(last<=max) return "";

		String timestamp=getTimestamp(pGallery);
		String key=getKey(pGallery);
		String css=getCSS(pGallery);
		

		//text and its translation
		String prev, next;
		String prefix;
		HttpServletRequest request=((com.enlightware.pixpack.Gallery)pGallery).getRequest();
		if(request==null) {
			prefix=PREFIX;
			prev="<";
			next=">";
		} else {
			prefix=request.getRequestURL().toString();
			prefix=(prefix.indexOf('?')==-1 ? "?" : "&");
			
			Text i18n=((com.enlightware.pixpack.Gallery)pGallery).getI18n();
			if(i18n!=null) {
				prev=i18n.getText("gallery.previous");
				next=i18n.getText("gallery.next");
			} else {
				prev="<";
				next=">";
			}
		}
		
		
		String result="<div class=\"gallery_page\">";
		if(position>1) result+="<a href=\""+convertQuery(prefix, position-max, timestamp, key, css)+"\"><b>"+prev+"</b></a>&#160;";
		int start=(position/max);
		int count=last/max;
		//if more than count*max e.g 12
		if(last%max!=0) count++;

		int i=1;
		if(start>3) i=start-3;
		if(start<count+3 && count>start+3) count=start+3;
		
		while(i<=count) {
			if(start!=(i-1)) result+="<a href=\""+convertQuery(prefix, i*max-max+1, timestamp, key, css)+"\">"+i+"</a>&#160;";
			else result+="<b>"+i+"</b>&#160;";
			i++;
		}
		
		if((position+max)<=last) result+="<a href=\""+convertQuery(prefix, position+max, timestamp, key, css)+"\"><b>"+next+"</b></a>";
		result+="</div>";
		
		return result;
	}
	
	private static String convertQuery(String pPrefix, int pPosition, String pTimestamp, String pKey, String pCss) {
		return pPrefix+"timestamp="+pTimestamp+"&amp;key="+pKey+"&amp;position="+pPosition+(pCss!=null ? "&amp;css="+pCss : "");
	}
}
