<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.enlightware.pixpack.Preview,com.enlightware.pixpack.Server" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("preview") %></h1>
<div class="preview" style="text-align: center">

<%
	String timestamp=request.getParameter("timestamp");
	String key=request.getParameter("key");
	
	String image, message;

	if(com.laukien.string.String.isEmpty(timestamp) || com.laukien.string.String.isEmpty(key)) {
		out.println(i18n.getText("preview.error")+": Invalid Format");
		return;
	}

	Preview preview=new Preview();
	preview.setTimestamp(timestamp);
	preview.setKey(key);
	out.println(preview.show());
	
%>
	<br/>
	<div class="message_attention" style="text-align: center">
		<%=i18n.getText("lawhint") %>
	</div>
</div>

<script Language="JavaScript">
document.write ('<scr' + 'ipt Language="JavaScript" src="http://www.euros4click.de/showme.php?id=9002&rnd=' + Math.random() + '"></scr' + 'ipt>');
</script>
