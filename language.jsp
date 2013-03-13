<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("language") %></h1>
<p>
<%=i18n.getText("language.text") %>
</p>
<form id="language_form" action="/language" method="post">
	<%=com.enlightware.pixpack.Language.createMenu(i18n)%>
	<hr/>
	<input type="submit" value="<%=i18n.getText("change") %>"/>
</form>
<script type="text/javascript">
	setLanguage('<%=i18n.getLanguage() %>');
</script>
