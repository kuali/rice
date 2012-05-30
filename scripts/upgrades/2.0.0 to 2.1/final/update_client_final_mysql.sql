


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-19.sql
-- 


CREATE TABLE KRNS_MAINT_DOC_ATT_LST_T  (
    ATT_ID      varchar(40) NOT NULL,
	DOC_HDR_ID	varchar(14) NOT NULL,
	ATT_CNTNT 	longblob NOT NULL,
	FILE_NM   	varchar(150) NULL,
	CNTNT_TYP 	varchar(255) NULL,
	OBJ_ID    	varchar(36) NOT NULL,
	VER_NBR   	decimal(8,0) NOT NULL DEFAULT 0,
	PRIMARY KEY(ATT_ID),
	CONSTRAINT KRNS_MAINT_DOC_ATT_LST_FK1 foreign key (DOC_HDR_ID) references KRNS_MAINT_DOC_T (DOC_HDR_ID)
);

ALTER TABLE KRNS_MAINT_DOC_ATT_LST_T
	ADD CONSTRAINT KRNS_MAINT_DOC_ATT_LST_TC0
	UNIQUE (OBJ_ID);

create index KRNS_MAINT_DOC_ATT__LST_TI1 on KRNS_MAINT_DOC_ATT_LST_T (DOC_HDR_ID);

create table KRNS_MAINT_DOC_ATT_S (
  id bigint(19) not null auto_increment,
  primary key (id)
) ENGINE MyISAM;
alter table KRNS_MAINT_DOC_ATT_S auto_increment = 10000;










-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-17.sql
-- 



-- KULRICE-7237: KRNS_NTE_T is selected by a field with no indexes - full table scan every time
create index KRNS_NTE_TI1 on KRNS_NTE_T (RMT_OBJ_ID);