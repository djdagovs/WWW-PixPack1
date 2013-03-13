<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFolder, com.enlightware.pixpack.DBFSFile" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) return;
	String source=(String)session.getAttribute("fileman.folder");
	if(source==null) return;
%>

<h3><%=i18n.getText("fileman.info") %></h3>
<p><%=i18n.getText("fileman.folder") %>:
<%
	DBFSFolder folder=new DBFSFolder();
	folder.setUsername(login.getUsername());
	folder.setName(source);
	
	if(com.laukien.string.String.isEmpty(source)) {
		out.println("/</p>");
	
		try {
			out.println("<p>"+i18n.getText("fileman.folder.count")+": "+folder.count()+"</p>");
		} catch(Exception e) {
			out.println(i18n.getText("fileman.info.error"));
		}
	} else {
		out.println(source+"</p>");
	}

	try {
		out.println("<p>"+i18n.getText("fileman.folder.size")+": "+((float)(folder.size()/1024))+" kB</p>");
	} catch(Exception e) {
		out.println(i18n.getText("fileman.info.error")+e);
	}

	DBFSFile file=new DBFSFile();
	file.setUsername(login.getUsername());
	try {
		out.println("<p>"+i18n.getText("fileman.file.count")+": "+file.count(source)+"</p>");
	} catch(Exception e) {
		out.println(i18n.getText("fileman.info.error"));
	}
%>
