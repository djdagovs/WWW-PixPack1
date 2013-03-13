<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="statistic" class="com.enlightware.pixpack.Statistic" scope="request"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()!=Account.SYSTEM_ID && login.getParentId()!=Account.ADMIN_ID) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("admin.statistic") %></h3>
<table style="width: 250px;">
	<tr>
		<th>
			<%=i18n.getText("admin.statistic.user") %>
		</th>
		<td>
			<jsp:getProperty name="statistic" property="userCount"/>
		</td>
	</tr>
	<tr>
		<th>
			<%=i18n.getText("admin.statistic.file") %>
		</th>
		<td>
			<jsp:getProperty name="statistic" property="fileCount"/>
		</td>
	</tr>
	<tr>
		<th>
			<%=i18n.getText("admin.statistic.folder") %>
		</th>
		<td>
			<jsp:getProperty name="statistic" property="folderCount"/>
		</td>
	</tr>
	<tr>
		<th>
			<%=i18n.getText("admin.statistic.view") %>
		</th>
		<td>
			<jsp:getProperty name="statistic" property="viewCount"/>
		</td>
	</tr>
	<tr>
		<th>
			<%=i18n.getText("admin.statistic.session") %>
		</th>
		<td>
			<jsp:getProperty name="statistic" property="sessionCount"/>
		</td>
	</tr>
</table>
