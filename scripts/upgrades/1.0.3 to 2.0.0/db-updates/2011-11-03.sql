-- Make typ_id column optional where appropriate

ALTER TABLE krms_rule_t MODIFY typ_id varchar2(40) null;
ALTER TABLE krms_agenda_t MODIFY typ_id varchar2(40) null;
ALTER TABLE krms_cntxt_t MODIFY typ_id varchar2(40) null;
