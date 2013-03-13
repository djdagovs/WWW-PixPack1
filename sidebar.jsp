<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.enlightware.pixpack.Preview" %>

	<div id="design_sidebar">
		<div class="logo">
			<a href="http://joetoe.com" onclick="return open_link('http://joetoe.com')" title="JoeToe-Portal">
				<img title="JoeToe Portal" alt="JoeToe" src="/image/joetoe.jpg"/>
				<h2>JoeToe</h2>
			</a>
		</div>

		<div class="separator"><%=i18n.getText("recommendation") %></div>
		<div class="recommendation">
<%if(i18n.getLanguage().equals("de")) { %>
			<a href="http://dict-ator.com" onclick="return open_link('http://dict-ator.com')" title="Ein großartiges Online-Wörterbuch">Dict-ator</a>
			<a href="http://einfachlachen.de" onclick="return open_link('http://einfachlachen.de')" title="Lustige Bilder, viele Witze und freche Sprüche">Einfach Lachen</a>
			<a href="http://afeni.net" onclick="return open_link('http://afeni.net')" title="Afeni - Die größte deutsche Zitatesammlung">Zitate</a>
			<a href="http://spruch.joetoe.com" onclick="return open_link('http://spruch.joetoe.com')" title="Sprüche für jede Gelegenheit">Sprüche</a>
			<a href="http://witz.joetoe.com" onclick="return open_link('http://witz.joetoe.com')" title="Die besten Witze">Witze</a>
			<a href="http://software.enlightware.com" onclick="return open_link('http://software.joetoe.com')" title="Kostenlose Software von Enlightware">Software</a>
<%} else { %>
			<a href="http://dict-ator.com" onclick="return open_link('http://dict-ator.com')" title="A great online translator">Dict-ator</a>
			<a href="http://funsturbation.com" onclick="return open_link('http://funsturbation.com')" title="Funny pictures, many jokes and cheeky sayings">Funsturbation</a>
			<a href="http://quote.joetoe.com" onclick="return open_link('http://quote.joetoe.com')" title="Quote - The world largest collection of english quotes and aphorisms!">Quotations</a>
			<a href="http://saying.joetoe.com" onclick="return open_link('http://saying.joetoe.com')" title="Sayings for each opportunity">Sayings</a>
			<a href="http://joke.joetoe.com" onclick="return open_link('http://joke.joetoe.com')" title="The best jokes">Jokes</a>
			<a href="http://software.enlightware.com" onclick="return open_link('http://software.joetoe.com')" title="Free software provided by Enlightware">Software</a>
<%} %>
		</div>

		<div class="separator"><%=i18n.getText("preview") %></div>
		<div class="preview">
<%		
	Preview preview=new Preview();
//false
	if(false && Math.random()<0.5) {
		preview.setTimestamp("20070224141302422");
		preview.setKey("nacmbjbcjt");
	} else {
		preview.setTimestamp("20070226143549970");
		preview.setKey("tabciowjbm");
	}
	//preview.setLink("");
	//preview.setDescription("");
	//preview.setStatus(com.enlightware.pixpack.User.STATUS_PREMIUM);
	out.println(preview.show());
%>	
		</div>
		<br/>
		
<!--
		<div class="separator"><%=i18n.getText("project") %></div>
		&nbsp;
		<a href="http://gagpack.net" title="GagPack - PixPack Funpage">GagPack - Fun</a>
		<br/>
-->	

		<div class="separator"><%=i18n.getText("advertisement") %></div>
<script type="text/javascript"><!--
google_ad_client = "pub-2267474373281398";
google_ad_width = 120;
google_ad_height = 240;
google_ad_format = "120x240_as";
google_ad_type = "text";
//2007-02-19: pixpack
google_ad_channel = "3678740287";
google_color_border = "FFFFFF";
google_color_bg = "FFFFFF";
google_color_link = "000000";
google_color_text = "000000";
google_color_url = "000000";
//--></script>
<script type="text/javascript"
  src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
		<br/>