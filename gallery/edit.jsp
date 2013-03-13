<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<%@page import="com.enlightware.pixpack.User,com.enlightware.pixpack.Account"%>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<jsp:useBean id="gallery" class="com.enlightware.pixpack.Gallery" scope="request"/>

<%
//Valid user
if(login.getParentId()<1) request.getRequestDispatcher("/login.html").forward(request,response);
%>
<h3><%=i18n.getText("gallery.edit")%></h3>
<%
	int id=-1;
	try {
		id=Integer.parseInt(request.getParameter("id"));
	} catch(Exception e) {
		request.getRequestDispatcher("/gallery.index.html").forward(request,response);
	}

	//Check permission
	if(!gallery.getGalleryById(id) || !gallery.getUser().equals(login.getUsername())) {
		com.enlightware.pixpack.Message.setMessage(request,i18n.getText("gallery.edit.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
		return;
	}

	if(request.getParameter("action")==null) {
%>
	<form id="form_gallery" action="gallery.edit.html" method="post">
		<input type="hidden" name="action" value="true"/>
		<input type="hidden" name="id" value="<%=gallery.getId() %>"/>
		<p>
			<%=i18n.getText("gallery.id")%>:
			<%=gallery.getTimestamp()%>_<%=gallery.getKey()%>@<%=gallery.getFolder()%>
		</p>
		<p>
			<%=i18n.getText("gallery.title")%>
			<br/>
			<input type="text" name="title" class="field" value="<%=gallery.getTitle()!=null ? gallery.getTitle() : "" %>"/>
		</p>
		<p>
			<%=i18n.getText("gallery.description")%>
			<br/>
			<textarea name="description" name="description" class="area"><%=gallery.getDescription()!=null ? gallery.getDescription() : ""%></textarea>
		</p>
		<p>
			<table>
				<tr>
					<td><%=i18n.getText("gallery.col")%></td>
					<td>&nbsp;</td>
					<td><%=i18n.getText("gallery.row")%></td>
				</tr>
				<tr>
					<td>
						<select name="col">
							<option>--</option>
							<option>1</option>
							<option>2</option>
							<option>3</option>
							<option>4</option>
							<option>5</option>
							<option>6</option>
							<option>7</option>
							<option>9</option>
							<option>10</option>
							<option>11</option>
							<option>12</option>
							<option>13</option>
							<option>14</option>
							<option>15</option>
							<option>16</option>
							<option>17</option>
							<option>18</option>
							<option>19</option>
							<option>20</option>
						</select>
					</td>
					<td>&nbsp;</td>
					<td>
						<select name="row">
							<option>--</option>
							<option>1</option>
							<option>2</option>
							<option>3</option>
							<option>4</option>
							<option>5</option>
							<option>6</option>
							<option>7</option>
							<option>9</option>
							<option>10</option>
							<option>11</option>
							<option>12</option>
							<option>13</option>
							<option>14</option>
							<option>15</option>
							<option>16</option>
							<option>17</option>
							<option>18</option>
							<option>19</option>
							<option>20</option>
							<option>25</option>
							<option>30</option>
							<option>35</option>
							<option>40</option>
							<option>45</option>
							<option>50</option>
						</select>
					</td>
				</tr>
			</table>
		</p>				
		<p>
			<input type="checkbox" name="sort_time" value="true"<%=gallery.isSortByTimestamp() ? " checked=\"checked\"" : "" %>/>
			<%=i18n.getText("gallery.sort.time")%>	
			<br/>
			<input type="checkbox" name="sort_order" value="true"<%=gallery.isSortOrder() ? " checked=\"checked\"" : "" %>/>
			<%=i18n.getText("gallery.sort.order")%>	
		</p>

		<hr/>
		<input type="submit" value="<%=i18n.getText("gallery.edit") %>"/>
		<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/gallery.index.html'"/>
	</form>
	<script type="text/javascript">
	<!--
		var form=document.getElementById('form_gallery');
		if(form) {
			form.col.value=<%=gallery.getCol() %>;
			form.row.value=<%=gallery.getRow() %>;
			form.getElementsByTagName('input')[2].focus();	//hidden+2
		}
	-->
	</script>
<%
	} else {
	if(request.getParameter("title")!=null) gallery.setTitle(request.getParameter("title"));
	if(request.getParameter("description")!=null) gallery.setDescription(request.getParameter("description"));
	if(request.getParameter("col")!=null) {
		try {
	 gallery.setCol(Short.parseShort(request.getParameter("col")));
		} catch(Exception e) {
	gallery.setCol((short)-1);
		}
	}
	if(request.getParameter("row")!=null) {
		try {
	 gallery.setRow(Short.parseShort(request.getParameter("row")));
		} catch(Exception e) {
	gallery.setRow((short)-1);
		}
	}

	gallery.setSortByTimestamp(request.getParameter("sort_time")!=null && request.getParameter("sort_time").equals("true"));
	gallery.setSortOrder(request.getParameter("sort_order")!=null && request.getParameter("sort_order").equals("true"));

	gallery.edit();
	com.enlightware.pixpack.Message.setMessage(request,i18n.getText("gallery.edit.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
	request.getRequestDispatcher("/gallery.index.html").forward(request,response);
}
%>