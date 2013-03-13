<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.IO_5.jar" prefix="io" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:text id="i18n" bundle="pixpack"/>
<io:content id="content" />

<%
	String language=i18n.getLanguage();
	String user=com.enlightware.pixpack.Login.getUsername(request);
%>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<jsp:setProperty name="login" property="i18n" value="<%=i18n%>"/>

<jsp:useBean id="msg" class="com.enlightware.pixpack.Message" scope="request"/>
<jsp:setProperty name="msg" property="request" value="<%=request%>"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	
	<%@include file="/head.jsp" %>

	<body id="design_content">
		<div style="margin: 5px;">
			<table>
				<tr>
					<td>
						<a href="http://pixpack.net" title="PixPack">
							<img alt="PixPack" title="PixPack" src="/image/icon/logo64.gif" border="0"/>
						</a>
					</td>
					<td>
<%if(user==null) {%>
						<span class="message_attention"><%=i18n.getText("login.no") %></span>
						<br/>
						<br/>
						<a href="/login.jsp?forward=/index.sidebar" target="_self"><%=i18n.getText("login.on") %></a>
<%} else { %>
						<b><i><%=user %></i></b>
<%} %>
					</td>
				</tr>
			</table>
			<br/>
			<b><%=i18n.getText("slogan") %></b>
			<div class="<jsp:getProperty name="msg" property="messageTypeAsClass"/>" style="text-align: left;">
				<jsp:getProperty name="msg" property="message"/>
			</div>
		
			<jsp:include page="<%="/sidebar/"+content.getFile() %>"/>
		
		</div>
	</body>
</html>