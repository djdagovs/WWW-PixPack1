<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<div id="upload_help" style="display: none;">
				<p>
					<%=i18n.getText("upload.format") %>
					<br/>
					<i>
						<%=com.enlightware.pixpack.Lib.getUploadFormat()%>
					</i>
				</p>
				<p>
					<%=i18n.getText("upload.size") %>
					<br/>
					<i>
						<!--Anonymous: &lt;=1
						<br/>-->
						Free: &lt;=4
						<br/>
						<a href="/premium.html">Premium</a>: &lt;=8
					</i>
				</p>
			</div>
