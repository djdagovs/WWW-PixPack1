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

	if(com.laukien.string.String.isEmpty(name)) {
%>

<h3><%=i18n.getText("fileman.folder.rename") %></h3>
<p><%=i18n.getText("fileman.folder.rename.text") %></p>
<form onsubmit="return dummy()">
	<input type="text" id="fileman.name" size="64" class="field" value="<%=source==null ? "" : source %>"/>
	<button onclick="fileman_folder_rename()"><%=i18n.getText("fileman.folder.rename") %></button>
</form>

<%
} else {
	if(source==null) {
		out.println(i18n.getText("fileman.folder.rename.error"));
		return;
	}
	
	DBFSFolder folder=new DBFSFolder();
	folder.setUsername(login.getUsername());
	folder.setName(source);
	try {
		if(folder.exists() && folder.rename(name)) out.println(i18n.getText("fileman.folder.rename.ok"));
		else out.println(i18n.getText("fileman.folder.rename.error"));
	} catch(Exception e) {
		out.println(i18n.getText("fileman.folder.rename.error"));
	}
}
%>