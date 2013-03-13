<%@ page pageEncoding="UTF-8" %>
<%
	int step=Integer.parseInt(request.getParameter("step"));
	String user=request.getParameter("mail");
%>

<%if(step==1) { %>
<h3>Register sie sich bei PixPack für ihren persönlichen Service!</h3>
<p>
	Bitte gib eine gültige EMail-Adresse an, zu der wir dir deine Registrierungsbestätigung senden.
	<br/>
	Deine Adresse wird ausschließlich zur Nutzerverwaltung verwendet.
</p>
<%} else if(step==2) { %>
<h3>Lieber <%=user %>, vielen Danke für deine Registrierung bei PixPack.</h3>
<p>
	Bitte prüfe dein Email-Postfach und klicke den Registrierungslink, um dich anzumelden.
</p>
<%} %>
