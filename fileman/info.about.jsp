<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h3><%=i18n.getText("fileman.info") %></h3>
<p><%=i18n.getText("fileman.info.about") %>
<%
		if(i18n.getLanguage().equals("de")) {
%>
		<jsp:include page="../about_de.jsp"/>
<%
	} else { %>
		<jsp:include page="../about_en.jsp"/>
<%
	}
%>
