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

INSERT INTO KRMS_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,DESC_TXT,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1000','An identifier for a PeopleFlow','PeopleFlow','peopleFlowId','KR-RULE',0)
/
INSERT INTO KRMS_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,DESC_TXT,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1001','If true, execute the action','Invalid Rule','ruleTypeCode','KR-RULE',1)
/
INSERT INTO KRMS_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,DESC_TXT,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1004','Error','Error Action','actionTypeCode','KR-RULE',1)
/
INSERT INTO KRMS_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,DESC_TXT,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1005','Message validation action returns','Action Message','actionMessage','KR-RULE',1)
/
INSERT INTO KRMS_ATTR_DEFN_T (ACTV,ATTR_DEFN_ID,DESC_TXT,LBL,NM,NMSPC_CD,VER_NBR)
  VALUES ('Y','1006','PeopleFlow Name','PeopleFlow Name','peopleFlowName','KR-RULE',1)
/
