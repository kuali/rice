-- 
-- Copyright 2009 The Kuali Foundation
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
Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleService'
/
Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Workgroup'
/
Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentSearch'
/
-- Following lines commented out because these records should already be in the master rice database
-- These were needed to run against the KFS rice database since they were missing.
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'Backdoor', 'F7E44233C2C440FFB1A399548951160A', 1, 'Backdoor', 'Y')
--/
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'ActionList', '1821D8BAB21E498F9FB1ECCA25C37F9B', 1, 'Action List', 'Y')
--/
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'EDocLite', '51DD5B9FACDD4EDAA9CA8D53A82FCCCA', 1, 'eDocLite', 'Y')
--/
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'Feature', 'BBD9976498A4441F904013004F3D70B3', 1, 'Feature', 'Y')
--/
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'Mailer', '5DB9D1433E214325BE380C82762A223B', 1, 'Mailer', 'Y')
--/
--Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'QuickLinks', '3E26DA76458A46D68CBAF209DA036157', 1, 'Quick Link', 'Y')
/
Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'Notification', 'D04AFB1812E34723ABEB64986AC61DC9', 1, 'Notification', 'Y')
/

UPDATE KRNS_PARM_T SET PARM_DTL_TYP_CD = 'DocSearchCriteriaDTO' where NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentSearch'
/
UPDATE KRNS_PARM_T SET PARM_DTL_TYP_CD = 'Notification' where NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Workgroup'
/

