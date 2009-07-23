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
-- KULRICE-3126 This will turn off field level help by default.
UPDATE KRNS_PARM_T SET TXT = 'N' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'All' AND PARM_NM = 'ENABLE_FIELD_LEVEL_HELP_IND'
/
-- KULRICE-3349: Add the doc handler URL to the Campus, Campus Type, Country, County, Postal Code, and State document types.
UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='CampusMaintenanceDocument' OR DOC_TYP_NM='CampusTypeMaintenanceDocument' OR DOC_TYP_NM='CountryMaintenanceDocument' OR DOC_TYP_NM='CountyMaintenanceDocument' OR DOC_TYP_NM='PostalCodeMaintenanceDocument' OR DOC_TYP_NM='StateMaintenanceDocument'
/
--KULRICE-3283
ALTER TABLE KRNS_PARM_T ADD APPL_NMSPC_CD  varchar2(20) default 'KUALI' not null
/
ALTER TABLE KRNS_PARM_T DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE KRNS_PARM_T ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM, APPL_NMSPC_CD)
/
