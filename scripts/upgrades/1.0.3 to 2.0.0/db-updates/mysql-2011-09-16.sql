-- KULRICE-5360 add KIM entity fields
alter table KRIM_ENTITY_NM_T add column TITLE_NM VARCHAR(20);
alter table KRIM_ENTITY_NM_T add column NOTE_MSG VARCHAR(1024);
alter table KRIM_ENTITY_NM_T add column NM_CHNG_DT DATETIME;

alter table KRIM_ENTITY_ADDR_T add column ATTN_LINE VARCHAR(45);
alter table KRIM_ENTITY_ADDR_T add column ADDR_FMT VARCHAR(256);
alter table KRIM_ENTITY_ADDR_T add column MOD_DT DATETIME;
alter table KRIM_ENTITY_ADDR_T add column VALID_DT DATETIME;
alter table KRIM_ENTITY_ADDR_T add column VALID_IND VARCHAR(1);
alter table KRIM_ENTITY_ADDR_T add column NOTE_MSG VARCHAR(1024);

alter table KRIM_ENTITY_BIO_T add column NOTE_MSG VARCHAR(1024);
alter table KRIM_ENTITY_BIO_T add column GNDR_CHG_CD VARCHAR(20);

alter table KRIM_PND_NM_MT add column TITLE_NM VARCHAR(20);
alter table KRIM_PND_NM_MT add column NOTE_MSG VARCHAR(1024);
alter table KRIM_PND_NM_MT add column NM_CHNG_DT DATETIME;

alter table KRIM_PND_ADDR_MT add column ATTN_LINE VARCHAR(45);
alter table KRIM_PND_ADDR_MT add column ADDR_FMT VARCHAR(256);
alter table KRIM_PND_ADDR_MT add column MOD_DT DATETIME;
alter table KRIM_PND_ADDR_MT add column VALID_DT DATETIME;
alter table KRIM_PND_ADDR_MT add column VALID_IND VARCHAR(1);
alter table KRIM_PND_ADDR_MT add column NOTE_MSG VARCHAR(1024);

