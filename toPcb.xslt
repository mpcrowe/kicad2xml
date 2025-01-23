<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="fn"
	>

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="/"><xsl:apply-templates/></xsl:template>
<xsl:template match="eagle"><xsl:apply-templates/></xsl:template>
<xsl:template match="drawing"><xsl:apply-templates/></xsl:template>
<xsl:template match="library"><xsl:apply-templates/></xsl:template>
<xsl:template match="packages"><xsl:apply-templates/></xsl:template>

<xsl:template match="package">
Element [0x00 "<xsl:value-of select="@name"/>" "" "" 0.000000mm 0.000000mm 0.000000mm 0.000000mm 0 100 0x00]
(
	<xsl:apply-templates/>
)
</xsl:template>
<xsl:template match="text"></xsl:template>
<!--
<xsl:template match="wire[@layer = '51']">	ElementLine[ <xsl:value-of select="@y1"/>mm <xsl:value-of select="@x1"/>mm <xsl:value-of select="@y2"/>mm <xsl:value-of select="@x2"/>mm <xsl:value-of select="@width"/>mm ]
</xsl:template>
-->
<xsl:template match="wire[@layer = '21']">	ElementLine[ <xsl:value-of select="@y1"/>mm <xsl:value-of select="@x1"/>mm <xsl:value-of select="@y2"/>mm <xsl:value-of select="@x2"/>mm <xsl:value-of select="@width"/>mm ]
</xsl:template>
<xsl:template match="rectangle"></xsl:template>
<xsl:template match="circle[@layer = '21']">	ElementArc[ <xsl:value-of select="@y"/>mm <xsl:value-of select="@x"/>mm <xsl:value-of select="@width"/>mm  <xsl:value-of select="@width"/>mm 0 360 1000 ]
</xsl:template>
<xsl:template match="hole">	Pin[ <xsl:value-of select="@y"/>mm  <xsl:value-of select="@x"/>mm 12200 0  <xsl:value-of select="@drill"/>mm <xsl:value-of select="@drill"/>mm "mech" "" "hole"]
</xsl:template>
<!-- for SMD the x and y are the center of the pad, not the edge, also needs to account for thickness/2 number for the pcb format -->
<xsl:template match="smd">	Pad [ <xsl:value-of select="@y -(@dy div 2) + (@dx div 2)"/>mm <xsl:value-of select="@x"/>mm <xsl:value-of select="@y + (@dy div 2) -(@dx div2) "/>mm <xsl:value-of select="@x"/>mm <xsl:value-of select="@dx"/>mm 8mil <xsl:value-of select="@dx + 0.3"/>mm  "<xsl:value-of select="@name"/>" "<xsl:value-of select="@name"/>" "square" ]</xsl:template>

<xsl:template match="*">
</xsl:template>


</xsl:stylesheet>
