--
-- Copyright 2005-2015 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
-- KULRICE-833  The following SQL can be used to create a sample eDocLite that uses Xpath to determine
--                which branches to execute.
--

--
-- KREW_RULE_TMPL_T
--

INSERT INTO KREW_RULE_TMPL_T(RULE_TMPL_ID, NM, RULE_TMPL_DESC, DLGN_RULE_TMPL_ID, VER_NBR, OBJ_ID)
  VALUES('1645', 'BloomingtonBranchApprovalRuleTemplate', 'BloomingtonBranchApprovalRuleTemplate', NULL, 1, uuid())
/
INSERT INTO KREW_RULE_TMPL_T(RULE_TMPL_ID, NM, RULE_TMPL_DESC, DLGN_RULE_TMPL_ID, VER_NBR, OBJ_ID)
  VALUES('1646', 'IndianapolisBranchApprovalRuleTemplate', 'IndianapolisBranchApprovalRuleTemplate', NULL, 1, uuid())
/
INSERT INTO KREW_RULE_TMPL_T(RULE_TMPL_ID, NM, RULE_TMPL_DESC, DLGN_RULE_TMPL_ID, VER_NBR, OBJ_ID)
  VALUES('1647', 'OtherCampusBranchApprovalRuleTemplate', 'OtherCampusBranchApprovalRuleTemplate', NULL, 1, uuid())
/

--
-- KREW_DOC_TYP_T
--

INSERT INTO KREW_DOC_TYP_T(DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, PREV_DOC_TYP_VER_NBR, DOC_TYP_DESC, DOC_HDLR_URL, POST_PRCSR, JNDI_URL, BLNKT_APPR_PLCY, ADV_DOC_SRCH_URL, RTE_VER_NBR, NOTIFY_ADDR, APPL_ID, EMAIL_XSL, SEC_XML, VER_NBR, BLNKT_APPR_GRP_ID, RPT_GRP_ID, GRP_ID, HELP_DEF_URL, OBJ_ID, DOC_SEARCH_HELP_URL, DOC_HDR_ID, AUTHORIZER)
  VALUES('3046', NULL, 'XPathSplitNodeExampleDocType', 0, 1, 1, 'XPath SplitNode Example DocType', NULL, 'XPath SplitNode Example DocType', '${workflow.url}/EDocLite', 'org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor', NULL, NULL, NULL, '2', NULL, NULL, NULL, NULL, 1, '1', NULL, '1', NULL, uuid(), NULL, NULL, NULL)
/

--
-- KREW_RTE_BRCH_PROTO_T
--

INSERT INTO KREW_RTE_BRCH_PROTO_T(RTE_BRCH_PROTO_ID, BRCH_NM, VER_NBR)
  VALUES('2990', 'BloomingtonBranch', 1)
/
INSERT INTO KREW_RTE_BRCH_PROTO_T(RTE_BRCH_PROTO_ID, BRCH_NM, VER_NBR)
  VALUES('2993', 'IndianapolisBranch', 1)
/
INSERT INTO KREW_RTE_BRCH_PROTO_T(RTE_BRCH_PROTO_ID, BRCH_NM, VER_NBR)
  VALUES('2995', 'OtherCampusBranch', 1)
/

--
-- KREW_RTE_NODE_T
--

INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2994', '3046', 'OtherCampusApproval', 'org.kuali.rice.kew.engine.node.RequestsNode', 'OtherCampusBranchApprovalRuleTemplate', 'FR', 0, 1, 'P', '2995', 1, NULL, '1', NULL)
/
INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2987', '3046', 'Initiated', 'org.kuali.rice.kew.engine.node.InitialNode', NULL, NULL, 0, 0, 'P', NULL, 1, NULL, '1', NULL)
/
INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2989', '3046', 'BloomingtonBranchApproval', 'org.kuali.rice.kew.engine.node.RequestsNode', 'BloomingtonBranchApprovalRuleTemplate', 'FR', 0, 1, 'P', '2990', 1, NULL, '1', NULL)
/
INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2991', '3046', 'CampusJoin', 'org.kuali.rice.kew.engine.node.SimpleJoinNode', NULL, NULL, 0, 0, 'P', '2990', 1, NULL, '1', NULL)
/
INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2992', '3046', 'IndianapolisBranchApproval', 'org.kuali.rice.kew.engine.node.RequestsNode', 'IndianapolisBranchApprovalRuleTemplate', 'FR', 0, 1, 'P', '2993', 1, NULL, '1', NULL)
/
INSERT INTO KREW_RTE_NODE_T(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND, MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
  VALUES('2988', '3046', 'CampusSplit', 'org.kuali.rice.kew.engine.node.XPathSplitNode', NULL, NULL, 0, 0, 'P', NULL, 1, NULL, '1', NULL)
/

--
-- KREW_DOC_TYP_PROC_T
--

INSERT INTO KREW_DOC_TYP_PROC_T(DOC_TYP_PROC_ID, DOC_TYP_ID, INIT_RTE_NODE_ID, NM, INIT_IND, VER_NBR)
  VALUES('2986', '3046', '2987', 'PRIMARY', 1, 1)
/

--
-- KREW_RTE_NODE_CFG_PARM_T
--

INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2676', '2987', 'finalApproval', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2670', '2988', 'type', 'org.kuali.rice.kew.engine.node.XPathSplitNode')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2668', '2988', 'mandatoryRoute', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2647', '2992', 'ruleTemplate', 'IndianapolisBranchApprovalRuleTemplate')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2659', '2991', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2657', '2991', 'mandatoryRoute', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2667', '2988', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2671', '2988', 'branchDecisions', NULL)
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2644', '2992', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2672', '2988', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2675', '2987', 'mandatoryRoute', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2661', '2989', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2654', '2994', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2653', '2994', 'ruleTemplate', 'OtherCampusBranchApprovalRuleTemplate')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2662', '2989', 'mandatoryRoute', 'true')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2656', '2991', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2646', '2992', 'finalApproval', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2652', '2994', 'finalApproval', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2674', '2987', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2649', '2994', 'contentFragment', '<requests name="OtherCampusApproval">
<activationType>P</activationType>
<mandatoryRoute>true</mandatoryRoute>
<finalApproval>false</finalApproval>
<ruleTemplate>OtherCampusBranchApprovalRuleTemplate</ruleTemplate>
</requests>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2677', '2987', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2643', '2992', 'contentFragment', '<requests name="IndianapolisBranchApproval">
<activationType>P</activationType>
<mandatoryRoute>true</mandatoryRoute>
<finalApproval>false</finalApproval>
<ruleTemplate>IndianapolisBranchApprovalRuleTemplate</ruleTemplate>
</requests>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2655', '2991', 'contentFragment', '<join name="CampusJoin">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</join>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2645', '2992', 'mandatoryRoute', 'true')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2663', '2989', 'finalApproval', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2664', '2989', 'ruleTemplate', 'BloomingtonBranchApprovalRuleTemplate')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2648', '2992', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2666', '2988', 'contentFragment', '<split name="CampusSplit">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
<type>org.kuali.rice.kew.engine.node.XPathSplitNode</type>
<branchDecisions>
<xpath branchName="BloomingtonBranch" expression="boolean(//data/version[@current=''true'']/field[@name=''campus'']/value=''BL'')"/>
<xpath branchName="IndianapolisBranch" expression="boolean(//data/version[@current=''true'']/field[@name=''campus'']/value=''IN'')"/>
<default branchName="OtherCampusBranch"/>
</branchDecisions>
</split>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2665', '2989', 'ruleSelector', 'Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2669', '2988', 'finalApproval', 'false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2660', '2989', 'contentFragment', '<requests name="BloomingtonBranchApproval">
<activationType>P</activationType>
<mandatoryRoute>true</mandatoryRoute>
<finalApproval>false</finalApproval>
<ruleTemplate>BloomingtonBranchApprovalRuleTemplate</ruleTemplate>
</requests>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2651', '2994', 'mandatoryRoute', 'true')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2673', '2987', 'contentFragment', '<start name="Initiated">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</start>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2650', '2994', 'activationType', 'P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
  VALUES('2658', '2991', 'finalApproval', 'false')
/

--
-- KREW_RTE_NODE_LNK_T
--

INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2987', '2988')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2988', '2989')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2988', '2992')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2988', '2994')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2989', '2991')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2992', '2991')
/
INSERT INTO KREW_RTE_NODE_LNK_T(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
  VALUES('2994', '2991')
/

--
-- KREW_RULE_T
--

INSERT INTO KREW_RULE_T(RULE_ID, NM, RULE_TMPL_ID, RULE_EXPR_ID, ACTV_IND, RULE_BASE_VAL_DESC, FRC_ACTN, DOC_TYP_NM, DOC_HDR_ID, TMPL_RULE_IND, FRM_DT, TO_DT, DACTVN_DT, CUR_IND, RULE_VER_NBR, DLGN_IND, PREV_VER_RULE_ID, ACTVN_DT, VER_NBR, OBJ_ID)
  VALUES('1648', 'BloomingtonBranchApprovalRule', '1645', NULL, 1, 'Bloomington Branch Approval Rule', 0, 'XPathSplitNodeExampleDocType', NULL, 0, NULL, NULL, NULL, 1, 0, 0, NULL, '2015-05-20 12:01:00', 1, uuid())
/
INSERT INTO KREW_RULE_T(RULE_ID, NM, RULE_TMPL_ID, RULE_EXPR_ID, ACTV_IND, RULE_BASE_VAL_DESC, FRC_ACTN, DOC_TYP_NM, DOC_HDR_ID, TMPL_RULE_IND, FRM_DT, TO_DT, DACTVN_DT, CUR_IND, RULE_VER_NBR, DLGN_IND, PREV_VER_RULE_ID, ACTVN_DT, VER_NBR, OBJ_ID)
  VALUES('1649', 'IndianapolisBranchApprovalRule', '1646', NULL, 1, 'Indianapolis Branch Approval Rule', 0, 'XPathSplitNodeExampleDocType', NULL, 0, NULL, NULL, NULL, 1, 0, 0, NULL, '2015-05-20 12:01:00', 1, uuid())
/
INSERT INTO KREW_RULE_T(RULE_ID, NM, RULE_TMPL_ID, RULE_EXPR_ID, ACTV_IND, RULE_BASE_VAL_DESC, FRC_ACTN, DOC_TYP_NM, DOC_HDR_ID, TMPL_RULE_IND, FRM_DT, TO_DT, DACTVN_DT, CUR_IND, RULE_VER_NBR, DLGN_IND, PREV_VER_RULE_ID, ACTVN_DT, VER_NBR, OBJ_ID)
  VALUES('1650', 'OtherCampusBranchApprovalRule', '1647', NULL, 1, 'Other Branch Approval Rule', 0, 'XPathSplitNodeExampleDocType', NULL, 0, NULL, NULL, NULL, 1, 0, 0, NULL, '2015-05-20 12:01:00', 1, uuid())
/

--
-- KREW_RULE_RSP_T
--

INSERT INTO KREW_RULE_RSP_T(RULE_RSP_ID, RSP_ID, RULE_ID, PRIO, ACTN_RQST_CD, NM, TYP, APPR_PLCY, VER_NBR, OBJ_ID)
  VALUES('2066', '2065', '1648', 1, 'A', 'testuser1', 'F', 'F', 1, uuid())
/
INSERT INTO KREW_RULE_RSP_T(RULE_RSP_ID, RSP_ID, RULE_ID, PRIO, ACTN_RQST_CD, NM, TYP, APPR_PLCY, VER_NBR, OBJ_ID)
  VALUES('2068', '2067', '1649', 1, 'A', 'testuser2', 'F', 'F', 1, uuid())
/
INSERT INTO KREW_RULE_RSP_T(RULE_RSP_ID, RSP_ID, RULE_ID, PRIO, ACTN_RQST_CD, NM, TYP, APPR_PLCY, VER_NBR, OBJ_ID)
  VALUES('2070', '2069', '1650', 1, 'A', 'testuser3', 'F', 'F', 1, uuid())
/

--
-- KRCR_STYLE_T
--

INSERT INTO KRCR_STYLE_T(STYLE_ID, NM, XML, ACTV_IND, VER_NBR, OBJ_ID)
  VALUES('2023', 'XPathSplitNodeExampleStyle', '<xsl:stylesheet xmlns:my-class="xalan://org.kuali.rice.edl.framework.util.EDLFunctions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
        <xsl:include href="widgets"/>
        <xsl:output indent="yes" method="html" omit-xml-declaration="yes" version="4.01"/>
        <xsl:param name="overrideMain" select="''true''"/>
        <!-- if "overrideMain" = true, please customize the "mainForm" template, otherwise please customize the "mainBody" template -->
        <xsl:template name="mainForm">
          <html xmlns="">
            <head>
              <xsl:call-template name="htmlHead"/>
            </head>
            <body onload="onPageLoad()">
              <xsl:call-template name="header"/>
              <xsl:call-template name="instructions"/>
              <xsl:call-template name="errors"/>
              <xsl:variable name="formTarget" select="''EDocLite''"/>
              <div id="mainform-div">
                <form action="{$formTarget}" enctype="multipart/form-data" id="edoclite" method="post" onsubmit="return validateOnSubmit(this)">
                  <xsl:call-template name="hidden-params"/>
                  <xsl:call-template name="mainBody"/>
                  <br/>
                  <xsl:call-template name="buttons"/>
                </form>
              </div>
              <xsl:call-template name="footer"/>
            </body>
          </html>
        </xsl:template>
        <xsl:template name="mainBody">
          <table align="center" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" width="80%" xmlns="">
            <tr>
              <td align="left" border="3" class="thnormal" colspan="100%">
                <br/>
                <h2>Computer Request</h2>
				 Attributes marked with a <font color="#ff0000">*</font> are required fields.
                <br/>
              </td>
            </tr>
			<tr>
              <td class="thnormal" colspan="1">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''campus''"/>
                  <xsl:with-param name="renderCmd" select="''title''"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''campus''"/>
                  <xsl:with-param name="renderCmd" select="''input''"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="1">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''building''"/>
                  <xsl:with-param name="renderCmd" select="''title''"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''building''"/>
                  <xsl:with-param name="renderCmd" select="''input''"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="1">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''room''"/>
                  <xsl:with-param name="renderCmd" select="''title''"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="''room''"/>
                  <xsl:with-param name="renderCmd" select="''input''"/>
                </xsl:call-template>
              </td>
            </tr>
          </table>
        </xsl:template>
      </xsl:stylesheet>', 1, 1, uuid())
/

--
-- KREW_EDL_DEF_T
--

INSERT INTO KREW_EDL_DEF_T(EDOCLT_DEF_ID, NM, XML, ACTV_IND, VER_NBR, OBJ_ID)
  VALUES(2022, 'XPathSplitNodeExampleForm', '<edl name="XPathSplitNodeExampleForm" title="Computer Request">
      <security/>
      <createInstructions/>
      <instructions/>
      <fieldDef name="campus" title="Campus">
        <value>Select</value>
        <display>
          <type>select</type>
          <values title="(BL) Bloomington">BL</values>
          <values title="(IN) Indianapolis">IN</values>
          <values title="(EI) East Indy">EI</values>
          <values title="(WI) West Indy">WI</values>
        </display>
        <validation required="true">
          <regex>[^Select]</regex>
          <message>Please Select a Campus.</message>
        </validation>
      </fieldDef>
      <fieldDef name="building" title="building">
        <display>
          <type>text</type>
        </display>
        <validation required="true"/>
      </fieldDef>
      <fieldDef name="room" title="room">
        <display>
          <type>text</type>
        </display>
        <validation required="true"/>
      </fieldDef>
    </edl>', 1, 1, uuid())
/

--
-- KREW_EDL_ASSCTN_T
--

INSERT INTO KREW_EDL_ASSCTN_T(EDOCLT_ASSOC_ID, DOC_TYP_NM, EDL_DEF_NM, STYLE_NM, ACTV_IND, VER_NBR, OBJ_ID)
  VALUES(2024, 'XPathSplitNodeExampleDocType', 'XPathSplitNodeExampleForm', 'XPathSplitNodeExampleStyle', 1, 1, uuid())
/