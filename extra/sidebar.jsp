<!-- http://de.selfhtml.org/navigation/sidebars/ -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("extra.sidebar") %></h1>
<%if(i18n.getLanguage().equals("de")) { %>
	<jsp:include page="sidebar_de.jsp"/>
<%} else { %>
	<jsp:include page="sidebar_en.jsp"/>
<%} %>
<i>
	<b>
<%
	com.laukien.information.http.ClientInformation info=new com.laukien.information.http.ClientInformation(request);
	if(info.isIE()) out.println(i18n.getText("extra.sidebar.msie"));
	else if(info.isFirefox()) out.println(i18n.getText("extra.sidebar.firefox"));
	else if(info.isOpera()) out.println(i18n.getText("extra.sidebar.opera"));
	else if(info.isMozilla() || info.isNetscape()) out.println(i18n.getText("extra.sidebar.netscape"));
	else out.println(i18n.getText("extra.sidebar.other"));
%>
	</b>
</i>