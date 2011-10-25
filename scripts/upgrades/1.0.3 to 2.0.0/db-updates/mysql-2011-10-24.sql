alter table krcr_cmpnt_t drop column cmpnt_set_id;

create table krcr_drvd_cmpnt_t (
  nmspc_cd varchar(20) not null,
  cmpnt_cd varchar(100) not null,
  nm varchar(255),
  cmpnt_set_id varchar(40) not null,
  primary key (nmspc_cd, cmpnt_cd))
ENGINE = InnoDB;
