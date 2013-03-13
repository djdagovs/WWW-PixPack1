<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFile, com.enlightware.pixpack.Server" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%if(!login.isPermission()) return; %>

<h3><%=i18n.getText("fileman.file.hotlink") %></h3>
<p><%=i18n.getText("fileman.file.hotlink.text") %></p>

<%
	String source=(String)session.getAttribute("fileman.file");
	if(source==null) return;
	
	String image, message;
	boolean isError=false;
	 DBFSFile dbfs=new DBFSFile();
	
	 dbfs.setInternalFilename(source);
	 //"overwrite" user
	 dbfs.setUsername(login.getUsername());

	 try {
		 dbfs.select();
			 
		 //image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+'/'+(user!=null ? user+'/' : "")+timestamp+"_"+key+'.'+dbfs.getExtension().getName();
		 image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+"/_"+dbfs.getTimestamp()+"_"+dbfs.getKey()+'.'+dbfs.getExtension().getName();
		 message=dbfs.getDescription();
	 } catch(Exception e) {
		 image="image/error.gif";
		 message=i18n.getText("show.error");
		 isError=true;
	 }

	request.setAttribute("upload.file.name",dbfs.getTimestamp()+'_'+dbfs.getKey()+'.'+dbfs.getExtension().getName()); %>
	<div class="hotlink">
		<%@include file="/hotlink_include.jsp" %>
	</div>
