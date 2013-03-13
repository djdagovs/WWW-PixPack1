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
%>

<h3><%=i18n.getText("fileman.file.status") %></h3>
<p><%=i18n.getText("fileman.file.status.text") %></p>
<form onsubmit="return dummy()">
	<p>
		<input type="checkbox" name="public" id="fileman.status.public" <%=(file.isPublic() ? "checked=\"checked\"" : "") %>/>
		<%=i18n.getText("fileman.file.status.public") %>
	</p>
	<p>
		<input type="checkbox" name="adult" id="fileman.status.adult" <%=(file.isAdult() ? "checked=\"checked\"" : "") %>/>
		<%=i18n.getText("fileman.file.status.adult") %>
	</p>
	<button onclick="fileman_file_status()"><%=i18n.getText("fileman.file.status") %></button>
</form>

<%
} else {
	if(name.length()!=2) {
		out.println(i18n.getText("fileman.file.status.error"));
		return;
	}
	
	boolean isPublic, isAdult;
	
	isPublic=name.substring(0,1).equals("1");
	isAdult=name.substring(1,2).equals("1");
	
	DBFSFile file=new DBFSFile();
	file.setUsername(login.getUsername());
	file.setInternalFilename(source);
	file.status(isPublic, isAdult);
	
	out.println(i18n.getText("fileman.file.status.ok"));
}
%>