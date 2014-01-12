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

TRUNCATE TABLE KRMS_RULE_T
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','stub rule lorem ipsum','Rule1','KR-RULE-TEST','T1000','T1000',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','Frog specimens bogus rule foo','Rule2','KR-RULE-TEST','T1001','T1001',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','Bloomington campus code rule','Rule3','KR-RULE-TEST','T1002','T1002',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','check for possible BBQ ingiter hazard','Rule4','KR-RULE-TEST','T1003','T1003',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','remembered to wear socks','Rule5','KR-RULE-TEST','T1004','T1004',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','good behavior at carnival','Rule6','KR-RULE-TEST','T1005','T1005',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','is KRMS in da haus','Rule7','KR-RULE-TEST','T1006','T1006',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','For testing compound props','CmpdTestRule','KR-RULE-TEST','T1007','T1007',1)
/
INSERT INTO KRMS_RULE_T (ACTV,DESC_TXT,NM,NMSPC_CD,PROP_ID,RULE_ID,VER_NBR)
  VALUES ('Y','Does PO require my approval','Going Away Party for Travis','KR-RULE-TEST','T1011','T1008',1)
/
