<?xml version="1.0"?>
<xsl:stylesheet version ="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
         xmlns:fo="http://www.w3.org/1999/XSL/Format" >

<!--
  The business template contains the "business logic" for this document  This includes templates for sections which expand to encompass multiple entries for a node in the XXML file

  NOTE: The inport statement will not recognize a variable.  $url cannot be used to set the path to RS_Business_4.xsl
  Define "url" and that path will lead to  the checkbox images used in the imported stylesheet.  xx

  Variables are used mainly to make the code more understandable.  There might also be preformance advantages.  By putting variables in memory, disk access time and CPU time would
  probably be reduced, provided diskRAM swapping did not occur frequently.
-->

<!--	
	      <xsl:import href="RS_Business.xsl" />
        <xsl:variable name="url" select="'.'" />
-->
        <xsl:import href="http://docs.onestart.iu.edu/dav/ERA/xslt/dev/routingForm/RS_Business.xsl" />
        <xsl:variable name="url" select="'http://docs.onestart.iu.edu/dav/ERA/xslt/dev/routingForm'" />

        <xsl:variable name="routingForm" select="PROPOSAL/ROUTING_FORM" />
        <xsl:variable name="agency" select="PROPOSAL/ROUTING_FORM/AGENCY" />
        <xsl:variable name="risk" select="PROPOSAL/ROUTING_FORM/RESEARCH_RISK" />
        <xsl:variable name="type" select="PROPOSAL/ROUTING_FORM/TYPE" />
        <xsl:variable name="principals" select="PROPOSAL/ROUTING_FORM/PRINCIPLES" />
        <xsl:variable name="purpose" select="PROPOSAL/ROUTING_FORM/PURPOSE" />
        <xsl:variable name="details" select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL" />
        <xsl:variable name="amounts_dates" select="PROPOSAL/ROUTING_FORM/AMOUNTS_DATES" />
        <xsl:variable name="keywords" select="PROPOSAL/ROUTING_FORM/KEYWORDS" />

        <xsl:variable name="emailContact" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@EMAIL" />
        <xsl:variable name="faxContact" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FAX_NUMBER" />
        <xsl:variable name="phoneContact" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@PHONE_NUMBER" />
        <xsl:variable name="firstNameContact" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FIRST_NAME" />
        <xsl:variable name="lastNameContact" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@LAST_NAME" />

        <xsl:variable name="lastNamePrincipal" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/@LAST_NAME" />
        <xsl:variable name="firstNamePrincipal" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/@FIRST_NAME" />
        
        <!-- 4/26/2005 dterret: An additional bit of data to be displayed.-->
        <xsl:variable name="submittingOrg" select="/PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG"/>
        <xsl:variable name="chart" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG/@SUBMITTING_CHART" />
        <xsl:variable name="org" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG/@SUBMITTING_ORG" />

       <xsl:variable name="chartPD" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/HOME_ORG/@HOME_CHART" />
        <xsl:variable name="orgPD" select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/HOME_ORG/@HOME_ORG" />


<!--   The following attribute sets are intuitively named.  "a" refers to the left side of a cell, "b" refers to the top of the cell, "c" refers the right side, and "d" refers to the bottom.    Attribute
        set "abc" shows solid borders on the left side, top, and bottom of a cell.             -->

<xsl:attribute-set name="abcd" >
  <xsl:attribute name="border-style" >solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="abd" >
  <xsl:attribute name="border-style" >solid</xsl:attribute>
  <xsl:attribute name="border-right-width" >0mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="ab" >
  <xsl:attribute name="border-left-style" >solid</xsl:attribute>
  <xsl:attribute name="border-top-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="bc" >
  <xsl:attribute name="border-right-style" >solid</xsl:attribute>
  <xsl:attribute name="border-top-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="ad" >
  <xsl:attribute name="border-left-style" >solid</xsl:attribute>
  <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cd" >
  <xsl:attribute name="border-right-style" >solid</xsl:attribute>
  <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="bcd" >
        <xsl:attribute name="border-right-style" >solid</xsl:attribute>
         <xsl:attribute name="border-top-style">solid</xsl:attribute>
          <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="abd" >
  <xsl:attribute name="border-style" >solid</xsl:attribute>
  <xsl:attribute name="border-right-width" >0mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="abc" >
  <xsl:attribute name="border-left-style" >solid</xsl:attribute>
  <xsl:attribute name="border-top-style">solid</xsl:attribute>
  <xsl:attribute name="border-right-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="a" >
  <xsl:attribute name="border-left-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="b" >
  <xsl:attribute name="border-top-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="c" >
  <xsl:attribute name="border-right-style">solid</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="d" >
  <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
</xsl:attribute-set>

<!--  Defining an all-encompassing template here is standard procedure.  The same is true of "fo:root."    -->
                    <xsl:template match="/">

        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">


<fo:layout-master-set>
    <fo:simple-page-master master-name="first"
                           page-height="11in"
                           page-width="8.5in"
                           margin-bottom="0.2in"
                           margin-left="0.5in"
                           margin-right="0.5in"
                                margin-top="0.5in">

                <fo:region-after extent="0.25in" />

             <fo:region-body margin-bottom="0.35in" />
      </fo:simple-page-master>
 </fo:layout-master-set>

  <fo:page-sequence master-reference="first">

  <!--  The following code controls page numbering  It is used in conjunction with <fo:block id="End" ></fo:block> at the end of the stylesheet.-->

        <fo:static-content flow-name="xsl-region-after">
                <fo:block text-align="center" font-size="8pt" padding="3mm">
                <fo:page-number /> of <fo:page-number-citation ref-id="End" />
                </fo:block>
        </fo:static-content>

<!--   This table was intended for use throughout the entire document.  A numbered grid (below the header block) can be temporarily placed anywhere on the page.  The grid makes it convenient to place items precisely the first time, spanning and eliminating cells with minimal experimentation.  The original plan was modified, and a table almost identical to this one is used in a template to display pages after page 1..  -->


                <fo:flow flow-name="xsl-region-body">

        <fo:table table-layout="fixed" >


        <fo:table-column column-width="4mm"/>			4
        <fo:table-column column-width="7mm"/>			11
        <fo:table-column column-width="4mm"/>			15
        <fo:table-column column-width="11mm"/>			26
        <fo:table-column column-width="4mm"/>			30

        <fo:table-column column-width="5mm"/>			35
        <fo:table-column column-width="10mm"/>			45
        <fo:table-column column-width="11mm"/>              56
        <fo:table-column column-width="22mm"/>			78
        <fo:table-column column-width="22mm"/>			100

        <fo:table-column column-width="5mm"/>			105
        <fo:table-column column-width="10mm"/>			115
        <fo:table-column column-width="5mm"/>			120
        <fo:table-column column-width="5mm"/> 			125
        <fo:table-column column-width="5mm"/>      		130

        <fo:table-column column-width="4mm"/>			134
        <fo:table-column column-width="6mm"/>			140
        <fo:table-column column-width="3mm"/>			143

       <fo:table-column column-width="9mm"/>			152
       <fo:table-column column-width="8mm"/>			160

        <fo:table-column column-width="12mm"/>			177

        <fo:table-column column-width="18.5mm"/>		190.5


        <fo:table-header  font-size="9pt" >

                        <fo:table-row height="5mm">

                                <fo:table-cell number-columns-spanned="5" />

                                <fo:table-cell number-columns-spanned="12" padding-left="20mm">
                                        <fo:block ><fo:inline font-weight="bold" font-size="11pt">Indiana University ERA: Routing Form Overview</fo:inline></fo:block>
                                </fo:table-cell>


                                <fo:table-cell number-columns-spanned="4" >
                                        <fo:block text-align="right"><fo:inline font-weight="bold" >Tracking #</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block text-align="right"><xsl:value-of select="$routingForm/@TRACKING_NUMBER" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                                <fo:table-row height="5mm">

                                <fo:table-cell number-columns-spanned="18">
                                        <fo:block></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3" >
                                        <fo:block text-align="right"><fo:inline font-weight="bold">Proposal #</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block text-align="right"><xsl:value-of select="$routingForm/@PROPOSAL_NUMBER" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>




        </fo:table-header>

                <fo:table-body font-size="9pt" >

<!--       ** NUMBERED GRID **   This is the grid that can be temporarily displayed as a guide.  Just cut and paste it where it is needed, and delete it when you are done.  This grid is for use inside the root template.  An extra cell was added for the template called "Next."  Grid code is located in that template.

                        <fo:table-row >

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>1</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" >
                                        <fo:block>2</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>3</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>4</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>5</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>6</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>7</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>8</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>9</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>10</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>11</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>12</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>13</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>14</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>15</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>16</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>17</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>18</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>19</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>20</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>21</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd">
                                        <fo:block>22</fo:block>
                                </fo:table-cell>

                        </fo:table-row>

-->

                        <fo:table-row>

                                <fo:table-cell number-columns-spanned="23" text-align="center" font-weight="bold"><fo:block>
                                        <fo:block>To assure on time delivery, each request and approved routing form must be received by</fo:block>
                                the sponsored research office at least<fo:inline text-decoration="underline"> three (3) full business days</fo:inline> prior to the due date</fo:block>

                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="5mm" />

                        <fo:table-row>

                                <fo:table-cell number-columns-spanned="12">
                                        <fo:block><fo:inline font-weight="bold" >AGENCY: <xsl:value-of select=" $agency/AGENCY_DATA/AGENCY_FULL_NAME" /></fo:inline></fo:block>
                                </fo:table-cell>

<!--
                                <fo:table-cell number-columns-spanned="8">
                                        <fo:block>
                                                <xsl:value-of select=" $agency/AGENCY_DATA/AGENCY_FULL_NAME" />
                                        </fo:block>
                                </fo:table-cell>
-->

                                <!-- 10/06/2003 pcberg: Added
                                     fo:block-container. See
                                     Keystone #1424. -->
                                <fo:table-cell number-columns-spanned="10" number-rows-spanned="3">
                                  <fo:block-container height="15mm" width="70mm">
                                    <fo:block><fo:inline font-weight="bold">COPIES:</fo:inline>Send
                                    <xsl:choose>
                                      <!-- 06/25/2003 pcberg: Changed
                                           condition, was broken before.. -->
                                      <xsl:when test="$agency/AGENCY_DELIVERY/@COPIES != ''" >
                                        <fo:inline text-decoration="underline"> <xsl:value-of select="$agency/AGENCY_DELIVERY/@COPIES" /> </fo:inline> to the agency.
                                      </xsl:when>
                                      <xsl:otherwise>
                                        ___  to the agency.
                                      </xsl:otherwise>
                                    </xsl:choose>
                                    Submit 2 additional copies plus the number required by your department and school.</fo:block>
                                  </fo:block-container>
                                </fo:table-cell>
                              </fo:table-row>

                              <!-- blank row to push heading down. -->
                              <fo:table-row height="5mm">
                                <fo:table-cell number-columns-spanned="12">
                                  <fo:block><fo:inline font-weight="bold" ></fo:inline></fo:block>
                                </fo:table-cell>
                              </fo:table-row>

                              <fo:table-row>
                                <fo:table-cell number-columns-spanned="12">
                                  <fo:block><fo:inline font-weight="bold" >PRIMARY DELIVERY ADDRESS</fo:inline></fo:block>
                                </fo:table-cell>
                              </fo:table-row>

        <fo:table-row height="2mm">

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>
        </fo:table-row>


                        <fo:table-row white-space-collapse="false">

                                <fo:table-cell  xsl:use-attribute-sets = "abcd" number-columns-spanned="11" number-rows-spanned="7" padding-left="1mm" padding="0.5mm">
                                  <fo:block-container height="28mm" width="105mm">
                                        <fo:block> <xsl:value-of select="$agency/AGENCY_DELIVERY/DELIVERY_INSTRUCTIONS" />  </fo:block>
                                      </fo:block-container>
                                </fo:table-cell>

                        </fo:table-row>

        <fo:table-row height="5mm">

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="8" display-align="after">
                        <fo:block><fo:inline font-weight="bold" >DUE DATE</fo:inline> (mm/dd/yyyy)</fo:block>
                </fo:table-cell>

                <fo:table-cell display-align="after">
                        <fo:block text-align="center"><fo:inline font-weight="bold">TIME</fo:inline></fo:block>
                </fo:table-cell>

        </fo:table-row>


        <fo:table-row height="5mm">

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

<!--  The toggle templates are defined in the imported business logic stylesheet.  These templates display a square or a square containing a check mark, depending on what is in the XML file being
 oarsed,
        display-align="after" aligns the checkbox with the text.  This could also be accomplished with cell padding.   -->
  -->

                <fo:table-cell line-height="7pt" display-align="after">
                        <fo:block><xsl:call-template name="ToggleReceipt" /></fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="3" display-align="after"  line-height="7pt">
                        <fo:block>Receipt</fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="3" display-align="after"  line-height="7pt">
                        <fo:block text-align="center" ><xsl:call-template name="DueDateReceipt" /></fo:block>
                </fo:table-cell>

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell display-align="after" line-height="7pt" number-columns-spanned="3">
                        <fo:block text-align="left"><xsl:value-of select="$agency/DUE_DATE/@DUE_TIME" /></fo:block>
                </fo:table-cell>

        </fo:table-row>


        <fo:table-row height="5mm">

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell line-height="7pt" display-align="after">
                        <fo:block><xsl:call-template name="TogglePostmark" /></fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="3" display-align="after" line-height="7pt">
                        <fo:block>Postmark</fo:block>
                </fo:table-cell>

                <fo:table-cell  number-columns-spanned="3"  display-align="after" line-height="7pt">
                        <fo:block text-align="center" ><xsl:call-template name="DueDatePostmark" /></fo:block>
                </fo:table-cell>

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

        </fo:table-row>


        <fo:table-row height="5mm">

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell line-height="7pt" display-align="after">
                        <fo:block><xsl:call-template name="ToggleTarget" /></fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="3" display-align="after" line-height="7pt">
                        <fo:block>Target</fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="3" display-align="after" line-height="7pt">
                        <fo:block text-align="center" ><xsl:call-template name="DueDateTarget" /></fo:block>
                </fo:table-cell>

                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>

        </fo:table-row>


        <fo:table-row height="5mm">
                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>
        </fo:table-row>


        <fo:table-row height="1mm">
                <fo:table-cell>
                        <fo:block></fo:block>
                </fo:table-cell>
        </fo:table-row>

        <fo:table-row height="4.5mm">

                <fo:table-cell display-align="after">
                        <fo:block><xsl:call-template name="ToggleAdditionalInst_Yes" /></fo:block>
                </fo:table-cell>

                <fo:table-cell display-align="after">
                        <fo:block>Yes</fo:block>
                </fo:table-cell>

                <fo:table-cell display-align="after">
                        <fo:block><xsl:call-template name="ToggleAdditionalInst_No" /></fo:block>
                </fo:table-cell>

                <fo:table-cell display-align="after">
                        <fo:block>No</fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="8" display-align="after">
                        <xsl:call-template name="SeeAttached" />
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="10" display-align="after">
                        <fo:block>CFDA #: <xsl:value-of select="$routingForm/PROJECT_INFORMATION/@CFDA_TXT" /></fo:block>
                </fo:table-cell>
                
                        </fo:table-row>

        <fo:table-row height="5mm" >

        <fo:table-cell number-columns-spanned="12" />

                                <fo:table-cell number-columns-spanned="5" >
                                        <fo:block>PA, RFA, RFP #:</fo:block>
                                </fo:table-cell>

<!--     TO DO:   Get data from the XML file   -->

                                <fo:table-cell number-columns-spanned="5"  number-rows-spanned="2">
                                        <fo:block><xsl:value-of select="$agency/AGENCY_DATA/@PROGRAM_ANNOUNCEMENT_NUMBER" /></fo:block>
                                </fo:table-cell>

                </fo:table-row>

        <fo:table-row height="5mm">

                <fo:table-cell number-columns-spanned="12" />
                
                <fo:table-cell number-columns-spanned="5">
                        <fo:block></fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="5">
                        <fo:block></fo:block>
                </fo:table-cell>

        </fo:table-row >

        <fo:table-row height="5mm">

                <fo:table-cell number-columns-spanned="6">
                        <fo:block><fo:inline font-weight="bold">PROJECT DIRECTOR:</fo:inline> </fo:block>
                </fo:table-cell>

                <fo:table-cell number-columns-spanned="4">
                        <fo:block  white-space-collapse="false"><xsl:value-of select="substring(concat($lastNamePrincipal,', ',$firstNamePrincipal), 1, 20)" /></fo:block>
                </fo:table-cell>

                <fo:table-cell>
                        <fo:block text-align="right"><fo:inline text-align="right" ><xsl:value-of select="concat($chartPD,'-',$orgPD) "/></fo:inline></fo:block>
                </fo:table-cell>

                <fo:table-cell />

                <fo:table-cell number-columns-spanned="9">
                        <fo:block><fo:inline font-weight="bold">PURPOSE</fo:inline></fo:block>
                </fo:table-cell>

        </fo:table-row >


                        <fo:table-row  >
													
                                <fo:table-cell number-columns-spanned="7" >
                                	<fo:block>Submitting Org: <xsl:value-of select="concat($chart,'-',$org)" /></fo:block>
                                </fo:table-cell>
                                
                                <!-- 4/26/2005 dterret: An additional bit of data to be displayed. -->
                                <fo:table-cell number-columns-spanned="4">
                                	<fo:block text-align="right">
                                  	<xsl:value-of select="substring($submittingOrg,0,25)"/>
                                	</fo:block>
                                </fo:table-cell>
                                
                                <fo:table-cell/>

                                <fo:table-cell >
                                        <fo:block ><xsl:call-template name="ToggleResearch" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="8" display-align="after">
                                        <fo:block>Research</fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row >

                                <fo:table-cell number-columns-spanned="12">
                                        <fo:block wrap-option="no-wrap">Campus Address: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_CAMPUS_ADDRESS,1,40)" /></fo:block>zzz
                                </fo:table-cell>

                                <fo:table-cell >
                        <fo:block><xsl:call-template name="ToggleInstruction" /></fo:block>

                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="7" display-align="after">
                                        <fo:block>Instruction</fo:block>
                                </fo:table-cell>

                        </fo:table-row>


                        <fo:table-row >

                                <fo:table-cell number-columns-spanned="6">
                                        <fo:block>Phone: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_PHONE,1,40)" /></fo:block>zzz
                                </fo:table-cell>


                                <fo:table-cell number-columns-spanned="5">
                                        <fo:block text-align="right">E-mail: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_EMAIL,1,40)" /></fo:block>zzz
                                </fo:table-cell>

                                <fo:table-cell />

                           <fo:table-cell >

                        <fo:block><xsl:call-template name="ToggleService" /></fo:block>

                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2" padding-top="1mm">
                                        <fo:block white-space-collapse="false">Service</fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="1" padding-top="1mm">
                                        <fo:block white-space-collapse="false" text-indent="2mm">/</fo:block>
                                </fo:table-cell>

<!--  The "number-rows-spanned" attribute forces wrapped text down below the first line of text.  Without "number-rows-spanned='4'," the wrapped text would force the earlier lines
         of text up above each new line of wrapped text   -->

                                <fo:table-cell number-columns-spanned="6" number-rows-spanned="4" padding-top="1mm">
                                        <fo:block>Other: <xsl:value-of select="substring($purpose/PURPOSE_DESCRIPTION, 1, 100)" /></fo:block>
                                </fo:table-cell>
                        </fo:table-row>


                        <fo:table-row height="1mm" />

                        <fo:table-row height="5mm">

                                <fo:table-cell >
                                <fo:block><xsl:call-template name="ToggleCOPD_Yes" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell display-align="after">
                                        <fo:block>Yes</fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block ><xsl:call-template name="ToggleCOPD_No" /> </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="9" display-align="after">
                                        <fo:block white-space-collapse="false">No   Co-Project Director(s)</fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="0.8mm" />

 For the next three rows, we will calll a template which will provide a row for each Fellow, Contact, Phone, E-mail address and Fax number.    -->

                        <fo:table-row height="4mm">

                                <fo:table-cell number-columns-spanned="11" >
                                        <fo:block>Fellow: <xsl:value-of select="substring($principals/FELLOW,1, 50)" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="4" />

                        </fo:table-row>


                        <fo:table-row height="4.3mm">

<!--  The substring function says to begin with the first character of the name, and truncate at the 30th character   -->

                                <fo:table-cell number-columns-spanned="8" >
                                        <fo:block wrap-option="no-wrap">Contact: <xsl:value-of select="substring(concat($lastNameContact,',  ', $firstNameContact), 1,30)" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">
                                        <fo:block  text-align="right">Phone: <xsl:value-of select="$phoneContact" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>


                        <fo:table-row height="4.3mm">
                                <fo:table-cell number-columns-spanned="8" >
                                        <fo:block>E-mail: <xsl:value-of select="$emailContact" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">

                                                <xsl:if test="not(PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FAX_NUMBER = '')" >
                                                <fo:block  text-align="right">Fax: <xsl:value-of select="$faxContact" /></fo:block>
                                        </xsl:if>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="4mm"/>

                <fo:table-row >

<!--    white-space-collapse="true" is the default.  That code appears below to accommodate changing the attribute value to "false" if need be.   When the value is "false," new lines and multiple spaces are reproduced in PDF output just as they appear in the XML file.    -->

                                <fo:table-cell number-columns-spanned="22"  number-rows-spanned="3">

                                        <fo:block white-space-collapse="true"><fo:inline font-weight="bold">PROJECT TITLE: </fo:inline><xsl:value-of select="substring($routingForm/PROJECT_INFORMATION/PROJECT_TITLE, 1, 200)" />

                                        </fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="5mm" />
                        <fo:table-row height="5mm" />
                        <fo:table-row height="2mm" />
                                        <fo:table-row >

                                <fo:table-cell number-columns-spanned="22"  number-rows-spanned="3">
                                        <fo:block white-space-collapse="true"><fo:inline font-weight="bold">LAY DESCRIPTION: </fo:inline><xsl:value-of select="substring($routingForm/PROJECT_INFORMATION/LAY_DESCRIPTION, 1, 300)" />

                                        </fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="5mm" />
                        <fo:table-row height="5mm" />
                        <fo:table-row height="2mm" />

                        <fo:table-row >

                                <fo:table-cell number-columns-spanned="6">
                                        <fo:block ></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2">
                                        <fo:block  text-align="center">Direct Costs</fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2">
                                        <fo:block  text-align="center">Indirect Costs</fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">
                                        <fo:block  text-align="center">Total</fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block ></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="4">
                                        <fo:block  text-align="center" text-indent="3mm">Start</fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">
                                        <fo:block  text-align="center" text-indent="3mm">End</fo:block>
                                </fo:table-cell>

                        </fo:table-row>


                        <fo:table-row >

                                <fo:table-cell number-columns-spanned="6">
                                        <fo:block font-weight="bold">AMOUNT &amp;<!-- <xsl:value-of disable-output-escaping="yes" select="  '&amp;'  "/>--> DATES</fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2">
                                        <fo:block  text-align="center">

                                        <xsl:if test="$amounts_dates/DIRECT_COSTS &gt; 0" >
                                                 $<xsl:value-of select="format-number($amounts_dates/DIRECT_COSTS, '###,###') " />
                                        </xsl:if>

                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2">
                                        <fo:block  text-align="center">

                                        <xsl:if test="$amounts_dates/INDIRECT_COSTS &gt; 0" >
                                                $<xsl:value-of select="format-number($amounts_dates/INDIRECT_COSTS, '###,###') " />
                                         </xsl:if>

                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">
                                        <fo:block  text-align="center">

                                        <xsl:if test="$amounts_dates/TOTAL_COSTS &gt; 0" >
                                                $<xsl:value-of select="format-number($amounts_dates/TOTAL_COSTS, '###,###') " />
                                         </xsl:if>

                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block ></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="4">
                                        <fo:block  text-align="center"><xsl:value-of select="$amounts_dates/START_DATE" /></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="3">
                                        <fo:block  text-align="center"><xsl:value-of select="$amounts_dates/STOP_DATE" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

<!--  The following row serves merely to create space.   -->

                        <fo:table-row height="7mm"/>

                        <fo:table-row height="4mm">
                                <fo:table-cell>
                                        <fo:block><fo:inline font-weight="bold">TYPE</fo:inline></fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="2mm"/>

                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@NEW" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell  display-align="after">
                                        <fo:block text-indent="1.5mm">New</fo:block>
                                </fo:table-cell>

                        </fo:table-row>


                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@RENEWAL-NOT_PREVIOUSLY_COMMITTED" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="8"  display-align="after">
                                        <fo:block text-indent="1.5mm">Renewal - Not Previously Committed</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets="ab" number-columns-spanned="3" display-align="center">
                                        <fo:block text-indent="2mm">Prior Agency Grant <xsl:text disable-output-escaping="yes">#</xsl:text></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="10" xsl:use-attribute-sets="bc" display-align="center" >
                                        <fo:block><xsl:value-of select="$type/PRIOR_GRANT" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>


                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@RENEWAL-PREVIOUSLY_COMMITTED" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="8"  display-align="after">
                                        <fo:block text-indent="1.5mm">Renewal - Previously Committed</fo:block>
                                </fo:table-cell>

<!--  The space after "Current Grant" is reflected in the PDF output.  One space is always carried through.  For more spaces, "white-space-collapse = 'false' " is a required
        block or row attribute.   -->

                                <fo:table-cell xsl:use-attribute-sets="a" number-columns-spanned="3" display-align="center">
                                        <fo:block text-indent="2mm">Current Grant <xsl:text disable-output-escaping="yes">#</xsl:text></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="10" xsl:use-attribute-sets="c" display-align="center">
                                        <fo:block><xsl:value-of select="$type/CURRENT_GRANT" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@SUPPLEMENTAL_FUNDS" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="8"  display-align="after">
                                        <fo:block text-indent="1.5mm">Supplemental Funds</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets="a" number-columns-spanned="3" display-align="after">
                                        <fo:block text-indent="2mm">IU Account <xsl:text disable-output-escaping="yes">#</xsl:text></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="10" xsl:use-attribute-sets="c" display-align="after">
                                        <fo:block><xsl:value-of select="$type/IU_ACCOUNT" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@TIME_EXTENSION" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="8"  display-align="after">
                                        <fo:block text-indent="1.5mm">Time extension</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets="ad" number-columns-spanned="3" display-align="after">
                                        <fo:block text-indent="2mm">Current IU Proposal <xsl:text disable-output-escaping="yes">#</xsl:text></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="10" xsl:use-attribute-sets="cd" display-align="after">
                                        <fo:block><xsl:value-of select="$type/CURRENT_PROPOSAL" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                                <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@BUDGET_REVISION_ACTIVE" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>


                                <fo:table-cell number-columns-spanned="21"  display-align="after">
                                        <fo:block text-indent="1.5mm">Budget revision to active project</fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                                <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@BUDGET_REVISION_PENDING" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="21"  display-align="after">
                                        <fo:block text-indent="1.5mm">Budget revision to pending proposal</fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="4mm">

                                <fo:table-cell  >
                                        <fo:block>
                                                <xsl:call-template name="Toggle" >
                                                        <xsl:with-param name="checkboxNode" select="$type/@OTHER" />
                                                </xsl:call-template>
                                        </fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="2" display-align="after">
                                        <fo:block text-indent="1.5mm">Other: </fo:block>
                                </fo:table-cell>

<!--  The "number-rows-spanned" attribute forces wrapped text down below the first line of text.  Without "number-rows-spanned='2'," the wrapped text would stay on the original line, and the original text would be forced up, above it.  -->

                                <fo:table-cell number-columns-spanned="13"  number-rows-spanned="2" padding-top="1.2mm" >
                                        <fo:block ><xsl:value-of select="$type/@OTHER_DESCRIPTION" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="4mm" >

                        <fo:table-cell number-columns-spanned="3"/>

                                <fo:table-cell number-columns-spanned="16">
                                        <fo:block> </fo:block>
                                </fo:table-cell>
                        </fo:table-row>


                        <fo:table-row height="5mm">
                                <fo:table-cell >
                                        <fo:block></fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="4mm">
                                <fo:table-cell number-columns-spanned="18">
                                        <fo:block><fo:inline font-weight="bold">RESEARCH RISK</fo:inline></fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="2mm"  keep-with-next="always"  />

                <fo:table-row height="4mm">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>YES</fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                                <fo:block text-indent="5mm">NO</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$risk/@HUMAN_SUBJECTS_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@HUMAN_SUBJECTS_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="8">
                                <fo:block>Human Subjects</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">

                                <fo:block text-indent="5mm">

                                        <xsl:call-template name="Toggle" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@ANIMAL_IND" />
                                        </xsl:call-template>

                                </fo:block>

                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">

                                <fo:block text-indent="5mm">

                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@ANIMAL_IND" />
                                        </xsl:call-template>

                                </fo:block>

                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="8">
                                <fo:block>Animals</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">

                                <fo:block text-indent="5mm">

                                        <xsl:call-template name="Toggle" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@BIOSAFETY_IND" />
                                        </xsl:call-template>

                                </fo:block>

                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                        <fo:block text-indent="5mm">
                                <xsl:call-template name="Toggle_No" >
                                        <xsl:with-param name="checkboxNode" select="$risk/@BIOSAFETY_IND" />
                                </xsl:call-template>
                        </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="8">
                                <fo:block>Biosafety (rDNA)</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$risk/@HUMAN_TISSUE_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@HUMAN_TISSUE_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="19">
                                        <fo:block>Human Tissue or Fluids:
                                                <xsl:if test="not($risk/@HUMAN_TISSUE_TYPE = 'Z' )" >
                                                        <xsl:value-of select="$risk/@HUMAN_TISSUE_TYPE" />
                                        </xsl:if>
                                </fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$risk/@PATHOGENIC_AGENTS_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@PATHOGENIC_AGENTS_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="19">
                            <fo:block>Pathogenic Agents:
                                        <xsl:if test="not($risk/@PATHOGENIC_AGENTS_TYPE = 'Z')" >
                                                <xsl:value-of select="$risk/@PATHOGENIC_AGENTS_TYPE" />
                                </xsl:if>
                            </fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$risk/@CONFLICT_OF_INTEREST_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$risk/@CONFLICT_OF_INTEREST_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>


                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Does any individual responsible for this project's design, conduct, or reporting</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="3">
                                <fo:block></fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block text-indent="2mm">have a disclosable financial conflict of interest related to this project?</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                </fo:table-body>
        </fo:table>

                                        <xsl:call-template name="Next" />
                </fo:flow>
        </fo:page-sequence>

</fo:root>
</xsl:template>

<!--   COMMENT:  The table is re-defined because table header output on Page 1 differs from required output on subsequent pages.   The change is accomplished by calling the template "Next."  There are other ways of accomplishing this (some possibly supported by FOP), but the technique used here is very simple and straightforward.  Other possibilities are using static content, the way we defined page numbering, and let this document consist of two pages.  Now, it is one page which adds content through the following template called "Next."
-->
                <xsl:template name="Next" >

        <fo:table table-layout="fixed" break-before="page">


        <fo:table-column column-width="4mm"/>			4
        <fo:table-column column-width="7mm"/>			11
        <fo:table-column column-width="4mm"/>			15
        <fo:table-column column-width="11mm"/>			26
        <fo:table-column column-width="4mm"/>			30

        <fo:table-column column-width="5mm"/>			35
        <fo:table-column column-width="10mm"/>			45
        <fo:table-column column-width="11mm"/>              56
        <fo:table-column column-width="16mm"/>			78
       <fo:table-column column-width="6mm"/>			94

        <fo:table-column column-width="22mm"/>			100

        <fo:table-column column-width="5mm"/>			105
        <fo:table-column column-width="10mm"/>			115
        <fo:table-column column-width="5mm"/>			120
        <fo:table-column column-width="5mm"/> 			125
        <fo:table-column column-width="5mm"/>      		130

        <fo:table-column column-width="4mm"/>			134
        <fo:table-column column-width="6mm"/>			140
        <fo:table-column column-width="3mm"/>			143

       <fo:table-column column-width="9mm"/>			152
       <fo:table-column column-width="8mm"/>			160

        <fo:table-column column-width="12mm"/>			177

        <fo:table-column column-width="18.5mm"/>		190.5


        <fo:table-header  font-size="9pt" >

                        <fo:table-row height="5mm">

                                <fo:table-cell number-columns-spanned="18" />

                                <fo:table-cell number-columns-spanned="4" >
                                        <fo:block text-align="right"><fo:inline font-weight="bold" >Tracking #</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block text-align="right"><xsl:value-of select="$routingForm/@TRACKING_NUMBER" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                        <fo:table-row height="5mm">

                                <fo:table-cell number-columns-spanned="18">
                                        <fo:block></fo:block>
                                </fo:table-cell>

                                <fo:table-cell number-columns-spanned="4" >
                                                <fo:block text-align="right"><fo:inline font-weight="bold">Proposal #</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell >
                                        <fo:block text-align="right"><xsl:value-of select="$routingForm/@PROPOSAL_NUMBER" /></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

        </fo:table-header>


                <fo:table-body font-size="9pt" >



<!--       ** NUMBERED GRID **   This is the grid that can be temporarily displayed as a guide.  Just cut and paste it where it is needed in the Next template (you are in it now), and delete it when you are done.

                        <fo:table-row >

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>1</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" >
                                        <fo:block>2</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>3</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>4</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>5</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>6</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>7</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>8</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>9</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>10</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>11</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>12</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>13</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>14</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>15</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>16</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>17</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>18</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>19</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>20</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>21</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd">
                                        <fo:block>22</fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd">
                                        <fo:block>23</fo:block>
                                </fo:table-cell>

                        </fo:table-row>
-->


                        <fo:table-row >
                                <fo:table-cell number-columns-spanned="5">
                                        <fo:block><fo:inline font-weight="bold">PROJECT DETAILS</fo:inline></fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                        <fo:table-row height="2mm"/>

                        <fo:table-row height="5mm">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>YES</fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                                <fo:block text-indent="5mm">NO</fo:block>
                        </fo:table-cell>

                </fo:table-row>


        <!-- The templates named "Toggle" and "Toggle_No" are defined in the business logic file, which is imported into this file.  They determine whether a graphic of a plain square, or a graphic of a square with a checkmark in it will be displayed.  -->

                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@COST_SHARE_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@COST_SHARE_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Cost Share</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@FEDERAL_PASS_THROUGH_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@FEDERAL_PASS_THROUGH_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>


                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Federal pass-through source: <xsl:value-of select="$details/FED_PASS_THROUGH_SOURCE" /></fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@OFF_CAMPUS" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@OFF_CAMPUS" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Project is off-campus</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@PROGRAM_INCOME_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@PROGRAM_INCOME_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Program income is anticipated</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@SUBCONTRACTS" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@SUBCONTRACTS" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Subcontract(s)</fo:block>
                        </fo:table-cell>

                </fo:table-row>

<!--    white-space-collapse="true" is the default.  That code appears below to accommodate changing the attribute value to "false" if need be.   When the value is "false," new lines and multiple spaces are reproduced in PDF output just as they appear in the XML file.    -->



                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@INVENTIONS_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@INVENTIONS_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Inventions have been conceived or reduced to practice under prior research on this project</fo:block>
                        </fo:table-cell>

                </fo:table-row>

                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@NEW_SPACE_REQ_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@NEW_SPACE_REQ_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>New space or remodeling will be required</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@INTERNATIONAL_COLLAB_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@INTERNATIONAL_COLLAB_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Project involves collaborative activities with foreign partner or has an international focus</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@FOREIGN_TRAVEL_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@FOREIGN_TRAVEL_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Project requires foreign travel</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@OTHER_UNIV_INVOLVMENT_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@OTHER_UNIV_INVOLVMENT_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Other I.U. campuses, schools, or units are involved</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                <fo:table-row height="4mm" display-align="after">

                        <fo:table-cell number-columns-spanned="1">
                                <fo:block>
                                <xsl:call-template name="Toggle" >
                                        <xsl:with-param name="checkboxNode" select="$details/@ALLOCATE_PCT_CREDIT_IND" />
                                </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="2">
                                <fo:block text-indent="5mm">
                                        <xsl:call-template name="Toggle_No" >
                                                <xsl:with-param name="checkboxNode" select="$details/@ALLOCATE_PCT_CREDIT_IND" />
                                        </xsl:call-template>
                                </fo:block>
                        </fo:table-cell>

                        <fo:table-cell number-columns-spanned="18">
                                <fo:block>Allocate Percent Credit to more than one individual or department (Optional for BL campus)</fo:block>
                        </fo:table-cell>

                </fo:table-row>


                        <fo:table-row height="5mm" />

                        <fo:table-row >
                                <fo:table-cell number-columns-spanned="5">
                                        <fo:block><fo:inline font-weight="bold">APPROVALS</fo:inline></fo:block>
                                </fo:table-cell>
                        </fo:table-row>

                                        <fo:table-row  height="2mm" />

                        <fo:table-row  height="6mm">

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="9" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Name</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Title/Role</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="0.5mm"><fo:inline font-weight="bold"  >Chart</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Org</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="5" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Action</fo:inline></fo:block>
                                </fo:table-cell>

                                <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Action Date</fo:inline></fo:block>
                                </fo:table-cell>

                        </fo:table-row>

                <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER" />

                <!-- Those variables define the existance (or non existance)
                     of Additional Detail items. If no Additional Detail item
                     exists then the Additional Detail item heading isn't
                     shows. Purpose of the variables is so not to replicate
                     the conditions. -->
                <xsl:variable name="ADDITIONAL_DEL_SET" select="$agency/AGENCY_DELIVERY/@ADDITIONAL_DELIVERY_INSTRUCTIONS_IND = 'Y'"/>
                <xsl:variable name="RESEARCH_RISKS_SET" select="(PROPOSAL/ROUTING_FORM/RESEARCH_RISK/@HUMAN_SUBJECTS_IND = 'Y') or (PROPOSAL/ROUTING_FORM/RESEARCH_RISK/@ANIMAL_IND = 'Y') or (PROPOSAL/ROUTING_FORM/RESEARCH_RISK/@BIOSAFETY_IND = 'Y')"/>
                <xsl:variable name="COST_SHARE_SET" select="$details/@COST_SHARE_IND = 'Y'"/>
                <xsl:variable name="SUBCONTRACTS_SET" select="$details/@SUBCONTRACTS = 'Y'"/>
                <xsl:variable name="CO-PD_SET" select="$principals/@CO-PD_IND = 'Y'"/>
                <xsl:variable name="OTHER_SET" select="$details/@OTHER_UNIV_INVOLVMENT_IND = 'Y'"/>
                <xsl:variable name="ALLOCATE_CREDIT_SET" select="$details/@ALLOCATE_PCT_CREDIT_IND = 'Y'"/>
                <xsl:variable name="KEYWORDS_SET" select="count(PROPOSAL/ROUTING_FORM/KEYWORDS/KEYWORD) > 0"/>
                <xsl:variable name="COMMENTS_SET" select="count(PROPOSAL/ROUTING_FORM/COMMENTS/COMMENT) > 0"/>

                <xsl:if test="$ADDITIONAL_DEL_SET
                              or $RESEARCH_RISKS_SET
                              or $COST_SHARE_SET
                              or $SUBCONTRACTS_SET
                              or $CO-PD_SET
                              or $OTHER_SET
                              or $ALLOCATE_CREDIT_SET
                              or $KEYWORDS_SET
                              or $COMMENTS_SET">
                  <fo:table-row height="7mm" >
                    <fo:table-cell number-columns-spanned="23" xsl:use-attribute-sets="d" >
                      <fo:block></fo:block>
                    </fo:table-cell>
                  </fo:table-row>

                  <fo:table-row font-size="11pt" height="8mm">
                    <fo:table-cell number-columns-spanned="12" display-align="center">
                      <fo:block><fo:inline font-weight="bold" >ADDITIONAL DETAIL</fo:inline></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <fo:table-row height="2mm" />
                </xsl:if>

                  <!-- ADDITIONAL DELIVERY INSTRUCTIONS TABLE -->
                  <xsl:if test="$ADDITIONAL_DEL_SET">
                    <fo:table-row height="5mm">
                      <fo:table-cell height="5mm" number-columns-spanned="12" display-align="center">
                        <fo:block><fo:inline font-weight="bold" >ADDITIONAL DELIVERY INSTRUCTIONS</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    <!-- Additional Delivery Instructions Values -->
                    <fo:table-row height="10mm" >
                      <fo:table-cell number-columns-spanned="23" xsl:use-attribute-sets="abcd" padding-left="1mm">
                        <fo:block><xsl:value-of select="$agency/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS" /></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row height="5mm" />
                  </xsl:if>

                  <!-- RESEARCH RISKS TABLE -->
                  <xsl:if test="$RESEARCH_RISKS_SET">
                    <fo:table-row  keep-with-next="always"  >
                      <fo:table-cell number-columns-spanned="5">
                        <fo:block><fo:inline font-weight="bold">RESEARCH RISK</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row  keep-with-next="always"  height="2mm"/>
                    <fo:table-row  keep-with-next="always"   height="6mm">
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Study Type</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Approval Status</fo:inline></fo:block>
                      </fo:table-cell>

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Study Number</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Approval Date</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Study Review Status</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold"  >Exemption #</fo:inline></fo:block>
                      </fo:table-cell>
                      
                    </fo:table-row>

                    <!-- Research Risks Values -->
                    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/RESEARCH_RISK/STUDY" />
                      
                    <fo:table-row   height="5mm" />
                  </xsl:if>

                  <!-- COST SHARE TABLE -->
                  <xsl:if test="$COST_SHARE_SET">
                    <fo:table-row  keep-with-next="always"  >
                      <fo:table-cell number-columns-spanned="5">
                        <fo:block><fo:inline font-weight="bold">COST SHARE</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row  keep-with-next="always"  height="2mm"/>

                    <fo:table-row  keep-with-next="always"   height="6mm">

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="7" display-align="center" padding-top="0.9mm" padding-left="1mm" >
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" > Chart</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm" padding-left="1mm" >
                        <fo:block ><fo:inline font-weight="bold"  > Org</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="8" display-align="center" padding-top="0.9mm" padding-left="1mm" >
                        <fo:block ><fo:inline font-weight="bold"  > Account</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                        <fo:block text-align="center"><fo:inline font-weight="bold"  > Amount</fo:inline></fo:block>
                      </fo:table-cell>
                      
                    </fo:table-row>
                    
                    <!-- IU Cost Share Values -->
                    <!-- 06/25/2003 pcberg: Added. -->
                    <xsl:apply-templates select="$details/IU_COST_SHARE" />
                      
                    <fo:table-row   height="6mm">
                        
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="17" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm">Total IU Cost Share</fo:block>
                      </fo:table-cell>
                        
<!--  The condition " [. &gt; 0] " prevents "NAN" from being 	displayed when there is no entry for AMOUT under IU_COST_SHARE   Amount is a required field under the first DTD, but it is not clear that this will always be enforced, or that a user might not enter non-numerical character values.  Attempts to include empty strings or non-numerical characters in the sum() argument results in "NAN" being displayed.   -->

                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm"  text-align="right" padding-right="1mm">
                        <fo:block text-indent="1mm"  >$ <xsl:value-of select="format-number(sum(PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/IU_COST_SHARE/@AMOUNT [. &gt; 0]), '###,###'  )" />						</fo:block>
                      </fo:table-cell>
                          
                    </fo:table-row>
                    <fo:table-row  keep-with-next="always"   height="6mm">

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="17" display-align="center" padding-top="0.9mm" >
                        <fo:block text-indent="1mm" padding-right="1mm">Total Third Party Cost Share</fo:block>
                      </fo:table-cell>
                      
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm"  text-align="right" padding-right="1mm">
                        <fo:block text-indent="1mm" >$ <xsl:value-of select="format-number(sum($details/OTHER_COST_SHARE/@AMOUNT[. &gt; 0]), '###,###') " />  </fo:block>
                      </fo:table-cell>
                        
                    </fo:table-row>
                        
                    <fo:table-row  keep-with-next="always"   height="6mm">
                        
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="17" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm">Total  Cost Share</fo:block>
                      </fo:table-cell>
                        
                        
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm" padding-right="1mm">
                        <fo:block text-indent="1mm"  text-align="right">$ <xsl:value-of select="format-number(sum(PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/IU_COST_SHARE/@AMOUNT [. &gt; 0]) +  sum($details/OTHER_COST_SHARE/@AMOUNT[. &gt; 0]), '###,###'  )" /></fo:block >
                      </fo:table-cell>
                        
                    </fo:table-row>
                      
                    <fo:table-row   height="6mm" />
                  </xsl:if>

                  <!--  The keep-with-next="always" row attribute definition assures that there is no page break between succeeding rows with that attribute.  The row definition in the PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR template below also has the keep-with-next attribute.  -->

                  <!-- SUBCONTRACTS TABLE -->
                  <xsl:if test="$SUBCONTRACTS_SET">
                    <fo:table-row  keep-with-next="always"  >
                      <fo:table-cell number-columns-spanned="15">
                        <fo:block><fo:inline font-weight="bold">SUBCONTRACT(S)</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row  keep-with-next="always"  height="2mm" />

                    <fo:table-row  keep-with-next="always"  height="6mm" >

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="17" display-align="center" padding-top="0.9mm" padding-left="1mm">
                        <fo:block ><fo:inline font-weight="bold">Source</fo:inline></fo:block>
                      </fo:table-cell>

                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"  text-align="center"><fo:inline font-weight="bold">Amount</fo:inline></fo:block >
                      </fo:table-cell>

                    </fo:table-row>

                    <!-- Subcontractors Values -->
                    <xsl:apply-templates select="$details/SUBCONTRACTOR" />

                    <fo:table-row    height="6mm" />
                  </xsl:if>

                  <!-- CO-PROJECT DIRECTORS TABLE -->
                  <xsl:if test="$CO-PD_SET">
                    <fo:table-row  keep-with-next="always"   >
                      <fo:table-cell number-columns-spanned="15">
                        <fo:block><fo:inline font-weight="bold">CO-PROJECT DIRECTOR(S)</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row   height="2mm" keep-with-next="always" />

                    <fo:table-row    height="6mm"  >

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="20" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Name</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Chart</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="1" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm" ><fo:inline font-weight="bold" >Org</fo:inline></fo:block >
                      </fo:table-cell>
                      
                    </fo:table-row>
                    
                    <!-- Co-Project Directors Values -->
                    <xsl:apply-templates select="$principals/CO-PROJECT_DIRECTORS/CO-PROJECT_DIRECTOR" mode="one" />
                      
                    <fo:table-row    height="6mm" />
                  </xsl:if>

                  <!-- OTHER IU CAMPUSES etc. TABLE -->
                  <xsl:if test="$OTHER_SET">
                    <fo:table-row  keep-with-next="always"   >
                      <fo:table-cell number-columns-spanned="15">
                        <fo:block><fo:inline font-weight="bold">OTHER IU CAMPUSES, SCHOOLS OR UNITS</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row keep-with-next="always"  height="2mm" />
                      
                    <fo:table-row  keep-with-next="always"   height="6mm"  >

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Chart</fo:inline></fo:block>
                      </fo:table-cell>
                        
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Org</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="18" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"  ><fo:inline font-weight="bold" >Org Name</fo:inline></fo:block >
                      </fo:table-cell>
                      
                    </fo:table-row>
                    
                    <!-- Other Iu Campuses etc. Values -->
                    <xsl:apply-templates select="$details/OTHER_UNIV_ORG" />
                    <fo:table-row    height="6mm" />
                  </xsl:if>

                  <!-- % INTELLECTUAL CREDIT TABLE -->
                  <xsl:if test="$ALLOCATE_CREDIT_SET">
                    <fo:table-row  keep-with-next="always"   >
                      <fo:table-cell number-columns-spanned="15">
                        <fo:block><fo:inline font-weight="bold">% INTELLECTUAL CREDIT</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row  keep-with-next="always"  height="2mm" />

                    <fo:table-row  keep-with-next="always"  height="6mm"  >

                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="16" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Name</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Chart</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm"><fo:inline font-weight="bold" >Org</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="1" display-align="center" padding-top="0.9mm">
                        <fo:block text-indent="1mm" ><fo:inline font-weight="bold" >% Credit</fo:inline></fo:block >
                      </fo:table-cell>

                    </fo:table-row>

                    <!-- % Interlectual Credit Values -->
                    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/PERCENT_CREDIT" mode="three" />

                    <fo:table-row  height="6mm" />
                  </xsl:if>

                  <!-- KEYWORDS TABLE -->
                  <xsl:if test="$KEYWORDS_SET">
                    <fo:table-row  keep-with-next="always"  height="6mm">
                      <fo:table-cell number-columns-spanned="22">
                        <fo:block><fo:inline font-weight="bold">KEY WORDS: </fo:inline><xsl:value-of select="substring(PROPOSAL/ROUTING_FORM/KEY_WORDS, 1, 200)" /></fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row>
                      <fo:table-cell number-columns-spanned="22">
                        <fo:block font-size="10pt">
                          <!-- Keywords Values -->
                          <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/KEYWORDS" />
                        </fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <fo:table-row  height="6mm" />
                  </xsl:if>

                  <!-- pcberg 08/14/03: Added Comments. -->
                  <!-- COMMENTS TABLE -->
                  <xsl:if test="$COMMENTS_SET">
                    <fo:table-row keep-with-next="always">
                      <fo:table-cell number-columns-spanned="22">
                        <fo:block><fo:inline font-weight="bold">COMMENTS</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row height="2mm" keep-with-next="always"/>

                    <fo:table-row height="6mm">
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="9" display-align="center" padding-top="0.9mm" padding-left="1mm">
                        <fo:block><fo:inline font-weight="bold">Entered By</fo:inline></fo:block>
                      </fo:table-cell>
                     
                      <fo:table-cell xsl:use-attribute-sets = "abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm" padding-left="1mm">
                        <fo:block><fo:inline font-weight="bold">Date and Time Stamp</fo:inline></fo:block>
                      </fo:table-cell>
                      
                      <fo:table-cell xsl:use-attribute-sets = "abcd" number-columns-spanned="11" display-align="center" padding-top="0.9mm" padding-left="1mm">
                        <fo:block><fo:inline font-weight="bold">Topic</fo:inline></fo:block>
                      </fo:table-cell>
                    </fo:table-row>

                    <!-- Comment Values -->
                    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/COMMENTS"/>
                  </xsl:if>

                </fo:table-body>
              </fo:table>

<!--  The following block is used to determine the length of the document.  This information is used to output page numbering.    -->

        <fo:block id="End" />

        </xsl:template>

</xsl:stylesheet>
