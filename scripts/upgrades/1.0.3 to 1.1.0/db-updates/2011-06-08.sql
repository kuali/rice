-- make krms_rule_t.prop_id nullable
alter table krms_rule_t modify prop_id varchar2(40) DEFAULT NULL;

-- add krms_actn_t.nmspc_cd
alter table krms_actn_t add nmspc_cd varchar2(40) not null;

-- make krms_agenda_t default to 'Y'
alter table krms_agenda_t modify actv varchar2(1) DEFAULT 'Y';

-- make krms_prop_t.typ_id nullable 
alter table krms_prop_t modify typ_id varchar2(40) DEFAULT NULL;

-- change krms_rule_t.descr_txt to desc_t for consistency
alter table krms_rule_t rename column descr_txt to desc_txt;

