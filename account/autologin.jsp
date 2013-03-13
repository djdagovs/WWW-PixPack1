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

<%
	if(account.isSubmitted()) {
		com.enlightware.pixpack.Message.setMessage(request,i18n.getText("account.autologin.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
		request.getRequestDispatcher("/account.index.html").forward(request,response);
	}
%>

<h3><%=i18n.getText("account") %> - <%=i18n.getText("account.autologin") %></h3>
<p>
	<%=i18n.getText("account.autologin.text") %>
</p>
<form name="account_form" action="/autologin" method="post">
	<input type="hidden" name="page" value="autologin"/>
	<p>
		<%=i18n.getText("account.autologin") %>
		<input type="checkbox" name="autologin" value="true" <jsp:getProperty name="account" property="autoLoginAttribute"/>/>
	</p>
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("change") %>"/>
	<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/account.index.html'"/>
</form>
	<script type="text/javascript">
	<!--
		document.account_form.autologin.focus();
	//-->
	</script>
