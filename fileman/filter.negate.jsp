<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.DBFSFolder, com.enlightware.pixpack.DBFSFile, com.enlightware.pixpack.Server, java.io.File, com.laukien.bean.magick.Image, com.laukien.bean.magick.template.Thumbnail"  %>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<jsp:useBean id="login" class="com.enlightware.pixpack.Login" scope="request"/>
<jsp:setProperty name="login" property="request" value="<%=request%>"/>
<%
	if(!login.isPermission()) return;
	String fileName=(String)request.getParameter("file");
	if(fileName!=null) fileName=fileName.trim();
	String folderName=(String)request.getParameter("folder");
	if(folderName!=null) folderName=folderName.trim();
	String source=(String)session.getAttribute("fileman.file");

	DBFSFile file=new DBFSFile();
	file.setUsername(login.getUsername());
	file.setInternalFilename(source);
	file.select();
	
	if(com.laukien.string.String.isEmpty(fileName)) {
%>

<jsp:useBean id="folder" class="com.enlightware.pixpack.DBFSPresentation" scope="request"/>
<jsp:setProperty name="folder" property="request" value="<%=request%>"/>
<jsp:setProperty name="folder" property="i18n" value="<%=i18n%>"/>
<jsp:setProperty name="folder" property="selectionId" value="fileman.folder"/>
<jsp:setProperty name="folder" property="selectionMark" value="<%=(String)session.getAttribute("fileman.folder")%>"/>
<jsp:setProperty name="folder" property="username" value="<%=login.getUsername() %>"/>

<h3><%=i18n.getText("fileman.filter.negate") %></h3>
<p><%=i18n.getText("fileman.filter.negate.text") %></p>
<form onsubmit="return dummy()">
	<table>
		<tr>
			<td style="padding-right: 20px">
				<%=i18n.getText("fileman.name") %>
				<br/>
				<input type="text" class="field" id="fileman.file" value="<%=file.getName() %>_<%=i18n.getText("fileman.filter.negate") %>"/>
			</td>
			<td>
				<%=i18n.getText("fileman.folder") %>
				<br/>
				<jsp:getProperty name="folder" property="folders"/>
			</td>
		</tr>
	</table>
	<br/>
	<button onclick="fileman_filter_simple('negate')"><%=i18n.getText("fileman.filter.negate") %></button>
</form>

<%
	} else {
		if(source==null || fileName==null || folderName==null) {
			out.println(i18n.getText("fileman.filter.error"));
			return;
		}

		String internalFilename=com.enlightware.pixpack.Lib.getUniqueKey();
		int server=Server.getRandomNumber();
		File inFile=new File(Server.getPath()+File.separator+Server.buildName(file.getServer())+File.separator+file.getTimestamp()+'_'+file.getKey()+'.'+file.getExtension().getName());
		File outFile=new File(Server.getPath()+File.separator+Server.buildName(server)+File.separator+internalFilename+".jpg");
		
		com.laukien.bean.magick.Negate filter=new com.laukien.bean.magick.Negate();
		filter.setInputFile(inFile);
		filter.setOutputFile(outFile);
		filter.setImageIndex(0);
		filter.execute();
		
		if(filter.isError() && !outFile.exists()) {
			com.enlightware.pixpack.Log.write("filter.negate.jsp - Filter: "+filter.getRuntimeCommand()+'\n'+filter.getResult(),com.enlightware.pixpack.Log.SYSTEM);
			out.println(i18n.getText("fileman.filter.error"));
			return;
		}

		//get ImageInfo
		com.laukien.bean.magick.Image info=filter.getImage();

		//Tumbnail
		inFile=outFile;
		File thumbFile=new File(Server.getPath()+File.separator+Server.buildName(server)+File.separator+'_'+internalFilename+".jpg");
		
		Thumbnail thumbnail=new Thumbnail();
		if(info!=null && info.getHeight()>info.getWidth()) thumbnail.setAlignment(Thumbnail.ALIGN_HEIGHT);
		thumbnail.setInputFile(inFile);
		thumbnail.setOutputFile(thumbFile);
		thumbnail.execute();
		if(thumbnail.isError() && !thumbFile.exists()) {
			//remove source-file
			com.laukien.io.file.Delete.delete(inFile);
			com.enlightware.pixpack.Log.write("filter.masaic.jsp - Thumbnail: "+thumbnail.getRuntimeCommand()+'\n'+thumbnail.getResult(),com.enlightware.pixpack.Log.SYSTEM);
			out.println(i18n.getText("fileman.filter.error"));
			return;
		}

		//set new parameters
		file.setInternalFilename(internalFilename);
		file.setServer(server);
		file.setStatus(DBFSFile.STATUS_DEFAULT);
		file.setName(fileName);
		file.setFolder(new DBFSFolder(folderName));
		file.setExtension("jpg");
		
		//image information
		file.setWidth(info.getWidth());
		file.setHeight(info.getHeight());
		file.setSize(info.getSize());
		file.insert();

		out.println(i18n.getText("fileman.filter.ok"));
	}
%>