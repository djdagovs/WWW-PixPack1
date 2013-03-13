<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.enlightware.pixpack.external.com.enlightware.pixpack.common.Blog" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h3><%=i18n.getText("community.news")%></h3>

<%=Blog.getNews(i18n.getLanguage())%>
