--
-- Copyright 2005-2017 The Kuali Foundation
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

INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2011',0,'1',0,'placeholder','2004','org.kuali.rice.kew.engine.node.InitialNode',2)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2012',0,'1',0,'placeholder','2006','org.kuali.rice.kew.engine.node.InitialNode',2)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2023',1,'1',0,'Adhoc Routing','2039','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2024',0,'1',0,'Initiated','2041','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2024',0,'1',0,'ReviewersNode','FR','ReviewersRouting','2042','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2024',0,'1',0,'RequestsNode','FR','NotificationRouting','2043','org.kuali.rice.kew.engine.node.RequestsNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2031',0,'1',0,'Initiated','2061','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2032',0,'1',0,'Initiated','2063','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2033',0,'1',0,'Initiated','2065','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2034',0,'1',0,'Initiated','2067','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2680',0,0,'PreRoute','2840','org.kuali.rice.kew.engine.node.InitialNode',2)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2995',0,0,'AdHoc','2898','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2995',0,0,'RoleType','RM','org.kuali.rice.kew.role.RoleRouteModule','2899','org.kuali.rice.kew.engine.node.RoleNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2996',0,0,'AdHoc','2901','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2996',0,0,'GroupType','RM','org.kuali.rice.kew.role.RoleRouteModule','2902','org.kuali.rice.kew.engine.node.RoleNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2997',0,0,'AdHoc','2904','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2997',0,0,'GroupType','RM','org.kuali.rice.kew.role.RoleRouteModule','2905','org.kuali.rice.kew.engine.node.RoleNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_MTHD_CD,RTE_MTHD_NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','2997',0,0,'RoleType','RM','org.kuali.rice.kew.role.RoleRouteModule','2906','org.kuali.rice.kew.engine.node.RoleNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2998',0,0,'AdHoc','2908','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('S','2999',0,0,'AdHoc','2910','org.kuali.rice.kew.engine.node.InitialNode',1)
/
INSERT INTO KREW_RTE_NODE_T (ACTVN_TYP,DOC_TYP_ID,FNL_APRVR_IND,GRP_ID,MNDTRY_RTE_IND,NM,RTE_NODE_ID,TYP,VER_NBR)
  VALUES ('P','3007',0,'1',0,'Initiated','2917','org.kuali.rice.kew.engine.node.InitialNode',1)
/
