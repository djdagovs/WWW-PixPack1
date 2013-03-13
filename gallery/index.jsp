<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>

<jsp:useBean id="gallery" class="com.enlightware.pixpack.Gallery" scope="request"/>
<jsp:setProperty name="gallery" property="request" value="<%=request%>"/>


<h3><%=i18n.getText("gallery") %></h3>
<p><%=i18n.getText("gallery.text") %></p>
<ul>
	<li>
		<a href="/gallery.add.html"><%=i18n.getText("gallery.add") %></a>
	</li>
</ul>

<div id="gallery.hotlink" class="hotlink gallery_hotlink">
	<div>
		<span onclick="document.getElementById('gallery.hotlink').style.display='none'">X</span>
	</div>
	<form onsubmit="return dummy()">
		<input id="gallery.hotlink.field" type="text" readonly="readonly" onclick="this.select()"/>
	</form>
</div>

<div id="gallery.inlet" class="hotlink gallery_inlet">
	<div>
		<span onclick="document.getElementById('gallery.inlet').style.display='none'">X</span>
	</div>
	<form onsubmit="return dummy()">
		<textarea id="gallery.inlet.field" readonly="readonly" onclick="this.select()" style="height: 130px"></textarea>
	</form>
</div>

<table style="width: 100%">
	<tr class="row0">
		<th>
			<a href="?order=title">
				<%=i18n.getText("gallery.title") %>
			</a>
		</th>
		<th>
			<a href="?order=folder">
				<%=i18n.getText("gallery.folder") %>
			</a>
		</th>
		<th>
			<a href="?order=template">
				<%=i18n.getText("gallery.template") %>
			</a>
		</th>
		<th>
			<a href="?order=count">
				<%=i18n.getText("gallery.count") %>
			</a>
		</th>
		<th>
			<%=i18n.getText("admin.account.option") %>
		</th>
	</tr>
	<jsp:getProperty name="gallery" property="list"/>
</table>

