--
-- Copyright 2005-2011 The Kuali Foundation
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

ALTER TABLE KREW_ACTN_RQST_T RENAME COLUMN IGN_PREV_ACTN_IND TO FRC_ACTN
/
ALTER TABLE KREW_RULE_T RENAME COLUMN IGNR_PRVS TO FRC_ACTN
/
ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
ALTER TABLE KRIM_ROLE_RSP_ACTN_T RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, FRC_ACTN, PRIORITY_NBR )
AS
select
rsp.nmspc_cd as rsp_nmspc_cd
, rsp.rsp_id
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
, rm.MBR_ID
, rm.MBR_TYP_CD
, rm.ROLE_MBR_ID
, actn.ACTN_TYP_CD
, actn.ACTN_PLCY_CD
, actn.FRC_ACTN
, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd
, rsp_id
, role_id
, role_mbr_id
/
