<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String filename=(String)request.getAttribute("upload.file.name");
	if(filename!=null) {
		com.enlightware.pixpack.DBFSFile file=new com.enlightware.pixpack.DBFSFile();
		try {
	file.setInternalFilename(filename);
		} catch(Exception e) {
	throw new com.laukien.exception.ParameterException("hotlink_include.jsp: Invalid Filename");
		}
		String timestamp=file.getTimestamp();
		String key=file.getKey();
		String extension=file.getExtension().getName();
		String srvId=com.enlightware.pixpack.Server.buildName(file.getServer());
		if(!com.enlightware.pixpack.Lib.isWebExtension(extension)) {
	extension="jpg";
	filename=filename.substring(0,filename.lastIndexOf('.')+1)+extension;
		}


		String user=com.enlightware.pixpack.Lib.getUsermail(request);
		if(user==null) user="";
		
		String srvName=com.enlightware.pixpack.Server.getURL();
%>
<div>
	<input type="hidden" id="hotlink.srvname" value="<%=srvName %>"/>
	<input type="hidden" id="hotlink.srvid" value="<%=srvId %>"/>
	<input type="hidden" id="hotlink.timestamp" value="<%=timestamp %>"/>
	<input type="hidden" id="hotlink.key" value="<%=key %>"/>
	<input type="hidden" id="hotlink.extension" value="<%=extension %>"/>
	<input type="hidden" id="hotlink.user" value="<%=user %>"/>
	<input type="hidden" id="hotlink.copyright" value="off"/>
	<input type="hidden" id="hotlink.copyright.on" value="<%=i18n.getText("hotlink.copyright.on") %>"/>
	<input type="hidden" id="hotlink.copyright.off" value="<%=i18n.getText("hotlink.copyright.off") %>"/>
	<input type="hidden" id="hotlink.thumbnail" value="off"/>
	<input type="hidden" id="hotlink.thumbnail.on" value="<%=i18n.getText("hotlink.thumbnail.on") %>"/>
	<input type="hidden" id="hotlink.thumbnail.off" value="<%=i18n.getText("hotlink.thumbnail.off") %>"/>
	<input type="hidden" id="hotlink.statistic" value="off"/>
	<input type="hidden" id="hotlink.statistic.on" value="<%=i18n.getText("hotlink.statistic.on") %>"/>
	<input type="hidden" id="hotlink.statistic.off" value="<%=i18n.getText("hotlink.statistic.off") %>"/>
	<input type="hidden" id="hotlink.message.superfast" value="<%=i18n.getText("hotlink.message.superfast") %>"/>
	<input type="hidden" id="hotlink.message.fast" value="<%=i18n.getText("hotlink.message.fast") %>"/>
	<input type="hidden" id="hotlink.message.slow" value="<%=i18n.getText("hotlink.message.slow") %>"/>
	<input type="hidden" id="hotlink.message.superslow" value="<%=i18n.getText("hotlink.message.superslow") %>"/>

	<table class="hotlink">
		<tr>
			<td>
				<input id="hotlink.link.forum1" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="[URL=http://<%=srvName %>/show/<%=timestamp%>/<%=key%>][IMG]http://<%=srvId %>.<%=srvName %>/<%=filename%>[/IMG][/URL]" readonly="readonly"/>
			</td>
			<th>
				<%=i18n.getText("hotlink.link.forum1") %>
			</th>
		</tr>
		<tr>
			<td>
				<input id="hotlink.link.forum2" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="[url=http://<%=srvName %>/show/<%=timestamp%>/<%=key%>][img=http://<%=srvId %>.<%=srvName %>/<%=filename%>][/url]" readonly="readonly"/>
			</td>
			<th>
				<%=i18n.getText("hotlink.link.forum2") %>
			</th>
		</tr>
		<tr>
			<td>
				<input id="hotlink.link.website" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="<a href=&#34;http://<%=srvName %>/show/<%=timestamp%>/<%=key%>&#34;><img src=&#34;http://<%=srvId %>.<%=srvName %>/<%=filename%>&#34; border=&#34;0&#34; alt=&#34;Image hosted by PixPack.net and powered by Enlightware.com&#34;/></a>" readonly="readonly"/>
			</td>
			<th>
				<%=i18n.getText("hotlink.link.website") %>
			</th>
		</tr>
		<tr>
			<td>
				<input id="hotlink.link.show" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="http://<%=srvName %>/show/<%=timestamp%>/<%=key%>" readonly="readonly"/>
			</td>
			<th>
				<a href="http://<%=srvName %>/show/<%=timestamp%>/<%=key%>">
					<%=i18n.getText("hotlink.link.show") %>
				</a>
			</th>
		</tr>
		<tr>
			<td>
				<input id="hotlink.link.direct" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="http://<%=srvId %>.<%=srvName %>/<%=filename%>" readonly="readonly"/>
			</td>
			<th>
				<%=i18n.getText("hotlink.link.direct") %>
			</th>
		</tr>
		<tr>
			<td>
				<input id="hotlink.link.quick" type="text" onfocus="hotlink_highlight(this)" onselect="hotlink_highlight(this)" value="<a href=&#34;http://<%=srvName %>/show/<%=timestamp%>/<%=key%>&#34;><img src=&#34;http://<%=srvId %>.<%=srvName %>/_<%=filename%>&#34; border=&#34;0&#34; alt=&#34;Image hosted by PixPack.net and powered by Enlightware.com&#34;/></a>" readonly="readonly"/>
			</td>
			<th class="attention">
				<%=i18n.getText("hotlink.link.quick") %>
			</th>
		</tr>
	</table>
	<%=i18n.getText("hotlink.message") %>:&nbsp;
	<b id="hotlink.message" class="message_information"><%=i18n.getText("hotlink.message.fast") %></b>
	<hr/>
	<button id="hotlink.copyright.button" onclick="hotlink_option('copyright')" disabled=\"disabled\"><%=i18n.getText("hotlink.copyright.on") %></button>
	<button id="hotlink.thumbnail.button" onclick="hotlink_option('thumbnail')"><%=i18n.getText("hotlink.thumbnail.on") %></button>
	<button id="hotlink.statistic.button" onclick="hotlink_option('statistic')"<%=(user.length()==0 ? " disabled=\"disabled\"" : "")%>><%=i18n.getText("hotlink.statistic.on") %></button>
</div>
<%} else out.println("Error - no filename");%>