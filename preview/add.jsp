<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>

<jsp:useBean id="preview" class="com.enlightware.pixpack.Preview" scope="request"/>
<jsp:setProperty name="preview" property="request" value="<%=request%>"/>

<%
	if(preview.isSubmitted()) {
		preview.setLink(request.getParameter("link"));
		preview.setFolder(request.getParameter("folder"));
		preview.setDescription(request.getParameter("description"));
		preview.setAllfolder(request.getParameter("allfolder")!=null && request.getParameter("allfolder").equals("true"));
		preview.setWindow(request.getParameter("window")!=null && request.getParameter("window").equals("true"));
		
		if(login.getParentId()==com.enlightware.pixpack.Account.BUSINESS_ID) {
			preview.setSubfolder(request.getParameter("subfolder")!=null && request.getParameter("subfolder").equals("true"));
		}
		
		if(preview.add()) {
			com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.add.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
			request.getRequestDispatcher("/preview.index.html").forward(request,response);
			return;
		} else com.enlightware.pixpack.Message.setMessage(request,i18n.getText("preview.add.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
	}
%>
<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>
<jsp:setProperty name="folder" property="selectionException" value="/"/>
<jsp:setProperty name="folder" property="username" value="<%=login.getUsername() %>"/>

<jsp:useBean id="template" class="com.enlightware.pixpack.Template" scope="request"/>
<jsp:setProperty name="template" property="request" value="<%=request%>"/>

<h3><%=i18n.getText("preview.add") %></h3>
<p><%=i18n.getText("preview.add.text") %></p>
<form id="form_preview" action="preview.add.html" method="post">

<%if(login.getStatus()>=com.enlightware.pixpack.User.STATUS_PREMIUM) { %>
	<p>
		<%=i18n.getText("preview.link") %>
		<br/>
		<input type"=text" name="link" class="field"/>
	</p>
	<p>
		<%=i18n.getText("preview.description") %>
		<br/>
		<textarea name="description" class="area"></textarea>
	</p>
<%} %>

	<p>
		<%=i18n.getText("preview.folder") %>
		<br/>
		<jsp:getProperty name="folder" property="folders"/>
		<br/>
		<input type="checkbox" name="allfolder" value="true" onchange='preview_allfolder()'/>
		<%=i18n.getText("preview.allfolder") %>	
	</p>
	<p>
		<input type="checkbox" name="window" value="true"/>
		<%=i18n.getText("preview.window") %>	
	</p>
	
<%if(login.getParentId()==com.enlightware.pixpack.Account.BUSINESS_ID) { %>
	<p>
		<input type="checkbox" name="subfolder" value="true"/>
		<%=i18n.getText("preview.subfolder") %>	
	</p>
<%} %>	
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("preview.add") %>"/>
	<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/preview.index.html'"/>
</form>

<script type="text/javascript">
<!--
	var form=document.getElementById('form_preview');
	if(form) form.elements[0].focus();
-->
</script>

