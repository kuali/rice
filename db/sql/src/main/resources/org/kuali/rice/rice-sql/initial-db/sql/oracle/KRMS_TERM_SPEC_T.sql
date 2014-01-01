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

TRUNCATE TABLE KRMS_TERM_SPEC_T DROP STORAGE
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','Size in # of students of the campus','campusSize','KR-RULE-TEST','T1000','java.lang.Integer',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','is the principal in the organization','orgMember','KR-RULE-TEST','T1001','java.lang.Boolean',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','null','Campus Code','KR-RULE-TEST','T1002','java.lang.String',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','null','bogusFundTermSpec','KR-RULE-TEST','T1003','java.lang.String',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','Purchase Order Value','PO Value','KR-RULE-TEST','T1004','java.lang.Integer',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','Purchased Item Type','PO Item Type','KR-RULE-TEST','T1005','java.lang.String',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','Charged To Account','Account','KR-RULE-TEST','T1006','java.lang.String',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','Special Event','Occasion','KR-RULE-TEST','T1007','java.lang.String',1)
/
INSERT INTO KRMS_TERM_SPEC_T (ACTV,DESC_TXT,NM,NMSPC_CD,TERM_SPEC_ID,TYP,VER_NBR)
  VALUES ('Y','null','campusCode','KR-RULE-TEST','T1008','java.lang.String',1)
/
