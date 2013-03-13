<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:pixpack="com.enlightware.pixpack.xsl"
		exclude-result-prefixes="#default pixpack">

	<xsl:import href="../template.xsl"/>

	<xsl:template match="/gallery">
		<xsl:apply-templates select="title"/>
		<xsl:apply-templates select="description"/>

		<div class="gallery_image">
			<xsl:choose>
				<xsl:when test="$col and $col>0">
					<table>
						<tr>
							<xsl:for-each select="image">
								<td>
									<xsl:call-template name="image"/>
								</td>
								<xsl:choose>
									<xsl:when test="(position() mod $col=0) and position()!=last()">
										<xsl:text disable-output-escaping="yes">
											&lt;/tr&gt;&lt;tr&gt;
										</xsl:text>
									</xsl:when>
<!--									
									<xsl:when test="position()=last()">
										<xsl:if test="position() mod $col!=0">
											<xsl:call-template name="endTable">
												<xsl:with-param name="position" select="position()"/>
											</xsl:call-template>
										</xsl:if>
										<xsl:if test="$row">
											<xsl:if test="$position&gt;1">
												<a>
													<xsl:attribute name="href">
														<xsl:text>?position=</xsl:text>
														<xsl:value-of select="$position - $col * $row"/>
														<xsl:text>&amp;</xsl:text>
														<xsl:call-template name="addParameter"/>
													</xsl:attribute>
													<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
												</a>
											</xsl:if>
											<xsl:if test="position()+$position&lt;=$last">
												<a>
													<xsl:attribute name="href">
														<xsl:text>?position=</xsl:text>
														<xsl:value-of select="$position + position()"/>
														<xsl:text>&amp;</xsl:text>
														<xsl:call-template name="addParameter"/>
													</xsl:attribute>
													<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
												</a>
											</xsl:if>
										</xsl:if>
									</xsl:when>
-->
								</xsl:choose>
							</xsl:for-each>
						</tr>
					</table>
					<xsl:value-of disable-output-escaping="yes" select="pixpack:Gallery.page($gallery)"/>
 				</xsl:when>
				<xsl:otherwise>
					<div style="float: left">
						<xsl:for-each select="image">
							<xsl:call-template name="image"/>
						</xsl:for-each>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:call-template name="advertisement"/>
	</xsl:template>
	
	<xsl:template match="title">
		<div class="gallery_title">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>
		
	<xsl:template match="description">
		<div class="gallery_description">
			<xsl:for-each select=".">
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates/>
				</xsl:copy>
			</xsl:for-each>
		</div>
	</xsl:template>
	
	<xsl:template match="image">
	</xsl:template>

	<xsl:template name="image">
		<a style="text-decoration: none">
			<xsl:attribute name="href">
				<xsl:text>javascript:image_open('</xsl:text>
				<xsl:text>http://</xsl:text>
				<xsl:value-of select="$url"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="timestamp"/>
				<xsl:text>_</xsl:text>
				<xsl:value-of select="key"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of select="extension"/>
				<xsl:text>',</xsl:text>
				<xsl:value-of select="width"/>
				<xsl:text>,</xsl:text>
				<xsl:value-of select="height"/>
				<xsl:text>)</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="name"/>
			</xsl:attribute>

			<img>
				<xsl:attribute name="src">
					<xsl:text>http://</xsl:text>
					<xsl:value-of select="$url"/>
					<xsl:text>/</xsl:text>
					<xsl:text>_</xsl:text>
					<xsl:value-of select="timestamp"/>
					<xsl:text>_</xsl:text>
					<xsl:value-of select="key"/>
					<xsl:text>.</xsl:text>
					<xsl:value-of select="extension"/>
				</xsl:attribute>
				<xsl:attribute name="alt">
					<xsl:value-of select="name"/>
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="name"/>
				</xsl:attribute>
			</img>
		</a>
	</xsl:template>
	
	<xsl:template name="endTable">
		<xsl:param name="position"/>

		<td>&#160;</td>
<!--		
		<xsl:if test="$position and $position mod $col!=0">
			<xsl:call-template name="endTable">
				<xsl:with-param name="position" value="$position+1"/>
			</xsl:call-template>
		</xsl:if>
-->
	</xsl:template>
</xsl:stylesheet>