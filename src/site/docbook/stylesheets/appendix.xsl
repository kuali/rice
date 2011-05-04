<?xml version='1.0'?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:d="http://docbook.org/ns/docbook"
	exclude-result-prefixes="d"
	version="1.0">

	<!-- updating toc to prepend the word Appendix to appendices  -->	
	<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl"/>
	<xsl:template match="lineannotation">
		<fo:inline font-style="italic">
			<xsl:call-template name="inline.charseq" />
		</fo:inline>
	</xsl:template>
	
	<xsl:template name="toc.line">
		<xsl:param name="toc-context" select="NOTANODE"/>
		<xsl:variable name="id">
			<xsl:call-template name="object.id"/>
		</xsl:variable>
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="self::appendix">
					<xsl:call-template name="gentext">
						<xsl:with-param name="key">Appendix</xsl:with-param>
					</xsl:call-template>
					<xsl:text> </xsl:text>
					<xsl:apply-templates select="." mode="label.markup"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="." mode="label.markup"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<fo:block xsl:use-attribute-sets="toc.line.properties">
			<fo:inline keep-with-next.within-line="always">
				<fo:basic-link internal-destination="{$id}">
					<xsl:if test="$label != ''">
						<xsl:copy-of select="$label"/>
						<xsl:value-of select="$autotoc.label.separator"/>
					</xsl:if>
					<xsl:apply-templates select="." mode="titleabbrev.markup"/>
				</fo:basic-link>
			</fo:inline>
			<fo:inline keep-together.within-line="always">
				<xsl:text> </xsl:text>
				<fo:leader leader-pattern="dots" leader-pattern-width="3pt" leader-alignment="reference-area" keep-with-next.within-line="always"/>
				<xsl:text> </xsl:text>
				<fo:basic-link internal-destination="{$id}">
					<fo:page-number-citation ref-id="{$id}"/>
				</fo:basic-link>
			</fo:inline>
		</fo:block>
	</xsl:template>
</xsl:stylesheet>