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


DROP INDEX FP_DOC_HEADER_TC0 ON krns_doc_hdr_t
;


CREATE UNIQUE INDEX KRNS_DOC_HDR_TC0
    ON krns_doc_hdr_t(OBJ_ID)
;


DROP INDEX FP_MAINT_DOC_ATTACHMENT_TC0 ON krns_maint_doc_att_t
;


CREATE UNIQUE INDEX KRNS_MAINT_DOC_ATT_TC0
    ON krns_maint_doc_att_t(OBJ_ID)
;


DROP INDEX FP_MAINT_LOCK_TC0 ON krns_maint_lock_t
;


CREATE UNIQUE INDEX KRNS_MAINT_LOCK_TC0
    ON krns_maint_lock_t(OBJ_ID)
;


DROP INDEX FP_MAINTENANCE_DOCUMENT_TC0 ON krns_maint_doc_t
;


CREATE UNIQUE INDEX KRNS_MAINT_DOC_TC0
    ON krns_maint_doc_t(OBJ_ID)
;


DROP INDEX FS_ADHOC_RTE_ACTN_RECP_TC0 ON krns_adhoc_rte_actn_recip_t
;


CREATE UNIQUE INDEX KRNS_ADHOC_RTE_ACTN_RECIP_TC0
    ON krns_adhoc_rte_actn_recip_t(OBJ_ID)
;


DROP INDEX FS_LOOKUP_RESULTS_MTC0 ON krns_lookup_rslt_t
;


CREATE UNIQUE INDEX KRNS_LOOKUP_RSLT_TC0
    ON krns_lookup_rslt_t(OBJ_ID)
;


DROP INDEX FS_LOOKUP_SELECTIONS_MTC0 ON krns_lookup_sel_t
;


CREATE UNIQUE INDEX KRNS_LOOKUP_SEL_TC0
    ON krns_lookup_sel_t(OBJ_ID)
;


DROP INDEX KNS_PESSIMISTIC_LOCK_TC0 ON krns_pessimistic_lock_t
;


CREATE UNIQUE INDEX KRNS_PESSIMISTIC_LOCK_TC0
    ON krns_pessimistic_lock_t(OBJ_ID)
;


DROP INDEX SH_ATT_TC0 ON krns_att_t
;


CREATE UNIQUE INDEX KRNS_ATT_TC0
    ON krns_att_t(OBJ_ID)
;


DROP INDEX SH_NTE_TC0 ON krns_nte_t
;


CREATE UNIQUE INDEX KRNS_NTE_TC0
    ON krns_nte_t(OBJ_ID)
;


DROP INDEX SH_NTE_TYP_TC0 ON krns_nte_typ_t
;


CREATE UNIQUE INDEX KRNS_NTE_TYP_TC0
    ON krns_nte_typ_t(OBJ_ID)
;

ALTER TABLE KRNS_LOOKUP_RSLT_T MODIFY PRNCPL_ID VARCHAR(40)
;

ALTER TABLE KRNS_LOOKUP_SEL_T MODIFY PRNCPL_ID VARCHAR(40)
;

ALTER TABLE KRNS_NTE_T MODIFY AUTH_PRNCPL_ID VARCHAR(40)
;

ALTER TABLE KRNS_PESSIMISTIC_LOCK_T MODIFY PRNCPL_ID VARCHAR(40)

;
