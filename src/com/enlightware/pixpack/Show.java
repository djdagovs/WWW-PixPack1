package com.enlightware.pixpack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Show extends HttpServlet {
	
	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		String timestamp;
		try {
			timestamp=pRequest.getRequestURI().substring(6);
			if(timestamp.charAt(timestamp.length()-1)=='/') timestamp=timestamp.substring(0,timestamp.length()-1);
		} catch(Exception e) {
			pRequest.getRequestDispatcher("/show.html").forward(pRequest, pResponse);
			return;
		}
		
		try {
			String key=null;
			String user=null;

			//key
			int idx=timestamp.indexOf('/');
			key=timestamp.substring(idx+1);
			timestamp=timestamp.substring(0,idx);
			//user
			idx=key.indexOf('/');
			if(idx!=-1) {
				user=key.substring(idx+1);
				key=key.substring(0,idx);
			} else user=null;
			
			pRequest.setAttribute("timestamp", timestamp);
			pRequest.setAttribute("key", key);
			if(user!=null) pRequest.setAttribute("user", user);

			pRequest.getRequestDispatcher("/show.html").forward(pRequest, pResponse);
		} catch(Exception e) {
			pRequest.getRequestDispatcher("/home.html").forward(pRequest, pResponse);
		}
	}
}