<!-- http://de.selfhtml.org/navigation/sidebars/ -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="extra" class="com.enlightware.pixpack.Extra" scope="request"/>
<jsp:setProperty name="extra" property="request" value="<%=request%>"/>

<h1><%=i18n.getText("extra.random") %></h1>
<p style="text-align: center">
	<a href="/extra.random.html"><%=i18n.getText("extra.random.shuffle") %></a>
</p>
<jsp:getProperty name="extra" property="randomGallery"/>
<p style="text-align: center">
	<a href="extra.random.html"><%=i18n.getText("extra.random.shuffle") %></a>
</p>

<script Language="JavaScript">
document.write ('<scr' + 'ipt Language="JavaScript" src="http://www.euros4click.de/showme.php?id=9002&rnd=' + Math.random() + '"></scr' + 'ipt>');
</script>
