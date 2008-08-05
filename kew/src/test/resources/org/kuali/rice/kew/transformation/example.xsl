<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="html" indent="yes"/>
<xsl:include href="http://localhost:8080/en-dev/example3.xsl"/>

<xsl:template match="/">
  <html>
    <xsl:apply-templates/>
  <hr/>
  </html>
</xsl:template>

<xsl:template match="sms">
  <head>
   <title>
    A simple message 
   </title>
  </head>
   <body>
    <xsl:apply-templates/>
   </body>
</xsl:template>

<xsl:template match="title">
  <hr/>
  <h1>
    <xsl:apply-templates/>
  </h1>
</xsl:template>

<xsl:template match="mesg">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="tm">
    <xsl:apply-templates/>
  <sup>(TM)</sup>
</xsl:template>

<xsl:template match="like">
  <xsl:call-template name="like"/>
</xsl:template>



</xsl:stylesheet>

