create table en_edoclt_assoc_t (
  edoclt_assoc_id number(19) not null,
  edoclt_assoc_doctype_nm varchar(200) not null,
  edoclt_assoc_def_nm varchar(200),   
  edoclt_assoc_style_nm varchar(200),
  edoclt_assoc_actv_ind number(1) not null,
  db_lock_ver_nbr number(8) default 0,
  CONSTRAINT en_edoclt_assoc_t PRIMARY KEY (edoclt_assoc_id)
)
/