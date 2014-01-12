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

TRUNCATE TABLE KRIM_EMP_TYP_T
/
INSERT INTO KRIM_EMP_TYP_T (ACTV_IND,DISPLAY_SORT_CD,EMP_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','02','N',STR_TO_DATE( '20081113140632', '%Y%m%d%H%i%s' ),'Non-Professional','5B97C50B03826110E0404F8189D85213',1)
/
INSERT INTO KRIM_EMP_TYP_T (ACTV_IND,DISPLAY_SORT_CD,EMP_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','99','O',STR_TO_DATE( '20081113140633', '%Y%m%d%H%i%s' ),'Other','5B97C50B03836110E0404F8189D85213',1)
/
INSERT INTO KRIM_EMP_TYP_T (ACTV_IND,DISPLAY_SORT_CD,EMP_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','01','P',STR_TO_DATE( '20081113140633', '%Y%m%d%H%i%s' ),'Professional','5B97C50B03846110E0404F8189D85213',1)
/
