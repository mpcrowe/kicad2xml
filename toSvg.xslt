<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/2000/svg"
	xmlns:math="http://exslt.org/math"
        extension-element-prefixes="math"
>
	<xsl:import href="./functions/sqrt/math.sqrt.template.xsl"/>
	<xsl:output indent="yes" cdata-section-elements="style" />
	<xsl:variable name="scale">1000</xsl:variable>

	<xsl:template match="kicad_mod">
		<xsl:element name="svg">
			<xsl:attribute name="height"><xsl:value-of select="15 * $scale"/></xsl:attribute>
			<xsl:attribute name="width"><xsl:value-of select="15 * $scale"/></xsl:attribute>
			<xsl:element name="g">
				<xsl:apply-templates select="footprint" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="footprint">
		<xsl:element name="g">
			<xsl:attribute name="transform">translate(<xsl:value-of select="7 * $scale"/>, <xsl:value-of select="7 * $scale"/>)</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="fp_line">
		<xsl:element name="path">
		<xsl:attribute name="d">M<xsl:value-of select="./start/@x * $scale"/><xsl:text> </xsl:text><xsl:value-of select="0 + (./start/@y *$scale )"/> L<xsl:value-of select="./end/@x * $scale "/><xsl:text> </xsl:text> <xsl:value-of select="0 + (./end/@y *$scale )"/> </xsl:attribute>
		<xsl:choose>
			<xsl:when test="./layer/@val = 'F.CrtYd'">
				<xsl:attribute name="style">stroke:magenta; stroke-width:<xsl:value-of select="round((./width/@val)*$scale + 0.5)"/>; fill:magenta</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.SilkS'">
				<xsl:attribute name="style">stroke:lightyellow; stroke-width:<xsl:value-of select="round((./width/@val)*$scale + 0.5)"/>; fill:lightyellow</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.Fab'">
				<xsl:attribute name="style">stroke:lightseagreen; stroke-width:<xsl:value-of select="round((./width/@val)*$scale +0.5)"/>; fill:lightseagreen</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="style">stroke:black; stroke-width:<xsl:value-of select="round((./width/@val)*$scale +0.5)"/>; fill:black</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template match="fp_text">
		<xsl:element name="text">
		<xsl:attribute name="x"><xsl:value-of select="./at/@x *$scale"/></xsl:attribute>
		<xsl:attribute name="y"><xsl:value-of select="./at/@y *$scale"/></xsl:attribute>
		<xsl:attribute name="font-size"><xsl:value-of select="1.5 * ./effects/font/size/@h *$scale"/></xsl:attribute>
		
		<xsl:choose>
			<xsl:when test="./layer/@val = 'F.CrtYd'">
				<xsl:attribute name="fill">red</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.SilkS'">
				<xsl:attribute name="fill">lightyellow</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.Fab'">
				<xsl:attribute name="fill">lightseagreen</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="fill">black</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="./text"/>
		</xsl:element>
	</xsl:template>



	<xsl:template match="pad">
		<xsl:element name="path">
		<xsl:attribute name="d">M<xsl:value-of 
select="(./at/@x - (./size/@w div 2)) * $scale"/><xsl:text> </xsl:text><xsl:value-of 
select="./at/@y * $scale"/> L<xsl:value-of 
select="(./at/@x + (./size/@w div 2)) * $scale "/><xsl:text> </xsl:text> <xsl:value-of 
select="./at/@y * $scale"/> </xsl:attribute>
		<xsl:choose>
			<xsl:when test="./layer/@val = 'F.CrtYd'">
				<xsl:attribute name="style">stroke:magenta; stroke-width:<xsl:value-of
select="round((./size/@h)*$scale + 0.5)"/>; fill:magenta</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.SilkS'">
				<xsl:attribute name="style">stroke:lightyellow; stroke-width:<xsl:value-of 
select="round((./size/@h)*$scale + 0.5)"/>; fill:lightyellow</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.Fab'">
				<xsl:attribute name="style">stroke:lightseagreen; stroke-width:<xsl:value-of 
select="round((./size/@h)*$scale +0.5)"/>; fill:lightseagreen</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="style">stroke:black; stroke-width:<xsl:value-of 
select="round((./size/@h)*$scale +0.5)"/>; fill:black</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:element>
	</xsl:template>


	<xsl:template match="fp_circle">
		<xsl:element name="circle">
		<xsl:variable name="dx" select="./end/@x - ./center/@x"/>
		<xsl:variable name="dy" select="./end/@y - ./center/@y"/>
		<xsl:variable name="radius">
		<xsl:call-template name="math:sqrt">
			<xsl:with-param name="value" select="$dx*$dx + $dy*$dy" />
		</xsl:call-template>
		</xsl:variable>

		<xsl:attribute name="r"> <xsl:value-of select="$scale * $radius"/></xsl:attribute>
		<xsl:attribute name="cx"> <xsl:value-of select="$scale * ./center/@x"/></xsl:attribute>
		<xsl:attribute name="cy"> <xsl:value-of select="$scale * ./center/@y"/></xsl:attribute>
		<xsl:choose>
			<xsl:when test="./layer/@val = 'F.CrtYd'">
				<xsl:attribute name="style">stroke:magenta; stroke-width:<xsl:value-of select="round((./width/@val)*$scale + 0.5)"/>; fill:magenta</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.SilkS'">
				<xsl:attribute name="style">stroke:lightyellow; stroke-width:<xsl:value-of select="round((./width/@val)*$scale + 0.5)"/>; fill:lightyellow</xsl:attribute>
			</xsl:when>
			<xsl:when test="./layer/@val = 'F.Fab'">
				<xsl:attribute name="style">stroke:lightseagreen; stroke-width:<xsl:value-of select="round((./width/@val)*$scale +0.5)"/>; fill:lightseagreen</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="style">stroke:black; stroke-width:<xsl:value-of select="round((./width/@val)*$scale +0.5)"/>; fill:black</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template match="*"/>

</xsl:stylesheet>
