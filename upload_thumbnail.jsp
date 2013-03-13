<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<div id="upload_thumbnail" style="display: none;">
				<p>
					<%=i18n.getText("upload.thumbnail.size") %>
					<br/>
					<select name="thumbnail.size" class="field">
					    <option value="16x16">16x16 (<%=i18n.getText("image.size.16x16") %>)</option>
					    <option value="32x32">32x32 (<%=i18n.getText("image.size.32x32") %>)</option>
					    <option value="64x64">64x64 (<%=i18n.getText("image.size.64x64") %>)</option>
					    <option value="100x75">100x75 (<%=i18n.getText("image.size.100x75") %>)</option>
					    <option value="160x120" selected="selected">160x120 (<%=i18n.getText("image.size.160x120") %>)</option>
					    <option value="320x240">320x240 (<%=i18n.getText("image.size.320x240") %>)</option>
					    <option value="640x480">640x480 (<%=i18n.getText("image.size.640x480") %>)</option>
					</select>
				</p>
				<p>
					<input type="radio" name="thumbnail.option" value="auto"/>
					<%=i18n.getText("upload.thumbnail.auto") %>
					<br/>
					<input type="radio" checked="checked" name="thumbnail.option" value="cut"/>
					<%=i18n.getText("upload.thumbnail.cut") %>
					<br/>
					<input type="radio" name="thumbnail.option" value="width"/>
					<%=i18n.getText("upload.thumbnail.width") %>
					<br/>
					<input type="radio" name="thumbnail.option" value="height"/>
					<%=i18n.getText("upload.thumbnail.height") %>
				</p>
			</div>
<script type="text/javascript">
<!--
	if('<%=(String)session.getAttribute("upload.thumbnail.size") %>'!='null') {
		document.getElementsByName('thumbnail.size')[0].value='<%=(String)session.getAttribute("upload.thumbnail.size") %>';
		option='<%=(String)session.getAttribute("upload.thumbnail.option") %>';
		if(option=='auto') document.getElementsByName('thumbnail.option')[0].checked=true;
		else if(option=='width') document.getElementsByName('thumbnail.option')[2].checked=true;
		else if(option=='height') document.getElementsByName('thumbnail.option')[3].checked=true;
	}
//-->
</script>