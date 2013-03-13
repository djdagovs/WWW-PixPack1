<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>
<jsp:setProperty name="account" property="i18n" value="<%=i18n%>"/>

<%
	if(account.isSubmitted())
		if(account.delete()) {
			com.enlightware.pixpack.Message.setMessage(request,i18n.getText("account.delete.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
			request.getRequestDispatcher("/home.html").forward(request,response);
		} else com.enlightware.pixpack.Message.setMessage(request,i18n.getText("account.delete.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
%>

<h3><%=i18n.getText("account") %> - <%=i18n.getText("account.delete") %></h3>
<p>
	<%=i18n.getText("account.delete.text") %>
</p>
<form name="account_form" action="/autologin" method="post">
	<input type="hidden" name="page" value="delete"/>
	<input type="hidden" name="autologin" value="false"/>
	<p>
		<%=i18n.getText("login.password") %>
		<br/>
		<input type="password" name="password" class="field" size="64"/>
	</p>
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("delete") %>"/>
	<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/account.index.html'"/>
</form>
	<script type="text/javascript">
	<!--
		document.account_form.password.focus();
	//-->
	</script>
