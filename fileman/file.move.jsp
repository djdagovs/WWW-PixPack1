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
%>

<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>

<h3><%=i18n.getText("fileman.file.move") %></h3>
<p><%=i18n.getText("fileman.file.move.text") %></p>

<form onsubmit="return dummy()">
	<input type="hidden" id="fileman.method" value="local"/>
	<table>
		<tr>
			<td>
				<input type="radio" name="move" value="local" onclick="fileman_folder_move_switch('local')" checked="checked"/>
				<span onclick="fileman_folder_move_switch('local')" style="cursor: default;"><%=i18n.getText("fileman.folder.move.local") %></span>
				<br/>
				<input type="radio" name="move" value="global" onclick="fileman_folder_move_switch('global')"/>
				<span onclick="fileman_folder_move_switch('global')" style="cursor: default;"><%=i18n.getText("fileman.folder.move.global") %></span>
			</td>
			<td>
				<div id="fileman.global" class="hidden">
					<jsp:setProperty name="folder" property="selectionName" value="global"/>
					<jsp:getProperty name="folder" property="folders"/>
				</div>
				<div id="fileman.local" class="visible">
					<jsp:setProperty name="folder" property="username" value="<%=login.getUsername() %>"/>
					<jsp:setProperty name="folder" property="selectionName" value="local"/>
					<jsp:setProperty name="folder" property="selectionException" value="<%=source %>"/>
					<jsp:getProperty name="folder" property="folders"/>
				</div>
			</td>
		</tr>
	</table>
	<br/>
	<br/>
	<button onclick="fileman_file_move()"><%=i18n.getText("fileman.file.move") %></button>
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