<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/2000/svg"
>
	<xsl:output indent="yes" cdata-section-elements="style" />

	<xsl:template match="kicad_mod">
	<svg height="2000" width="3000">
	<defs>
		<style type="text/css"><![CDATA[g.bar text {
		font-family: Arial;
		text-anchor: middle;
		fill: white;
		}
		g.bar rect {
			fill: black;
		}
		]]></style>
	</defs>
		<xsl:element name="g">
			<xsl:apply-templates select="footprint" />
		</xsl:element>
</svg>
</xsl:template>

	<xsl:template match="footprint">
			<xsl:element name="g">
				<xsl:attribute name="transform">translate(600,600)</xsl:attribute>
			<xsl:apply-templates/>
			</xsl:element>
	</xsl:template>

	<xsl:template match="fp_line">
	<xsl:variable name="scale">100</xsl:variable>

		<xsl:element name="path">
		<xsl:attribute name="d">M<xsl:value-of select="./start/@x *
$scale"/><xsl:text> </xsl:text><xsl:value-of 
select="0 + (./start/@y *$scale )"/> L<xsl:value-of 
select="./end/@x * $scale "/><xsl:text> </xsl:text> <xsl:value-of 
select="0 + (./end/@y *$scale )"/> </xsl:attribute>
		<xsl:choose>
			<xsl:when test="./layer/@val = 'F.CrtYd'">
				<xsl:attribute name="style">stroke:magenta; stroke-width:<xsl:value-of
select="round((./width/@val)*$scale + 0.5)"/>; fill:magenta</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.SilkS'">
				<xsl:attribute name="style">stroke:lightyellow; stroke-width:<xsl:value-of
select="round((./width/@val)*$scale + 0.5)"/>; fill:lightyellow</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.Fab'">
				<xsl:attribute name="style">stroke:lightseagreen; stroke-width:<xsl:value-of
select="round((./width/@val)*$scale +0.5)"/>; fill:lightseagreen</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="style">stroke:black; stroke-width:<xsl:value-of
select="round((./width/@val)*$scale +0.5)"/>; fill:black</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="*"/>
</xsl:stylesheet>
