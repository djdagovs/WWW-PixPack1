<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("account") %></h3>
<p><%=i18n.getText("account.text") %></p>
<ul>
<!--
	<li>
		<a href="/account.update.html"><%=i18n.getText("account.update") %></a>
	</li>
-->
	<li>
		<a href="/account.username.html"><%=i18n.getText("account.username") %></a>
	</li>
	<li>
		<a href="/account.password.html"><%=i18n.getText("account.password") %></a>
	</li>
	<li>
		<a href="/account.autologin.html"><%=i18n.getText("account.autologin") %></a>
	</li>
	<li>
		<a href="/sendpassword.html"><%=i18n.getText("login.lost") %></a>
	</li>	
	<li>
		<a href="/account.delete.html"><%=i18n.getText("account.delete") %></a>
	</li>
</ul>	