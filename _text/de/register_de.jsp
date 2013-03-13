<%@ page pageEncoding="UTF-8" %>
<%
	int step=Integer.parseInt(request.getParameter("step"));
	String user=request.getParameter("mail");
%>

<%if(step==1) { %>
<h3>Register sie sich bei PixPack für ihren persönlichen Service!</h3>
<p>
	Bitte geben sie eine gültige EMail-Adresse an zu der wir ihnen ihre Registrierungsbestätigung senden.
	<br/>
	Ihre Adresse wird ausschließlich zur Nutzerverwaltung verwendet.
</p>
<%} else if(step==2) { %>
<h3>Lieber <%=user %>, vielen Danke für ihre Registrierung bei PixPack.</h3>
<p>
	Bitte prüfen sie ihr Email-Postfach und klicken sie den Registrierungslink um sich anzumelden.
</p>
<%} %>
