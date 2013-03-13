<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<jsp:setProperty name="login" property="i18n" value="<%=i18n%>"/>

<%
	String forward=request.getParameter("forward");
	if(forward==null) forward="/";
	
	if(login.isSubmitted()) {
		if(login.access()) {
			request.getRequestDispatcher(forward).forward(request,response);
		}
	}
%>
	<form name="login_form" method="post" action="/login.html">
		<input type="hidden" name="forward" value="<%=forward %>"/>
		<p>
			<%=i18n.getText("login.name") %>
			<br/>
			<input type="text" name="name" value="<%=request.getParameter("name")==null ? "" :  request.getParameter("name") %>"/>
		</p>
		<p>
			<%=i18n.getText("login.password") %>
			<br/>
			<input type="password" name="password"/>
		</p>
		<hr/>
		<input type="submit" name="submit" value="<%=i18n.getText("login.submit") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/home.html'"/>
	</form>
	<script type="text/javascript">
	<!--
		document.login_form.name.focus();
	//-->
	</script>
	<br/>
	<a href="/register.html"><%=i18n.getText("register") %></a>
	<br/>
	<a href="/sendpassword.html"><%=i18n.getText("login.lost") %></a>

			