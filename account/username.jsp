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
	if(account.isSubmitted() && account.changeUsername()) {
		//message
		com.enlightware.pixpack.Message.setMessage(request,i18n.getText("account.username.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);

		//redirect to the right page
		//see "forward"
		request.setAttribute("forward","true");
		if(account.isAutoLogin()) request.getRequestDispatcher("/account.autologin.html").forward(request,response);
		else request.getRequestDispatcher("/account.index.html").forward(request,response);
	}
%>

<h3><%=i18n.getText("account") %> - <%=i18n.getText("account.username") %></h3>
<p>
	<%=i18n.getText("account.username.text") %>
</p>
<form name="account_form" action="account.username.html" method="post">
	<p>
		<%=i18n.getText("login.password") %>
		<br/>
		<input type="password" name="password" class="field" size="64"/>
	</p>
	<p>
		<%=i18n.getText("account.username") %>
		<br/>
		<input type="text" name="name" class="field" size="128"/>
	</p>
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("change") %>"/>
	<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/account.index.html'"/>
</form>
	<script type="text/javascript">
	<!--
		document.account_form.password.focus();
	//-->
	</script>
