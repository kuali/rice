--
-- Copyright 2005-2012 The Kuali Foundation
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

--INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,POST_PRCSR,RTE_VER_NBR,VER_NBR,OBJ_ID)
--  VALUES (1,1,'KualiDocument',2680,'KualiDocument',0,'KualiDocument','none','2',2,'B169E3D4890B4A9293E46CE58385B547');
--INSERT INTO KREW_DOC_TYP_T (ACTV_IND,CUR_IND,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,LBL,PARNT_ID,RTE_VER_NBR,VER_NBR,OBJ_ID)
--  VALUES (1,1,'Parent Document Type for all Rice Documents',2681,'RiceDocument',0,'Rice Document',2680,'2',3,'B169E3D4890B4A9293E46CE58385B548');
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,PARNT_ID,POST_PRCSR,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR,OBJ_ID)
  VALUES (1,'1',1,'docHandler is now mandatory...','Workflow Maintenance Document',2682,'EDENSERVICE-DOCS',1,'1','Workflow Maintenance Document',2681,'none',2010,'2',1,'B169E3D4890B4A9293E46CE58385B549');
INSERT INTO KREW_DOC_TYP_T (ACTV_IND,BLNKT_APPR_GRP_ID,CUR_IND,DOC_HDLR_URL,DOC_TYP_DESC,DOC_TYP_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,GRP_ID,LBL,PARNT_ID,POST_PRCSR,PREV_DOC_TYP_VER_NBR,RTE_VER_NBR,VER_NBR,OBJ_ID)
  VALUES (1,'1',1,'${application.url}/travelDocument2.do?methodToCall=docHandler','Create a New Travel Request',2683,'TravelRequest',1,'1','Travel Request',2681,'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor',2028,'2',1,'B169E3D4890B4A9293E46CE58385B54A');
