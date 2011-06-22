alter table KRIM_PERM_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_PERM_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_PERM_TMPL_T add constraint KRIM_PERM_TMPL_TC1 unique (NM, NMSPC_CD);

alter table KRIM_RSP_TMPL_T change column NMSPC_CD NMSPC_CD varchar(40) not null;
alter table KRIM_RSP_TMPL_T change column NM NM varchar(100) not null;
alter table KRIM_RSP_TMPL_T add constraint KRIM_RSP_TMPL_TC1 unique (NM, NMSPC_CD);