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
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentType' AND parm_nm = 'DOCUMENT_TYPE_SEARCH_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'GlobalReviewer' AND parm_nm = 'REPLACE_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Note' AND parm_nm = 'NOTE_CREATE_NEW_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_CREATE_NEW_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_LOCKING_ON_IND'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_SEARCH_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleTemplate' AND parm_nm = 'RULE_TEMPLATE_CREATE_NEW_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleTemplate' AND parm_nm = 'RULE_TEMPLATE_SEARCH_INSTRUCTION'
/
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'PESSIMISTIC_LOCK_ADMIN_GROUP'
/
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'EXCEPTION_GROUP'
/
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'SUPERVISOR_GROUP'
/
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Batch' AND parm_nm = 'SCHEDULE_ADMIN_GROUP'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Backdoor' AND parm_nm = 'TARGET_FRAME_NAME'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'ActionList' AND parm_nm = 'HELP_DESK_NAME_GROUP'
/
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'ROUTE_LOG_POPUP_IND'
/

