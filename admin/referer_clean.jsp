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

	referer.clean();
%>

<h3><%=i18n.getText("admin.referer.clean") %></h3>
<ul>
	<li><a href="/admin.referer.html"><%=i18n.getText("admin.referer") %></a></li>
</ul>
<br/>