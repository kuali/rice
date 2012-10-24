
-- add category name (which is part of composite fk) to app doc stat
alter table KREW_DOC_TYP_APP_DOC_STAT_T add column CAT_NM varchar(64)
;

-- add index for queries from category to status.  Using non-standard index name to follow table precedent.
CREATE INDEX KREW_DOC_TYP_APP_DOC_STAT_T2 on KREW_DOC_TYP_APP_DOC_STAT_T (DOC_TYP_ID, CAT_NM)
;

-- add sequence number column for ordering to app doc stat
alter table KREW_DOC_TYP_APP_DOC_STAT_T add column SEQ_NO DECIMAL(5,0)
;

-- create category table
CREATE TABLE KREW_DOC_TYP_APP_STAT_CAT_T  (
    DOC_TYP_ID  varchar(40) NOT NULL,
	CAT_NM      varchar(64) NOT NULL,
    VER_NBR     decimal(8,0) DEFAULT '0',
    OBJ_ID      varchar(36) NOT NULL,

	PRIMARY KEY(DOC_TYP_ID, CAT_NM),
	CONSTRAINT KREW_DOC_TYP_APP_STAT_CAT_FK1 foreign key (DOC_TYP_ID) references KREW_DOC_TYP_T (DOC_TYP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;

-- object id must be unique
ALTER TABLE KREW_DOC_TYP_APP_STAT_CAT_T
	ADD CONSTRAINT KREW_DOC_TYP_APP_STAT_CAT_TC1
	UNIQUE (OBJ_ID)
;

-- add constraint to tie app doc stat and category together
alter table KREW_DOC_TYP_APP_DOC_STAT_T add constraint KREW_DOC_TYP_APP_DOC_STAT_FK1
foreign key KREW_DOC_TYP_APP_DOC_STAT_TI2 (DOC_TYP_ID, CAT_NM) references KREW_DOC_TYP_APP_STAT_CAT_T (DOC_TYP_ID, CAT_NM)
;
