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

TRUNCATE TABLE KRIM_EXT_ID_TYP_T
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','05','N','HR',STR_TO_DATE( '20081113140634', '%Y%m%d%H%i%s' ),'Human Resources ID','5B97C50B038A6110E0404F8189D85213',1)
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','06','N','LICENSE',STR_TO_DATE( '20081113140634', '%Y%m%d%H%i%s' ),'Driver\'s License','5B97C50B038B6110E0404F8189D85213',1)
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','01','N','LOGON',STR_TO_DATE( '20081113140634', '%Y%m%d%H%i%s' ),'Logon ID','5B97C50B038C6110E0404F8189D85213',1)
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','07','N','RFID',STR_TO_DATE( '20081113140634', '%Y%m%d%H%i%s' ),'RFID Implant','5B97C50B038D6110E0404F8189D85213',1)
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','02','Y','SSN',STR_TO_DATE( '20081113140634', '%Y%m%d%H%i%s' ),'Social Security Number','5B97C50B038E6110E0404F8189D85213',1)
/
INSERT INTO KRIM_EXT_ID_TYP_T (ACTV_IND,DISPLAY_SORT_CD,ENCR_REQ_IND,EXT_ID_TYP_CD,LAST_UPDT_DT,NM,OBJ_ID,VER_NBR)
  VALUES ('Y','03','Y','TAX',STR_TO_DATE( '20081113140635', '%Y%m%d%H%i%s' ),'Tax ID','5B97C50B038F6110E0404F8189D85213',1)
/
