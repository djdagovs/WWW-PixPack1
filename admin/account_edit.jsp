<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="account" class="com.enlightware.pixpack.Account" scope="request"/>
<jsp:setProperty name="account" property="request" value="<%=request%>"/>

<%
	//Not-Admin and Not-Business
	if(login.getParentId()<Account.SYSTEM_ID || login.getParentId()>Account.BUSINESS_ID) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("admin.account.edit")%></h3>
<%
	int id=-1;
	User user=null;
	try {
		id=Integer.parseInt(request.getParameter("id"));
		user=Account.getUserById(id);

		//Check permission
		if(!account.isUserPermission(id) || user==null) {
	com.enlightware.pixpack.Message.setMessage(request,i18n.getText("admin.account.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
	return;
		}
	} catch(Exception e) {
		request.getRequestDispatcher("/admin.account.html").forward(request,response);
	}
	

	if(request.getParameter("action")==null) {
%>
	<form id="form_account" action="admin.account_edit.html" method="post">
		<input type="hidden" name="action" value="true"/>
		<p>
			<%=i18n.getText("admin.account.id") %>
			<br/>
			<input type="hidden" name="id" value="<%=user.getId() %>"/>
			<%=user.getId() %>
		</p>
		<p>
			<%=i18n.getText("admin.account.name") %>
			<br/>
			<input type="text" name="name" value="<%=user.getName() %>" class="field"/>
		</p>
		<p>
			<%=i18n.getText("admin.account.password") %>
			<br/>
			<input type="password" name="password" class="field"/><span style="color: #E8EEFC;"><%=user.getPassword() %></span>
		</p>
		<p>
			<%=i18n.getText("admin.account.mail") %>
			<br/>
			<input type="text" name="mail" value="<%=user.getMail() %>" class="field"/>
		</p>
		<p>
			<%=i18n.getText("admin.account.status") %>
			<br/>
			<select name="status">
				<option value="<%=User.STATUS_DENIED %>">DENIED</option>
				<option value="<%=User.STATUS_NONE %>">NONE</option>
				<option value="<%=User.STATUS_FREE %>">FREE</option>
<%
	if(login.getStatus()>=User.STATUS_PREMIUM) {
		out.println("<option value=\""+User.STATUS_PREMIUM+"\">PREMIUM</option>");
	}
%>
			</select>
		</p>
<%if(login.getUser().getId()==Account.SYSTEM_ID || login.getUser().getId()==Account.ADMIN_ID || login.getUser().getId()==Account.BUSINESS_ID) { %>		
		<p>
			<%=i18n.getText("admin.account.parent") %>
			<br/>
			<select name="parent">
<%if(login.getParentId()!=Account.BUSINESS_ID) {%>
				<option value="<%=Account.ADMIN_ID %>">ADMIN</option>
<%} %>
				<option value="<%=Account.FREE_ID %>">FREE</option>
				<option value="<%=Account.PREMIUM_ID %>">PREMIUM</option>
				<option value="<%=Account.BUSINESS_ID %>">BUSINESS</option>
			</select>
		</p>
<%} %>
		<script type="text/javascript">
		<!--
			var form=document.getElementById('form_account');
			form.status.value=<%=user.getStatus() %>;
			if(form.parent) form.parent.value=<%=user.getParentId() %>;
		-->
		</script>
		<hr/>
		<input type="submit" value="<%=i18n.getText("admin.account.edit") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/admin.account.html'"/>
	</form>
<%} else {
	if(request.getParameter("name")!=null) user.setName(request.getParameter("name"));
	if(request.getParameter("password")!=null && request.getParameter("password").trim().length()>0) user.setPassword(request.getParameter("password"));
	if(request.getParameter("mail")!=null) user.setMail(request.getParameter("mail"));
	if(request.getParameter("status")!=null) user.setStatus(Integer.parseInt(request.getParameter("status")));
	if(request.getParameter("parent")!=null) user.setParentId(Integer.parseInt(request.getParameter("parent")));
	Account.edit(user);
	Forum.edit(user);
	request.getRequestDispatcher("/admin.account.html").forward(request,response);
} %>