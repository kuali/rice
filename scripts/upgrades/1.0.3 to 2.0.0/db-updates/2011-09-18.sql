alter table KREW_PPL_FLW_T modify(TYP_ID NULL)
/
alter table KREW_PPL_FLW_MBR_T DROP COLUMN dlgt_frm_id
/

-- -----------------------------------------------------
-- Table krew_ppl_flw_dlgt_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_dlgt_t (
  ppl_flw_dlgt_id VARCHAR2(40) NOT NULL ,
  ppl_flw_mbr_id VARCHAR2(40) NOT NULL ,
  mbr_id VARCHAR2(40) NOT NULL ,
  mbr_typ_cd VARCHAR2(1) NOT NULL ,
  dlgn_typ_cd VARCHAR2(1) NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (ppl_flw_dlgt_id) ,
  CONSTRAINT krew_ppl_flw_dlgt_fk1
    FOREIGN KEY (ppl_flw_mbr_id )
    REFERENCES krew_ppl_flw_mbr_t (ppl_flw_mbr_id ))
/

CREATE INDEX krew_ppl_flw_dlgt_ti1 ON krew_ppl_flw_dlgt_t (ppl_flw_mbr_id)
/

CREATE SEQUENCE krew_ppl_flw_dlgt_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/