<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Image Upload -->
			<%@include file="/upload_head.jsp" %>
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
			