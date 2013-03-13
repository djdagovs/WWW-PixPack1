<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<div>
		<div id="fileman_navigation_root" class="fileman_navigation">
			<div style="float: left;">
				<a href="javascript:menu('fileman_root_file')"><%=i18n.getText("fileman.file") %></a>
				<ul id="menu_fileman_root_file" class="menu">
					<!--
					<li>
						<a href="javascript:fileman_call('file.upload')">
							<span class="nobr"><%=i18n.getText("fileman.file.upload") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.download')">
							<span class="nobr"><%=i18n.getText("fileman.file.download") %></span>
						</a>
					</li>
					<li><hr/></li>
					-->
					<li>
						<a href="javascript:fileman_restore()">
							<span class="nobr">
								<%=i18n.getText("fileman.file.restore") %>
								<img src="/image/icon/premium16.gif" alt="Premium"/>	
							</span>
						</a>
					</li>
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:fileman_call('folder.add')"><%=i18n.getText("fileman.folder.add") %></a>
			</div>
			<div style="float:left;">
				<a href="javascript:menu('fileman_root_info')"><%=i18n.getText("fileman.info") %></a>
				<ul id="menu_fileman_root_info" class="menu">
					<li>
						<a href="javascript:fileman_call('info.folder')">
							<span class="nobr"><%=i18n.getText("fileman.info.folder") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="javascript:fileman_call('info.fileman')">
							<span class="nobr"><%=i18n.getText("fileman.info.fileman") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('info.about')">
							<span class="nobr"><%=i18n.getText("fileman.info.about") %></span>
						</a>
					</li>
				</div>
			</div>
		</div>
		
		<div id="fileman_navigation_folder" class="fileman_navigation" style="display: none;">
			<div style="float: left;">
				<a href="javascript:menu('fileman_folder_file')"><%=i18n.getText("fileman.file") %></a>
				<ul id="menu_fileman_folder_file" class="menu">
					<!--
					<li>
						<a href="javascript:fileman_call('file.upload')">
							<span class="nobr"><%=i18n.getText("fileman.file.upload") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.download')">
							<span class="nobr"><%=i18n.getText("fileman.file.download") %></span>
						</a>
					</li>
					<li><hr/></li>
					-->
					<li>
						<a href="javascript:fileman_restore()">
							<span class="nobr">
								<%=i18n.getText("fileman.file.restore") %>
								<img src="/image/icon/premium16.gif" alt="Premium"/>	
							</span>
						</a>
					</li>
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:menu('fileman_folder_edit')"><%=i18n.getText("fileman.edit") %></a>
				<ul id="menu_fileman_folder_edit" class="menu">
					<li>
						<a href="javascript:fileman_call('folder.rename')">
							<span class="nobr"><%=i18n.getText("fileman.folder.rename") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('folder.move')">
							<span class="nobr"><%=i18n.getText("fileman.folder.move") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('folder.delete')">
							<span class="nobr"><%=i18n.getText("fileman.folder.delete") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('folder.status')">
							<span class="nobr"><%=i18n.getText("fileman.folder.status") %></span>
						</a>
					</li>
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:menu('fileman_folder_info')"><%=i18n.getText("fileman.info") %></a>
				<ul id="menu_fileman_folder_info" class="menu">
					<li>
						<a href="javascript:fileman_call('info.folder')">
							<span class="nobr"><%=i18n.getText("fileman.info.folder") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="javascript:fileman_call('info.fileman')">
							<span class="nobr"><%=i18n.getText("fileman.info.fileman") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('info.about')">
							<span class="nobr"><%=i18n.getText("fileman.info.about") %></span>
						</a>
					</li>
				</ul>
			</div>
		</div>
		
		<div id="fileman_navigation_file" class="fileman_navigation" style="display: none;">
			<div style="float: left;">
				<a href="javascript:menu('fileman_file')"><%=i18n.getText("fileman.file") %></a>
				<ul id="menu_fileman_file" class="menu">
					<!--
					<li>
						<a href="javascript:fileman_call('file.upload')">
							<span class="nobr"><%=i18n.getText("fileman.file.upload") %></span>
						</a>
					</li>
					-->
					<li>
						<a href="javascript:fileman_call('file.download')">
							<span class="nobr"><%=i18n.getText("fileman.file.download") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="javascript:fileman_call('file.hotlink')">
							<span class="nobr"><%=i18n.getText("fileman.file.hotlink") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.protect')">
							<span class="nobr">
								<%=i18n.getText("fileman.file.protect") %>
								<img src="/image/icon/premium16.gif" alt="Premium"/>	
								</span>
						</a>
					</li>
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:menu('fileman_edit')"><%=i18n.getText("fileman.edit") %></a>
				<ul id="menu_fileman_edit" class="menu">
					<li>
						<a href="javascript:fileman_call('file.rename')">
							<span class="nobr"><%=i18n.getText("fileman.file.rename") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.move')">
							<span class="nobr"><%=i18n.getText("fileman.file.move") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.delete')">
							<span class="nobr"><%=i18n.getText("fileman.file.delete") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.desc')">
							<span class="nobr"><%=i18n.getText("fileman.file.desc") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.status')">
							<span class="nobr"><%=i18n.getText("fileman.file.status") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="javascript:fileman_call('file.trim')">
							<span class="nobr"><%=i18n.getText("fileman.file.trim") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('file.strip')">
							<span class="nobr"><%=i18n.getText("fileman.file.strip") %></span>
						</a>
					</li>
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:menu('fileman_filter')"><%=i18n.getText("fileman.filter") %></a>
				<ul id="menu_fileman_filter" class="menu">
				<!--
					<li>
						<a href="javascript:fileman_call('filter.blur')">
							<span class="nobr"><%=i18n.getText("fileman.filter.blur") %></span>
						</a>
					</li>
				-->
					<li>
						<a href="javascript:fileman_call('filter.despeckle')">
							<span class="nobr"><%=i18n.getText("fileman.filter.despeckle") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.enhance')">
							<span class="nobr"><%=i18n.getText("fileman.filter.enhance") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.equalize')">
							<span class="nobr"><%=i18n.getText("fileman.filter.equalize") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.flip')">
							<span class="nobr"><%=i18n.getText("fileman.filter.flip") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.flop')">
							<span class="nobr"><%=i18n.getText("fileman.filter.flop") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.monochrome')">
							<span class="nobr"><%=i18n.getText("fileman.filter.monochrome") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.negate')">
							<span class="nobr"><%=i18n.getText("fileman.filter.negate") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.normalize')">
							<span class="nobr"><%=i18n.getText("fileman.filter.normalize") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.ping')">
							<span class="nobr"><%=i18n.getText("fileman.filter.ping") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.transpose')">
							<span class="nobr"><%=i18n.getText("fileman.filter.transpose") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('filter.transverse')">
							<span class="nobr"><%=i18n.getText("fileman.filter.transverse") %></span>
						</a>
					</li>
				<!--	
					<li>
						<a href="javascript:fileman_call('filter.sharp')">
							<span class="nobr"><%=i18n.getText("fileman.filter.sharp") %></span>
						</a>
					</li>
				-->
				</ul>
			</div>
			<div style="float: left;">
				<a href="javascript:menu('fileman_info')"><%=i18n.getText("fileman.info") %></a>
				<ul id="menu_fileman_info" class="menu">
					<li>
						<a href="javascript:fileman_call('info.file')">
							<span class="nobr"><%=i18n.getText("fileman.info.file") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="javascript:fileman_call('info.fileman')">
							<span class="nobr"><%=i18n.getText("fileman.info.fileman") %></span>
						</a>
					</li>
					<li>
						<a href="javascript:fileman_call('info.about')">
							<span class="nobr"><%=i18n.getText("fileman.info.about") %></span>
						</a>
					</li>
				</ul>
			</div>
		</div>
		
		<div id="fileman_navigation_restore" class="fileman_navigation" style="display: none;">
			<div style="float: left;">
				<a href="javascript:fileman_restore_delete()" style="background-color: red"><%=i18n.getText("fileman.file.restore.delete") %></a>
			</div>
		</div>
	</div>