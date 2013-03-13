<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<p onclick="upload_type('file')">
				<input type="radio" name="type" value="file" checked="checked"/>
				<%=i18n.getText("upload.file") %>
				<br/>
				<input id="upload_file" type="file" name="file" class="field"/>
			</p>
			<p onclick="upload_type('url')">
				<input type="radio" name="type" value="url"/>
				<%=i18n.getText("upload.url") %>
				<br/>
				<input id="upload_url" type="text" name="url" class="field" disabled="disabled"/>
			</p>
<script type="text/javascript">
<!--
	if('<%=(String)session.getAttribute("upload.file") %>'!='null') upload_type('url');
//-->
</script>