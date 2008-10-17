alter table "EN_DOC_HDR_T" drop column "DOC_OVRD_IND"
/
alter table "EN_DOC_HDR_T" drop column "DOC_LOCK_CD"
/
alter table "EN_RTE_NODE_T" drop column "CONTENT_FRAGMENT"
/
alter table "EN_DOC_HDR_T" drop column "DTYPE"
/
alter table "EN_ACTN_ITM_T" drop column "DTYPE"
/
alter table "EN_USR_T" drop column "DTYPE"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_LIST_ATTRIB_CLS_NM"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_EMAIL_ATTRIB_CLS_NM"
/
alter table "KRICE"."EN_DOC_TYP_T" drop column "CSTM_DOC_NTE_ATTRIB_CLS_NM" 
/