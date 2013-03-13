<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>

<jsp:useBean id="preview" class="com.enlightware.pixpack.Preview" scope="request"/>
<jsp:setProperty name="preview" property="request" value="<%=request%>"/>


<h3><%=i18n.getText("preview") %></h3>
<p><%=i18n.getText("preview.text") %></p>
<ul>
	<li>
		<a href="/preview.add.html"><%=i18n.getText("preview.add") %></a>
	</li>
</ul>

<div id="preview.hotlink" class="hotlink preview_hotlink">
	<div>
		<span onclick="document.getElementById('preview.hotlink').style.display='none'">X</span>
	</div>
	<form onsubmit="return dummy()">
		<input id="preview.hotlink.field" type="text" readonly="readonly" onclick="this.select()"/>
	</form>
</div>

<div id="preview.inlet" class="hotlink preview_inlet">
	<div>
		<span onclick="document.getElementById('preview.inlet').style.display='none'">X</span>
	</div>
	<form onsubmit="return dummy()">
		<textarea id="preview.inlet.field" readonly="readonly" onclick="this.select()" style="height: 130px"></textarea>
	</form>
</div>

<table style="width: 100%">
	<tr class="row0">
		<th>
			<a href="?order=timestamp">
				<%=i18n.getText("preview.timestamp") %>
			</a>
		</th>
		<th>
			<a href="?order=folder">
				<%=i18n.getText("preview.folder") %>
			</a>
		</th>
		<th>
			<a href="?order=count">
				<%=i18n.getText("preview.count") %>
			</a>
		</th>
		<th>
			<%=i18n.getText("admin.account.option") %>
		</th>
	</tr>
	<jsp:getProperty name="preview" property="list"/>
</table>

