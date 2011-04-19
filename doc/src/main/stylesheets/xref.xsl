<?xml version='1.0'?>
<!-- Overrides ulink to make it colored and underlined -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:exsl="http://exslt.org/common"
                xmlns:xlink='http://www.w3.org/1999/xlink'
                exclude-result-prefixes="exsl xlink"
                version='1.0'>
 
<xsl:template match="link" name="link">
  <xsl:param name="linkend" select="@linkend"/>
  <xsl:param name="targets" select="key('id',$linkend)"/>

  <xsl:param name="target" select="$targets[1]"/>

  <xsl:variable name="xrefstyle">
    <xsl:choose>
      <xsl:when test="@role and not(@xrefstyle) 
                      and $use.role.as.xrefstyle != 0">
        <xsl:value-of select="@role"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@xrefstyle"/>

      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="content">
    <fo:inline xsl:use-attribute-sets="xref.properties" text-decoration="underline" color="blue">
      <xsl:choose>
        <xsl:when test="count(child::node()) &gt; 0">
          <!-- If it has content, use it -->

          <xsl:apply-templates/>
        </xsl:when>
        <!-- look for an endterm -->
        <xsl:when test="@endterm">
          <xsl:variable name="etargets" select="key('id',@endterm)"/>
          <xsl:variable name="etarget" select="$etargets[1]"/>
          <xsl:choose>
            <xsl:when test="count($etarget) = 0">
              <xsl:message>

                <xsl:value-of select="count($etargets)"/>
                <xsl:text>Endterm points to nonexistent ID: </xsl:text>
                <xsl:value-of select="@endterm"/>
              </xsl:message>
              <xsl:text>???</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="$etarget" mode="endterm"/>

            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <!-- Use the xlink:href if no other text -->
        <xsl:when test="@xlink:href">
	  <xsl:call-template name="hyphenate-url">
	    <xsl:with-param name="url" select="@xlink:href"/>
	  </xsl:call-template>
        </xsl:when>

        <xsl:otherwise>
          <xsl:message>
            <xsl:text>Link element has no content and no Endterm. </xsl:text>
            <xsl:text>Nothing to show in the link to </xsl:text>
            <xsl:value-of select="$target"/>
          </xsl:message>
          <xsl:text>???</xsl:text>

        </xsl:otherwise>
      </xsl:choose>
    </fo:inline>
  </xsl:variable>

  <xsl:call-template name="simple.xlink">
    <xsl:with-param name="node" select="."/>
    <xsl:with-param name="linkend" select="$linkend"/>
    <xsl:with-param name="content" select="$content"/>

  </xsl:call-template>

  <!-- Add standard page reference? -->
  <xsl:choose>
    <!-- page numbering on link only enabled for @linkend -->
    <!-- There is no link element in DB5 with xlink:href -->
    <xsl:when test="not($linkend)">
    </xsl:when>
    <!-- negative xrefstyle in instance turns it off -->

    <xsl:when test="starts-with(normalize-space($xrefstyle), 'select:') 
                  and contains($xrefstyle, 'nopage')">
    </xsl:when>
    <xsl:when test="(starts-with(normalize-space($xrefstyle), 'select:') 
                  and $insert.link.page.number = 'maybe'  
                  and (contains($xrefstyle, 'page')
                       or contains($xrefstyle, 'Page')))
                  or ( $insert.link.page.number = 'yes' 
                     or $insert.link.page.number = '1')
                  or local-name($target) = 'para'">
      <xsl:apply-templates select="$target" mode="page.citation">
        <xsl:with-param name="id" select="$linkend"/>
      </xsl:apply-templates>
    </xsl:when>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
