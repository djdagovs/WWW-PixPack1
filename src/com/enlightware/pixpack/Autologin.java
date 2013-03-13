package com.enlightware.pixpack;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enlightware.pixpack.Account;

public class Autologin extends HttpServlet {
	private static final long serialVersionUID = 20060823L;

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		Account account=new Account();
		account.setRequest(pRequest);
		account.setResponse(pResponse);
		
		String page=pRequest.getParameter("page");
		if(com.laukien.string.String.isEmpty(page)) page="index";
		
		if(!account.isSubmitted()) pRequest.getRequestDispatcher("/account.index.html").forward(pRequest,pResponse);
		else {
			if(account.autologin()) pRequest.getRequestDispatcher("/account."+page+".html").forward(pRequest,pResponse);
			else pRequest.getRequestDispatcher("/index.html").forward(pRequest,pResponse);	//session-user invalid
		}
	}
}
