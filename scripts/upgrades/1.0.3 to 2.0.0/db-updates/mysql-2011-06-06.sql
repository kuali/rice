--
-- Copyright 2005-2017 The Kuali Foundation
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

ALTER TABLE KREW_DOC_TYP_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
/
ALTER TABLE KREW_RULE_ATTR_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
/
ALTER TABLE KRSB_SVC_DEF_T CHANGE APPL_NMSPC APPL_ID VARCHAR(255)
/
ALTER TABLE KRSB_MSG_QUE_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255)
/
ALTER TABLE KRNS_NMSPC_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(255)
/
ALTER TABLE KRNS_PARM_T CHANGE APPL_NMSPC_CD APPL_ID VARCHAR(255)
/

RENAME TABLE KRNS_NMSPC_T TO KRCR_NMSPC_T
/
RENAME TABLE KRNS_PARM_TYP_T TO KRCR_PARM_TYP_T
/
RENAME TABLE KRNS_PARM_DTL_TYP_T TO KRCR_PARM_DTL_TYP_T
/
RENAME TABLE KRNS_PARM_T TO KRCR_PARM_T
/

RENAME TABLE KRNS_CAMPUS_T TO KRLC_CMP_T
/
RENAME TABLE KRNS_CMP_TYP_T TO KRLC_CMP_TYP_T
/
RENAME TABLE KR_COUNTRY_T TO KRLC_CNTRY_T
/
RENAME TABLE KR_STATE_T TO KRLC_ST_T
/
RENAME TABLE KR_POSTAL_CODE_T TO KRLC_PSTL_CD_T
/
RENAME TABLE KR_COUNTY_T TO KRLC_CNTY_T
/
