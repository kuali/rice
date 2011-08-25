alter table KRIM_PERM_TMPL_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_PERM_TMPL_T modify NM varchar2(100) not null
/
alter table KRIM_PERM_TMPL_T add constraint KRIM_PERM_TMPL_TC1 unique (NM, NMSPC_CD)
/

alter table KRIM_RSP_TMPL_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_RSP_TMPL_T modify NM varchar2(100) not null
/
alter table KRIM_RSP_TMPL_T add constraint KRIM_RSP_TMPL_TC1 unique (NM, NMSPC_CD)
/