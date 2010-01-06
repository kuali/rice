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
-- KULRICE-2981 - drop unused tables

DROP TABLE KR_QRTZ_PAUSED_TRIGGERS_GRPS
/

-- KULRICE-2982

ALTER TABLE krns_parm_t DROP COLUMN grp_nm
/
ALTER TABLE krns_parm_t DROP COLUMN actv_ind
/
ALTER TABLE krns_parm_t MODIFY parm_nm VARCHAR2(255)
/
ALTER TABLE krew_rule_t MODIFY nm NULL
/
ALTER TABLE krns_parm_t DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE krns_parm_t ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM)
/
ALTER TABLE KRIM_ROLE_DOCUMENT_T DROP COLUMN role_typ_name
/
ALTER TABLE KRIM_ATTR_DEFN_T DROP COLUMN srvc_nm
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN rsp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN role_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN grp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN prncpl_id
/
ALTER TABLE krim_role_rsp_actn_t MODIFY role_rsp_id NULL
/
DROP TABLE KRIM_GRP_GRP_T
/
DROP TABLE KRIM_GRP_PRNCPL_T
/
DROP TABLE KRIM_DLGN_PRNCPL_T
/
DROP TABLE KRIM_DLGN_GRP_T
/
DROP TABLE KRIM_DLGN_ROLE_T
/
DROP TABLE KRIM_ROLE_PRNCPL_T
/
DROP TABLE KRIM_ROLE_GRP_T
/
DROP TABLE KRIM_ROLE_ROLE_T
/
