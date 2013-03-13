<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("premium") %></h1>
<%if(i18n.getLanguage().equals("de")) { %>
	<jsp:include page="premium_de.jsp"/>
<%} else { %>
	<jsp:include page="premium_en.jsp"/>
<%} %>
<span class="message_attention"><%=i18n.getText("premium.no") %></span>
<br/>
<br/>
<a href="/donation.html">
	<b><%=i18n.getText("donation") %></b>
</a>