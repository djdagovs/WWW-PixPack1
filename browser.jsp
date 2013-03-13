<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/lib/la-TagLib.I18n_5.jar" prefix="i18n" %>
<i18n:textObject id="i18n"/>

<%
	String lang=i18n.getLanguage();
	String google;
	if(lang.equals("de")) google="CAAQ0aSTlwIaCN06TiWDrw1MKL2093M";
	else if(lang.equals("es")) google="CAAQhYCUlwIaCLdDbow5Yr0VKP-393M";
	else if(lang.equals("fr")) google="CAAQ0bqUlwIaCCBrhb28I_zVKJW593M";
	else google="CAAQvYeYhAIaCEsdV5WZ4TVGKOm293M";
%>

<h3><%=i18n.getText("browser") %>Browser</h3>
<p>
	<%=i18n.getText("browser.text") %>
</p>
<table class="browser">
	<tr>
		<td colspan="2">
<script type="text/javascript"><!--
google_ad_client = "pub-2267474373281398";
google_ad_width = 468;
google_ad_height = 60;
google_ad_format = "468x60_as_rimg";
google_cpa_choice = "<%=google %>";
google_ad_channel = "0089546036";
//--></script>
<script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
</script>
		</td>		
	</tr>
	<tr><td colspan="2">&#160;</td></tr>
	<tr><td colspan="2">&#160;</td></tr>
	<tr>
		<td>
			<a href="http://www.mozilla.com/firefox" title="Firefox" target="_blank">
				<img src="/image/browser/firefox.jpg" title="Firefox"/>
				<br/>
				Firefox
			</a>
		</td>
		<td class="small"><%=i18n.getText("browser.firefox") %></td>		
	<tr>
	<tr><td colspan="2">&#160;</td></tr>
	<tr>
		<td>
			<a href="http://www.flock.com" title="Flock" target="_blank">
				<img src="/image/browser/flock.jpg" title="Flockx"/>
				<br/>
				Flock
			</a>
		</td>
		<td class="small"><%=i18n.getText("browser.flock") %></td>		
	<tr>
	<tr><td colspan="2">&#160;</td></tr>
	<tr>
		<td>
			<a href="http://www.mozilla.org/products/mozilla1.x" title="Mozilla" target="_blank">
				<img src="/image/browser/mozilla.jpg" title="Mozilla"/>
				<br/>
				Mozilla
			</a>
		</td>
		<td class="small"><%=i18n.getText("browser.mozilla") %></td>		
	<tr>
	<tr><td colspan="2">&#160;</td></tr>
	<tr>
		<td>
			<a href="http://www.mozilla.org/projects/seamonkey" title="SeaMonkey" target="_blank">
				<img src="/image/browser/seamonkey.jpg" title="SeaMonkey"/>
				<br/>
				SeaMonkey
			</a>
		</td>
		<td class="small"><%=i18n.getText("browser.seamonkey") %></td>		
	<tr>
	<tr><td colspan="2"><hr/></td></tr>
	<tr>
		<td>
			<a href="http://www.konqueror.org" title="Konqueror" target="_blank">
				<img src="/image/browser/konqueror.jpg" title="Konqueror"/>
				<br/>
				Konqueror
			</a>
		</td>
		<td class="small"><%=i18n.getText("browser.konqueror") %></td>		
	<tr>
</table>