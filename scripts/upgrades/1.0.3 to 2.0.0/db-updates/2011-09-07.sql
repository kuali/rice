-- KULRICE-5360 rename KIM entity fields
alter table KRIM_ENTITY_NM_T rename column TITLE_NM to PREFIX_NM
/

alter table KRIM_ENTITY_BIO_T rename column BIRTH_STATE_CD to BIRTH_STATE_PVC_CD
/

alter table KRIM_ENTITY_ADDR_T rename column POSTAL_STATE_CD to STATE_PVC_CD
/
alter table KRIM_ENTITY_ADDR_T rename column CITY_NM to CITY
/

alter table KRIM_PND_NM_MT rename column TITLE_NM to PREFIX_NM
/
alter table KRIM_PND_ADDR_MT rename column POSTAL_STATE_CD to STATE_PVC_CD
/
alter table KRIM_PND_ADDR_MT rename column CITY_NM to CITY
/

