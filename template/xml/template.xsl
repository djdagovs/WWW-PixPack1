<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:pixpack="com.enlightware.pixpack.xsl"
		exclude-result-prefixes="#default pixpack">
<!--
	<xsl:import href="../template.xsl"/>
-->
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>	
	</xsl:template>
</xsl:stylesheet>