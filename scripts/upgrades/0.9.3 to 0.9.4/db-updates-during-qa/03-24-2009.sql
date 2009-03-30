alter table krns_att_t modify att_typ_cd VARCHAR2(40)
/

UPDATE KRIM_ATTR_DEFN_T set APPL_URL='${application.url}'
/