<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<h1><%=i18n.getText("imprint") %></h1>
<br/>
<%=i18n.getText("imprint.law") %>
<h3>Laukien dot COM</h3>
<br/>
<p>
Stehan Laukien
<br/>
<br/>
Sternbergerstr. 2
<br/>
18109 Rostock
</p>
<br/>
<p>
<b><%=i18n.getText("phone") %>:</b> +49 (0) 173 20 580 36
<br/>
<b><%=i18n.getText("mail") %>:</b>
<script type="text/javascript" src="http://jttp.org/mailcrypt.js?decrypt=XOR:PBMaAQwGGmABARhJJCc5V0xSL0EGSB4="></script>
<noscript>
<h3>jttp://MailCrypt</h3>
<a href="http://jttp.org/tool.mailcrypt.html" title="Joe's WebTools - MailCrypt">Joe's WebTools - MailCrypt</a>
</noscript>
<br/>
<b><%=i18n.getText("internet") %>:</b> <a href="http://pixpack.net">http://PixPack.net</a>
</p>
<p>
<b><%=i18n.getText("director") %>:</b> Stephan Laukien
</p>
<h3><%=i18n.getText("imprint.disclaimer") %></h3>
<div class="disclaimer">
<%
	String imprint=i18n.getLanguage();
	if(imprint==null || !imprint.equals("de")) imprint="en";
	imprint="/imprint_"+imprint+".jsp";
%>
<jsp:include page="<%=imprint %>"/>
</div>