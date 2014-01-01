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

TRUNCATE TABLE KRMS_AGENDA_ITM_T DROP STORAGE
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,ALWAYS,RULE_ID,VER_NBR,WHEN_FALSE,WHEN_TRUE)
  VALUES ('T1000','T1000','T1005','T1000',1,'T1004','T1001')
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,ALWAYS,RULE_ID,VER_NBR)
  VALUES ('T1000','T1001','T1002','T1001',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,ALWAYS,RULE_ID,VER_NBR,WHEN_FALSE)
  VALUES ('T1000','T1002','T1003','T1002',1,'T1006')
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1000','T1003','T1003',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1000','T1004','T1004',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1000','T1005','T1005',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1000','T1006','T1006',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1001','T1007','T1007',1)
/
INSERT INTO KRMS_AGENDA_ITM_T (AGENDA_ID,AGENDA_ITM_ID,RULE_ID,VER_NBR)
  VALUES ('T1002','T1008','T1008',1)
/
