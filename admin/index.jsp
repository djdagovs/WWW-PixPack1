<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
//login.dummy();
	if(login.getParentId()!=Account.SYSTEM_ID && login.getParentId()!=Account.ADMIN_ID) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("admin") %></h3>
<p><%=i18n.getText("admin.text") %></p>
