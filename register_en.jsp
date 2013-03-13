<%@ page pageEncoding="UTF-8" %>
<%
	int step=Integer.parseInt(request.getParameter("step"));
	String user=request.getParameter("mail");;
%>

<%if(step==1) { %>
<h3>Register with PixPack to receive personalized service!</h3>
<p>
	Please use a valid email address and we will send you a confirmation email with your registration link.
	<br/>
	We will never sell or reveal your email address.
</p>
<%} else if(step==2) { %>
<h3>Dear <%=user %>, thank you for registering your account on PixPack.</h3>
<p>
	Please check your email for your registration link.
	<br/>
	Click the registration link to login.
</p>
<%} %>
