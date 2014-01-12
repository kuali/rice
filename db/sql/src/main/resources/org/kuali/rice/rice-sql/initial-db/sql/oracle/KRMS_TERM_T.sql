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

TRUNCATE TABLE KRMS_TERM_T DROP STORAGE
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('Bloomington Campus Size','T1000','T1000',1)
/
INSERT INTO KRMS_TERM_T (TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('T1002','T1002',1)
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('Fund Name','T1003','T1003',1)
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('PO Value','T1004','T1004',1)
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('PO Item Type','T1005','T1005',1)
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('Account','T1006','T1006',1)
/
INSERT INTO KRMS_TERM_T (DESC_TXT,TERM_ID,TERM_SPEC_ID,VER_NBR)
  VALUES ('Occasion','T1007','T1007',1)
/
