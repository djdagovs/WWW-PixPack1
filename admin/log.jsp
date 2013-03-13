<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="log" class="com.enlightware.pixpack.Log" scope="request"/>
<jsp:setProperty name="log" property="request" value="<%=request%>"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()!=Account.SYSTEM_ID && login.getParentId()!=Account.ADMIN_ID) request.getRequestDispatcher("/login.html").forward(request,response);

	log.clean();
%>
<h3><%=i18n.getText("admin.log") %></h3>
<a href="?clean=true"><%=i18n.getText("admin.log.clean") %></a>
<br/>
<br/>
<table style="width: 100%">
	<tr class="row0">
		<th>
			<a href="?order=id">
				<%=i18n.getText("admin.log.id") %>
			</a>
		</th>
		<th>
			<a href="?order=timestamp">
				<%=i18n.getText("admin.log.timestamp") %>
			</a>
		</th>
		<th>
			<a href="?order=message">
				<%=i18n.getText("admin.log.message") %>
			</a>
		</th>
		<th>
			<a href="?order=status">
				<%=i18n.getText("admin.log.status") %>
			</a>
		</th>
	</tr>
	<jsp:getProperty name="log" property="logList"/>
</table>
