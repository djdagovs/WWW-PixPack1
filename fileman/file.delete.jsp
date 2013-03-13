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

<h3><%=i18n.getText("fileman.file.delete") %></h3>
<p><%=i18n.getText("fileman.file.delete.text") %></p>
<form onsubmit="return dummy()">
	<input type="hidden" id="fileman.name" value="delete"/>
	<button onclick="fileman_file_delete()"><%=i18n.getText("fileman.file.delete") %></button>
</form>

<%
	} else {
		if(source==null || !name.equals("delete")) {
			out.println(i18n.getText("fileman.file.delete.error"));
			return;
		}
		
		DBFSFile file=new DBFSFile();
		file.setUsername(login.getUsername());
		file.setInternalFilename(source);
		file.delete(); 
		
		out.println(i18n.getText("fileman.file.delete.ok"));
	}
%>