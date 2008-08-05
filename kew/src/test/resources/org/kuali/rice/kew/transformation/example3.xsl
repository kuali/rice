<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="html" indent="yes"/>
<xsl:template name="like">
  <b>
  <font color="red">
    <xsl:apply-templates/>
  </font> 
  </b>
</xsl:template>

</xsl:stylesheet>

