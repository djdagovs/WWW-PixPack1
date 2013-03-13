<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<div id="upload_detail" style="display: none;">
				<p>
					<%=i18n.getText("upload.filename") %>
					<br/>
					<input id="upload_filename" type="text" name="filename" class="field" onfocus="upload_extractFilename()"/>
				</p>
				<p>
					<%=i18n.getText("upload.folder") %>
					<br/>
					<jsp:getProperty name="folder" property="folders"/>
				</p>
				<p>
					<%=i18n.getText("upload.description") %>
					<br/>
					<textarea name="description" class="area"></textarea>
				</p>
				<p>
					<table>
						<tr>
							<td>
								<input type="checkbox" name="adult" value="true"/>
								<%=i18n.getText("upload.adult") %>
							</td>
<%if(session.getAttribute("login.user")!=null) { %>
							<td>&nbsp;</td>
							<td>
								<input type="checkbox" name="public" checked="checked" value="true"/>
								<%=i18n.getText("upload.public") %>
							</td>
<%} %>
						</tr>
					</table>
				</p>
			</div>
<script type="text/javascript">
<!--
	if('<%=(String)session.getAttribute("upload.detail.adult") %>'!='null') {
		if('<%=(String)session.getAttribute("upload.detail.adult") %>'=='true') document.getElementsByName('adult')[0].checked=true;
		if(document.getElementsByName('public')[0] && '<%=(String)session.getAttribute("upload.detail.public") %>'=='false') document.getElementsByName('public')[0].checked=false;
	}
//-->
</script>