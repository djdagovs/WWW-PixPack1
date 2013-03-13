<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User, com.enlightware.pixpack.Account, com.enlightware.pixpack.Forum"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="preview" class="com.enlightware.pixpack.Preview" scope="request"/>

<%
//Valid user
if(login.getParentId()<1) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("preview.edit") %></h3>
<%
	int id=-1;
	try {
		id=Integer.parseInt(request.getParameter("id"));
	} catch(Exception e) {
		request.getRequestDispatcher("/preview.index.html").forward(request,response);
	}

	//Check permission
	if(!preview.getPreviewById(id) || !preview.getUser().equals(login.getUsername())) {
		com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.edit.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
		return;
	}

	if(request.getParameter("action")==null) {
%>
	<form id="form_preview" action="preview.edit.html" method="post">
		<input type="hidden" name="action" value="true"/>
		<input type="hidden" name="id" value="<%=preview.getId() %>"/>
		<p>
			<%=i18n.getText("preview.id") %>:
			<%=preview.getTimestamp() %>_<%=preview.getKey() %>@<%=preview.isAllfolder() ? "*" : preview.getFolder() %>
		</p>

<%if(login.getStatus()>=com.enlightware.pixpack.User.STATUS_PREMIUM) { %>
		<p>
			<%=i18n.getText("preview.link") %>
			<br/>
			<input type="text" name="link" class="field" value="<%=preview.getLink()!=null ? preview.getLink() : "" %>"/>
		</p>
		<p>
			<%=i18n.getText("preview.description") %>
			<br/>
			<textarea name="description" class="area"><%=preview.getDescription()!=null ? preview.getDescription() : "" %></textarea>
		</p>
<%} %>

		<p>
			<input type="checkbox" name="window" value="true"<%=preview.isWindow() ? " checked=\"checked\"" : "" %>/>
			<%=i18n.getText("preview.window") %>	
		</p>
		<hr/>
		<input type="submit" value="<%=i18n.getText("preview.edit") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/preview.index.html'"/>
	</form>
	<script type="text/javascript">
	<!--
		var form=document.getElementById('form_preview');
		if(form) {
			form.getElementsByTagName('input')[2].focus();	//hidden+2
		}
	-->
	</script>
<%} else {
	preview.setLink(request.getParameter("link"));
	preview.setDescription(request.getParameter("description"));
	preview.setWindow(request.getParameter("window")!=null && request.getParameter("window").equals("true"));

	preview.edit();
	com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.edit.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
	request.getRequestDispatcher("/preview.index.html").forward(request,response);
} %>