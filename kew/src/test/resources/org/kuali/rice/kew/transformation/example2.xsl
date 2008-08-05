<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="html" indent="yes"/>

<table>
	<tr>
		<td><xsl:call-template name="like">
                <xsl:with-param name="department" select="department"/>
			</xsl:call-template>
			
		</td>
	</tr>
</table>

</xsl:stylesheet>

