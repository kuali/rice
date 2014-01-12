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

TRUNCATE TABLE KRMS_TERM_RSLVR_T
/
INSERT INTO KRMS_TERM_RSLVR_T (ACTV,NM,NMSPC_CD,OUTPUT_TERM_SPEC_ID,TERM_RSLVR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','campusSizeResolver','KR-RULE-TEST','T1000','T1000','T1000',1)
/
INSERT INTO KRMS_TERM_RSLVR_T (ACTV,NM,NMSPC_CD,OUTPUT_TERM_SPEC_ID,TERM_RSLVR_ID,TYP_ID,VER_NBR)
  VALUES ('Y','orgMemberResolver','KR-RULE-TEST','T1001','T1001','T1000',1)
/
