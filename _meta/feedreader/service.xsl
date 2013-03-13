<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	xmlns:wfw="http://wellformedweb.org/CommentAPI/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:fr="com.laukien.bean.feedreader.XSL"
	exclude-result-prefixes="#default xsl content wfw dc fr">
 
 	<xsl:param name="locale" select="'en_US'"/>
 	<xsl:param name="detail" select="'false'"/>
 	<xsl:param name="content" select="'false'"/>
 	<xsl:param name="description" select="'false'"/>
 
	<xsl:template match="rss">
		<xsl:apply-templates/>
	</xsl:template>
 
	<xsl:template match="channel">
		<xsl:apply-templates select="item"/>
	</xsl:template>
 
	<xsl:template match="item">
		<xsl:if test="position()&lt;=4">
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="link" />
					</xsl:attribute>
					<xsl:attribute name="onclick">
						<xsl:text>return open_link('</xsl:text>
						<xsl:value-of select="link" />
						<xsl:text>')</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="title" />
				</a>
			</li>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>