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

TRUNCATE TABLE KREN_RECIP_LIST_T DROP STORAGE
/
INSERT INTO KREN_RECIP_LIST_T (CHNL_ID,RECIP_ID,RECIP_LIST_ID,RECIP_TYP_CD)
  VALUES (4,'testuser1',1,'USER')
/
INSERT INTO KREN_RECIP_LIST_T (CHNL_ID,RECIP_ID,RECIP_LIST_ID,RECIP_TYP_CD)
  VALUES (4,'testuser3',2,'USER')
/
