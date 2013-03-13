package com.enlightware.pixpack;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laukien.array.Contains;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Message;
import com.laukien.i18n.Translate;
import com.laukien.taglib.i18n.Text;
/**
 * Redirecter - Will be used if PixPack got a database backend.
 * 
 * @author Stephan Laukien
 */
public class Language extends HttpServlet {
	private static final long serialVersionUID = 20060904L;
	private static final String[] LANGUAGES=new String[] {"en","de"};

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		Text i18n=Lib.getI18n(pRequest);
		if(i18n==null) return;
		
		String lang=pRequest.getParameter("language");
		if(lang==null || !Contains.value(LANGUAGES,lang)) return;
		
		//set the new language
		i18n.setLanguage(lang);

		//setCookie
		Cookie cookie;
		cookie=new Cookie("language",lang);
		cookie.setMaxAge(31536000);	//one year
		pResponse.addCookie(cookie);
		Lib.setI18n(pRequest,i18n);
		
		//message and reload
		Message.setMessage(pRequest,i18n.getText("language.ok"),Message.TYPE_INFORMATION);
		pRequest.getRequestDispatcher("language.html").forward(pRequest,pResponse);
	}

	public static String createMenu(Text pI18n) throws IOException {
		StringBuffer sb=new StringBuffer();
		for(int i=0; i<LANGUAGES.length; i++) {
			sb.append("<p onclick=\"setLanguage('"+LANGUAGES[i]+"')\">");
			sb.append("<input type=\"radio\" name=\"language\" value=\""+LANGUAGES[i]+"\"/>");
			sb.append("<img alt=\""+Translate.getLanguage(new Locale(LANGUAGES[i]),pI18n.getLocale())+"\" src=\"image/flag/"+LANGUAGES[i]+".gif\"/>");
			sb.append("&#160;"+Translate.getLanguage(new Locale(LANGUAGES[i]),pI18n.getLocale()));
			sb.append("</p>");
			
		}
		return sb.toString();
	}
	
	public void destroy() {
		
	}
}
