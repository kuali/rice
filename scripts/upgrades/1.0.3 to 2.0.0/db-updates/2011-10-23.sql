alter table krcr_cmpnt_t add cmpnt_set_id varchar2(40)
/
create table krcr_cmpnt_set_t (
  cmpnt_set_id varchar2(40) not null,
  last_updt_ts date not null,
  chksm varchar2(40) not null,
  ver_nbr number(8) default 0 not null,
  primary key (cmpnt_set_id) )
/
