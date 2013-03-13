<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

	<p>
		<ul>
			<li><%=i18n.getText("advertisement.top") %>: <b>240</b> €/<%=i18n.getText("month") %></li>
			<li><%=i18n.getText("advertisement.right") %>: <b>120</b> €/<%=i18n.getText("month") %></li>
			<li><%=i18n.getText("advertisement.bottom") %>: <b>75</b> €/<%=i18n.getText("month") %></li>
			<li><%=i18n.getText("advertisement.link") %>: <b>30</b> €/<%=i18n.getText("month") %></li>
		</ul>
	</p>
	<h3><%=i18n.getText("advertisement.info") %>:</h3>
	<form id="advertisement.form" action="http://info.joetoe.com/contact" method="post">
		<input type="hidden" name="pid" value="pixpack"/>
		<input type="hidden" name="language" value="<%=i18n.getLanguage() %>"/>
		<input type="hidden" name="scope" value="advertisement"/>
		<p>
			<%=i18n.getText("contact.name") %>:
			<br/>
			<input type="text" name="name" size="64" class="field"/>
		</p>
		<p>
			<%=i18n.getText("contact.mail") %>:
			<br/>
			<input type="text" name="mail" size="64" class="field"/>
		</p>
		<p>
			<%=i18n.getText("contact.subject") %>:
			<br/>
			<input type="text" name="subject" size="128" class="field"/>
		</p>
		<p>
			<%=i18n.getText("contact.text") %>:
			<br/>
			<textarea name="message" class="area"></textarea>
		</p>
		<hr/>
		<input type="submit" name="submit" value="<%=i18n.getText("contact.submit") %>"/>
	</form>
	<script type="text/javascript">
	<!--
		document.getElementById("advertisement.form").name_first.focus()
	//->
	</script>