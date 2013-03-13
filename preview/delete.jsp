<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.Preview"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="preview" class="com.enlightware.pixpack.Preview" scope="request"/>

<%
//Valid user
	if(login.getParentId()<1) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("preview.delete") %></h3>
<%
	int id=-1;
	try {
		id=Integer.parseInt(request.getParameter("id"));
	} catch(Exception e) {
		request.getRequestDispatcher("/preview.index.html").forward(request,response);
	}
	

	//Check permission
	if(!preview.getPreviewById(id) || !preview.getUser().equals(login.getUsername())) {
		com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.delete.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
		return;
	}

	if(request.getParameter("action")==null) {
%>
	<p>
		<%=i18n.getText("preview.id") %>:
		<%=preview.getTimestamp() %>_<%=preview.getKey() %>@<%=preview.getFolder() %>
	</p>
	<hr/>
	<form id="form_preview" action="preview.delete.html" method="post">
		<input type="hidden" name="action" value="true"/>
		<input type="hidden" name="id" value="<%=id %>"/>
		<input type="submit" value="<%=i18n.getText("preview.delete") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/preview.index.html'"/>
	</form>
<%} else {
	preview.delete();
	com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.delete.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
	request.getRequestDispatcher("/preview.index.html").forward(request,response);
} %>