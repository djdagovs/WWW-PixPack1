<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.enlightware.pixpack.DBFSFile,com.enlightware.pixpack.DBFSFolder,com.enlightware.pixpack.Server" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<%
	String timestamp=(String)request.getAttribute("timestamp");
	if(timestamp==null) timestamp=request.getParameter("timestamp");
	String key=(String)request.getAttribute("key");
	if(key==null) key=request.getParameter("key");
	if(timestamp==null || key==null) {
		pageContext.forward("/home.html");
		return;
	}
	String user=(String)request.getAttribute("user");
	if(user==null) request.getParameter("user");
	
	String image, message;
	boolean isError=false;
	 DBFSFile dbfs=new DBFSFile();
	
	if(com.laukien.string.String.isEmpty(timestamp) || com.laukien.string.String.isEmpty(key)) {
		image="image/error.gif";
		message=i18n.getText("show.error")+": Invalid Format";
		isError=true;
	} else {
		 dbfs.setTimestamp(timestamp);
		 dbfs.setKey(key);

		 //check if a comment was added
		 String comment=request.getParameter("comment");
		 if(comment!=null) {
	 dbfs.setUsername(com.enlightware.pixpack.Login.getUsername(request));
	 dbfs.addComment(comment);
		 }

		 //(re)set the user which is given by the URL
		 dbfs.setUsername(user);
		 
		 try {
	 dbfs.select(); //update the statistics
	 dbfs.increment();
	 
	 //image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+'/'+(user!=null ? user+'/' : "")+timestamp+"_"+key+'.'+dbfs.getExtension().getName();
	 image="http://"+Server.buildName(dbfs.getServer())+'.'+Server.getURL()+'/'+timestamp+"_"+key+'.'+dbfs.getExtension().getName();
	 message=dbfs.getDescription();
		 } catch(Exception e) {
	 image="image/error.gif";
	 message=i18n.getText("show.error");
	 isError=true;
		 }
		 
	}
%>

<h1><%=i18n.getText("show") %></h1>
<div class="show">

<%if(dbfs.isAdult() || !dbfs.isPublic()) { %>
	<img alt="Access denied" src="/image/access.gif"/>
<%} else{ %>
	<img alt="PixPack" src="<%=image %>" <%=dbfs.getWidth()>700 ? "width=\"700px\"" : "" %> style="cursor: pointer" onclick="image_open('<%=image %>',<%=dbfs.getWidth() %>,<%=dbfs.getHeight() %>)"/>
<%} %>

	<p class="message"><%=message==null ? "&nbsp;" : com.laukien.string.Replace.replace(message,"\n","<br/>") %></p>
<%
	if(!isError) {
		request.setAttribute("vote","true");
%>
	<div class="message_information" style="font-weight: bold; cursor: pointer" onclick="document.getElementById('show_info').style.display='block'"><%=i18n.getText("image.info") %></div>
	<div id="show_info" style="display: none">
		<%@include file="/show_include.jsp" %>
	</div>
<%} %>
</div>

<div class="message_attention" style="text-align: center">
	<%=i18n.getText("lawhint") %>
	<br/>
	<a href="http://info.joetoe.com/abuse?project=pixpack&subject=<%=i18n.getText("file") %>: <%=timestamp %>_<%=key %>" target="_blank"><%=i18n.getText("abuse") %></a>
</div>

<hr/>
<table style="width: 100%">
	<tr>
		<td style="vertical-align: top">
			<h2><%=i18n.getText("comment") %></h2>
			<%=dbfs.getCommentsAsString() %>
			<form method="post" action="/show.html">
				<input type="hidden" name="timestamp" value="<%=timestamp %>"/>
				<input type="hidden" name="key" value="<%=key %>"/>
				<p>
					<%=i18n.getText("comment.add") %>:
					<br/>
					<textarea class="area" name="comment"></textarea>
				</p>
				<input type="submit" value="<%=i18n.getText("comment.send") %>"/>
			</form>
		</td>
		<td style="vertical-align: top">
<!--JavaScript Tag // Tag for network 525: EAG // Website: Laukien dot COM // Page: pixpack.net // Placement: pixpack.net-communication, technics-300 x 250 (1398152) // created at: 25-Oct-07 PM 01:06-->
<script language="javascript"><!--
document.write('<scr'+'ipt language="javascript1.1" src="http://adserver.easyad.info/addyn|3.0|525|1398152|0|170|ADTECH;loc=100;target=_blank;grp=[group];misc='+new Date().getTime()+'"></scri'+'pt>');
//-->
</script><noscript><a href="http://adserver.easyad.info/adlink|3.0|525|1398152|0|170|ADTECH;loc=300;grp=[group]" target="_blank"><img src="http://adserver.easyad.info/adserv|3.0|525|1398152|0|170|ADTECH;loc=300;grp=[group]" border="0" width="300" height="250"/></a></noscript>
<!-- End of JavaScript Tag -->
		</td>
	</tr>
</table>
<script Language="JavaScript">
document.write ('<scr' + 'ipt Language="JavaScript" src="http://www.euros4click.de/showme.php?id=9002&rnd=' + Math.random() + '"></scr' + 'ipt>');
</script>
<%/*
<!--JavaScript Tag // Tag for network 525: EAG // Website: Laukien dot COM // Page: pixpack.net // Placement: pixpack.net-communication, technics-1 x 1 (1398153) // created at: 25-Oct-07 PM 01:06-->
<script language="javascript"><!--
document.write('<scr'+'ipt language="javascript1.1" src="http://adserver.easyad.info/addyn|3.0|525|1398153|0|16|ADTECH;loc=100;target=_blank;grp=[group];misc='+new Date().getTime()+'"></scri'+'pt>');
//-->
</script><noscript><a href="http://adserver.easyad.info/adlink|3.0|525|1398153|0|16|ADTECH;loc=300;grp=[group]" target="_blank"><img src="http://adserver.easyad.info/adserv|3.0|525|1398153|0|16|ADTECH;loc=300;grp=[group]" border="0" width="1" height="1"/></a></noscript>
<!-- End of JavaScript Tag -->
*/%>