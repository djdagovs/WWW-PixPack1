<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<jsp:useBean id="inlet" class="com.enlightware.pixpack.Inlet" scope="request"/>
<jsp:setProperty name="inlet" property="request" value="<%=request%>"/>

<%
		try {
		request.setCharacterEncoding(inlet.getCharset());
		response.setCharacterEncoding(inlet.getCharset());
	} catch(Exception e) {
		out.println("<div style=\"color: red; font-weight: bold;\">Invalid charset ("+inlet.getCharset()+")<br/><br/>"+e+".</div>");
	}
%>

<%@ taglib uri="/WEB-INF/lib/la-TagLib.IO_5.jar" prefix="io" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:text id="i18n" bundle="pixpack"/>
<io:content id="content" />

<%
	//set the given language
	if(inlet.getLanguage()!=null) i18n.setLanguage(inlet.getLanguage());
	String language=i18n.getLanguage();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	
	<%@include file="/head.jsp" %>

	<body id="design_content">
<%
		if(!inlet.isUploaded()) {
		inlet.save();	//saves some variables in the current session
%>
		<jsp:useBean id="lib" class="com.enlightware.pixpack.Lib" scope="request"/>
		<jsp:setProperty name="lib" property="request" value="<%=request%>"/>
		<jsp:setProperty name="lib" property="i18n" value="<%=i18n%>"/>

		<form name="upload_form" method="post" action="upload" enctype="multipart/form-data" class="upload_form">
			<input type="hidden" name="key" value="<jsp:getProperty name="lib" property="uniqueKey"/>">
			<input type="hidden" name="forward" value="inlet.jsp">
			<input type="hidden" name="user" value="<jsp:getProperty name="inlet" property="user"/>">
			<input type="hidden" name="filename" value="<jsp:getProperty name="inlet" property="key"/>">
			<p>
				<%=i18n.getText("upload.file")%>
				<br/>
				<input type="radio" name="type" value="file" checked="checked" onclick="upload_type('file')"/>
				<input id="upload_file" type="file" name="file" class="field"/>
			</p>
			<p>
				<%=i18n.getText("upload.url")%>
				<br/>
				<input type="radio" name="type" value="url" onclick="upload_type('url')"/>
				<input id="upload_url" type="text" name="url" class="field" disabled="disabled"/>
			</p>
<!--			
			<p>
				<%=i18n.getText("upload.scope") %>
				<br/>
				<jsp:getProperty name="lib" property="scopes"/>
			<p>
				<%=i18n.getText("upload.description") %>
				<br/>
				<textarea name="description" class="area"></textarea>
			</p>
-->			
			<hr/>
			<input type="submit" name="submit" class="upload_submit" value="<%=i18n.getText("upload.submit") %>"/>
		</form>
		<script type="text/javascript">
		<!--
			document.upload_form.file.focus();
		//-->
		</script>
<%
		} else {
		inlet.load();	//sets some variables from the session
%>

<%
if(inlet.getCallback()==null) {
%>
		<jsp:useBean id="msg" class="com.enlightware.pixpack.Message" scope="request"/>
		<jsp:setProperty name="msg" property="request" value="<%=request%>"/>
		
		<div class="<jsp:getProperty name="msg" property="messageTypeAsClass"/>">
			<jsp:getProperty name="msg" property="message"/>
		</div>

		<script type="text/javascript">
		<!--
			window.resizeTo(600,400);
		//-->
		</script>
		<%@include file="/hotlink.jsp" %>

<%
	} else { 
		inlet.transfer();
%>
		<script type="text/javascript">
		<!--
			//eval('opener.'+'hotlink'+'("<jsp:getProperty name="inlet" property="filename"/>")');
			
			
			window.close();
		//-->
		</script>
<%}	//callback %>

	<%@include file="/square.jsp" %>


<%
	}	//upload
%>
	</body>
</html>