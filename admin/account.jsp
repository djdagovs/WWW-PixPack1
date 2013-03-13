<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.admin.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("admin.account") %></h3>
<table style="width: 100%">
	<tr class="row0">
		<th>
			<a href="?order=id">
				<%=i18n.getText("admin.account.id") %>
			</a>
		</th>
		<th>
			<a href="?order=name">
				<%=i18n.getText("admin.account.name") %>
			</a>
		</th>
		<th>
			<a href="?order=last">
				<%=i18n.getText("admin.account.last") %>
			</a>
		</th>
		<th>
			<a href="?order=status">
				<%=i18n.getText("admin.account.status") %>
			</a>
		</th>
		<th>
			<%=i18n.getText("admin.account.option") %>
		</th>
	</tr>
	<jsp:getProperty name="account" property="list"/>
</table>
