<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("donation") %></h1>
<%if(i18n.getLanguage().equals("de")) { %>
	<jsp:include page="donation_de.jsp"/>
<%} else { %>
	<jsp:include page="donation_en.jsp"/>
<%} %>
<script Language="JavaScript">
document.write ('<scr' + 'ipt Language="JavaScript" src="http://www.euros4click.de/showme.php?id=9002&rnd=' + Math.random() + '"></scr' + 'ipt>');
</script>
