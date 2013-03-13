<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account,com.enlightware.pixpack.admin.Image"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<jsp:useBean id="image" class="com.enlightware.pixpack.admin.Image" scope="request"/>
<jsp:setProperty name="image" property="request" value="<%=request%>"/>
<jsp:setProperty name="image" property="edit" value="true"/>
<jsp:setProperty name="image" property="anonymous" value="true"/>

<%
//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);

	//updates the last submission
	image.update();
%>
<script>
	function admin_image_status(id, status) {
		document.getElementById(id).value=status;
	}
</script>

<h3><%=i18n.getText("admin.image") %></h3>

<form action="/admin.image.html" method="post">
	<jsp:getProperty name="image" property="list"/>
	
	<div style="text-align: center">
		<input type="submit"/>
	</div>
</form>
