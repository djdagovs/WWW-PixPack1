<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>

<%
	if(!login.isPermission()) request.getRequestDispatcher("/login.html").forward(request,response);
%>

<jsp:useBean id="gallery" class="com.enlightware.pixpack.Gallery" scope="request"/>
<jsp:setProperty name="gallery" property="request" value="<%=request%>"/>

<%
	if(gallery.isSubmitted()) {
		gallery.setTitle(request.getParameter("title"));
		gallery.setFolder(request.getParameter("folder"));
		gallery.setDescription(request.getParameter("description"));
		gallery.setTemplate(request.getParameter("template"));
		short col, row;
		try {
			col=Short.parseShort(request.getParameter("col"));
			try {
				row=Short.parseShort(request.getParameter("row"));
			} catch(Exception e) {
				row=-1;
			}
		} catch(Exception e) {
			//if col is not set row is undefined too
			col=row=-1;
		}
		gallery.setCol(col);
		gallery.setRow(row);
		gallery.setSortByTimestamp(request.getParameter("sort_time")!=null && request.getParameter("sort_time").equals("true"));
		gallery.setSortOrder(request.getParameter("sort_order")!=null && request.getParameter("sort_order").equals("true"));
		
		if(login.getParentId()==com.enlightware.pixpack.Account.BUSINESS_ID) {
			gallery.setSubfolder(request.getParameter("subfolder")!=null && request.getParameter("subfolder").equals("true"));
		}
		
		if(gallery.add()) {
			com.enlightware.pixpack.Message.setMessage(request,i18n.getText("gallery.add.ok"),com.enlightware.pixpack.Message.TYPE_INFORMATION);
			request.getRequestDispatcher("/gallery.index.html").forward(request,response);
			return;
		} else com.enlightware.pixpack.Message.setMessage(request,i18n.getText("gallery.add.error"),com.enlightware.pixpack.Message.TYPE_ERROR);
	}
%>
<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionName" value="folder"/>
<jsp:setProperty name="folder" property="selectionException" value="/"/>
<jsp:setProperty name="folder" property="username" value="<%=login.getUsername() %>"/>

<jsp:useBean id="template" class="com.enlightware.pixpack.Template" scope="request"/>
<jsp:setProperty name="template" property="request" value="<%=request%>"/>

<h3><%=i18n.getText("gallery.add") %></h3>
<p><%=i18n.getText("gallery.add.text") %></p>
<form id="form_gallery" action="gallery.add.html" method="post">
	<p>
		<%=i18n.getText("gallery.title") %>
		<br/>
		<input type"=text" name="title" class="field"/>
	</p>
	<p>
		<%=i18n.getText("gallery.description") %>
		<br/>
		<textarea name="description" class="area"></textarea>
	</p>
	<p>
		<%=i18n.getText("gallery.folder") %>
		<br/>
		<jsp:getProperty name="folder" property="folders"/>
	</p>
	<p>
		<%=i18n.getText("gallery.template") %>
		<br/>
		<jsp:getProperty name="template" property="galleryList"/>
	</p>
	<p>
		<table>
			<tr>
				<td><%=i18n.getText("gallery.col") %></td>
				<td>&nbsp;</td>
				<td><%=i18n.getText("gallery.row") %></td>
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
		<input type="checkbox" name="sort_time" value="true"/>
		<%=i18n.getText("gallery.sort.time") %>	
		<br/>
		<input type="checkbox" name="sort_order" value="true"/>
		<%=i18n.getText("gallery.sort.order") %>	
	</p>
	
<%if(login.getParentId()==com.enlightware.pixpack.Account.BUSINESS_ID) { %>
	<p>
		<input type="checkbox" name="subfolder" value="true"/>
		<%=i18n.getText("gallery.subfolder") %>	
	</p>
<%} %>	
	<hr/>
	<input type="submit" name="submit" value="<%=i18n.getText("gallery.add") %>"/>
	<input type="button" value="<%=i18n.getText("cancel") %>" onclick="document.location.href='/gallery.index.html'"/>
</form>

<script type="text/javascript">
<!--
	var form=document.getElementById('form_gallery');
	if(form) form.elements[0].focus();
-->
</script>

