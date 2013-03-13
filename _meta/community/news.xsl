<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pixpack="com.enlightware.pixpack.xsl.Function">
 
 	<xsl:param name="language" select="'en_US'"/>
 
	<xsl:template match="rss">
		<xsl:apply-templates/>
	</xsl:template>
 
	<xsl:template match="channel">
		<b>
			<a target="_blank">
				<xsl:attribute name="href">
					<xsl:value-of select="link"/>
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="description"/>
				</xsl:attribute>
				<xsl:value-of select="title"/>
			</a>
		</b>
		<br/>
		<br/>
	<!--
		<div class="channel">
			<xsl:element name="a">
     			<xsl:attribute name="href">
      				<xsl:value-of select="link" />
				</xsl:attribute>
				<xsl:value-of select="title" />
			</xsl:element>
		</div>
		<xsl:apply-templates select="description"/>
	-->
		<table>
			<xsl:apply-templates select="item"/>
		</table>
		<br/>
		<b>
			<a target="_blank">
				<xsl:attribute name="href">
					<xsl:value-of select="link"/>
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="description"/>
				</xsl:attribute>
				<xsl:value-of select="title"/>
			</a>
		</b>
	</xsl:template>
 
	<xsl:template match="item">
		<tr>
			<td>
				<img style="width: 16px; height: 16px">
					<xsl:attribute name="src">
						<xsl:choose>
							<xsl:when test="contains(category,'JoeToe')">
								<xsl:text>http://joetoe.com/favicon.ico</xsl:text>
								</xsl:when>
							<xsl:when test="contains(category,'Image')">
								<xsl:text>http://image.joetoe.com/favicon.ico</xsl:text>
							</xsl:when>
							<xsl:when test="contains(category,'HowTo')">
								<xsl:text>http://howto.joetoe.com/favicon.ico</xsl:text>
							</xsl:when>
							<xsl:when test="contains(category,'Text')">
								<xsl:text>http://text.joetoe.com/favicon.ico</xsl:text>
							</xsl:when>
							<xsl:when test="contains(category,'Fun')">
								<xsl:text>http://fun.joetoe.com/favicon.ico</xsl:text>
							</xsl:when>
							<xsl:when test="contains(category,'Search')">
								<xsl:text>http://search.joetoe.com/favicon.ico</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>http://enlightware.com/favicon.ico</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</img>
			</td>
			<td>
				<a target="_blank">
					<xsl:attribute name="href">
						<xsl:value-of select="link" />
					</xsl:attribute>
					<xsl:value-of select="title" />
				</a>
			</td>
			<td>
				<i>
					<xsl:value-of select="pixpack:convertBlogDate(pubDate,$language)"/>
				</i>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>