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
-- KULRICE-3212 
ALTER TABLE KREW_DOC_HDR_T MODIFY (APP_DOC_ID VARCHAR2(255))
/

-- KULRICE-3015 - Standardize length of document type name and lbl columns
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_RULE_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_DMP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (LBL VARCHAR2(128))
/
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/


