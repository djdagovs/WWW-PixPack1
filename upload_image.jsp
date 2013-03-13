<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<div id="upload_image" style="display: none;">
				<p>
					<%=i18n.getText("upload.image.size") %>
					<br/>
					<select name="image.size" class="field">
					    <option value="" selected="selected"><%=i18n.getText("upload.image.size.none") %></option>
					    <option value="100x75">100x75 (<%=i18n.getText("image.size.100x75") %>)</option>
					    <option value="64x64">64x64 (<%=i18n.getText("image.size.64x64") %>)</option>
					    <option value="160x120">160x120 (<%=i18n.getText("image.size.160x120") %>)</option>
					    <option value="320x240">320x240 (<%=i18n.getText("image.size.320x240") %>)</option>
					    <option value="640x480">640x480 (<%=i18n.getText("image.size.640x480") %>)</option>
						<option value="800x600">800x600 (<%=i18n.getText("image.size.800x600") %>)</option>
					    <option value="1024x768">1024x768 (<%=i18n.getText("image.size.1024x768") %>)</option>
					    <option value="1280x1024">1280x1024 (<%=i18n.getText("image.size.1280x1024") %>)</option>
					    <option value="1600x1200">1600x1200 (<%=i18n.getText("image.size.1600x1200") %>)</option>
					</select>
				</p>
				<p>
					<input type="checkbox" checked="checked" name="image.optimize" value="true"/>
					<%=i18n.getText("upload.optimize") %>
				</p>
			</div>
<script type="text/javascript">
<!--
	if('<%=(String)session.getAttribute("upload.image.size") %>'!='null') {
		document.getElementsByName('image.size')[0].value='<%=(String)session.getAttribute("upload.image.size") %>';
		if('<%=(String)session.getAttribute("upload.image.optimize") %>'=='false') document.getElementsByName('image.optimize')[0].checked=false;
	}
//-->
</script>
			