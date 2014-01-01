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

TRUNCATE TABLE KRCR_PARM_TYP_T
/
INSERT INTO KRCR_PARM_TYP_T (ACTV_IND,NM,OBJ_ID,PARM_TYP_CD,VER_NBR)
  VALUES ('Y','Authorization','53680C68F593AD9BE0404F8189D80A6C','AUTH',1)
/
INSERT INTO KRCR_PARM_TYP_T (ACTV_IND,NM,OBJ_ID,PARM_TYP_CD,VER_NBR)
  VALUES ('Y','Config','53680C68F591AD9BE0404F8189D80A6C','CONFG',1)
/
INSERT INTO KRCR_PARM_TYP_T (ACTV_IND,NM,OBJ_ID,PARM_TYP_CD,VER_NBR)
  VALUES ('Y','Help','53680C68F594AD9BE0404F8189D80A6C','HELP',1)
/
INSERT INTO KRCR_PARM_TYP_T (ACTV_IND,NM,OBJ_ID,PARM_TYP_CD,VER_NBR)
  VALUES ('Y','Document Validation','53680C68F592AD9BE0404F8189D80A6C','VALID',1)
/
