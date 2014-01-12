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

TRUNCATE TABLE KRMS_TYP_T
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Notify PeopleFlow','KR-RULE','notificationPeopleFlowActionTypeService','1000',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Route to PeopleFlow','KR-RULE','approvalPeopleFlowActionTypeService','1001',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Validation Rule','KR-RULE','validationRuleTypeService','1002',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Validation Action','KR-RULE','validationActionTypeService','1003',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','TermResolver','KR-RULE-TEST','T1000',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','CAMPUS','KR-RULE-TEST','myCampusService','T1001',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','KrmsActionResolverType','KR-RULE-TEST','testActionTypeService','T1002',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','CONTEXT','KR-RULE-TEST','T1003',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','AGENDA','KR-RULE-TEST','T1004',1)
/
INSERT INTO KRMS_TYP_T (ACTV,NM,NMSPC_CD,SRVC_NM,TYP_ID,VER_NBR)
  VALUES ('Y','Campus Agenda','KR-RULE-TEST','campusAgendaTypeService','T1005',1)
/
