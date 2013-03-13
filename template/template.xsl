<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:pixpack="com.enlightware.pixpack.xsl"
		exclude-result-prefixes="#default pixpack">
		
<!-- Parameters -->
	<xsl:param name="gallery"/>


<!-- Variables -->
	<xsl:variable name="url" select="pixpack:Gallery.getURL()"/>
	<xsl:variable name="timestamp" select="pixpack:Gallery.getTimestamp($gallery)"/>
	<xsl:variable name="key" select="pixpack:Gallery.getKey($gallery)"/>
	<xsl:variable name="template" select="pixpack:Gallery.getTemplate($gallery)"/>
	<xsl:variable name="style" select="pixpack:Gallery.getStyle($gallery)"/>
	<xsl:variable name="position" select="pixpack:Gallery.getPosition($gallery)"/>
	<xsl:variable name="last" select="pixpack:Gallery.getLast($gallery)"/>
	<xsl:variable name="col" select="pixpack:Gallery.getCol($gallery)"/>
	<xsl:variable name="row" select="pixpack:Gallery.getRow($gallery)"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>PixPack</title>
				<xsl:if test="//style">
					<link rel="stylesheet" type="text/css">
						<xsl:attribute name="href">
							<xsl:text>http://</xsl:text>
							<xsl:value-of select="$url"/>
							<xsl:text>/template/</xsl:text>
							<xsl:value-of select="$template"/>
							<xsl:text>/style/</xsl:text>
							<xsl:value-of select="$style"/>
							<xsl:text>.css</xsl:text>
						</xsl:attribute>
					</link>
				</xsl:if>
				<xsl:if test="pixpack:Gallery.getCSS($gallery)">
					<link rel="stylesheet" type="text/css">
						<xsl:attribute name="href">
							<xsl:value-of select="pixpack:Gallery.getCSS($gallery)"/>
						</xsl:attribute>
					</link>
				</xsl:if>
				<script type="text/javascript">
					<xsl:attribute name="src">
						<xsl:text>http://</xsl:text>
						<xsl:value-of select="$url"/>
						<xsl:text>/template/</xsl:text>
						<xsl:value-of select="$template"/>
						<xsl:text>/script/default.js</xsl:text>
					</xsl:attribute>
					&#160;
				</script>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="addParameter">
		<xsl:text>timestamp=</xsl:text>
		<xsl:value-of select="$timestamp"/>
		<xsl:text>&amp;key=</xsl:text>
		<xsl:value-of select="$key"/>
	</xsl:template>
	
	<xsl:template name="advertisement">
		<xsl:if test="pixpack:Gallery.isAdvertisement($gallery)">
		<div style="text-align: center">

<!--JavaScript General Redirect Tag // Tag for network 525: EAG // Website: Laukien dot COM // Page: pixpack.net // Placement: pixpack.net-communication, technics-468 x 60 (1448415) // created at: 25-Oct-07 PM 01:06-->
<script language="javascript1.1" src="http://adserver.easyad.info/addyn|3.0|525|1448415|0|1|ADTECH;loc=100;target=_blank;misc=[TIMESTAMP];rdclick="></script><noscript><a href="http://adserver.easyad.info/adlink|3.0|525|1448415|0|1|ADTECH;loc=300;misc=[TIMESTAMP];rdclick=" target="_blank"><img src="http://adserver.easyad.info/adserv|3.0|525|1448415|0|1|ADTECH;loc=300;misc=[TIMESTAMP]" border="0" width="468" height="60"/></a></noscript>
<!-- End of JavaScript Tag -->

		</div>
		</xsl:if>
	</xsl:template>
		
</xsl:stylesheet>