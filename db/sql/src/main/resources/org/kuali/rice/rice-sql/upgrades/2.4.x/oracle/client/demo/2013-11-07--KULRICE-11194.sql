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

-- KULRICE-11194  Travel Demo App data inconsistancies
-- cleanup of inconsistencies in data definitions and some value enhancements


ALTER TABLE TRVL_MLG_RT_T ADD MLG_RT_TEMP NUMBER
/  
UPDATE TRVL_MLG_RT_T SET MLG_RT_TEMP = MLG_RT
/
ALTER TABLE TRVL_MLG_RT_T MODIFY MLG_RT NULL
/  
UPDATE TRVL_MLG_RT_T SET MLG_RT=null
/  
ALTER TABLE TRVL_MLG_RT_T MODIFY MLG_RT NUMBER(7,3)
/  
UPDATE TRVL_MLG_RT_T SET MLG_RT=MLG_RT_TEMP
/  
ALTER TABLE TRVL_MLG_RT_T MODIFY MLG_RT NOT NULL
/  
ALTER TABLE TRVL_MLG_RT_T DROP COLUMN MLG_RT_TEMP
/

UPDATE TRVL_MLG_RT_T SET MLG_RT='0.305' WHERE MLG_RT_ID='10000'
/

CREATE TABLE TRVL_TRAVELER_DTL_T_TEMP AS SELECT * FROM TRVL_TRAVELER_DTL_T
/
TRUNCATE TABLE TRVL_TRAVELER_DTL_T
/
ALTER TABLE TRVL_TRAVELER_DTL_T MODIFY ID VARCHAR(40)
/
INSERT INTO TRVL_TRAVELER_DTL_T SELECT * FROM TRVL_TRAVELER_DTL_T_TEMP
/
DROP TABLE TRVL_TRAVELER_DTL_T_TEMP
/

UPDATE TRVL_TRAVELER_DTL_T SET DRIVE_LIC_EXP_DT='08-NOV-18', DRIVE_LIC_NUM='CA12345678' WHERE ID='1'
/

UPDATE TRVL_EXP_ITM_T SET EXP_AMT='1278.97' WHERE EXP_ITM_ID='10000'
/

ALTER TABLE TRVL_AUTH_DOC_T MODIFY TRAVELER_DTL_ID VARCHAR(40)
/
