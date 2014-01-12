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

TRUNCATE TABLE KRIM_ENTITY_CACHE_T DROP STORAGE
/
INSERT INTO KRIM_ENTITY_CACHE_T (ENTITY_ID,ENTITY_TYP_CD,FIRST_NM,LAST_NM,LAST_UPDT_TS,OBJ_ID,PRNCPL_ID,PRNCPL_NM,PRSN_NM)
  VALUES ('1100','PERSON','admin','admin',TO_DATE( '20121125192925', 'YYYYMMDDHH24MISS' ),'6d4e59c3-caf1-453d-8724-0998765eb180','admin','admin','admin, admin')
/
