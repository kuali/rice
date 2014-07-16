--
-- Copyright 2005-2014 The Kuali Foundation
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

# -----------------------------------------------------------------------
# KREW_ATTR_DEFN_T
# -----------------------------------------------------------------------
INSERT INTO KREW_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,CMPNT_NM,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1','edu.sampleu.travel.bo.TravelAccount','Travel Number','number','KR-SAP',1)
/
INSERT INTO KREW_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,CMPNT_NM,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','2','edu.sampleu.travel.bo.FiscalOfficer','id','KR-SAP',1)
/

# -----------------------------------------------------------------------
# KREW_DOC_TYP_T
# -----------------------------------------------------------------------
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',0,'${kr.url}/maintenance.do?methodToCall=docHandler','Create a New Travel Account Maintenance Document','2029','TravelAccountMaintenanceDocument',0,'1','Travel Account Maintenance Document','6166CBA1BA6F644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create a New Travel Fiscal Officer','2030','FiscalOfficerMaintenanceDocument',0,'1','Travel Fiscal Officer','6166CBA1BA70644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_PLCY,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'NONE',1,'${workflow.url}/EDocLite','eDoc.Example1 Parent Doctype','2217','eDoc.Example1.ParentDoctype',0,'2200','eDoc.Example1 Parent Document','6166CBA1BA7B644DE0404F8189D86C09','org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'none','SampleThinClientDocument','2282','SampleThinClientDocument',0,'1','SampleThinClientDocument','6166CBA1BA7D644DE0404F8189D86C09','org.kuali.rice.kew.postprocessor.DefaultPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_PLCY,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'NONE',1,'${workflow.url}/EDocLite','eDoc.Example1 Request DocumentType','2440','eDoc.Example1Doctype',0,'2200','eDoc.Example1 Request DocumentType','6166CBA1BA80644DE0404F8189D86C09','2217','org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/travelDocument2.do?methodToCall=docHandler','Create a New Travel Request','2683','TravelRequest',0,'1','Travel Request','6166CBA1BA84644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,APPL_ID,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'RECIPE','1',1,'${application.url}/kr/maintenance.do?methodToCall=docHandler','Parent Document for Recipe Maintenance Documents','2704','RecipeParentMaintenanceDocument',0,'1','Recipe Maintenance Document Parent','327B8EEB-BC71-4701-A9E8-B4FC878FFCA6','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,APPL_ID,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'RECIPE','1',1,'${application.url}/kr/maintenance.do?methodToCall=docHandler','Create or Update a Recipe Category','2705','RecipeCategoryMaintenanceDocument',0,'1','Recipe Category Maintenance','E9CB1AAD-0015-16D5-9149-EC2A4AEDE932','2704','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,APPL_ID,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'RECIPE','1',1,'${application.url}/kr/maintenance.do?methodToCall=docHandler','Create or Update a Recipe Ingredient','2706','RecipeIngredientMaintenanceDocument',0,'1','Recipe Ingredient Maintenance','41800805-9154-D43E-785F-3E76255F7F97','2704','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,APPL_ID,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'RECIPE','1',1,'${application.url}/kr/maintenance.do?methodToCall=docHandler','Create or Update a Recipe','2707','RecipeMaintenanceDocument',0,'1','Recipe Maintenance','C1CED233-6389-D07B-8ADD-B8043E50B599','2704','edu.sampleu.recipe.kew.RecipesPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a New Travel Account Maintenance Document','3006','TravelAccountMaintenanceDocument',1,'1','Travel Account Maintenance Document','0b89a795-8079-460a-b456-39c5c477938b','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2029','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/kr-krad/approval?methodToCall=docHandler','Create a New Travel Authorization Document','3008','TravelAuthorization',0,'1','Travel Authorization Document','b9110d38-8fe0-4123-a65a-5df2336795fa','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','1',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/krad/maintenance?methodToCall=docHandler&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo','Create a New Fiscal Officer Maintenance Document','3009','FiscalOfficerInfoMaintenanceDocument',0,'1','Fiscal Officer Info Maintenance Document','a4ecd35b-f35a-40ac-a9e1-ed47201281ec','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/

# -----------------------------------------------------------------------
# KREW_DOC_TYP_ATTR_T
# -----------------------------------------------------------------------
INSERT INTO KREW_DOC_TYP_ATTR_T (DOC_TYP_ATTRIB_ID,DOC_TYP_ID,ORD_INDX,RULE_ATTR_ID)
  VALUES ('2008','2029',1,'1014')
/
INSERT INTO KREW_DOC_TYP_ATTR_T (DOC_TYP_ATTRIB_ID,DOC_TYP_ID,ORD_INDX,RULE_ATTR_ID)
  VALUES ('2009','3006',1,'1014')
/

# -----------------------------------------------------------------------
# KREW_DOC_TYP_PROC_T
# -----------------------------------------------------------------------
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2029','2058',1,'2057','PRIMARY',2)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2030','2060',1,'2059','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2282','2345',1,'2344','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2440','2582',1,'2580','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2683','2851',1,'2846','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2704','2894',1,'2892','PRIMARY',4)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('2707','2897',1,'2895','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('3006','2916',1,'2914','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('3008','2920',1,'2919','PRIMARY',1)
/
INSERT INTO KREW_DOC_TYP_PROC_T (DOC_TYP_ID,DOC_TYP_PROC_ID,INIT_IND,INIT_RTE_NODE_ID,NM,VER_NBR)
  VALUES ('3009','2922',1,'2921','PRIMARY',1)
/

# -----------------------------------------------------------------------
# KREW_EDL_DEF_T
# -----------------------------------------------------------------------
INSERT INTO KREW_EDL_DEF_T (ACTV_IND,EDOCLT_DEF_ID,NM,OBJ_ID,VER_NBR,XML)
  VALUES (1,2008,'eDoc.Example1.Form','6166CBA1BC0B644DE0404F8189D86C09',1,'<edl name="eDoc.Example1.Form" title="Example 1">
      <security/>
      <createInstructions>** Questions with an asterisk are required.</createInstructions>
      <instructions>** Questions with an asterisk are required.</instructions>
      <validations/>
      <attributes/>
      <fieldDef name="userName" title="Full Name">
        <display>
          <type>text</type>
          <meta>
            <name>size</name>
            <value>40</value>
          </meta>
        </display>
        <validation required="true">
          <message>Please enter your full name</message>
        </validation>
      </fieldDef>
      <fieldDef name="rqstDate" title="Requested Date of Implementation:">
        <display>
          <type>text</type>
        </display>
        <validation required="true">
          <regex>^[0-1]?[0-9](/|-)[0-3]?[0-9](/|-)[1-2][0-9][0-9][0-9]$</regex>
          <message>Enter a valid date in the format mm/dd/yyyy.</message>
        </validation>
      </fieldDef>
      <fieldDef name="requestType" title="Request Type:">
        <display>
          <type>radio</type>
          <values title="New">New</values>
          <values title="Modification">Modification</values>
        </display>
        <validation required="true">
          <message>Please select a request type.</message>
        </validation>
      </fieldDef>
      <fieldDef attributeName="EDL.Campus.Example" name="campus" title="Campus:">krew_edl_def_t
        <display>
          <type>select</type>
          <values title="IUB">IUB</values>
          <values title="IUPUI">IUPUI</values>
        </display>
        <validation required="true">
          <message>Please select a campus.</message>
        </validation>
      </fieldDef>
      <fieldDef name="description" title="Description of Request:">
        <display>
          <type>textarea</type>
          <meta>
            <name>rows</name>
            <value>5</value>
          </meta>
          <meta>
            <name>cols</name>
            <value>60</value>
          </meta>
          <meta>
            <name>wrap</name>
            <value>hard</value>
          </meta>
        </display>
        <validation required="false"/>
      </fieldDef>
      <fieldDef name="fundedBy" title="My research/sponsored program work is funded by NIH or NSF.">
        <display>
          <type>checkbox</type>
          <values title="My research/sponsored program work is funded by NIH or NSF.">nihnsf</values>
        </display>
      </fieldDef>
      <fieldDef name="researchHumans" title="My research/sponsored program work involves human subjects.">
        <display>
          <type>checkbox</type>
          <values title="My research/sponsored program work involves human subjects.">humans</values>
        </display>
      </fieldDef>
    </edl>
')
/

# -----------------------------------------------------------------------
# KREW_EDL_ASSCTN_T
# -----------------------------------------------------------------------
INSERT INTO KREW_EDL_ASSCTN_T (ACTV_IND,DOC_TYP_NM,EDL_DEF_NM,EDOCLT_ASSOC_ID,OBJ_ID,STYLE_NM,VER_NBR)
  VALUES (1,'eDoc.Example1Doctype','eDoc.Example1.Form',2010,'6166CBA1BC0E644DE0404F8189D86C09','eDoc.Example1.Style',1)
/

# -----------------------------------------------------------------------
# KREW_RTE_NODE_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2029',0,'1',0,'Initiated','2057','org.kuali.rice.kew.engine.node.InitialNode',2)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2030',0,'1',0,'Initiated','2059','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2282',0,'1',0,'AdHoc','2344','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2440',0,'2201',0,'Initiated','2580','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2440',0,'2201',0,'eDoc.Example1.Node1','FR','eDoc.Example1.Node1','2581','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2683',0,'1',0,'PreRoute','2846','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2683',0,'1',0,'DestinationApproval','FR','TravelRequest-DestinationRouting','2847','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2683',0,'1',0,'TravelerApproval','FR','TravelRequest-TravelerRouting','2848','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2683',0,'1',0,'SupervisorApproval','FR','TravelRequest-SupervisorRouting','2849','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2683',0,'1',0,'AccountApproval','FR','TravelRequest-AccountRouting','2850','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2704',0,'1',0,'Adhoc Routing','2892','org.kuali.rice.kew.engine.node.InitialNode',4)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2704',0,'1',0,'Recipe Masters Group Approval','2893','org.kuali.rice.kew.engine.node.RequestsNode',4)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2707',0,'1',0,'Adhoc Routing','2895','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2707',0,'1',0,'Chicken Recipe Masters Group Approval','2896','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','3006',0,'1',0,'Initiated','2914','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('R','3006',0,'1',0,'PeopleFlows','RE','2915','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','3008',0,'1',0,'Initiated','2919','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','3009',0,'1',0,'Initiated','2921','org.kuali.rice.kew.engine.node.InitialNode',1)
/

# -----------------------------------------------------------------------
# KREW_RTE_NODE_CFG_PARM_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2162','2057','<start name="Initiated"><activationType>P</activationType><mandatoryRoute>false</mandatoryRoute><finalApproval>false</finalApproval></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2163','2057','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2164','2057','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2165','2057','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2166','2057','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2167','2059','<start name="Initiated"><activationType>P</activationType></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2168','2059','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2169','2059','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2243','2344','<start name="AdHoc"><activationType>P</activationType></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2244','2344','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2245','2344','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2360','2580','<start name="Initiated"><activationType>P</activationType><mandatoryRoute>false</mandatoryRoute><finalApproval>false</finalApproval></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2361','2580','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2362','2580','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2363','2580','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2364','2580','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2365','2581','<requests name="eDoc.Example1.Node1"><activationType>P</activationType><ruleTemplate>eDoc.Example1.Node1</ruleTemplate><mandatoryRoute>false</mandatoryRoute><finalApproval>false</finalApproval></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2366','2581','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleTemplate','2367','2581','eDoc.Example1.Node1')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2368','2581','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2369','2581','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2370','2581','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2397','2846','<start name="PreRoute"><activationType>P</activationType></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2398','2846','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2399','2846','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2400','2847','<requests name="DestinationApproval"><ruleTemplate>TravelRequest-DestinationRouting</ruleTemplate></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleTemplate','2401','2847','TravelRequest-DestinationRouting')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2402','2847','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2403','2848','<requests name="TravelerApproval"><ruleTemplate>TravelRequest-TravelerRouting</ruleTemplate></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleTemplate','2404','2848','TravelRequest-TravelerRouting')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2405','2848','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2406','2849','<requests name="SupervisorApproval"><ruleTemplate>TravelRequest-SupervisorRouting</ruleTemplate></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleTemplate','2407','2849','TravelRequest-SupervisorRouting')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2408','2849','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2409','2850','<requests name="AccountApproval"><ruleTemplate>TravelRequest-AccountRouting</ruleTemplate></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleTemplate','2410','2850','TravelRequest-AccountRouting')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2411','2850','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2412','2892','<start name="Adhoc Routing"><activationType>P</activationType><mandatoryRoute>false</mandatoryRoute><finalApproval>false</finalApproval></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2413','2892','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2414','2892','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2415','2892','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2416','2892','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2417','2893','<requests name="Recipe Masters Group Approval"><activationType>S</activationType><ruleSelector>Named</ruleSelector><ruleName>RecipeMastersGroupApproval</ruleName></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2418','2893','S')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2419','2893','Named')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleName','2420','2893','RecipeMastersGroupApproval')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2421','2895','<start name="Adhoc Routing"><activationType>P</activationType><mandatoryRoute>false</mandatoryRoute><finalApproval>false</finalApproval></start>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2422','2895','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2423','2895','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2424','2895','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2425','2895','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2426','2896','<requests name="Chicken Recipe Masters Group Approval"><activationType>S</activationType><ruleSelector>Named</ruleSelector><ruleName>ChickenRecipeMastersGroupApproval</ruleName></requests>')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2427','2896','S')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2428','2896','Named')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleName','2429','2896','ChickenRecipeMastersGroupApproval')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2461','2914','<start name="Initiated">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</start>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2462','2914','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2463','2914','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2464','2914','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2465','2914','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2466','2915','<requests name="PeopleFlows">
<activationType>R</activationType>
<rulesEngine executorClass="edu.sampleu.travel.workflow.TravelAccountRulesEngineExecutor"/>
</requests>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2467','2915','R')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID)
  VALUES ('rulesEngine','2468','2915')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2469','2915','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2475','2919','<start name="Initiated">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</start>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2476','2919','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2477','2919','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2478','2919','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2479','2919','Template')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('contentFragment','2480','2921','<start name="Initiated">
<activationType>P</activationType>
<mandatoryRoute>false</mandatoryRoute>
<finalApproval>false</finalApproval>
</start>
')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('activationType','2481','2921','P')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('mandatoryRoute','2482','2921','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('finalApproval','2483','2921','false')
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T (KEY_CD,RTE_NODE_CFG_PARM_ID,RTE_NODE_ID,VAL)
  VALUES ('ruleSelector','2484','2921','Template')
/

# -----------------------------------------------------------------------
# KREW_RTE_NODE_LNK_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2580','2581')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2846','2847')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2847','2848')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2848','2849')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2849','2850')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2892','2893')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2895','2896')
/
INSERT INTO KREW_RTE_NODE_LNK_T (FROM_RTE_NODE_ID,TO_RTE_NODE_ID)
  VALUES ('2914','2915')
/

# -----------------------------------------------------------------------
# KREW_RTE_BRCH_PROTO_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RTE_BRCH_PROTO_T (BRCH_NM,RTE_BRCH_PROTO_ID,VER_NBR)
  VALUES ('ApprovalBranch','2422',130)
/

# -----------------------------------------------------------------------
# KREW_RULE_ATTR_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_ATTR_T (APPL_ID,CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR)
  VALUES ('TRAVEL','edu.sampleu.travel.workflow.DestinationRuleAttribute','Department of Prudence Routing','Department of Prudence Routing','DestinationAttribute','6166CBA1B95A644DE0404F8189D86C09','1011','RuleAttribute',1)
/
INSERT INTO KREW_RULE_ATTR_T (APPL_ID,CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR)
  VALUES ('TRAVEL','edu.sampleu.travel.workflow.EmployeeAttribute','Employee Routing','Employee Routing','EmployeeAttribute','6166CBA1B95B644DE0404F8189D86C09','1012','RuleAttribute',1)
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR)
  VALUES ('edu.sampleu.travel.workflow.AccountAttribute','AccountAttribute','AccountAttribute','AccountAttribute','6166CBA1B95C644DE0404F8189D86C09','1013','RuleAttribute',2)
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.krad.workflow.attribute.KualiXmlSearchableAttributeImpl','The search attribute used to find documents by account number.','Account Number Attribute','TravelAccountDocumentAccountNumberAttribute','6166CBA1B95D644DE0404F8189D86C09','1014','SearchableXmlAttribute',1,'<searchingConfig>
                <fieldDef name="accountNumber" title="kuali_dd_label(TravelAccount)">
                    <display>
                        <type>text</type>
                    </display>
                    <validation required="false"/>
                    <fieldEvaluation>
                        <xpathexpression>wf:xstreamsafe(\'//newMaintainableObject/businessObject/number\')</xpathexpression>
                    </fieldEvaluation>
                </fieldDef>
            </searchingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.rule.xmlrouting.StandardGenericXMLRuleAttribute','EDL School Routing','EDL Campus Routing','EDL.Campus.Example','6166CBA1B95F644DE0404F8189D86C09','1100','RuleXmlAttribute',2,'<routingConfig>
        <fieldDef name="campus" title="Campus" workflowType="ALL">
          <display>
            <type>select</type>
            <values title="IUB">IUB</values>
            <values title="IUPUI">IUPUI</values>
          </display>
          <validation required="false"/>
          <fieldEvaluation>
            <xpathexpression>//campus = wf:ruledata(\'campus\')</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
        <xmlDocumentContent>
          <campus>%campus%</campus>
        </xmlDocumentContent>
      </routingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR)
  VALUES ('edu.sampleu.travel.workflow.AccountAttribute','foo','foo','FiscalOfficer','6166CBA1B96D644DE0404F8189D86C09','1133','RuleAttribute',2)
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR)
  VALUES ('components.LoadTestActionListAttibute','LoadTestActionListAttribute','LoadTestActionListAttribute','LoadTestActionListAttribute','6166CBA1B9C4644DE0404F8189D86C09','1232','ActionListAttribute',1)
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute','XML Searchable attribute','XML Searchable attribute','XMLSearchableAttribute_CaseInsensitive','6166CBA1B9C6644DE0404F8189D86C09','1234','SearchableXmlAttribute',1,'<searchingConfig>
        <fieldDef name="givenname_nocase" title="First name">
          <display>
            <type>text</type>
          </display>
          <searchDefinition autoWildcardLocation="prefixonly" caseSensitive="false"/>
          <validation required="true">
            <regex>^[a-zA-Z ]+$</regex>
            <message>Invalid first name</message>
          </validation>
          <fieldEvaluation>
            <xpathexpression>//givenname/value</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
      </searchingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute','XML Searchable attribute','XML Searchable attribute','XMLSearchableAttributeStdLong','6166CBA1B9C7644DE0404F8189D86C09','1235','SearchableXmlAttribute',1,'<searchingConfig>
        <fieldDef name="testLongKey" title="Certain Long Value">
          <display>
            <type>text</type>
          </display>
          <searchDefinition allowWildcards="true" autoWildcardLocation="suffixOnly" dataType="long"/>
          <fieldEvaluation>
            <xpathexpression>//testLongKey/value</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
      </searchingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute','XML Searchable attribute','XML Searchable attribute','XMLSearchableAttributeStdFloat','6166CBA1B9C8644DE0404F8189D86C09','1236','SearchableXmlAttribute',1,'<searchingConfig>
        <fieldDef name="testFloatKey" title="Float in the Water">
          <display>
            <type>text</type>
          </display>
          <searchDefinition dataType="float"/>
          <fieldEvaluation>
            <xpathexpression>//testFloatKey/value</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
      </searchingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute','XML Searchable attribute','XML Searchable attribute','XMLSearchableAttributeStdCurrency','6166CBA1B9C9644DE0404F8189D86C09','1237','SearchableXmlAttribute',1,'<searchingConfig>
        <fieldDef name="testCurrencyKey" title="Dollars Here">
          <display>
            <type>text</type>
            <parameters name="displayFormatPattern">#.00</parameters>
          </display>
          <searchDefinition dataType="float"/>
          <fieldEvaluation>
            <xpathexpression>//testCurrencyKey/value</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
      </searchingConfig>')
/
INSERT INTO KREW_RULE_ATTR_T (CLS_NM,DESC_TXT,LBL,NM,OBJ_ID,RULE_ATTR_ID,RULE_ATTR_TYP_CD,VER_NBR,XML)
  VALUES ('org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute','XML Searchable attribute','XML Searchable attribute','XMLSearchableAttributeStdDateTime','6166CBA1B9CA644DE0404F8189D86C09','1238','SearchableXmlAttribute',1,'<searchingConfig>
        <fieldDef name="testDateTimeKey" title="Searchable Date Field">
          <display>
            <type>text</type>
          </display>
          <searchDefinition dataType="datetime" datePicker="false"/>
          <fieldEvaluation>
            <xpathexpression>//putWhateverWordsIwantInsideThisTag/testDateTimeKey/value</xpathexpression>
          </fieldEvaluation>
        </fieldDef>
      </searchingConfig>')
/

# -----------------------------------------------------------------------
# KREW_RULE_EXPR_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_EXPR_T (OBJ_ID,RULE_EXPR,RULE_EXPR_ID,TYP,VER_NBR)
  VALUES ('616A0754-3BA6-39DF-9A1B-11432849DC6B','import edu.sampleu.recipe.util.RecipeUtils
		        import org.apache.commons.lang.StringUtils
				import org.kuali.rice.kew.engine.RouteContext;
				import org.kuali.rice.kew.rule.Rule;
				import org.kuali.rice.kew.rule.RuleExpressionResult;

				String ingredients = RecipeUtils.getRecipeIngredientsFromRecipeDocumentContent(routeContext)

				if(StringUtils.containsIgnoreCase(ingredients, "chicken")) {
					return RecipeUtils.constructGroupApprovalRequest("KR-WKFLW:ChickenRecipeMasters", rule)
		        } else {
		        	/* Return an empty route request */
		        	return RecipeUtils.constructEmptyApprovalRequest(rule);
		        }','2000','BSF:groovy',1)
/
INSERT INTO KREW_RULE_EXPR_T (OBJ_ID,RULE_EXPR,RULE_EXPR_ID,TYP,VER_NBR)
  VALUES ('3364EB47-6407-9C45-8AAD-7A8310266208','import javax.xml.namespace.QName
				import org.kuali.rice.resourceloader.GlobalResourceLoader
				import edu.sampleu.magazine.service.MagazineService
		        import edu.sampleu.recipe.util.RecipeUtils
		        import org.apache.commons.lang.StringUtils
				import org.kuali.rice.kew.engine.RouteContext
				import org.kuali.rice.kew.rule.Rule
				import org.kuali.rice.kew.rule.RuleExpressionResult

				String origin = RecipeUtils.getRecipeOriginFromRecipeDocumentContent(routeContext)

				QName serviceName = new QName("magazineNamespace", "magazineSoapService")
				MagazineService magazineSoapService = (MagazineService) GlobalResourceLoader.getService(serviceName)
				String managedMagazines[] = magazineSoapService.getAllManagedMagazines()

				if(ArrayUtils.contains(managedMagazines, origin)) {
					return RecipeUtils.constructGroupApprovalRequest("KR-WKFLW:MagazineManagers", rule)
		        } else {
		        	/* Return an empty route request */
		        	return RecipeUtils.constructEmptyApprovalRequest(rule);
		        }','2001','BSF:groovy',1)
/

# -----------------------------------------------------------------------
# KREW_RULE_TMPL_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('Ack1Template','6166CBA1BA88644DE0404F8189D86C09','Acknowledgement 1 Template','1017',4)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('Ack2Template','6166CBA1BA89644DE0404F8189D86C09','Acknowledgement 2 Template','1018',4)
/
INSERT INTO KREW_RULE_TMPL_T (DLGN_RULE_TMPL_ID,NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('1015','TravelRequest-DestinationRouting','6166CBA1BA8E644DE0404F8189D86C09','Destination Routing','1026',8)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('TravelRequest-TravelerRouting','6166CBA1BA8F644DE0404F8189D86C09','Traveler Routing','1028',2)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('TravelRequest-SupervisorRouting','6166CBA1BA90644DE0404F8189D86C09','Supervisor Routing','1030',2)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('TravelRequest-AccountRouting','6166CBA1BA91644DE0404F8189D86C09','Travel Account Routing','1032',2)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('eDoc.Example1.Node1','6166CBA1BA92644DE0404F8189D86C09','eDocLite Example1 Routing','1101',2)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('WorkflowDocument2Template','6166CBA1BAE9644DE0404F8189D86C09','Workflow Document Template 2','1537',2)
/
INSERT INTO KREW_RULE_TMPL_T (NM,OBJ_ID,RULE_TMPL_DESC,RULE_TMPL_ID,VER_NBR)
  VALUES ('WorkflowDocument3Template','6166CBA1BAEA644DE0404F8189D86C09','Workflow Document Template 3','1538',2)
/

# -----------------------------------------------------------------------
# KREW_RULE_TMPL_ATTR_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_TMPL_ATTR_T (ACTV_IND,DSPL_ORD,OBJ_ID,REQ_IND,RULE_ATTR_ID,RULE_TMPL_ATTR_ID,RULE_TMPL_ID,VER_NBR)
  VALUES (1,3,'6166CBA1BB17644DE0404F8189D86C09',1,'1011','1027','1026',8)
/
INSERT INTO KREW_RULE_TMPL_ATTR_T (ACTV_IND,DSPL_ORD,OBJ_ID,REQ_IND,RULE_ATTR_ID,RULE_TMPL_ATTR_ID,RULE_TMPL_ID,VER_NBR)
  VALUES (1,4,'6166CBA1BB18644DE0404F8189D86C09',1,'1012','1029','1028',2)
/
INSERT INTO KREW_RULE_TMPL_ATTR_T (ACTV_IND,DSPL_ORD,OBJ_ID,REQ_IND,RULE_ATTR_ID,RULE_TMPL_ATTR_ID,RULE_TMPL_ID,VER_NBR)
  VALUES (1,5,'6166CBA1BB19644DE0404F8189D86C09',1,'1012','1031','1030',2)
/
INSERT INTO KREW_RULE_TMPL_ATTR_T (ACTV_IND,DSPL_ORD,OBJ_ID,REQ_IND,RULE_ATTR_ID,RULE_TMPL_ATTR_ID,RULE_TMPL_ID,VER_NBR)
  VALUES (1,6,'6166CBA1BB1A644DE0404F8189D86C09',1,'1013','1033','1032',2)
/
INSERT INTO KREW_RULE_TMPL_ATTR_T (ACTV_IND,DSPL_ORD,OBJ_ID,REQ_IND,RULE_ATTR_ID,RULE_TMPL_ATTR_ID,RULE_TMPL_ID,VER_NBR)
  VALUES (1,0,'6166CBA1BB1C644DE0404F8189D86C09',0,'1100','1102','1101',2)
/

# -----------------------------------------------------------------------
# KREW_RULE_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080801155902', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'TravelRequest',0,STR_TO_DATE( '20080801155902', '%Y%m%d%H%i%s' ),'TravelRequest.Destination.LasVegas','6166CBA1BBEA644DE0404F8189D86C09','Destination - Las Vegas','1046','1026',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080801155902', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'TravelRequest',0,STR_TO_DATE( '20080801155902', '%Y%m%d%H%i%s' ),'TravelRequest.Supervisor','6166CBA1BBEB644DE0404F8189D86C09','Supervisor Routing','1049','1028',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'TravelRequest',0,STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),'TravelRequest.DeanDirector','6166CBA1BBEC644DE0404F8189D86C09','Dean/Director Routing','1050','1030',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'TravelRequest',0,STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),'TravelRequest.FiscalOfficer','6166CBA1BBED644DE0404F8189D86C09','Fiscal Officer Routing','1051','1030',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'TravelRequest',0,STR_TO_DATE( '20080801155903', '%Y%m%d%H%i%s' ),'659D86718DD514C7E0404F8189D877C3','6166CBA1BBEE644DE0404F8189D86C09','Fiscal Officer Routing','1052','1032',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080916122615', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'eDoc.Example1Doctype',0,STR_TO_DATE( '20080916122615', '%Y%m%d%H%i%s' ),'eDoc.Example1Doctype.IUB','6166CBA1BBEF644DE0404F8189D86C09','Routing rule for EDocLite Example with IUB selected.','1103','1101',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DACTVN_DT,DLGN_IND,DOC_TYP_NM,FRC_ACTN,FRM_DT,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_TMPL_ID,RULE_VER_NBR,TMPL_RULE_IND,TO_DT,VER_NBR)
  VALUES (STR_TO_DATE( '20080916122616', '%Y%m%d%H%i%s' ),1,1,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0,'eDoc.Example1Doctype',0,STR_TO_DATE( '20080916122616', '%Y%m%d%H%i%s' ),'eDoc.Example1Doctype.IUPUI','6166CBA1BBF0644DE0404F8189D86C09','Routing rule for EDocLite Example with IUPUI selected.','1106','1101',0,0,STR_TO_DATE( '21000101000000', '%Y%m%d%H%i%s' ),0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DLGN_IND,DOC_TYP_NM,FRC_ACTN,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_ID,RULE_VER_NBR,TMPL_RULE_IND,VER_NBR)
  VALUES (STR_TO_DATE( '20090515122209', '%Y%m%d%H%i%s' ),1,1,0,'RecipeParentMaintenanceDocument',1,'RecipeMastersGroupApproval','D1216F1E-EAB3-68F8-FF50-1B20357669A4','This rule requires an approval from the RecipeMasters workgroup.','1642',0,0,0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DLGN_IND,DOC_TYP_NM,FRC_ACTN,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_EXPR_ID,RULE_ID,RULE_VER_NBR,TMPL_RULE_IND,VER_NBR)
  VALUES (STR_TO_DATE( '20090515122209', '%Y%m%d%H%i%s' ),1,1,0,'RecipeMaintenanceDocument',1,'ChickenRecipeMastersGroupApproval','8E72AF5C-609C-9337-2606-62D941A7D3FF','This rule requires an approval from the ChickenRecipeMasters workgroup if the ingredients for the recipe contains Chicken.','2000','1643',0,0,0)
/
INSERT INTO KREW_RULE_T (ACTVN_DT,ACTV_IND,CUR_IND,DLGN_IND,DOC_TYP_NM,FRC_ACTN,NM,OBJ_ID,RULE_BASE_VAL_DESC,RULE_EXPR_ID,RULE_ID,RULE_VER_NBR,TMPL_RULE_IND,VER_NBR)
  VALUES (STR_TO_DATE( '20090515122209', '%Y%m%d%H%i%s' ),1,1,0,'RecipeMaintenanceDocument',1,'MagazineGroupApproval','CF9FBB5A-942F-0DEB-327F-BE402D55AF51','This rule requires an approval from the Magazine workgroup if any of the sources lists one of the Magazine System\'s managed magazines.','2001','1644',0,0,0)
/

# -----------------------------------------------------------------------
# KREW_DLGN_RSP_T
# -----------------------------------------------------------------------
INSERT INTO KREW_DLGN_RSP_T (DLGN_RULE_BASE_VAL_ID,DLGN_RULE_ID,DLGN_TYP,OBJ_ID,RSP_ID,VER_NBR)
  VALUES ('1640','1641','S','07B43BF1-B39B-CD35-024D-C05AF402D6A6','2022',1)
/

# -----------------------------------------------------------------------
# KREW_RULE_EXT_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1047','1046','1027',1)
/
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1104','1103','1102',1)
/
INSERT INTO KREW_RULE_EXT_T (RULE_EXT_ID,RULE_ID,RULE_TMPL_ATTR_ID,VER_NBR)
  VALUES ('1107','1106','1102',1)
/

# -----------------------------------------------------------------------
# KREW_RULE_EXT_VAL_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('destination','1047','1048','las vegas',1)
/
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('campus','1104','1105','IUB',1)
/
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('campus','1107','1108','IUPUI',1)
/

# -----------------------------------------------------------------------
# KREW_RULE_RSP_T
# -----------------------------------------------------------------------
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','org.kuali.rice.ken.kew.ChannelReviewerRoleAttribute!reviewers','6166CBA1BBFC644DE0404F8189D86C09',1,'2020','1044','2021','R',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','user4','6166CBA1BBFD644DE0404F8189D86C09',1,'2022','1046','2023','F',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','edu.sampleu.travel.workflow.EmployeeAttribute!employee','6166CBA1BBFE644DE0404F8189D86C09',1,'2024','1049','2025','R',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','edu.sampleu.travel.workflow.EmployeeAttribute!supervisr','6166CBA1BBFF644DE0404F8189D86C09',1,'2026','1050','2027','R',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('K','F','edu.sampleu.travel.workflow.EmployeeAttribute!director','6166CBA1BC00644DE0404F8189D86C09',1,'2028','1051','2029','R',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','edu.sampleu.travel.workflow.AccountAttribute!FO','6166CBA1BC01644DE0404F8189D86C09',1,'2030','1052','2031','R',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','2202','6166CBA1BC02644DE0404F8189D86C09',1,'2040','1103','2041','G',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','2203','6166CBA1BC03644DE0404F8189D86C09',1,'2042','1106','2043','G',1)
/
INSERT INTO KREW_RULE_RSP_T (ACTN_RQST_CD,APPR_PLCY,NM,OBJ_ID,PRIO,RSP_ID,RULE_ID,RULE_RSP_ID,TYP,VER_NBR)
  VALUES ('A','F','9997','B1FCE360-EA7A-C2B8-9D70-88C39A982094',1,'2063','1642','2064','G',1)
/

# -----------------------------------------------------------------------
# KREW_TYP_T
# -----------------------------------------------------------------------
INSERT INTO KREW_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Sample Type','KR-SAP','sampleAppPeopleFlowTypeService','1',1)
/

# -----------------------------------------------------------------------
# KREW_TYP_ATTR_T
# -----------------------------------------------------------------------
INSERT INTO KREW_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','1',1,'1','1',1)
/
INSERT INTO KREW_TYP_ATTR_T (ACTV,ATTR_DEFN_ID,SEQ_NO,TYP_ATTR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','2',2,'2','1',1)
/

# -----------------------------------------------------------------------
# KREW_USR_OPTN_T
# -----------------------------------------------------------------------
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','ACTION_LIST_SIZE_NEW','10',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','ACTION_REQUESTED_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VER_NBR)
  VALUES ('admin','APP_DOC_STATUS_COL_SHOW_NEW',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','CLEAR_FYI_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','CURRENT_NODE_COL_SHOW_NEW','no',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DATE_CREATED_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DELEGATOR_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DELEGATOR_FILTER','Secondary Delegators on Action List Page',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_A','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_C','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_D','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_E','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_F','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_I','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_P','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_R','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_S','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COLOR_X','white',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOCUMENT_STATUS_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','DOC_TYPE_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','EMAIL_NOTIFICATION','immediate',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','EMAIL_NOTIFY_PRIMARY','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','EMAIL_NOTIFY_SECONDARY','no',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VER_NBR)
  VALUES ('admin','INITIATOR_COL_SHOW_NEW',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','LAST_APPROVED_DATE_COL_SHOW_NEW','no',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','NOTIFY_ACKNOWLEDGE','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','NOTIFY_APPROVE','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','NOTIFY_COMPLETE','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','NOTIFY_FYI','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','OPEN_ITEMS_NEW_WINDOW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','PRIMARY_DELEGATE_FILTER','Primary Delegates on Action List Page',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','REFRESH_RATE','15',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','TITLE_COL_SHOW_NEW','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','USE_OUT_BOX','yes',1)
/
INSERT INTO KREW_USR_OPTN_T (PRNCPL_ID,PRSN_OPTN_ID,VAL,VER_NBR)
  VALUES ('admin','WORKGROUP_REQUEST_COL_SHOW_NEW','yes',1)
/
