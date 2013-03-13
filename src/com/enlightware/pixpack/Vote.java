package com.enlightware.pixpack;

import java.io.IOException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laukien.bean.database.Database;
import com.enlightware.pixpack.Lib;
import com.enlightware.pixpack.Login;

public class Vote extends HttpServlet {
	private static final long serialVersionUID = 20061207L;

	public void service(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		if(!pRequest.getMethod().equals("POST")) return;
		String id=pRequest.getParameter("id");
		if(id==null || id.length()!=28) return;

		int value;
		try {
			value=Integer.parseInt(pRequest.getParameter("value"));
		} catch(Exception e) {
			return;
		}
		if(value<0 || value>10) return;
		
		int count;
		//registered users got a stronger voice (double; admin->10)
		if(Login.getUser(pRequest)!=null) {
			count=Login.getUser(pRequest).getStatus()+1;
			value=value*(count);
		} else count=1;
		
		String ip=pRequest.getRemoteAddr();

		//database
		Database db=new Database();
		PreparedStatement ps=null;

		try {
			db.setParameter(Lib.getParameter());

			db.connect();
			ps=db.prepareStatement("UPDATE file SET vote_sum=vote_sum+?, vote_count=vote_count+?, vote_ip=?::inet WHERE timestamp=? AND key=? AND vote_ip!=?::inet");
			ps.setInt(1, value);
			ps.setInt(2, count);
			ps.setString(3, ip);
			ps.setString(4, id.substring(0,17));
			ps.setString(5, id.substring(18));
			ps.setString(6, ip);
			ps.executeUpdate();
		} catch(Exception e) {
				//pResponse.getWriter().append("Error:\n"+e);
				return;
		} finally {
			if(ps!=null) {
				try {
					ps.close();
				} catch(Exception e) {
					// do nothing
				}
			}
			if(db!=null) db.close();
		}
		
		//pResponse.getWriter().append("OK");
	}
}
