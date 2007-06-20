create table en_edoclt_style_t (
  edoclt_style_id number(19) not null,
  edoclt_style_nm varchar(200) not null,
  edoclt_style_xml clob not null,
  edoclt_style_actv_ind number(1) not null,
  db_lock_ver_nbr number(8) default 0,
  CONSTRAINT en_edoclt_style_t PRIMARY KEY (edoclt_style_id)
)
/