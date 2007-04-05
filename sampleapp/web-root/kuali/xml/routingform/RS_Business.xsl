<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

        <xsl:template match="/">
                <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
                        <fo:layout-master-set>
                                <fo:simple-page-master master-name="first" page-height="11in" page-width="8.5in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in" margin-top="0.5in">
                                        <fo:region-body/>
                                </fo:simple-page-master>
                        </fo:layout-master-set>
                        <fo:page-sequence master-reference="first">
                                <fo:flow flow-name="xsl-region-body">
                                        <fo:block>

                </fo:block>
                                </fo:flow>
                        </fo:page-sequence>
                </fo:root>
        </xsl:template>


        <xsl:template name="Toggle">

                <xsl:param name="checkboxNode" />

                <xsl:if test="$checkboxNode = 'Y' " >
                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                </xsl:if>

                <xsl:if test="$checkboxNode = 'N'  or $checkboxNode = 'Z' "  >
                        <fo:external-graphic  height="4mm"  src="url({$url}/checkbox2.gif)" />
                </xsl:if>

        </xsl:template>


        <xsl:template name="Toggle_No">

                <xsl:param name="checkboxNode" />

                <xsl:if test="$checkboxNode = 'N' " >
                <fo:external-graphic height="4mm" src="url({$url}/checkbox2check.gif)" />
                </xsl:if>

                <xsl:if test="$checkboxNode = 'Y' or $checkboxNode = 'Z'  " >
                        <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                </xsl:if>

        </xsl:template>


        <xsl:template name="ToggleReceipt">

                <xsl:choose>

                        <xsl:when test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'A' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="TogglePostmark">

                <xsl:choose>

                        <xsl:when test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'P' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="ToggleTarget">

                <xsl:choose>

                        <xsl:when test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'T' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="ToggleResearch">

                <xsl:choose>

                        <xsl:when test="$purpose/@PURPOSE = 'C' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>



        <xsl:template name="ToggleInstruction">

                <xsl:choose>

                        <xsl:when test="$purpose/@PURPOSE = 'A' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>



        <xsl:template name="ToggleService">

                <xsl:choose>

                        <xsl:when test="$purpose/@PURPOSE = 'F' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>



        <xsl:template name="ToggleCOPD_Yes">

                <xsl:choose>

                        <xsl:when test="$principals/@CO-PD_IND = 'Y' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="ToggleCOPD_No">

                <xsl:choose>

                        <xsl:when test="$principals/@CO-PD_IND = 'N' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="ToggleAdditionalInst_Yes">

                <xsl:choose>

                        <xsl:when test="$agency/AGENCY_DELIVERY/@ADDITIONAL_DELIVERY_INSTRUCTIONS_IND = 'Y' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="ToggleAdditionalInst_No">

                <xsl:choose>

                        <xsl:when test="$agency/AGENCY_DELIVERY/@ADDITIONAL_DELIVERY_INSTRUCTIONS_IND = 'N' " >
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2check.gif)" />
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:external-graphic height="4mm"  src="url({$url}/checkbox2.gif)" />
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>


        <xsl:template name="DueDateReceipt" >
                <xsl:if test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'A' " >
                        <xsl:value-of select="$agency/DUE_DATE/@DUE_DATE" />
                </xsl:if>
        </xsl:template>

        <xsl:template name="DueDatePostmark" >
                <xsl:if test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'P' " >
                        <xsl:value-of select="$agency/DUE_DATE/@DUE_DATE" />
                </xsl:if>
        </xsl:template>

        <xsl:template name="DueDateTarget" >
                <xsl:if test="$agency/DUE_DATE/@DUE_DATE_TYPE = 'T' " >
                        <xsl:value-of select="$agency/DUE_DATE/@DUE_DATE" />
                </xsl:if>
        </xsl:template>



<!--  The following template iterates through all the PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER nodes in the XML file.  These correspond to rows in the database  -->

<xsl:template match="PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER" >

                        <fo:table-row  height="6mm">

                                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="9" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="concat(NAME/@LAST, ', ', NAME/@FIRST)" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  ><xsl:value-of select="@TITLE" /></fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@CHART" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@ORG" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="5" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@ACTION"/></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@ACTION_DATE" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>
        </xsl:template>


        <xsl:template match="PROPOSAL/ROUTING_FORM/RESEARCH_RISK/STUDY" >

                        <fo:table-row  height="6mm">

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm">

                                                <xsl:if test=" STUDY_TYPE = 'H' " >
                                                        Human
                                                </xsl:if>

                                                <xsl:if test=" STUDY_TYPE = 'A' " >
                                                        Animal
                                                </xsl:if>

                                                <xsl:if test=" STUDY_TYPE = 'R' " >
                                                        Recombinant DNA
                                                </xsl:if>

                                                <xsl:if test=" STUDY_TYPE = 'P' " >
                                                        Pathogenic Agent
                                                </xsl:if>

                                                <xsl:if test=" STUDY_TYPE = 'T' " >
                                                        Human Tissue
                                                </xsl:if>
                                                
  <!-- 06/25/2003 pcberg: Removed as per Loras request.
                                                <xsl:if test="STUDY_REVIEW_STATUS = 'F' " >
                                                	Full
                                                </xsl:if>
                                                
                                                 <xsl:if test="STUDY_REVIEW_STATUS = 'E' " >
                                                	Expedited
                                                </xsl:if>
                                                     
                                              <xsl:if test="STUDY_REVIEW_STATUS = 'X' " >
                                                	Exempt
                                                </xsl:if>
  -->
                                            

                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm">
                                          <fo:block text-indent="1mm"><xsl:value-of select="APPROVAL_STATUS" /></fo:block>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="STUDY_NUMBER" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="APPROVAL_DATE" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm">                                                                          
                                                <xsl:if test="STUDY_REVIEW_STATUS = 'F' " >
                                                	Full
                                                </xsl:if>
                                                
                                                 <xsl:if test="STUDY_REVIEW_STATUS = 'E' " >
                                                	Expedited
                                                </xsl:if>
                                                     
                                              <xsl:if test="STUDY_REVIEW_STATUS = 'X' " >
                                                	Exempt
                                                </xsl:if>                                         
						</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="EXEMPTION_NBR" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

        </xsl:template>

         <xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR" >

                        <fo:table-row  height="6mm" >

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="17" display-align="center" padding-left="1mm">
                                        <fo:block ><xsl:value-of select="@SOURCE" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center"  padding-right="1mm">

                                                <fo:block text-indent="1mm"  text-align="right">$ <xsl:value-of select="format-number(@AMOUNT, '###,###')" /></fo:block >

                                </fo:table-cell>

                        </fo:table-row>

        </xsl:template>


        <xsl:template name="Other" >


                <fo:table-row  height="6mm" >

                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="10" display-align="center" padding-top="0.9mm">
                                <fo:block text-indent="1mm">BL</fo:block>
                        </fo:table-cell>

                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="8" display-align="center" padding-top="0.9mm">
                                <fo:block text-indent="1mm">CHEM</fo:block>
                        </fo:table-cell>

                        <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="5" display-align="center" padding-top="0.9mm">
                                <fo:block text-indent="1mm"  text-align="center">COAS</fo:block >
                        </fo:table-cell>

                </fo:table-row>

        </xsl:template>


        <xsl:template match="PROPOSAL/ROUTING_FORM/PRINCIPLES/CO-PROJECT_DIRECTORS/CO-PROJECT_DIRECTOR" mode="one">

                        <fo:table-row  height="6mm" >

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="20" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm">
                                                <fo:block text-indent="1mm"><xsl:value-of select="concat(@LAST_NAME, ', ' ,@FIRST_NAME)" /></fo:block>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@CHART"  /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="1" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm" ><xsl:value-of select="@ORG" /></fo:block >
                                </fo:table-cell>

                        </fo:table-row>

           </xsl:template>

        <xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/OTHER_UNIV_ORG">

                        <fo:table-row  height="6mm" >

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm">
                                                <fo:block text-indent="1mm"><xsl:value-of select="@CHART" /></fo:block>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><xsl:value-of select="@ORG"  /></fo:block>
                                </fo:table-cell>
                                


                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="18" display-align="center" padding-top="0.9mm" padding-left="1mm">
                                        <fo:block><xsl:value-of select="substring(@ORG_NAME,1,70)" /></fo:block >
                                </fo:table-cell>

                        </fo:table-row>

        </xsl:template>

        <xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/PERCENT_CREDIT" mode="three" >

<!--  I DO NOT UNDERSTAND WHY " DOES NOT WORK HERE.  IT WORKS FIND IN COST SHARE!    -->

                <fo:table-row  height="6mm"  >

                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="16" display-align="center" padding-top="0.9mm" >
                                <fo:block text-indent="1mm"><xsl:value-of select="@NAME" /></fo:block>
                        </fo:table-cell>

                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm">
                                <fo:block text-indent="1mm"><xsl:value-of select="@CHART" /></fo:block>
                        </fo:table-cell>

                        <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                <fo:block text-indent="1mm"><xsl:value-of select="@ORG" /></fo:block>
                        </fo:table-cell>

                        <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="1" display-align="center" padding-top="0.9mm" padding-right="1mm">
                                <fo:block text-indent="1mm" text-align="right"><xsl:value-of select="@CREDIT" /></fo:block >
                        </fo:table-cell>

                </fo:table-row>


        </xsl:template>


        <xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/IU_COST_SHARE" >


                        <fo:table-row  height="6mm" >

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="7" display-align="center" padding-top="0.9mm" padding-left="1mm">
                                        <fo:block ><xsl:value-of select=" @CHART" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm" padding-left="1mm">
                                        <fo:block > <xsl:value-of select=" @ORG" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="8" display-align="center" padding-top="0.9mm" padding-left="1mm">
                                        <fo:block > <xsl:value-of select=" @ACCOUNT" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm" text-align="right" padding-right="1mm">
                                        <fo:block text-indent="1mm" padding-right="1mm">$<xsl:value-of select="format-number(@AMOUNT, '###,###')" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

        </xsl:template>

        <xsl:template name="SeeAttached" >

                <xsl:choose>

                        <xsl:when test="$agency/AGENCY_DELIVERY/@ADDITIONAL_DELIVERY_INSTRUCTIONS_IND = 'Y' " >
                                <fo:block>Additional delivery instructions <fo:inline font-weight="bold" >**SEE ATTACHED**</fo:inline></fo:block>
                        </xsl:when>

                        <xsl:otherwise>
                                <fo:block>Additional delivery instructions</fo:block>
                        </xsl:otherwise>

                </xsl:choose>

        </xsl:template>
        
        
        <xsl:template match="PROPOSAL/ROUTING_FORM/KEYWORDS/KEYWORD" >
        
		<xsl:value-of select="concat(., ', ')   " />
           
        </xsl:template>

        <!-- pcberg 08/14/03: Added Comments. -->
        <xsl:template match="PROPOSAL/ROUTING_FORM/COMMENTS/COMMENT" >
          <fo:table-row height="6mm">
            <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="9" display-align="center" padding-top="0.9mm" padding-left="1mm">
              <fo:block><xsl:value-of select="COMMENTATOR"/></fo:block>
            </fo:table-cell>

            <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm" padding-left="1mm">
              <fo:block> <xsl:value-of select="COMMENT_TIMESTAMP" /></fo:block>
            </fo:table-cell>

            <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="11" display-align="center" padding-top="0.9mm" padding-left="1mm">
              <fo:block> <xsl:value-of select="COMMENT_TOPIC"/></fo:block>
            </fo:table-cell>
          </fo:table-row>

          <fo:table-row height="6mm">
            <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="24" display-align="center" padding-top="0.9mm" padding-left="2mm">
              <fo:block padding-right="1mm"><fo:inline font-weight="bold">Comment: </fo:inline><xsl:value-of select="COMMENT_TEXT"/></fo:block>
            </fo:table-cell>
          </fo:table-row>
        </xsl:template>

</xsl:stylesheet>
