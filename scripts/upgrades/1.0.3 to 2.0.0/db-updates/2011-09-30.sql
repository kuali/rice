

-- -----------------------------------------------------
-- Table krms_cntxt_vld_agenda_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_agenda_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_agenda_t (
  cntxt_vld_agenda_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  agenda_typ_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_agenda_id) ,
  -- CREATE INDEX krms_cntxt_vld_agenda_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_agenda_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_agenda_ti1 on krms_cntxt_vld_agenda_t (cntxt_id ASC)
/

CREATE SEQUENCE krms_cntxt_vld_agenda_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/