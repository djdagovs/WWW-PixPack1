<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
//login.dummy();
if(login.getParentId()!=Account.SYSTEM_ID && login.getParentId()!=Account.ADMIN_ID) request.getRequestDispatcher("/login.html").forward(request,response);
	
	long max=Runtime.getRuntime().maxMemory();
	long total=Runtime.getRuntime().totalMemory();
	long free=Runtime.getRuntime().freeMemory();
%>
<h3><%=i18n.getText("admin.memory") %></h3>
<table style="width: 150px">
	<tr>
		<td>
			<%=i18n.getText("admin.memory.max") %>
		</td>
		<td>
			<%=max/1024 %> kB
		</td>
	</tr>
	<tr>
		<td>
			<%=i18n.getText("admin.memory.free") %>
		</td>
		<td>
			<%=(max-total+free)/1024 %> kB
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<hr/>
		</td>
	</tr>
	<tr>
		<td>
			<%=i18n.getText("admin.memory.total") %>
		</td>
		<td>
			<%=total/1024 %>kB
		</td>
	</tr>
	<tr>
		<td>
			<%=i18n.getText("admin.memory.free") %>
		</td>
		<td>
			<%=free/1024 %> kB
		</td>
	</tr>
</table>
<hr/>
<%
	Process p=Runtime.getRuntime().exec("df -h");
	java.io.InputStream in=p.getInputStream();
	int chr;
	while((chr=in.read())!=-1) {
		if(chr==10) out.append("<br/>");
		else if(chr==' ') out.append("&nbsp;");
		else out.append((char)chr);
	}
	out.flush();
	
%>
<p>
	<a href="/admin.memory.html"><%=i18n.getText("reload") %></a>
</p>