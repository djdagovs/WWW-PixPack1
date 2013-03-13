<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
		<div class="hotlink">
			<h3><%=i18n.getText("hotlink") %></h3>
			<form onsubmit="return dummy()">
				<%@include file="/hotlink_include.jsp" %>
			</form>
		</div>