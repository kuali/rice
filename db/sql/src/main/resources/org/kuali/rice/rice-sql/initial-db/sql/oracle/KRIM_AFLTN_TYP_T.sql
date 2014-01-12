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

TRUNCATE TABLE KRIM_AFLTN_TYP_T DROP STORAGE
/
INSERT INTO KRIM_AFLTN_TYP_T (ACTV_IND,AFLTN_TYP_CD,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','AFLT','d','N',TO_DATE( '20081113140630', 'YYYYMMDDHH24MISS' ),'Affiliate','5B97C50B03736110E0404F8189D85213',1)
/
INSERT INTO KRIM_AFLTN_TYP_T (ACTV_IND,AFLTN_TYP_CD,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','FCLTY','b','Y',TO_DATE( '20081113140630', 'YYYYMMDDHH24MISS' ),'Faculty','5B97C50B03746110E0404F8189D85213',1)
/
INSERT INTO KRIM_AFLTN_TYP_T (ACTV_IND,AFLTN_TYP_CD,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','STAFF','c','Y',TO_DATE( '20081113140630', 'YYYYMMDDHH24MISS' ),'Staff','5B97C50B03756110E0404F8189D85213',1)
/
INSERT INTO KRIM_AFLTN_TYP_T (ACTV_IND,AFLTN_TYP_CD,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','STDNT','a','N',TO_DATE( '20081113140630', 'YYYYMMDDHH24MISS' ),'Student','5B97C50B03766110E0404F8189D85213',1)
/
