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

ALTER TABLE TRVL_MLG_RT_T CHANGE COLUMN MLG_RT MLG_RT DECIMAL(7,3) NOT NULL
/

UPDATE TRVL_MLG_RT_T SET MLG_RT='0.305' WHERE MLG_RT_ID='10000'
/

ALTER TABLE TRVL_TRAVELER_DTL_T CHANGE COLUMN ID ID VARCHAR(40) NOT NULL DEFAULT '0'
/

UPDATE TRVL_TRAVELER_DTL_T SET DRIVE_LIC_EXP_DT='20150630', DRIVE_LIC_NUM='CA12345678' WHERE ID='1'
/

UPDATE TRVL_EXP_ITM_T SET EXP_AMT='1278.97' WHERE EXP_ITM_ID='10000'
/

ALTER TABLE TRVL_AUTH_DOC_T CHANGE COLUMN TRAVELER_DTL_ID TRAVELER_DTL_ID VARCHAR(40) NULL DEFAULT NULL
/ 