<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("tos") %></h1>
<a href="http://info.joetoe.com/tos">JoeToe - <%=i18n.getText("tos") %></a>
<br/>
<br/>
<%if(i18n.getLanguage().equals("de")) { %>
	<jsp:include page="tos_de.jsp"/>
<%} else { %>
	<jsp:include page="tos_en.jsp"/>
<%} %>