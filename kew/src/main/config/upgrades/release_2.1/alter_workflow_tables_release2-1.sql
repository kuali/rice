--Alter table statements--;
ALTER TABLE EN_ACTN_RQST_T ADD ACTN_RQST_RTE_NODE_INSTN_ID NUMBER(19);

alter table en_rule_attrib_t ADD RULE_ATTRIB_XML_RTE_TXT LONG NULL;

alter table EN_ACTN_RQST_T modify actn_rqst_rte_typ_nm varchar2(255) null;

ALTER TABLE EN_DOC_TYP_T ADD DOC_TYP_RTE_VER_NBR VARCHAR(2) DEFAULT '1' NOT NULL;

alter table EN_DOC_HDR_EXT_T modify DOC_HDR_EXT_VAL_KEY VARCHAR2(32);
alter table EN_DOC_HDR_EXT_T modify DOC_HDR_EXT_VAL VARCHAR2(256);

insert into EN_DOC_TYP_PLCY_RELN_T select distinct doc_typ_id, 'LOOK_FUTURE', 0, 0 from EN_DOC_TYP_PLCY_RELN_T;