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
-- DO NOT add comments before the blank line below, or they will disappear.
Alter table KREW_DOC_TYP_PROC_T modify INIT_RTE_NODE_ID DECIMAL null

delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentType' AND parm_nm = 'DOCUMENT_TYPE_SEARCH_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'GlobalReviewer' AND parm_nm = 'REPLACE_INSTRUCTION'
;

delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Note' AND parm_nm = 'NOTE_CREATE_NEW_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_CREATE_NEW_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_LOCKING_ON_IND'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'RULE_SEARCH_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleTemplate' AND parm_nm = 'RULE_TEMPLATE_CREATE_NEW_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleTemplate' AND parm_nm = 'RULE_TEMPLATE_SEARCH_INSTRUCTION'
;
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'PESSIMISTIC_LOCK_ADMIN_GROUP'
;
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'EXCEPTION_GROUP'
;
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Document' AND parm_nm = 'SUPERVISOR_GROUP'
;
delete from krns_parm_t where nmspc_cd = 'KR-NS' AND PARM_DTL_TYP_CD = 'Batch' AND parm_nm = 'SCHEDULE_ADMIN_GROUP'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Backdoor' AND parm_nm = 'TARGET_FRAME_NAME'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'ActionList' AND parm_nm = 'HELP_DESK_NAME_GROUP'
;
delete from krns_parm_t where nmspc_cd = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Rule' AND parm_nm = 'ROUTE_LOG_POPUP_IND'
;

UPDATE krew_doc_typ_t SET doc_hdlr_url = '${ken.url}/DetailView.form' WHERE doc_typ_nm = 'KualiNotification'
;
UPDATE krew_doc_typ_t SET doc_hdlr_url = '${ken.url}/AdministerNotificationRequest.form' WHERE doc_typ_nm = 'SendNotificationRequest'
;

Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'RuleService'
;
Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Workgroup'
;
Delete from KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentSearch'
;

Insert into KRNS_PARM_DTL_TYP_T (NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-WKFLW', 'Notification', 'D04AFB1812E34723ABEB64986AC61DC9', 1, 'Notification', 'Y')
;

UPDATE KRNS_PARM_T SET PARM_DTL_TYP_CD = 'DocSearchCriteriaDTO' where NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'DocumentSearch'
;
UPDATE KRNS_PARM_T SET PARM_DTL_TYP_CD = 'Notification' where NMSPC_CD = 'KR-WKFLW' AND PARM_DTL_TYP_CD = 'Workgroup'
;

CREATE TABLE KRIM_ENTITY_ETHNIC_T
(
      ID VARCHAR(40),
      ENTITY_ID VARCHAR(40),
      ETHNCTY_CD VARCHAR(40),
      SUB_ETHNCTY_CD VARCHAR(40),
      VER_NBR DECIMAL(8) default 1 NOT NULL,
      OBJ_ID VARCHAR(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_ETHNIC_TC0 UNIQUE (OBJ_ID)
)
;

ALTER TABLE KRIM_ENTITY_ETHNIC_T
    ADD CONSTRAINT KRIM_ENTITY_ETHNIC_TP1
PRIMARY KEY (ID)
;

CREATE TABLE KRIM_ENTITY_ETHNIC_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
)

;

CREATE TABLE KRIM_ENTITY_RESIDENCY_T
(
      ID VARCHAR(40),
      ENTITY_ID VARCHAR(40),
      DETERMINATION_METHOD VARCHAR(40),
      IN_STATE VARCHAR(40),
      VER_NBR DECIMAL(8) default 1 NOT NULL,
      OBJ_ID VARCHAR(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_RESIDENCY_TC0 UNIQUE (OBJ_ID)
)
;

ALTER TABLE KRIM_ENTITY_RESIDENCY_T
    ADD CONSTRAINT KRIM_ENTITY_RESIDENCY_TP1
PRIMARY KEY (ID)
;

CREATE TABLE KRIM_ENTITY_RESIDENCY_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
)

CREATE TABLE KRIM_ENTITY_VISA_T
(
      ID VARCHAR(40),
      ENTITY_ID VARCHAR(40),
      VISA_TYPE_KEY VARCHAR(40),
      VISA_ENTRY VARCHAR(40),
      VISA_ID VARCHAR(40),
      VER_NBR DECIMAL(8) default 1 NOT NULL,
      OBJ_ID VARCHAR(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_VISA_TC0 UNIQUE (OBJ_ID)
)
;
ALTER TABLE KRIM_ENTITY_VISA_T
    ADD CONSTRAINT KRIM_ENTITY_VISA_TP1
PRIMARY KEY (ID)
;
CREATE TABLE KRIM_ENTITY_VISA_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) 
;

INSERT into KRIM_ENTITY_ETHNIC_ID_S values();
;
INSERT INTO KRIM_ENTITY_ETHNIC_T ( ID, OBJ_ID, ENTITY_ID, ETHNCTY_CD )
    SELECT ((select max(id) from KRIM_ENTITY_ETHNIC_ID_S) , UUID(), bio.ENTITY_ID, bio.ETHNCTY_CD
        FROM KRIM_ENTITY_BIO_T bio
;

ALTER TABLE KRIM_ENTITY_BIO_T ADD DECEASED_DT DATE
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD MARITAL_STATUS VARCHAR(40)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD PRIM_LANG_CD VARCHAR(40)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD SEC_LANG_CD VARCHAR(40)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CNTRY_CD VARCHAR(2)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_STATE_CD VARCHAR(2)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CITY VARCHAR(30)
;
ALTER TABLE KRIM_ENTITY_BIO_T ADD GEO_ORIGIN VARCHAR(100)
;

-- drop ethnicity from bio table
ALTER TABLE KRIM_ENTITY_BIO_T DROP COLUMN ETHNCTY_CD
;

ALTER TABLE KRIM_ROLE_DOCUMENT_T ADD DESC_TXT VARCHAR(4000) 
;


CREATE INDEX krim_role_mbr_attr_data_ti1 ON krim_role_mbr_attr_data_t (role_mbr_id)
;
CREATE INDEX krim_role_mbr_ti1 ON krim_role_mbr_t (mbr_id)
;
CREATE INDEX krim_entity_addr_ti1 ON krim_entity_addr_t (entity_id)
;
CREATE INDEX krim_entity_email_ti1 ON krim_entity_email_t (entity_id)
;
CREATE INDEX krew_rte_node_cfg_parm_ti1 ON krew_rte_node_cfg_parm_t (rte_node_id)
;
CREATE INDEX krim_entity_phone_ti1 ON krim_entity_phone_t (entity_id)
;
CREATE INDEX krim_grp_mbr_ti1 ON krim_grp_mbr_t (mbr_id)
;

CREATE INDEX krim_entity_afltn_ti1 ON krim_entity_afltn_t (entity_id)
;
CREATE INDEX krim_entity_emp_info_ti1 ON krim_entity_emp_info_t (entity_id)
;
CREATE INDEX krim_entity_emp_info_ti2 ON krim_entity_emp_info_t (entity_afltn_id)
;
CREATE INDEX krim_entity_ent_typ_ti1 ON krim_entity_ent_typ_t (entity_id)
;
CREATE INDEX krim_entity_ext_id_ti1 ON krim_entity_ext_id_t (entity_id)
;
CREATE INDEX krim_entity_nm_ti1 ON krim_entity_nm_t (entity_id)
;
CREATE INDEX krim_perm_attr_data_ti1 ON krim_perm_attr_data_t (perm_id)
;
CREATE INDEX krim_role_perm_ti1 ON krim_role_perm_t (perm_id)
;
CREATE INDEX krim_role_rsp_ti1 ON krim_role_rsp_t (rsp_id)
;
CREATE INDEX krim_typ_attribute_ti1 ON krim_typ_attr_t (kim_typ_id) 
;


CREATE TABLE KRIM_ROLE_PERM_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) 

;

--KULRICE-3635
ALTER TABLE KRIM_PND_ROLE_MBR_MT
DROP COLUMN MBR_NM
; 


----KULRICE-3636 alter 100 intex here
DROP INDEX EN_DOC_TYP_TI1 ON krew_doc_typ_t
;

CREATE UNIQUE INDEX KREW_DOC_TYP_TI1
    ON krew_doc_typ_t(DOC_TYP_NM, DOC_TYP_VER_NBR)
;

DROP INDEX EN_RULE_TMPL_TI1 ON krew_rule_tmpl_t
;


CREATE UNIQUE INDEX KREW_RULE_TMPL_TI1
    ON krew_rule_tmpl_t(NM)
;


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


DROP INDEX KCB_MESSAGES_UK1 ON kren_msg_t
;


CREATE UNIQUE INDEX KREN_MSG_TC0
    ON kren_msg_t(ORGN_ID)
;


DROP INDEX KCB_MSG_DELIVS_UK1 ON kren_msg_deliv_t
;


CREATE UNIQUE INDEX KREN_MSG_DELIV_TC0
    ON kren_msg_deliv_t(MSG_ID, TYP_NM)
;


DROP INDEX KCB_RECIP_PREFS_UK1 ON kren_recip_prefs_t
;

CREATE UNIQUE INDEX KREN_RECIP_PREFS_TC0
    ON kren_recip_prefs_t(RECIP_ID, PROP)
;


DROP INDEX KR_KIM_ADDR_TYPE_TC0 ON krim_addr_typ_t
;

CREATE UNIQUE INDEX KRIM_ADDR_TYP_TC0
    ON krim_addr_typ_t(OBJ_ID)
;


DROP INDEX KR_KIM_ADDR_TYPE_TC1 ON krim_addr_typ_t
;

CREATE UNIQUE INDEX KRIM_ADDR_TYP_TC1
    ON krim_addr_typ_t(NM)
;


DROP INDEX KR_KIM_AFLTN_TYPE_TC0 ON krim_afltn_typ_t
;

CREATE UNIQUE INDEX KRIM_AFLTN_TYP_TC0
    ON krim_afltn_typ_t(OBJ_ID)
;


DROP INDEX KR_KIM_AFLTN_TYPE_TC1 ON krim_afltn_typ_t
;

CREATE UNIQUE INDEX KRIM_AFLTN_TYP_TC1
    ON krim_afltn_typ_t(NM)
;

DROP INDEX KR_KIM_ATTRIBUTE_TC0 ON krim_attr_defn_t
;

CREATE UNIQUE INDEX KRIM_ATTR_DEFN_TC0
    ON krim_attr_defn_t(OBJ_ID)
;


DROP INDEX KR_KIM_CTZNSHP_STAT_TC0 ON krim_ctznshp_stat_t
;

CREATE UNIQUE INDEX KRIM_CTZNSHP_STAT_TC0
    ON krim_ctznshp_stat_t(OBJ_ID)
;

DROP INDEX KR_KIM_CTZNSHP_STAT_TC1 ON krim_ctznshp_stat_t
;

CREATE UNIQUE INDEX KRIM_CTZNSHP_STAT_TC1
    ON krim_ctznshp_stat_t(NM)
;


DROP INDEX KR_KIM_DELE_MBR_ATTR_DATA_TC0 ON krim_dlgn_mbr_attr_data_t
;

CREATE UNIQUE INDEX KRIM_DLGN_MBR_ATTR_DATA_TC0
    ON krim_dlgn_mbr_attr_data_t(OBJ_ID)
;


DROP INDEX KR_KIM_DELE_TC0 ON krim_dlgn_t
;

CREATE UNIQUE INDEX KRIM_DLGN_TC0
    ON krim_dlgn_t(OBJ_ID)
;


DROP INDEX KR_KIM_EMAIL_TYPE_TC0 ON krim_email_typ_t
;

CREATE UNIQUE INDEX KRIM_EMAIL_TYP_TC0
    ON krim_email_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_EMAIL_TYPE_TC1 ON krim_email_typ_t
;

CREATE UNIQUE INDEX KRIM_EMAIL_TYP_TC1
    ON krim_email_typ_t(NM)
;


DROP INDEX KR_KIM_EMP_STAT_TC0 ON krim_emp_stat_t
;

CREATE UNIQUE INDEX KRIM_EMP_STAT_TC0
    ON krim_emp_stat_t(OBJ_ID)
;

DROP INDEX KR_KIM_EMP_STAT_TC1 ON krim_emp_stat_t
;

CREATE UNIQUE INDEX KRIM_EMP_STAT_TC1
    ON krim_emp_stat_t(NM)
;


DROP INDEX KR_KIM_EMP_TYPE_TC0 ON krim_emp_typ_t
;

CREATE UNIQUE INDEX  KRIM_EMP_TYP_TC0
    ON krim_emp_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_EMP_TYPE_TC1 ON krim_emp_typ_t
;

CREATE UNIQUE INDEX  KRIM_EMP_TYP_TC1
    ON krim_emp_typ_t(NM)
;

DROP INDEX KR_KIM_ENT_NAME_TYPE_TC0 ON krim_ent_nm_typ_t
;

CREATE UNIQUE INDEX KRIM_ENT_NM_TYP_TC0
    ON krim_ent_nm_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENT_NAME_TYPE_TC1 ON krim_ent_nm_typ_t
;

CREATE UNIQUE INDEX KRIM_ENT_NM_TYP_TC1
    ON krim_ent_nm_typ_t(NM)
;

DROP INDEX KR_KIM_ENT_TYPE_TC0 ON krim_ent_typ_t
;

CREATE UNIQUE INDEX KRIM_ENT_TYP_TC0
    ON krim_ent_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENT_TYPE_TC1 ON krim_ent_typ_t
;

CREATE UNIQUE INDEX KRIM_ENT_TYP_TC1
    ON krim_ent_typ_t(NM)
;

DROP INDEX KR_KIM_ENTITY_ADDR_TC0 ON krim_entity_addr_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_ADDR_TC0
    ON krim_entity_addr_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_AFLTN_TC0 ON krim_entity_afltn_t
;

CREATE UNIQUE INDEX  KRIM_ENTITY_AFLTN_TC0
    ON krim_entity_afltn_t(OBJ_ID)
;


DROP INDEX KR_KIM_ENTITY_BIO_TC0 ON krim_entity_bio_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_BIO_TC0
    ON krim_entity_bio_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_CTZNSHP_TC0 ON krim_entity_ctznshp_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_CTZNSHP_TC0
    ON krim_entity_ctznshp_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_EMAIL_TC0 ON krim_entity_email_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_EMAIL_TC0
    ON krim_entity_email_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_EMP_INFO_TC0 ON krim_entity_emp_info_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_EMP_INFO_TC0
    ON krim_entity_emp_info_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_EXT_ID_TC0 ON krim_entity_ext_id_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_EXT_ID_TC0
    ON krim_entity_ext_id_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_NAME_TC0 ON krim_entity_nm_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_NM_TC0
    ON krim_entity_nm_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_PHONE_TC0 ON krim_entity_phone_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_PHONE_TC0
    ON krim_entity_phone_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_PRIV_PREF_TC0 ON krim_entity_priv_pref_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_PRIV_PREF_TC0
    ON krim_entity_priv_pref_t(OBJ_ID)
;

DROP INDEX KR_KIM_ENTITY_TC0 ON krim_entity_t
;

CREATE UNIQUE INDEX KRIM_ENTITY_TC0
    ON krim_entity_t(OBJ_ID)
;

DROP INDEX KR_KIM_EXT_ID_TYPE_TC0 ON krim_ext_id_typ_t
;

CREATE UNIQUE INDEX KRIM_EXT_ID_TYP_TC0
    ON krim_ext_id_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_EXT_ID_TYPE_TC1 ON krim_ext_id_typ_t
;

CREATE UNIQUE INDEX KRIM_EXT_ID_TYP_TC1
    ON krim_ext_id_typ_t(NM)
;

DROP INDEX KR_KIM_GROUP_ATTR_DATA_TC0 ON krim_grp_attr_data_t
;

CREATE UNIQUE INDEX KRIM_GRP_ATTR_DATA_TC0
    ON krim_grp_attr_data_t(OBJ_ID)
;


DROP INDEX KR_KIM_GROUP_TC0 ON krim_grp_t
;

CREATE UNIQUE INDEX KRIM_GRP_TC0
    ON krim_grp_t(OBJ_ID)
;

DROP INDEX KR_KIM_GROUP_TC1 ON krim_grp_t
;

CREATE UNIQUE INDEX KRIM_GRP_TC1
    ON krim_grp_t(GRP_NM, NMSPC_CD)
;

DROP INDEX KR_KIM_PERM_ATTR_DATA_TC0 ON krim_perm_attr_data_t
;

CREATE UNIQUE INDEX KRIM_PERM_ATTR_DATA_TC0
    ON krim_perm_attr_data_t(OBJ_ID)
;

DROP INDEX KR_KIM_PERM_TC0 ON krim_perm_t
;

CREATE UNIQUE INDEX KRIM_PERM_TC0
    ON krim_perm_t(OBJ_ID)
;


DROP INDEX KR_KIM_PERM_TMPL_TC0 ON krim_perm_tmpl_t
;

CREATE UNIQUE INDEX KRIM_PERM_TMPL_TC0
    ON krim_perm_tmpl_t(OBJ_ID)
;

DROP INDEX KR_KIM_PHONE_TYPE_TC0 ON krim_phone_typ_t
;

CREATE UNIQUE INDEX KRIM_PHONE_TYP_TC0
    ON krim_phone_typ_t(OBJ_ID)
;

DROP INDEX KR_KIM_PHONE_TYPE_TC1 ON krim_phone_typ_t
;

CREATE UNIQUE INDEX KRIM_PHONE_TYP_TC1
    ON krim_phone_typ_t(PHONE_TYP_NM)
;

DROP INDEX KR_KIM_PRINCIPAL_TC0 ON krim_prncpl_t
;

CREATE UNIQUE INDEX KRIM_PRNCPL_TC0
    ON krim_prncpl_t(OBJ_ID)
;

DROP INDEX KR_KIM_PRINCIPAL_TC1 ON krim_prncpl_t
;

CREATE UNIQUE INDEX KRIM_PRNCPL_TC1
    ON krim_prncpl_t(PRNCPL_NM)
;

DROP INDEX KR_KIM_RESP_ATTR_DATA_TC0 ON krim_rsp_attr_data_t
;

CREATE UNIQUE INDEX KRIM_RSP_ATTR_DATA_TC0
    ON krim_rsp_attr_data_t(OBJ_ID)
;

DROP INDEX KR_KIM_RESP_TC0 ON krim_rsp_t
;

CREATE UNIQUE INDEX KRIM_RSP_TC0
    ON krim_rsp_t(OBJ_ID)
;

DROP INDEX KR_KIM_RESP_TMPL_TC0 ON krim_rsp_tmpl_t
;

CREATE UNIQUE INDEX KRIM_RSP_TMPL_TC0
    ON krim_rsp_tmpl_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_MBR_ATTR_DATA_TC0 ON krim_role_mbr_attr_data_t
;

CREATE UNIQUE INDEX KRIM_ROLE_MBR_ATTR_DATA_TC0
    ON krim_role_mbr_attr_data_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_PERM_TC0 ON krim_role_perm_t
;

CREATE UNIQUE INDEX KRIM_ROLE_PERM_TC0
    ON krim_role_perm_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_RESP_ACTN_TC0 ON krim_role_rsp_actn_t
;

CREATE UNIQUE INDEX KRIM_ROLE_RSP_ACTN_TC0
    ON krim_role_rsp_actn_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_RESP_TC0 ON krim_role_rsp_t
;

CREATE UNIQUE INDEX KRIM_ROLE_RSP_TC0
    ON krim_role_rsp_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_TC0 ON krim_role_t
;

CREATE UNIQUE INDEX KRIM_ROLE_TC0
    ON krim_role_t(OBJ_ID)
;

DROP INDEX KR_KIM_ROLE_TC1 ON krim_role_t
;

CREATE UNIQUE INDEX KRIM_ROLE_TC1
    ON krim_role_t(ROLE_NM, NMSPC_CD)
;

DROP INDEX KR_KIM_TYPE_ATTRIBUTE_TC0 ON krim_typ_attr_t
;

CREATE UNIQUE INDEX KRIM_TYP_ATTR_TC0
    ON krim_typ_attr_t(OBJ_ID)
;

DROP INDEX KR_KIM_TYPE_TC0 ON krim_typ_t
;

CREATE UNIQUE INDEX KRIM_TYP_TC0
    ON krim_typ_t(OBJ_ID)
;

DROP INDEX NOTIF_MSG_DELIVS_UK1 ON kren_ntfctn_msg_deliv_t
;

CREATE UNIQUE INDEX KREN_NTFCTN_MSG_DELIV_TC0
    ON kren_ntfctn_msg_deliv_t(NTFCTN_ID, RECIP_ID)
;

DROP INDEX NOTIFICATION_CHANNELS_UK1 ON kren_chnl_t
;

CREATE UNIQUE INDEX KREN_CHNL_TC0
    ON kren_chnl_t(NM)
;


DROP INDEX NOTIFICATION_CONTENT_TYPE_UK1 ON kren_cntnt_typ_t
;

CREATE UNIQUE INDEX KREN_CNTNT_TYP_TC0
    ON kren_cntnt_typ_t(NM, CNTNT_TYP_VER_NBR)
;


DROP INDEX NOTIFICATION_PRIORITIES_UK1 ON kren_prio_t
;

CREATE UNIQUE INDEX KREN_PRIO_TC0
    ON kren_prio_t(NM)
;

DROP INDEX NOTIFICATION_PRODUCERS_UK1 ON kren_prodcr_t
;

CREATE UNIQUE INDEX KREN_PRODCR_TC0
    ON kren_prodcr_t(NM)
;

DROP INDEX NOTIFICATION_RECIPIENTS_L_UK1 ON kren_recip_list_t
;

CREATE UNIQUE INDEX KREN_RECIP_LIST_TC0
    ON kren_recip_list_t(CHNL_ID, RECIP_TYP_CD, RECIP_ID)
;

DROP INDEX NOTIFICATION_RECIPIENTS_UK1 ON kren_recip_t
;

CREATE UNIQUE INDEX KREN_RECIP_TC0
    ON kren_recip_t(NTFCTN_ID, RECIP_TYP_CD, PRNCPL_ID)
;

DROP INDEX NOTIFICATION_REVIEWERS_UK1 ON kren_rvwer_t
;

CREATE UNIQUE INDEXKREN_RVWER_TC0
    ON kren_rvwer_t(CHNL_ID, TYP, PRNCPL_ID)
;

DROP INDEX NOTIFICATION_SENDERS_UK1 ON kren_sndr_t
;

CREATE UNIQUE INDEX KREN_SNDR_TC0
    ON kren_sndr_t(NTFCTN_ID, NM)
;

DROP INDEX SH_ATT_TC0 ON krns_att_t
;

CREATE UNIQUE INDEX KRNS_ATT_TC0
    ON krns_att_t(OBJ_ID)
;

DROP INDEX SH_CAMPUS_TC0 ON krns_campus_t
;

CREATE UNIQUE INDEX KRNS_CAMPUS_TC0
    ON krns_campus_t(OBJ_ID)
;

DROP INDEX SH_CMP_TYP_TC0 ON krns_cmp_typ_t
;

CREATE UNIQUE INDEX KRNS_CMP_TYP_TC0
    ON krns_cmp_typ_t(OBJ_ID)
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

DROP INDEX SH_PARM_DTL_TYP_TC0 ON krns_parm_dtl_typ_t
;

CREATE UNIQUE INDEX KRNS_PARM_DTL_TYP_TC0
    ON krns_parm_dtl_typ_t(OBJ_ID)
;

DROP INDEX SH_PARM_NMSPC_TC0 ON krns_nmspc_t
;

CREATE UNIQUE INDEX SKRNS_NMSPC_TC0
    ON krns_nmspc_t(OBJ_ID)
;

DROP INDEX SH_PARM_TC0 ON krns_parm_t
;

CREATE UNIQUE INDEX KRNS_PARM_TC0
    ON krns_parm_t(OBJ_ID)
;

DROP INDEX SH_PARM_TYP_TC0 ON krns_parm_typ_t
;

CREATE UNIQUE INDEX KRNS_PARM_TYP_TC0
    ON krns_parm_typ_t(OBJ_ID)
;

DROP INDEX USER_CHANNEL_SUBSCRIPTION_UK1 ON kren_chnl_subscrp_t
;

CREATE UNIQUE INDEX KREN_CHNL_SUBSCRP_TC0
    ON kren_chnl_subscrp_t(CHNL_ID, PRNCPL_ID)
;


----KULRICE-3627
UPDATE KREN_CNTNT_TYP_T SET 
    XSD = REPLACE(XSD, 
        'type="dateTime"', 'type="c:NonEmptyShortStringType"') 
    WHERE NM='Event'
go 

ALTER TABLE KRNS_LOOKUP_RSLT_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KRNS_LOOKUP_SEL_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KRNS_NTE_T MODIFY AUTH_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KRNS_PESSIMISTIC_LOCK_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_ACTN_ITM_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_ACTN_ITM_T MODIFY DLGN_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_ACTN_RQST_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_ACTN_TKN_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_ACTN_TKN_T MODIFY DLGTR_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_DOC_HDR_T MODIFY INITR_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_DOC_HDR_T MODIFY RTE_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_DOC_NTE_T MODIFY AUTH_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_EDL_DMP_T MODIFY DOC_HDR_INITR_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY DLGN_PRNCPL_ID VARCHAR2(40)
;
ALTER TABLE KREW_RMV_RPLC_DOC_T MODIFY PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_RMV_RPLC_DOC_T MODIFY RPLC_PRNCPL_ID VARCHAR(40)
;
ALTER TABLE KREW_USR_OPTN_T MODIFY PRNCPL_ID VARCHAR(40)
;

UPDATE KRNS_PARM_T SET TXT='MM/dd/yy;MM/dd/yyyy;MM/dd/yyyy HH:mm:ss;MM/dd/yy;MM-dd-yy;MMMM dd;yyyy;MMddyy' WHERE NMSPC_CD='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_DATE_FORMATS' AND APPL_NMSPC_CD='KUALI'
;
UPDATE KRNS_PARM_T SET TXT='MM/dd/yyyy hh:mm a;MM/dd/yyyy;MM/dd/yyyy HH:mm:ss;MM/dd/yy;MM-dd-yy;MMMM dd;yyyy;MMddyy' WHERE NMSPC_CD='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_TIMESTAMP_FORMATS' AND APPL_NMSPC_CD='KUALI'
;

update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fperson.htm' where doc_typ_nm = 'IdentityManagementPersonDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fgroup.htm' where doc_typ_nm = 'IdentityManagementGroupDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Frole.htm' where doc_typ_nm = 'IdentityManagementRoleDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpermission.htm' where doc_typ_nm = 'IdentityManagementGenericPermissionMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fresponsibility.htm' where doc_typ_nm = 'IdentityManagementReviewResponsibilityMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcampus.htm' where doc_typ_nm = 'CampusMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcountry.htm' where doc_typ_nm = 'CountryMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcounty.htm' where doc_typ_nm = 'CountyMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fpostalcode.htm' where doc_typ_nm = 'PostalCodeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fstate.htm' where doc_typ_nm = 'StateMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Faddresstype.htm' where doc_typ_nm = 'PMAT' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fcampustype.htm' where doc_typ_nm = 'CampusTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fphonetype.htm' where doc_typ_nm = 'PMPT' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fdocumenttype.htm' where doc_typ_nm = 'DocumentTypeDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparameter.htm' where doc_typ_nm = 'ParameterMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparametercomponent.htm' where doc_typ_nm = 'ParameterDetailTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fparametertype.htm' where doc_typ_nm = 'ParameterTypeMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;
update krew_doc_typ_t set help_def_url = 'default.htm?turl=WordDocuments%2Fnamespace.htm' where doc_typ_nm = 'NamespaceMaintenanceDocument' and actv_ind = 1 and cur_ind = 1
;


