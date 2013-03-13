<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<%
	//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("admin.account.delete")%></h3>
<%
	int id=-1;
	User user=null;
	try {
		id=Integer.parseInt(request.getParameter("id"));
		user=Account.getUserById(id);

		//Check permission
		if(!account.isUserPermission(id) || user==null) {
	com.enlightware.pixpack.Message.setMessage(request,i18n.getText("admin.account.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
	return;
		}
	} catch(Exception e) {
		request.getRequestDispatcher("/admin.account.html").forward(request,response);
	}
	

	if(request.getParameter("action")==null) {
%>
	<table>
		<tr>
			<th><%=i18n.getText("admin.account.id") %></th>
			<td><%=user.getId() %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("admin.account.name") %></th>
			<td><%=user.getName() %></td>
		</tr>
	</table>
	<hr/>
	<form onsubmit="return dummy()">
		<button onclick="document.location.href='admin.account_delete.html?id=<%=id %>&action=true'"><%=i18n.getText("admin.account.delete") %></button>
		<button onclick="document.location.href='/admin.account.html'"><%=i18n.getText("cancel") %></button>
	</form>
<%} else {
	Account.delete(id);
	Forum.delete(user);
	request.getRequestDispatcher("/admin.account.html").forward(request,response);
} %>