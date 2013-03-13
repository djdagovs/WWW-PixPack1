<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>
<jsp:setProperty name="folder" property="selectionMark" value="<%=(session.getAttribute("folder.select")!=null ? ((String)session.getAttribute("folder.select")) : "")  %>"/>
<jsp:setProperty name="folder" property="username" value="<%=com.enlightware.pixpack.Login.getUsername(request) %>"/>

<!-- Image Upload -->
			<%@include file="/upload_head.jsp" %>
			<input type="hidden" name="forward" value="index.sidebar"/>
			<%@include file="/upload_file.jsp" %>
<!-- Picture management -->
			<br/>
			<a href="javascript:upload_show('image')"><%=i18n.getText("upload.image") %></a>
			<%@include file="/upload_image.jsp" %>

			<br/>
			<a href="javascript:upload_show('thumbnail')"><%=i18n.getText("upload.thumbnail") %></a>
			<%@include file="/upload_thumbnail.jsp" %>

<!-- Ajax / Web 2.0 -->			
			<br/>
			<a href="javascript:upload_show('detail')"><%=i18n.getText("upload.detail") %></a>
			<%@include file="/upload_detail.jsp" %>
			
<!-- image formats -->
			<br/>
			<a href="javascript:upload_show('help')"><%=i18n.getText("upload.help") %></a>
			<%@include file="/upload_help.jsp" %>
			<br/>
			<br/>
			<hr/>
			<%@include file="/upload_submit.jsp" %>
			<%@include file="/upload_foot.jsp" %>