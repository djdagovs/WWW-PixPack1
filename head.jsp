<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String title=(String)request.getAttribute("title");
	if(title==null) title=content.getName();
	//else if(title.startsWith("folder.")) title=i18n.getText(title);
%>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<meta http-equiv="content-script-type" content="text/javascript"/>

		<meta http-equiv="content-style-type" content="text/css"/>
		<meta http-equiv="content-language" content="<%=language %>"/>
		<meta name="page-type" content="Enlightware / PixPack"/>
		<meta name="page-topic" content="Enlightware - Pixpack Image Upload and Hosting"/>
		<meta name="author" content="Laukien dot COM/Enlightware - PixPack"/>
		<meta name="company" content="Laukien dot COM/Enlightware - Pixpack"/>
		<meta name="description" content="PixPack - Free Image Hosting; Load balancing, more than 50 supported image formats and many, many additional services made PixPack the number one image upload and hosting service."/>
		<meta name="keywords" content="PixPack, <%=title %>, fast, schnell, secure, sicher, simple, einfach, image upload, free image hosting, kostenloser Bildupload, picture management, gallery, album, homepage, hosting, jpg, jpeg, png, gif, psd, tif, tiff, bmp, pcx, ajax, web 2.0, Enlightware, Laukien"/>

		<meta http-equiv="pragma" content="no-cache"/>
		<meta name="revisit-after" content="7 days"/>

		<meta http-equiv="reply-to" content="pixpack@enlightware.com"/>
		<meta http-equiv="imagetoolbar" content="no"/>

		<title>Pixpack - next generation image upload and hosting --> <%=title %> </title>

<%if(login.isPermission()) { %>
		<script type="text/javascript" src="/script/ajax.js"></script>
		<script type="text/javascript" src="/script/fileman.js"></script>
		<link href="/style/fileman.css" rel="stylesheet" type="text/css" />
<%} %>

		<link rel="stylesheet" type="text/css" href="http://joetoe.com/style.css" />
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />
		<link title="normal" href="/style/default.css" rel="stylesheet" type="text/css" />
		<!--[if IE]>
			<link href="/style/ie.css" rel="stylesheet" type="text/css" />
		<![endif]-->
		
		<script type="text/javascript" src="http://joetoe.com/script.js"></script>
		<script type="text/javascript" src="/script/default.js"></script>
		
	</head>
