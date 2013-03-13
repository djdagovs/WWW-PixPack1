<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Statistic" %>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.IO_5.jar" prefix="io" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>

<i18n:text id="i18n" bundle="pixpack"/>
<io:content id="content" />

<%
	//--------------------------------------------------\\
	//new com.enlightware.pixpack.Login(request).dummy();
//--------------------------------------------------\\
%>

<%
	Statistic.incView();
	String language=i18n.getLanguage();
	com.enlightware.pixpack.Account.checkAutologin(request, i18n);
	
	//load language from cookie
	if(session.isNew()) {
		Statistic.incSession();
		Cookie[] cookies=request.getCookies();
		if(cookies!=null) {
	for(int i=0; i<cookies.length; i++) {
		if(cookies[i].getName().equals("language")) {
			i18n.setLanguage(cookies[i].getValue());
			break;
		}
	}
		}
	}
	
	//set header
	int design_header;
	try {
		design_header=((Integer)session.getAttribute("design.header")).intValue();
	} catch(Exception e) {
		design_header=com.laukien.math.Random.randomInt(1,5);
		session.setAttribute("design.header", new Integer(design_header));
	}
%>
<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<jsp:setProperty name="login" property="i18n" value="<%=i18n%>"/>

<jsp:useBean id="msg" class="com.enlightware.pixpack.Message" scope="request"/>
<jsp:setProperty name="msg" property="request" value="<%=request%>"/>

<jsp:useBean id="communication" class="com.enlightware.pixpack.community.Communication" scope="request"/>
<jsp:setProperty name="communication" property="language" value="<%=language%>"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	
	<%@include file="/head.jsp" %>

	<body onload="joetoe_quicklink()">
	
		<div id="joetoe">
			<a href="http://joetoe.com" title="<%=i18n.getText("joetoe") %>">JoeToe</a>
		</div>		
		
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td style="width: 0px;">
				</td>
				<td id="design_content">
					<div id="design_top">
						<a class="home" href="http://pixpack.net/home.html" title="PixPack">
							&#160;
						</a>
<%if(login.isPermission()) { %>
						<div id="design_top_username">
							<jsp:getProperty name="login" property="username"/>
						</div>
<%} %>
						<div id="design_top_advertisement">
							<jsp:getProperty name="communication" property="text"/>
						</div>
						<%@include file="/navigation.jsp" %>
					</div>
					<div id="design_header" style="background-image: url(/image/header.<%=design_header %>.jpg)">
						<h1>PixPack - Image Upload</h1>
						<b>Free Image Hosting / kostenloser Bildupload</b>
						<br/>
						<i>Ajax/Web 2.0 supported image management</i>
						<h2>Part of the <a href="http://laukien.com" title="Laukien dot COM">Laukien dot COM</a>-Group</h2>
					</div>
			
<%if(request.getAttribute("upload.file.name")!=null) { %>
					<%@include file="/hotlink.jsp" %>
<%} %>
					<div style="margin: 5px;">
						<jsp:include page="<%=content.getFile() %>"/>
					</div>
					
					<div id="design_message" class="<jsp:getProperty name="msg" property="messageTypeAsClass"/>">
<%if(msg.getMessage().equals("&#160;")) {%>
						<b class="attention"><%=i18n.getText("tip") %>:</b> <jsp:getProperty name="communication" property="new"/>
<%} else { %>
						<jsp:getProperty name="msg" property="message"/>
<%} %>
					</div>

					<%@include file="/banner.jsp" %>		
					
					<div id="design_footer">
<!--
						<p>
							<h3><a href="/advertisement.html"><%=i18n.getText("advertisement") %></a></h3>
						</p>
-->
						<jsp:getProperty name="communication" property="random"/>
					</div>
				</td>
				<td>
					<%@include file="/sidebar.jsp" %>
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="design_bottom">
						<div style="float: left">
							&nbsp;
							<a href="http://joetoe.com" title="JoeToe">JoeToe</a>
							<a href="http://Enlightware.com" title="Enlightware">Enlightware</a>
							<a href="http://Laukien.com" title="Laukien dot COM">Laukien</a>
						</div>
						<div style="float: right">
							<a href="/tos.html"><%=i18n.getText("tos") %></a>
							<a href="http://info.joetoe.com/contact?language=<%=language %>&pid=pixpack"><%=i18n.getText("contact") %></a>
							<a href="http://info.joetoe.com/imprint?language=<%=language %>"><%=i18n.getText("imprint") %></a>
						</div>
				</td>
				<td></td>
			</tr>
		</table>
		
		<div id="ajax_status">...</div>

<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-780787-1";
urchinTracker();
</script>
		
	</body>
</html>
<%
	//set last page (for upload-referercheck)
	session.setAttribute("last", content.getName());
%>