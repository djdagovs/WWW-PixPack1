<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("about") %></h1>
<%if(i18n.getLanguage().equals("de")) { %>
	<jsp:include page="about_de.jsp"/>
<%} else { %>
	<jsp:include page="about_en.jsp"/>
<%} %>
<p>
	<a href="/donation.html" title="donation"><%=i18n.getText("donation") %></a>
</p>