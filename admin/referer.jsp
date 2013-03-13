<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account,com.enlightware.pixpack.admin.Image"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<jsp:useBean id="referer" class="com.enlightware.pixpack.admin.Referer" scope="request"/>
<jsp:setProperty name="referer" property="request" value="<%=request%>"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);

	//updates the last submission
	referer.update();
%>

<h3><%=i18n.getText("admin.referer") %></h3>

<ul>
	<li><a href="/admin.referer_clean.html"><%=i18n.getText("admin.referer.clean") %></a></li>
</ul>
<table style="width: 100%">
	<tr class="row0">
		<th>
			&nbsp;
		</th>
		<th>
			<a href="?order=id">
				<%=i18n.getText("admin.referer.id") %>
			</a>
		</th>
		<th>
			<a href="?order=count">
				<%=i18n.getText("admin.referer.count") %>
			</a>
		</th>
		<th>
			<a href="?order=name">
				<%=i18n.getText("admin.referer.name") %>
			</a>
		</th>
		<th style="width: 16px">
			&nbsp;
		</th>
	</tr>
	<jsp:getProperty name="referer" property="list"/>
</table>
</form>
