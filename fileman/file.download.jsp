<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFolder, com.enlightware.pixpack.DBFSFile, com.enlightware.pixpack.Account"  %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) return;
	String name=(String)request.getParameter("content");
	String source=(String)session.getAttribute("fileman.file");
	if(source==null) return;
	
	if(com.laukien.string.String.isEmpty(name)) {
		DBFSFile file=new DBFSFile();
		file.setInternalFilename(source);
		file.setUsername(login.getUsername());
		file.select();
%>

<jsp:useBean id="presentation" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="presentation" property="selectionId" value="fileman.format"/>
<jsp:setProperty name="presentation" property="selectionMark" value="<%=file.getExtension().getName()%>"/>

<h3><%=i18n.getText("fileman.file.download") %></h3>
<p><%=i18n.getText("fileman.file.download.text") %></p>

<form onsubmit="return dummy()">
	<p>
		<%=i18n.getText("fileman.file.download.name") %>
		<br/>
		<input type="text" size="128" id="fileman.name" class="field" value="<%=file.getName() %>"/>
	</p>
	<p>
		<%=i18n.getText("fileman.file.download.format") %>
		<br/>
		<jsp:getProperty name="presentation" property="writableFormats"/>
	</p>
	<br/>
	<button onclick="fileman_file_download()"><%=i18n.getText("fileman.file.download") %></button>
	<input type="hidden" id="fileman.filename" value="<%=file.getTimestamp()+'_'+file.getKey() %>"/>
</form>

<%
	} else {
		int pos;
		if(source==null || (pos=name.indexOf(':'))==-1) {
			out.println(i18n.getText("fileman.file.move.error"));
			return;
		}

		String method;
		String dest;
		method=name.substring(0,pos);
		dest=name.substring(pos+1);
		
		DBFSFile file=new DBFSFile();
		file.setUsername(login.getUsername());
		file.setInternalFilename(source);

		DBFSFolder folder=new DBFSFolder();
		if(method.equals("global")) {
			//Check if is the global folder exists --> ok
			folder.setUsername(Account.ANONYMOUS);
			folder.setName(dest);
			if(!folder.exists()) {
				out.println(i18n.getText("fileman.file.move.error"));
				return;
			}

			file.move(dest,Account.ANONYMOUS);
		} else {
			folder.setUsername(login.getUsername());
			folder.setName(dest);
			//Check if is the same local folder --> error
			if(dest.equals(source) || !folder.exists()) {
				out.println(i18n.getText("fileman.file.move.error"));
				return;
			}
			file.move(dest,login.getUsername());
		}

		out.println(i18n.getText("fileman.file.move.ok"));
	}
%>