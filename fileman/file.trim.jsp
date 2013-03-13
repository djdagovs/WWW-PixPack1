<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFile, com.enlightware.pixpack.Server, java.io.File, com.laukien.bean.magick.Trim, com.laukien.bean.magick.Image"  %>
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

<h3><%=i18n.getText("fileman.file.trim") %></h3>
<p><%=i18n.getText("fileman.file.trim.text") %></p>
<form onsubmit="return dummy()">
	<input type="hidden" id="fileman.name" value="trim"/>
	<button onclick="fileman_file_trim()"><%=i18n.getText("fileman.file.trim") %></button>
</form>

<%
	} else {
		if(source==null || !name.equals("trim")) {
			out.println(i18n.getText("fileman.file.trim.error"));
			return;
		}
		
		DBFSFile file=new DBFSFile();
		file.setUsername(login.getUsername());
		file.setInternalFilename(source);
		file.select(); 
		
		Trim trim=new Trim();
		trim.setInputFile(new File(Server.getPath()+File.separator+Server.buildName(file.getServer())+File.separator+file.getTimestamp()+'_'+file.getKey()+'.'+file.getExtension().getName()));
		trim.execute();
		
		//update image information
		Image image=trim.getImage();
		file.setWidth(image.getWidth());
		file.setHeight(image.getHeight());
		file.setSize(image.getSize());
		file.update();

		out.println(i18n.getText("fileman.file.trim.ok"));
	}
%>