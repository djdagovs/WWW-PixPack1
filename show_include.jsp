<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/script/vote.js"></script>
<!--

//-->
</script>
	<table>
		<tr>
			<th><%=i18n.getText("show.filename") %></th>
			<td><%=dbfs.getName() %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.folder") %></th>
			<td><%=(dbfs.getFolder().getId()!=-1 ? dbfs.getFolder().getName() : i18n.getText("show.folder.none")) %></td>
		</tr>
<%if(request.getAttribute("vote")!=null) {%>		
		<tr>
			<th><%=i18n.getText("vote") %></th>
			<td>
<%
	String id=dbfs.getTimestamp()+'_'+dbfs.getKey();
%>			
				<div style="width: 100px">
					<div class="vote" id="vote<%=id %>" style="float: left;">
						<div onclick="vote_set('<%=id %>', 2); vote_send('<%=id %>')">&nbsp;</div>
						<div onclick="vote_set('<%=id %>', 4); vote_send('<%=id %>')">&nbsp;</div>
						<div onclick="vote_set('<%=id %>', 6); vote_send('<%=id %>')">&nbsp;</div>
						<div onclick="vote_set('<%=id %>', 8); vote_send('<%=id %>')">&nbsp;</div>
						<div onclick="vote_set('<%=id %>', 10); vote_send('<%=id %>')" style="border-right: 1px solid black;">&nbsp;</div>
					</div>
					<script type="text/javascript">
						vote_set('<%=id %>',<%=dbfs.getVote() %>);
					</script>
				</div>
			</td>
		</tr>
<%} %>
		<tr>
			<th><%=i18n.getText("show.extension") %></th>
			<td><%=dbfs.getExtension().getName() %>&#160;<span class="small"><%=dbfs.getExtension().getMimetype() %>&#160;(<%=dbfs.getExtension().getDescription() %>)</span></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.geometry") %></th>
			<td><%=dbfs.getWidth() %>x<%=dbfs.getHeight() %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.size") %></th>
			<td><%=(dbfs.getSize()/1024)+1 %> kB</td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.owner") %></th>
			<td><%=dbfs.getUsername() %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.create") %></th>
			<td>
<%
	com.laukien.datetime.DateTime create=new com.laukien.datetime.DateTime(dbfs.getTimestamp());
	com.laukien.i18n.Date dateC=new com.laukien.i18n.Date(i18n.getLocale());
	dateC.setDate(create.getDate());
	com.laukien.i18n.Time timeC=new com.laukien.i18n.Time(i18n.getLocale());
	timeC.setTime(create.getTime());
	out.println(dateC.toString()+"&#160;"+timeC.toString());		
%>
			</td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.last") %></th>
			<td>
<%
	com.laukien.datetime.DateTime last=new com.laukien.datetime.DateTime(dbfs.getLast());
	com.laukien.i18n.Date dateL=new com.laukien.i18n.Date(i18n.getLocale());
	dateL.setDate(last.getDate());
	com.laukien.i18n.Time timeL=new com.laukien.i18n.Time(i18n.getLocale());
	timeL.setTime(last.getTime());
	out.println(dateL.toString()+"&#160;"+timeL.toString());		
%>
			</td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.count") %></th>
			<td><%=dbfs.getCount()+1 %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.public") %></th>
			<td><%=i18n.getText(String.valueOf(dbfs.isPublic())) %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.adult") %></th>
			<td><%=i18n.getText(String.valueOf(dbfs.isAdult())) %></td>
		</tr>
		<tr>
			<th><%=i18n.getText("show.protect") %></th>
			<td><%=i18n.getText(String.valueOf((dbfs.getProtect()!=null && dbfs.getProtect().length()!=0))) %></td>
		</tr>
	</table>