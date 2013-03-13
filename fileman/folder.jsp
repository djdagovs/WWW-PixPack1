<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	session.setAttribute("fileman.content",content);
%>

<h3><%=i18n.getText("fileman.folder") %></h3>
<p><%=i18n.getText("fileman.folder.text") %></p>
<form id="fileman_form" onsubmit="return dummy()">
	<%=i18n.getText("fileman.count") %>: <b><%=com.enlightware.pixpack.FileMan.countFiles(content,login.getUsername()) %></b>
<%
	if(com.laukien.string.String.isEmpty(content)) {
%>
	<hr/>
	<br/>
	<fieldset>
		<legend>
			<%=i18n.getText("fileman.folders") %>
		</legend>
		<%=com.enlightware.pixpack.FileMan.folder(content,login.getUsername()) %>
	</fieldset>
	<br/>
	<fieldset>
		<legend>
			<%=i18n.getText("fileman.folder.add") %>
		</legend>
		<p>
			<%=i18n.getText("fileman.folder.add.text") %>
		</p>
		<input type="text" id="fileman.folder.add" size="64" class="field"/>
		<button onclick="fileman_folder_add()"><%=i18n.getText("fileman.folder.add") %></button>
	</fieldset>
<%} else {%>
	<br/>
	<%=i18n.getText("fileman.name") %>: <b><%=content %></b>
	<hr/>

	<button onclick="fileman_show('fileman.folder.rename')"><%=i18n.getText("fileman.folder.rename") %></button>
	<button onclick="fileman_show('fileman.folder.move')"><%=i18n.getText("fileman.folder.move") %></button>
	<button onclick="fileman_show('fileman.folder.delete')"><%=i18n.getText("fileman.folder.delete") %></button>

	<fieldset class="hidden">
		<legend>
			<%=i18n.getText("fileman.folder.rename") %>
		</legend>
		<p>
			<%=i18n.getText("fileman.folder.rename.text") %>
		</p>
		<input type="text" id="fileman.folder.rename" size="64" class="field" value="<%=content %>"/>
		<button onclick="fileman_folder_rename()"><%=i18n.getText("fileman.folder.rename") %></button>
	</fieldset>

	<fieldset class="hidden">
		<legend>
			<%=i18n.getText("fileman.folder.move") %>
		</legend>
		<p>
			<%=i18n.getText("fileman.folder.move.text") %>
		</p>
		<input type="hidden" id="fileman.folder.move" value="local"/>
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
					<div id="fileman.folder.move.global" class="hidden">
						<jsp:setProperty name="folder" property="selectionName" value="global"/>
						<jsp:getProperty name="folder" property="folders"/>
					</div>
					<div id="fileman.folder.move.local" class="visible">
						<jsp:setProperty name="folder" property="username" value="<%=login.getUsername() %>"/>
						<jsp:setProperty name="folder" property="selectionName" value="local"/>
						<jsp:setProperty name="folder" property="selectionException" value="<%=content %>"/>
						<jsp:getProperty name="folder" property="folders"/>
					</div>
				</td>
			</tr>
		</table>
		<br/>
		<br/>
		<button onclick="fileman_folder_move()"><%=i18n.getText("fileman.folder.move") %></button>
	</fieldset>

	<fieldset class="hidden">
		<legend>
			<%=i18n.getText("fileman.folder.delete") %>
		</legend>
		<p>
			<%=i18n.getText("fileman.folder.delete.text") %>
		</p>
		<input type="hidden" id="fileman.folder.delete"/>	<!-- dummy -->
		<button onclick="fileman_folder_delete()"><%=i18n.getText("fileman.folder.delete") %></button>
	</fieldset>

<%} %>
</form>
