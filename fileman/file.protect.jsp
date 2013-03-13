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
	if(source==null) return;
	
	if(com.laukien.string.String.isEmpty(name)) {
		DBFSFile file=new DBFSFile();
		file.setInternalFilename(source);
		file.setUsername(login.getUsername());
		file.select();
		
		String protect=DBFSFile.correctProtect(file.getProtect(),' ');
%>

<h3><%=i18n.getText("fileman.file.protect") %></h3>
<p><%=i18n.getText("fileman.file.protect.text") %></p>
<form onsubmit="return dummy()">
	<input type="text" id="fileman.name" size="248" class="field" value="<%=protect %>"/>
	<button onclick="fileman_file_protect()"><%=i18n.getText("fileman.file.protect") %></button>
</form>

<%
} else {
	DBFSFile file=new DBFSFile();
	file.setUsername(login.getUsername());
	file.setInternalFilename(source);
	file.protect(name);
	
	out.println(i18n.getText("fileman.file.protect.ok"));
}
%>