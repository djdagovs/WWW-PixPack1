<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFolder"  %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) return;
	String name=(String)request.getParameter("content");
	if(name!=null) name=name.trim();
	String source=(String)session.getAttribute("fileman.folder");
	if(source==null) return;
	
	if(com.laukien.string.String.isEmpty(name)) {
%>

<h3><%=i18n.getText("fileman.folder.status") %></h3>
<p><%=i18n.getText("fileman.folder.status.text") %></p>
<form onsubmit="return dummy()">
	<p>
		<input type="checkbox" name="public" id="fileman.status.public" checked=\"checked\"/>
		<%=i18n.getText("fileman.folder.status.public") %>
	</p>
	<p>
		<input type="checkbox" name="adult" id="fileman.status.adult"/>
		<%=i18n.getText("fileman.folder.status.adult") %>
	</p>
	<button onclick="fileman_folder_status()"><%=i18n.getText("fileman.folder.status") %></button>
</form>

<%
} else {
	if(name.length()!=2) {
		out.println(i18n.getText("fileman.folder.status.error"));
		return;
	}
	
	boolean isPublic, isAdult;
	
	isPublic=name.substring(0,1).equals("1");
	isAdult=name.substring(1,2).equals("1");
	
	DBFSFolder folder=new DBFSFolder();
	folder.setUsername(login.getUsername());
	folder.setName(source);
	folder.status(isPublic, isAdult);
	
	out.println(i18n.getText("fileman.folder.status.ok"));
}
%>