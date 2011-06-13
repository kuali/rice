<?xml version='1.0'?>
<!-- Overrides ulink to make it colored and underlined -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xslthl" version="1.0">

    <xsl:import href="urn:docbkx:stylesheet" />

    <!-- Make hyperlinks blue and don't display the underlying URL -->
    <xsl:param name="ulink.show" select="0" />

    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">blue</xsl:attribute>
        <xsl:attribute name="text-decoration">underline</xsl:attribute>
    </xsl:attribute-set>

</xsl:stylesheet>

