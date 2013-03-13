<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.enlightware.pixpack.Gallery,com.enlightware.pixpack.Server" %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("gallery") %></h1>
<div class="gallery">

<%
	String timestamp=request.getParameter("timestamp");
	String key=request.getParameter("key");
	
	String image, message;

	if(com.laukien.string.String.isEmpty(timestamp) || com.laukien.string.String.isEmpty(key)) {
		out.println(i18n.getText("gallery.error")+": Invalid Format");
		return;
	}

	Gallery gallery=new Gallery();
	gallery.setRequest(request);
	gallery.setTimestamp(timestamp);
	gallery.setKey(key);
	try {
		//gallery.setPosition(Integer.parseInt(request.getParameter("position")));
		gallery.checkId();
	} catch(Exception e) {
		// do nothing
		out.println(i18n.getText("gallery.error")+": Invalid Format\n"+e);
		return;
	}
	//out.println(gallery.show());
	
%>
<script type="text/javascript" src="<%=Server.getRoot() %>/inlet/gallery.js?timestamp=<%=timestamp %>&key=<%=key %>&width=640&height=480&css=">
	<noscript><h3>PixPack - Inlet</h3><a href="http://pixpack.net" title="PixPack">PixPack</a><p><b>PixPack - Free Image Hosting.</b></p></noscript>
</script>
	<br/>
	<div class="message_attention" style="text-align: center; clear: both; float: none">
		<%=i18n.getText("lawhint") %>
		<br/>
		<a href="http://info.joetoe.com/abuse?project=pixpack&subject=<%=i18n.getText("gallery") %>: <%=timestamp %>_<%=key %>" target="_blank"><%=i18n.getText("abuse") %></a>
	</div>
</div>
<%
/*
<!--JavaScript Tag // Tag for network 525: EAG // Website: Laukien dot COM // Page: pixpack.net // Placement: pixpack.net-communication, technics-1 x 1 (1398153) // created at: 25-Oct-07 PM 01:06-->
<script language="javascript"><!--
document.write('<scr'+'ipt language="javascript1.1" src="http://adserver.easyad.info/addyn|3.0|525|1398153|0|16|ADTECH;loc=100;target=_blank;grp=[group];misc='+new Date().getTime()+'"></scri'+'pt>');
//-->
</script><noscript><a href="http://adserver.easyad.info/adlink|3.0|525|1398153|0|16|ADTECH;loc=300;grp=[group]" target="_blank"><img src="http://adserver.easyad.info/adserv|3.0|525|1398153|0|16|ADTECH;loc=300;grp=[group]" border="0" width="1" height="1"/></a></noscript>
<!-- End of JavaScript Tag -->
*/
%>
<script Language="JavaScript">
document.write ('<scr' + 'ipt Language="JavaScript" src="http://www.euros4click.de/showme.php?id=9002&rnd=' + Math.random() + '"></scr' + 'ipt>');
</script>
