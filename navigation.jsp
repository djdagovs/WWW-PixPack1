<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Account" %>

	<div id="design_navigation">
		<div class="left">
			<a href="/home.html"><%=i18n.getText("upload") %></a>
		</div>
<%if(login.isPermission()) { %>
			<div class="right">
				<a href="javascript:menu('login')">MyPix</a>
				<ul id="menu_login" class="menu mypix">
					<li>
						<a href="/fileman.index.html">
							<span class="nobr"><%=i18n.getText("fileman") %></span>
						</a>
					</li>					
					<li>
						<a href="/gallery.index.html">
							<span class="nobr"><%=i18n.getText("gallery") %></span>
						</a>
					</li>
					<li>
						<a href="/preview.index.html">
							<span class="nobr"><%=i18n.getText("preview") %></span>
						</a>
					</li>
					<!--
					<li>
						<a href="/homepage.index.html">
							<span class="nobr"><%=i18n.getText("homepage") %></span>
						</a>
					</li>
					-->
					<li>
						<a href="/account.index.html">
							<span class="nobr"><%=i18n.getText("account") %></span>
						</a>
					</li>
					<li><hr/></li>
					<li>
						<a href="/language.html">
							<span class="nobr"><%=i18n.getText("language") %></span>
						</a>
					</li>
					<li>
						<a href="/logout.html">
							<span class="nobr"><%=i18n.getText("login.out") %></span>
						</a>
					</li>
				</ul>
			</div>
<%if(login.getParentId()==Account.SYSTEM_ID || login.getParentId()==Account.ADMIN_ID) {%>
			<div class="right">
				<a href="javascript:menu('admin')"><%=i18n.getText("admin") %></a>
				<ul id="menu_admin" class="menu">
					<li>
						<a href="/admin.image.html">
							<span class="nobr"><%=i18n.getText("admin.image") %></span>
						</a>
					</li>
					<li>
						<a href="/admin.referer.html">
							<span class="nobr"><%=i18n.getText("admin.referer") %></span>
						</a>
					</li>
					<li>
						<a href="/admin.account.html">
							<span class="nobr"><%=i18n.getText("admin.account") %></span>
						</a>
					</li>
<!--
					<li>
						<a href="/admin.log.html">
							<span class="nobr"><%=i18n.getText("admin.log") %></span>
						</a>
					</li>
-->
					<li>
						<a href="/admin.memory.html">
							<span class="nobr"><%=i18n.getText("admin.memory") %></span>
						</a>
					</li>
					<li>
						<a href="/admin.statistic.html">
							<span class="nobr"><%=i18n.getText("admin.statistic") %></span>
						</a>
					</li>
				</ul>
			</div>
<%} else if(login.getParentId()==Account.BUSINESS_ID) {%>
			<div class="right">
				<a href="javascript:menu('admin')"><%=i18n.getText("admin") %></a>
				<ul id="menu_admin" class="menu">
					<li>
						<a href="/admin.image.html">
							<span class="nobr"><%=i18n.getText("admin.image") %></span>
						</a>
					</li>
					<li>
						<a href="/admin.account.html">
							<span class="nobr"><%=i18n.getText("admin.account") %></span>
						</a>
					</li>
				</ul>
			</div>
<%}
	} else { %>
			<div class="right">
				<a href="/register.html" style="color: red;"><%=i18n.getText("register") %></a>
				<a href="/login.html"><%=i18n.getText("login.on") %></a>
			</div>
<%} %>
			
			<div class="right">
				<a href="javascript:menu('community')"><%=i18n.getText("community") %></a>
				<ul id="menu_community" class="menu">
					<li>
						<a href="http://blog.enlightware.com">
							<span class="nobr"><%=i18n.getText("community.blog") %></span>
						</a>
					</li>
					<li>
					<!--
					<li>
						<a href="/community.chat.html">
							<span class="nobr"><%=i18n.getText("community.chat") %></span>
						</a>
					</li>
					<li>
					-->
						<a href="/community.news.html">
							<span class="nobr"><%=i18n.getText("community.news") %></span>
						</a>
					</li><hr/><li>
					<li>
						<a href="http://info.joetoe.com/contact?pid=pixpack&language=<%=language %>" onclick="return open_link('http://info.joetoe.com/contact?pid=pixpack&language=<%=language %>')";>
							<span class="nobr"><%=i18n.getText("contact") %></span>
						</a>
					</li>
					<li>
						<a href="/about.html">
							<span class="nobr"><%=i18n.getText("about") %></span>
						</a>
					</li>
					<li>
						<a href="/community.thanks.html">
							<span class="nobr"><%=i18n.getText("community.thanks") %></span>
						</a>
					</li>
					<li>
						<a href="/donation.html">
							<span class="nobr"><%=i18n.getText("donation") %></span>
						</a>
					</li>
				</ul>
			</div>
			<div class="right">
				<a href="javascript:menu('extra')"><%=i18n.getText("extra") %></a>
				<ul id="menu_extra" class="menu">
				<!--
					<a href="/extra.smilie.html">
						<span class="nobr"><%=i18n.getText("extra.smilie") %></span>
					</a>
					<br/>
					<a href="/extra.ecard.html">
						<span class="nobr"><%=i18n.getText("extra.ecard") %></span>
					</a>
					<br/>
					<a href="/extra.recommendation.html">
						<span class="nobr"><%=i18n.getText("extra.recommendation") %></span>
					</a>
					<hr/>
					-->
					<li>
						<a href="javascript:extra_bookmark()">
							<span class="nobr"><%=i18n.getText("extra.bookmark") %></span>
						</a>
					</li>
					<li>
						<a href="/extra.sidebar.html">
							<span class="nobr"><%=i18n.getText("extra.sidebar") %></span>
						</a>
					</li>
					<li>
						<a href="/extra.random.html">
							<span class="nobr"><%=i18n.getText("extra.random") %></span>
						</a>
					</li>
					<!--
					<hr/>
					<a href="/extra.firefox.html">
						<span class="nobr"><%=i18n.getText("extra.firefox") %></span>
					</a>
					<br/>
					<a href="/extra.widget.html">
						<span class="nobr"><%=i18n.getText("extra.widget") %></span>
					</a>
					<br/>
					<a href="/extra.upload.html">
						<span class="nobr"><%=i18n.getText("extra.upload") %></span>
					</a>
					<br/>
					<a href="/extra.random.html">
						<span class="nobr"><%=i18n.getText("extra.random") %></span>
					</a>
					<br/>
					<a href="/extra.gallery.html">
						<span class="nobr"><%=i18n.getText("extra.gallery") %></span>
					</a>
					<br/>
					<a href="/extra.homepage.html">
						<span class="nobr"><%=i18n.getText("extra.homepage") %></span>
					</a>
					<hr/>
					<a href="/extra.pay.html">
						<span class="nobr"><%=i18n.getText("extra.pay") %>
					</a>
					<br/>
					<a href="/extra.business.html">
						<span class="nobr"><%=i18n.getText("extra.business") %></span>
					</a>
					-->
				</ul>
			</div>
	</div>
