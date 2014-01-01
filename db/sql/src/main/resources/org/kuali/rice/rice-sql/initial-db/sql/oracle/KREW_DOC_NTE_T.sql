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

TRUNCATE TABLE KREW_DOC_NTE_T DROP STORAGE
/
INSERT INTO KREW_DOC_NTE_T (AUTH_PRNCPL_ID,CRT_DT,DOC_HDR_ID,DOC_NTE_ID,TXT,VER_NBR)
  VALUES ('admin',TO_DATE( '20080916130500', 'YYYYMMDDHH24MISS' ),'2213','2000','Added this test note.',1)
/
INSERT INTO KREW_DOC_NTE_T (AUTH_PRNCPL_ID,CRT_DT,DOC_HDR_ID,DOC_NTE_ID,TXT,VER_NBR)
  VALUES ('admin',TO_DATE( '20080916140236', 'YYYYMMDDHH24MISS' ),'2219','2002','This is a test note.',3)
/
INSERT INTO KREW_DOC_NTE_T (AUTH_PRNCPL_ID,CRT_DT,DOC_HDR_ID,DOC_NTE_ID,TXT,VER_NBR)
  VALUES ('user1',TO_DATE( '20080916142558', 'YYYYMMDDHH24MISS' ),'2219','2004','Added a note at second node.',2)
/
