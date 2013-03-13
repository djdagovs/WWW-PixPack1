<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account,com.enlightware.pixpack.admin.Image"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<jsp:useBean id="referer" class="com.enlightware.pixpack.admin.Referer" scope="request"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);

	//updates the last submission
	String id=request.getParameter("id");
	if(id==null) {
		out.print("Invalid ID");
		return;
	}
	
	try {
		String visible=request.getParameter("visible");
		if(visible==null) referer.read(Integer.parseInt(id));
		else {
			referer.visible(Integer.parseInt(id), visible.equals("false") ? false : true);
		}
	} catch(Exception e) {
		out.print("Invalid ID");
		return;
	}
%>

<h3><%=i18n.getText("admin.referer.show") %></h3>
<a href="/admin.referer.html"><%=i18n.getText("admin.referer.clean") %></a>
<br/>
<table>
	<tr>
		<th><%=i18n.getText("admin.referer.id") %>: </th>
		<td><jsp:getProperty name="referer" property="id"/></td>
	</tr>
	<tr>
		<th><%=i18n.getText("admin.referer.count") %>: </th>
		<td><jsp:getProperty name="referer" property="count"/></td>
	</tr>
	<tr>
		<th><%=i18n.getText("admin.referer.name") %>: </th>
		<td><jsp:getProperty name="referer" property="name"/></td>
	</tr>
</table>

<jsp:getProperty name="referer" property="gallery"/>
		
<form method="post" action="/admin.referer_show.html">
	<input type="hidden" name="id" value="<%=id %>"/>
	<div style="text-align: center">
		<%=i18n.getText("admin.referer.visible") %>
		<input type="radio" name="visible" value="true"/><%=i18n.getText("true") %>
		<input type="radio" name="visible" value="false"/><%=i18n.getText("false") %>
		<br/>
		<input type="submit"/>
	</div>
</form>
