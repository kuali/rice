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

TRUNCATE TABLE KRMS_CNTXT_T DROP STORAGE
/
INSERT INTO KRMS_CNTXT_T (ACTV,CNTXT_ID,DESC_TXT,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','CONTEXT1','null','Context1','KR-RULE-TEST','T1003',1)
/
INSERT INTO KRMS_CNTXT_T (ACTV,CNTXT_ID,DESC_TXT,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','CONTEXT_NO_PERMISSION','null','Context with no premissions','KRMS_TEST_VOID','T1003',1)
/
INSERT INTO KRMS_CNTXT_T (ACTV,CNTXT_ID,DESC_TXT,NM,NMSPC_CD,TYP_ID,VER_NBR)
  VALUES ('Y','trav-acct-test-ctxt','null','Travel Account','KR-SAP','T4',1)
/
