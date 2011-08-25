-- Oracle sql for KULRICE-5419:
alter table krms_cntxt_t add desc_txt varchar2(255) default null
/
alter table krms_term_spec_t add desc_txt varchar2(255) default null
/
alter table krms_term_t add desc_txt varchar2(255) default null
/
alter table krms_attr_defn_t add desc_txt varchar2(255) default null
/