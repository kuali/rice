------------------ KEW UPGRADE -----------------------
-- A few additions for JPA inheritance --
alter table EN_USR_T add (DTYPE VARCHAR2(50))
/
alter table EN_DOC_HDR_T add (DTYPE VARCHAR2(50))
/
alter table EN_ACTN_ITM_T add (DTYPE VARCHAR2(50))
/