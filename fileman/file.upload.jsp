<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFile"  %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) return;
	String name=(String)request.getParameter("content");
	if(name!=null) name=name.trim();
	String source=(String)session.getAttribute("fileman.file");

	if(com.laukien.string.String.isEmpty(name)) {
%>

<h3><%=i18n.getText("fileman.file.upload") %></h3>
<p><%=i18n.getText("fileman.file.upload.text") %></p>
<!--
<form onsubmit="return dummy()">
	<input type="hidden" id="fileman.name" value="delete"/>
	<button onclick="fileman_file_delete()"><%=i18n.getText("fileman.file.delete") %></button>
</form>
-->
<b>Not implemented yet!</b><br/>Use the default upload option.
<%
	} else {
	
	}
%>