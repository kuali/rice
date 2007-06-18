create table en_edoclt_def_t (
  edoclt_def_id number(19) not null,
  edoclt_def_nm varchar(200) not null,
  edoclt_def_xml clob not null,
  edoclt_def_actv_ind number(1) not null,
  db_lock_ver_nbr number(8) default 0,
  CONSTRAINT en_edoclt_def_t PRIMARY KEY (edoclt_def_id)
)
/