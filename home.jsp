<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>
<jsp:setProperty name="folder" property="selectionMark" value="<%=(session.getAttribute("folder.select")!=null ? ((String)session.getAttribute("folder.select")) : "")  %>"/>
<jsp:setProperty name="folder" property="username" value="<%=com.enlightware.pixpack.Login.getUsername(request) %>"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<br/>
<b><%=i18n.getText("slogan") %></b>
<br/>
<br/>
<%if(login.isPermission()) {%>
<%@include file="/upload_head.jsp" %>
<table style="width: 100%; height: 270px">
	<tr>
		<td style="width: 300px">
			<%@include file="/upload_file.jsp" %>
<!-- Picture management -->
			<br/>
			<a href="javascript:upload_show('image')"><%=i18n.getText("upload.image") %></a>

			<br/>
			<a href="javascript:upload_show('thumbnail')"><%=i18n.getText("upload.thumbnail") %></a>

<!-- Ajax / Web 2.0 -->			
			<br/>
			<a href="javascript:upload_show('detail')"><%=i18n.getText("upload.detail") %></a>
			
<!-- image formats -->
			<br/>
			<a href="javascript:upload_show('help')"><%=i18n.getText("upload.help") %></a>
		</td>
		<td>
	
			<div id="upload_advertisement">

<!--JavaScript Tag // Tag for network 525: EAG // Website: Laukien dot COM // Page: pixpack.net // Placement: pixpack.net-communication, technics-300 x 250 (1398152) // created at: 25-Oct-07 PM 01:06-->
<script language="javascript"><!--
document.write('<scr'+'ipt language="javascript1.1" src="http://adserver.easyad.info/addyn|3.0|525|1398152|0|170|ADTECH;loc=100;target=_blank;grp=[group];misc='+new Date().getTime()+'"></scri'+'pt>');
//-->
</script><noscript><a href="http://adserver.easyad.info/adlink|3.0|525|1398152|0|170|ADTECH;loc=300;grp=[group]" target="_blank"><img src="http://adserver.easyad.info/adserv|3.0|525|1398152|0|170|ADTECH;loc=300;grp=[group]" border="0" width="300" height="250"/></a></noscript>
<!-- End of JavaScript Tag -->

			</div>
			<%@include file="/upload_detail.jsp" %>
			<%@include file="/upload_image.jsp" %>
			<%@include file="/upload_thumbnail.jsp" %>
			<%@include file="/upload_help.jsp" %>
		</td>
	</tr>
</table>
<hr/>
<%@include file="/upload_submit.jsp" %>
<%@include file="/upload_foot.jsp" %>
<%}else { %>
<table style="width: 100%; height: 270px">
	<tr>
		<td style="width: 300px; padding-right: 20px;">
			<div class="box">
				<div class="title"><%=i18n.getText("login.on") %></div>
				<div class="content">
				<%=i18n.getText("login.text") %>
	<form name="login_form" method="post" action="/login.html">
		<input type="hidden" name="forward" value="/"/>
		<p>
			<%=i18n.getText("login.name") %>
			<br/>
			<input type="text" name="name" value="<%=request.getParameter("name")==null ? "" :  request.getParameter("name") %>"/>
		</p>
		<p>
			<%=i18n.getText("login.password") %>
			<br/>
			<input type="password" name="password"/>
		</p>
		<hr/>
		<input type="submit" name="submit" value="<%=i18n.getText("login.submit") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/home.html'"/>
	</form>
	<script type="text/javascript">
	<!--
		document.login_form.name.focus();
	//-->
	</script>
	<br/>
	<a href="/register.html"><%=i18n.getText("register") %></a>
	<br/>
	<a href="/sendpassword.html"><%=i18n.getText("login.lost") %></a>
				</div>
			</div>
		</td>
		<td>
<%if(i18n.getLanguage().equals("de")) { %>
			<jsp:include page="home_de.jsp"/>
<%} else { %>
			<jsp:include page="home_en.jsp"/>
<%} %>
		</td>
	</tr>
</table>
<%} %>
