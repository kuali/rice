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

TRUNCATE TABLE KRMS_TERM_RSLVR_PARM_SPEC_T DROP STORAGE
/
INSERT INTO KRMS_TERM_RSLVR_PARM_SPEC_T (NM,TERM_RSLVR_ID,TERM_RSLVR_PARM_SPEC_ID,VER_NBR)
  VALUES ('Campus Code','T1000','T1000',1)
/
INSERT INTO KRMS_TERM_RSLVR_PARM_SPEC_T (NM,TERM_RSLVR_ID,TERM_RSLVR_PARM_SPEC_ID,VER_NBR)
  VALUES ('Org Code','T1001','T1001',1)
/
INSERT INTO KRMS_TERM_RSLVR_PARM_SPEC_T (NM,TERM_RSLVR_ID,TERM_RSLVR_PARM_SPEC_ID,VER_NBR)
  VALUES ('Principal ID','T1001','T1002',1)
/
