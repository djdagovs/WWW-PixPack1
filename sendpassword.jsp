<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>
<jsp:setProperty name="account" property="i18n" value="<%=i18n%>"/>

<%
	if(account.isSubmitted()) {
		if(account.sendPassword()) {
			request.getRequestDispatcher("login.html").forward(request,response);
		}
	}
%>
	<h1><%=i18n.getText("login.lost") %></h1>
	<p>
		<%=i18n.getText("login.lost.text") %>
	</p>
	<form name="sendpassword_form" method="post" action="sendpassword.html">
		<input type="text" name="name"/>
		<hr/>
		<input type="submit" name="submit" value="<%=i18n.getText("login.lost.submit") %>"/>
	</form>
	<script type="text/javascript">
	<!--
		document.sendpassword_form.name.focus();
	//-->
	</script>
