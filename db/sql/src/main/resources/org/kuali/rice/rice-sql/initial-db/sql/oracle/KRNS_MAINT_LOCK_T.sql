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

TRUNCATE TABLE KRNS_MAINT_LOCK_T DROP STORAGE
/
INSERT INTO KRNS_MAINT_LOCK_T (DOC_HDR_ID,MAINT_LOCK_ID,MAINT_LOCK_REP_TXT,OBJ_ID,VER_NBR)
  VALUES ('2381','2006','edu.sampleu.travel.bo.TravelAccount!!number^^a11','6B9BCFF0-45A8-43B6-B837-92DB714C4A5E',1)
/
INSERT INTO KRNS_MAINT_LOCK_T (DOC_HDR_ID,MAINT_LOCK_ID,MAINT_LOCK_REP_TXT,OBJ_ID,VER_NBR)
  VALUES ('2382','2007','edu.sampleu.travel.bo.TravelAccount!!number^^a4','7F487602-E290-87FD-A4EF-C2764B6586B7',1)
/
INSERT INTO KRNS_MAINT_LOCK_T (DOC_HDR_ID,MAINT_LOCK_ID,MAINT_LOCK_REP_TXT,OBJ_ID,VER_NBR)
  VALUES ('2383','2008','edu.sampleu.travel.bo.TravelAccount!!number^^a5','D39B8A9F-601C-6207-F440-0456442C7266',1)
/
