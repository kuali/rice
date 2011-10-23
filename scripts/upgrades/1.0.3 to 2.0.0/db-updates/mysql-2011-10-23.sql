alter table krcr_cmpnt_t add column cmpnt_set_id varchar(40);

create table krcr_cmpnt_set_t (
  cmpnt_set_id varchar(40) not null,
  last_updt_ts datetime not null,
  chksm varchar(40) not null,
  ver_nbr decimal not null default 0,
  primary key (cmpnt_set_id))
ENGINE = InnoDB;