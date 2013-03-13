<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
//login.dummy();
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("fileman") %></h3>
<p><%=i18n.getText("fileman.text") %></p>

<!--[if IE]>
	<div style="display: none;">
		<img alt="file" src="../image/fileman/file.gif"/>
		<img alt="folder_close" src="../image/fileman/folder_close.gif"/>
		<img alt="folder_open" src="../image/fileman/folder_open.gif"/>
		<img alt="folder_root" src="../image/fileman/folder_root.gif"/>
	</div>
<![endif]-->

<%
	//?folder
	String folder=request.getParameter("folder");
	if(folder==null || folder.indexOf('\'')!=-1) folder="";
%>

	<%@include file="/fileman/navigation.jsp" %>
	
	<div style="float: none; clear: both;">
		<div id="design_fileman" style="float: left">
			<div id="design_fileman_folder">
				<script type="text/javascript">fileman_folder('<%=folder %>');</script>
			</div>
		</div>
		<div id="design_fileman_content">
			&#160;
		</div>
	</div>