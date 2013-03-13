<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
		<form id="upload_form" method="post" action="/upload" enctype="multipart/form-data" class="upload_form" onsubmit="waitForSubmit(this,'<%=i18n.getText("upload.process") %>')">
			<input type="hidden" name="key" value="<%=com.enlightware.pixpack.Lib.getUniqueKey() %>">
