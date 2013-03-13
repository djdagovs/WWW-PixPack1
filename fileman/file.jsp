<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.enlightware.pixpack.DBFSFile, com.enlightware.pixpack.Server" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>

<%
	if(!login.isPermission() || request.getAttribute("forward")==null || session.isNew()) {
		out.println("<p>"+i18n.getText("login.time")+"</p><a href=\"/login.html\">"+i18n.getText("login.on")+"</a>");
		return;
	}
	
	String content=request.getParameter("content");

	String image="image/error.gif";
	String thumbnail=image;
	String error=null;
	DBFSFile dbfs=new DBFSFile();
	
	if(com.laukien.string.String.isEmpty(content)) {
		error=i18n.getText("show.error")+": Invalid Format";
	} else {
		 dbfs.setInternalFilename(login.getUsername()+':'+content);

		 try {
			 dbfs.select();
			 
			 //image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+'/'+dbfs.getUsername()+'/'+dbfs.getTimestamp()+'_'+dbfs.getKey()+'.'+dbfs.getExtension().getName();
			 image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+'/'+dbfs.getTimestamp()+'_'+dbfs.getKey()+'.'+dbfs.getExtension().getName();
			 thumbnail="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+"/_"+dbfs.getTimestamp()+'_'+dbfs.getKey()+'.'+dbfs.getExtension().getName();
		 } catch(Exception e) {
			 error=i18n.getText("show.error");
		 }
		 
		if(error!=null) {
			out.println("<h3>"+i18n.getText("fileman.file")+" - "+content+"</h3>");
			out.println("<p class=\"message\">"+com.laukien.string.Replace.replace(error,"\n","<br/>")+"</p></div>");
			return;
		}
	}
%>

<h3><%=i18n.getText("fileman.file") %></h3>
<p><%=i18n.getText("fileman.file.text") %></p>

<img style="cursor: pointer" alt="PixPack" src="<%=thumbnail %>" onclick="image_open('<%=image %>',<%=dbfs.getWidth() %>,<%=dbfs.getHeight() %>)"/>
<br/>
<b><%=dbfs.getName() %></b>
