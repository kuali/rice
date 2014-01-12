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

TRUNCATE TABLE KREW_DOC_TYP_T
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Workflow Maintenance Document Type Document','2011','DocumentTypeDocument',0,'1','default.htm?turl=WordDocuments%2Fdocumenttype.htm','Workflow Maintenance Document Type Document','6166CBA1BA5D644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Rule Maintenance Document Type Document','2012','RoutingRuleDocument',0,'1','Rule Maintenance Document Type Document','6166CBA1BA5E644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${ken.url}/DetailView.form','This is the re-usable notification document type that will be used for delivering all notifications with KEW.','2023','KualiNotification',0,'2000','Notification','6166CBA1BA69644DE0404F8189D86C09','org.kuali.rice.ken.postprocessor.kew.NotificationPostProcessor','1',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${ken.url}/AdministerNotificationRequest.form','Create a New Notification Request','2024','SendNotificationRequest',0,'1','Send Notification Request','6166CBA1BA6A644DE0404F8189D86C09','org.kuali.rice.ken.postprocessor.kew.NotificationSenderFormPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',0,'${kr.url}/maintenance.do?methodToCall=docHandler','Create a New Travel Account Maintenance Document','2029','TravelAccountMaintenanceDocument',0,'1','Travel Account Maintenance Document','6166CBA1BA6F644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create a New Travel Fiscal Officer','2030','FiscalOfficerMaintenanceDocument',0,'1','Travel Fiscal Officer','6166CBA1BA70644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create/edit parameter namespaces','2031','NamespaceMaintenanceDocument',0,'1','default.htm?turl=WordDocuments%2Fnamespace.htm','Namespace','6166CBA1BA71644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create/edit a parameter type','2032','ParameterTypeMaintenanceDocument',0,'1','default.htm?turl=WordDocuments%2Fparametertype.htm','Parameter Type Maintenance Document','6166CBA1BA72644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create/edit a parameter detail type','2033','ParameterDetailTypeMaintenanceDocument',0,'1','default.htm?turl=WordDocuments%2Fparametercomponent.htm','Parameter Detail Type Maintenance Document','6166CBA1BA73644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create/edit a parameter','2034','ParameterMaintenanceDocument',0,'1','default.htm?turl=WordDocuments%2Fparameter.htm','Parameter Maintenance Document','6166CBA1BA74644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
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
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'KualiDocument','2680','KualiDocument',0,'KualiDocument','6166CBA1BA81644DE0404F8189D86C09','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'Parent Document Type for all Rice Documents','2681','RiceDocument',0,'Rice Document','6166CBA1BA82644DE0404F8189D86C09','2680','2',11)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/travelDocument2.do?methodToCall=docHandler','Create a New Travel Request','2683','TravelRequest',0,'1','Travel Request','6166CBA1BA84644DE0404F8189D86C09','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,0,'${application.url}/kim/identityManagementPersonDocument.do?methodToCall=docHandler','2697','IdentityManagementPersonDocument',0,'Person Document','6166CBA1BA85644DE0404F8189D86C09','2681','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','Routing Rule Delegation','2699','RoutingRuleDelegationMaintenanceDocument',0,'Routing Rule Delegation','A6DC8753-AF90-7A01-0EF7-E6D446529668','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,0,'${application.url}/kim/identityManagementGroupDocument.do?methodToCall=docHandler','2702','IdentityManagementGroupDocument',0,'Group','6BE7733A-643E-666C-CD18-E7439476C12E','2681','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,0,'${application.url}/kim/identityManagementRoleDocument.do?methodToCall=docHandler','2703','IdentityManagementRoleDocument',0,'Role','EAF58989-D602-4CB1-961D-E31E0383B2D6','2681','2',1)
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
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2708','CampusMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fcampus.htm','CampusMaintenanceDocument','616D94CA-D08D-D036-E77D-4B53DB34CD95','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2709','CampusTypeMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fcampustype.htm','CampusTypeMaintenanceDocument','DE0B8588-E459-C07A-87B8-6ACD693AE70C','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2710','CountryMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fcountry.htm','CountryMaintenanceDocument','82EDB593-97BA-428E-C6E7-A7F3031CFAEB','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2711','CountyMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fcounty.htm','CountyMaintenanceDocument','C972E260-5552-BB63-72E6-A514301B0326','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2712','PostalCodeMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fpostalcode.htm','PostalCodeMaintenanceDocument','B79D1104-BC48-1597-AFBE-773EED31A110','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2713','StateMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fstate.htm','StateMaintenanceDocument','EF2378F6-E770-D7BF-B7F1-C18881E3AFF0','2681','2',0)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'2994','IdentityManagementDocument',0,'Identity Management Document','944596CD-A7FC-0DEE-EDE1-52A52BED85CC','2681','2',6)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kim.url}/identityManagementRoleDocument.do?methodToCall=docHandler','2995','IdentityManagementRoleDocument',1,'default.htm?turl=WordDocuments%2Frole.htm','Role','FEA8D9DD-0592-0525-B2BD-2F4BA811CF30','2994','2703','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kim.url}/identityManagementGroupDocument.do?methodToCall=docHandler','2996','IdentityManagementGroupDocument',1,'default.htm?turl=WordDocuments%2Fgroup.htm','Group','D9636763-7749-8F3F-4570-21181E977AE3','2994','2702','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kim.url}/identityManagementPersonDocument.do?methodToCall=docHandler','2997','IdentityManagementPersonDocument',1,'default.htm?turl=WordDocuments%2Fperson.htm','Person','14C95FE4-1497-82C6-CBBD-BF16AD81B845','2994','2697','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2998','IdentityManagementReviewResponsibilityMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fresponsibility.htm','Review Responsibility','66413887-3C82-B12D-9563-0A893E8D1910','2994','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.url}/maintenance.do?methodToCall=docHandler','2999','IdentityManagementGenericPermissionMaintenanceDocument',0,'default.htm?turl=WordDocuments%2Fpermission.htm','Permission','A3AE4787-018E-1F17-6EB6-F2F0F366774F','2994','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/krmsAgendaEditor?methodToCall=docHandler','Create a KRMS Agenda','3000','AgendaEditorMaintenanceDocument',0,'1','KRMS Agenda Editor Maintenance Document','ebd70731-4d33-4c0b-a958-2b9ca047ae07','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a New Agenda','3001','AgendaMaintenanceDocument',0,'1','Agenda Maintenance Document','3198b708-6e29-4b19-bf35-51473cf8a3d1','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_HDLR_URL,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,OBJ_ID,PARNT_ID,RTE_VER_NBR,VER_NBR)
  VALUES (1,1,'${kr.krad.url}/peopleFlowMaintenance?methodToCall=docHandler','3002','PeopleFlowMaintenanceDocument',0,'PeopleFlowMaintenanceDocument','2c0a1333-f60b-47c4-a9b0-76f32f1ed97d','2681','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a KRMS Context','3003','ContextMaintenanceDocument',0,'1','KRMS Context Maintenance Document','87413487-8306-4130-b2df-a5d0e42243f9','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a KRMS Term','3004','TermMaintenanceDocument',0,'1','KRMS Term Maintenance Document','bc83f80b-85c4-40fd-998c-ef53fdd97e3f','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a KRMS Term Specification','3005','TermSpecificationMaintenanceDocument',0,'1','KRMS Term Specification Maintenance Document','57e7ee1d-e44a-4154-9ba4-ee562c434c98','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.krad.url}/maintenance?methodToCall=docHandler','Create a New Travel Account Maintenance Document','3006','TravelAccountMaintenanceDocument',1,'1','Travel Account Maintenance Document','0b89a795-8079-460a-b456-39c5c477938b','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2029','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,HELP_DEF_URL,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${kr.url}/maintenance.do?methodToCall=docHandler','Create/edit a component','3007','ComponentMaintenanceDocument',0,'1','default.htm?turl=WordDocuments%2Fparametertype.htm','Component Maintenance Document','e9e0086b-30ce-4a9b-a2d6-564772076a1e','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/kr-krad/approval?methodToCall=docHandler','Create a New Travel Authorization Document','3008','TravelAuthorization',0,'1','Travel Authorization Document','b9110d38-8fe0-4123-a65a-5df2336795fa','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','1',1)
/
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,OBJ_ID,PARNT_ID,POST_PRCSR,RTE_VER_NBR,VER_NBR)
  VALUES (1,'1',1,'${application.url}/krad/maintenance?methodToCall=docHandler&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo','Create a New Fiscal Officer Maintenance Document','3009','FiscalOfficerInfoMaintenanceDocument',0,'1','Fiscal Officer Info Maintenance Document','a4ecd35b-f35a-40ac-a9e1-ed47201281ec','2681','org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor','2',1)
/
