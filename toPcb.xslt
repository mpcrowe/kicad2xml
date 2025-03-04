<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="fn"
	>

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="/"><xsl:apply-templates/></xsl:template>
<xsl:template match="kicad_mod"><xsl:apply-templates/></xsl:template>
<xsl:template match="drawing"><xsl:apply-templates/></xsl:template>
<xsl:template match="library"><xsl:apply-templates/></xsl:template>
<xsl:template match="packages"><xsl:apply-templates/></xsl:template>

<xsl:template match="footprint">
Element [0x00 "<xsl:value-of select="./fp_text/text[ @name ='value' ]"/>" "" "" 0.000000mm 0.000000mm 0.000000mm 0.000000mm 0 100 0x00]
(
	<xsl:apply-templates/>
)
</xsl:template>
<xsl:template match="text"></xsl:template>
<!--
<xsl:template match="wire[@layer = '51']">	ElementLine[ <xsl:value-of select="@y1"/>mm <xsl:value-of select="@x1"/>mm <xsl:value-of select="@y2"/>mm <xsl:value-of select="@x2"/>mm <xsl:value-of select="@width"/>mm ]
</xsl:template>
-->
<xsl:template match="fp_line[layer/@val = 'F.SilkS']">	ElementLine[ <xsl:value-of select="format-number(start/@x, '0.###')"/>mm <xsl:value-of select="format-number(start/@y, '0.###')"/>mm <xsl:value-of select="format-number(end/@x, '0.###')"/>mm <xsl:value-of select="format-number(end/@y, '0.###')"/>mm <xsl:value-of select="width/@val"/>mm ]
</xsl:template>
<xsl:template match="fp_line[layer/@val = 'F.CrtYd']">	ElementLine[ <xsl:value-of select="format-number(start/@x, '0.###')"/>mm <xsl:value-of select="format-number(start/@y, '0.###')"/>mm <xsl:value-of select="format-number(end/@x, '0.###')"/>mm <xsl:value-of select="format-number(end/@y, '0.###')"/>mm <xsl:value-of select="width/@val"/>mm ]
</xsl:template>
<xsl:template match="fp_circle[layer/@val = 'F.SilkS']">	ElementArc[ <xsl:value-of select="center/@x"/>mm <xsl:value-of select="center/@y"/>mm <xsl:value-of select="width/@val"/>mm  <xsl:value-of select="width/@val"/>mm 0 360 1000 ]
</xsl:template>
<xsl:template match="fp_circle[layer/@val = 'F.Fab-ignore']">	ElementArc[ <xsl:value-of select="center/@x"/>mm <xsl:value-of select="center/@y"/>mm <xsl:value-of select="width/@val"/>mm  <xsl:value-of select="width/@val"/>mm 0 360 1000 ]
</xsl:template>
<xsl:template match="hole">	Pin[ <xsl:value-of select="@y"/>mm  <xsl:value-of select="@x"/>mm 12200 0  <xsl:value-of select="@drill"/>mm <xsl:value-of select="@drill"/>mm "mech" "" "hole"]</xsl:template>
<!-- for SMD the x and y are the center of the pad, not the edge, also needs to account for thickness/2 number for the pcb format -->
<xsl:template match="pad[@type='smd']">	
<xsl:choose>
<xsl:when test="at/@rot = '90'">
Pad [ <xsl:value-of select="format-number(at/@x -(size/@h div 2) + (size/@w div 2), '0.###')"/>mm <xsl:value-of select="format-number(at/@y, '0.###')"/>mm <xsl:value-of select="format-number(at/@x + (size/@h div 2) -(size/@w div2), '0.###') "/>mm <xsl:value-of select="format-number(at/@y, '0.###')"/>mm <xsl:value-of select="size/@w"/>mm 8mil <xsl:value-of select="size/@w + 0.3"/>mm  "<xsl:value-of select="@number"/>" "<xsl:value-of select="@number"/>" "square" ]
</xsl:when>
<xsl:otherwise>
Pad [ <xsl:value-of select="format-number(at/@x -(size/@w div 2) + (size/@h div 2), '0.###')"/>mm <xsl:value-of select="format-number(at/@y, '0.###')"/>mm <xsl:value-of select="format-number(at/@x + (size/@w div 2) -(size/@h div2), '0.###') "/>mm <xsl:value-of select="format-number(at/@y, '0.###')"/>mm <xsl:value-of select="size/@h"/>mm 8mil <xsl:value-of select="size/@h + 0.3"/>mm  "<xsl:value-of select="@number"/>" "<xsl:value-of select="@number"/>" "square" ]
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="*">
</xsl:template>


</xsl:stylesheet>
