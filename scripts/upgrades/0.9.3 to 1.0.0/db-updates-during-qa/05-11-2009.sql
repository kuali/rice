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
INSERT INTO krns_parm_t(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD)
  VALUES('KR-NS', 'All', 'DEFAULT_COUNTRY', '64B87B4C5E3B8F4CE0404F8189D8291A', 1, 'CONFG', 'US', 'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.', 'A')
/

UPDATE krim_typ_t 
    SET srvc_nm = 'rolePermissionTypeService'
WHERE nm = 'Role'
/

UPDATE krim_typ_t 
    SET srvc_nm = 'groupPermissionTypeService'
WHERE nm = 'Group'
/
UPDATE krim_typ_t
    SET nm = 'Edit Mode & Document Type'
      , srvc_nm = 'documentTypeAndEditModePermissionTypeService'
      WHERE nm = 'Edit Mode'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor'
    WHERE DOC_TYP_NM = 'KualiDocument'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = NULL
    WHERE DOC_TYP_NM = 'IdentityManagementPersonDocument'
/
COMMIT
/
INSERT INTO krns_parm_t ( NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, TXT, PARM_TYP_CD, PARM_DESC_TXT, CONS_CD, OBJ_ID ) 
    VALUES ( 
          'KR-IDM' 
        , 'Document' 
        , 'MAX_MEMBERS_PER_PAGE' 
        , '20' 
        , 'CONFG' 
        , 'The maximum number of role or group members to display at once on their documents. If the number is above this value, the document will switch into a paging mode with only this many rows displayed at a time.' 
        , 'A' 
        , '2238b58e-8fb9-102c-9461-def224dad9b3'
      ) 
/ 
COMMIT 
/ 
INSERT INTO krns_parm_dtl_typ_t 
    ( SELECT 'KR-IDM', parm_dtl_typ_cd, SYS_GUID(), 1, nm, 'Y' 
        FROM krns_parm_dtl_typ_t 
        WHERE NMSPC_CD = 'KR-NS' 
    ) 
/ 
COMMIT 
/ 
