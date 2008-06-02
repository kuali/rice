------------------ KEW UPGRADE -----------------------
-- A few additions for JPA inheritance --
alter table EN_USR_T add (DTYPE VARCHAR2(50))
/
alter table EN_DOC_HDR_T add (DTYPE VARCHAR2(50))
/
alter table EN_ACTN_ITM_T add (DTYPE VARCHAR2(50))
/
CREATE INDEX EN_ACTN_ITM_TI5 ON EN_ACTN_ITM_T (ACTN_ITM_PRSN_EN_ID, DLGN_TYP, DOC_HDR_ID)
/
