<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<jsp:setProperty name="login" property="i18n" value="<%=i18n%>"/>

<%
	//redirect if loged in
	if(login.isPermission()) request.getRequestDispatcher("home.html").forward(request,response);

	if(login.isRegisterLink()) {
		//request.setAttribute("forward","true");
		if(login.registerAndLogin()) {
	request.getRequestDispatcher("home.html").forward(request,response);
		} //else error
		
		return;
	}
%>	
	<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
	<jsp:setProperty name="account" property="request" value="<%=request%>"/>
	<jsp:setProperty name="account" property="i18n" value="<%=i18n%>"/>
<%
	String register=i18n.getLanguage();
	if(!register.equals("de")) register="en";
	register="register_"+register+".jsp";

	if(login.isSubmitted()) {
		if(account.register()) {
			%>
			<jsp:include page="<%=register %>">
				<jsp:param name="step" value="2"/>
			</jsp:include>
			<%
			return;
		} else {
			//error
		}
	} else {
%>
	<jsp:include page="<%=register %>">
		<jsp:param name="step" value="1"/>
	</jsp:include>
<%		
	}
%>
	<form name="register_form" method="post" action="register.html">
		<p>
			<%=i18n.getText("register.mail") %>
			<br/>
			<input type="text" name="mail" size="128" class="field"/>
		</p>
		<hr/>
		<input type="submit" name="submit" value="<%=i18n.getText("register") %>"/>
	</form>
	<script type="text/javascript">
	<!--
		document.register_form.mail.focus();
	//-->
	</script>

			