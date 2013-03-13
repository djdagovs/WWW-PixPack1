<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Message" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="contact" class="com.enlightware.pixpack.Contact" scope="request"/>
<jsp:setProperty name="contact" property="request" value="<%=request%>"/>
<jsp:setProperty name="contact" property="i18n" value="<%=i18n%>"/>

<%if(!contact.check() || !contact.isSubmitted()) { %>

<h1><%=i18n.getText("contact") %></h1>
<p><%=i18n.getText("contact.text") %></p>
<form method="post" action="contact.html">
	<p>
		<%=i18n.getText("contact.scope") %>
		<br/>
		<select name="scope" class="field">
			<option value="common"><%=i18n.getText("contact.scope.general") %></option>
			<option value="support"><%=i18n.getText("contact.scope.support") %></option>
			<option value="idea"><%=i18n.getText("contact.scope.idea") %></option>
			<option value="donation"><%=i18n.getText("contact.scope.donation") %></option>
			<option value="advertisement"><%=i18n.getText("contact.scope.advertisement") %></option>
			<option value="software"><%=i18n.getText("contact.scope.software") %></option>
			<option value="law"><%=i18n.getText("contact.scope.law") %></option>
		</select>
	</p>
	<p>
		<%=i18n.getText("contact.name") %>
		<br/>
		<input type="text" name="name" size="128" class="field" value="<%=contact.getName()==null ? "" : contact.getName() %>"/>
	</p>
	<p>
		<%=i18n.getText("contact.mail") %>
		<br/>
		<input type="text" name="mail" size="128" class="field" value="<%=contact.getMail()==null ? "" : contact.getMail() %>"/>
	</p>
	<p>
		<%=i18n.getText("contact.subject") %>:
		<br/>
		<input type="text" name="subject" size="128" class="field" value="<%=contact.getSubject()==null ? "" : contact.getSubject() %>"/>
	</p>
	<p>
		<%=i18n.getText("contact.message") %>
		<br/>
		<textarea name="message" class="area"><%=contact.getMessage()==null ? "" : contact.getMessage() %></textarea>
	</p>
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("contact.submit") %>"/>
</form>

<script type="text/javascript">
	document.forms[0].name.focus();
<%if(contact.getScope()!=null) out.println("document.forms[0].scope.value='"+contact.getScope()+"';"); %>
</script>

<%
} else {
	contact.write();
	//redirect
	request.getRequestDispatcher("/").forward(request,response);
} %>