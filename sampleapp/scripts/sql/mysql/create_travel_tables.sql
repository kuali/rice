# -----------------------------------------------------------------------
# TRV_ACCT
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT
/

CREATE TABLE TRV_ACCT
(
      ACCT_NUM VARCHAR(10)
        , ACCT_NAME VARCHAR(50)
        , ACCT_TYPE VARCHAR(100)
        , ACCT_FO_ID DECIMAL(14)
    
    , CONSTRAINT TRV_ACCTP1 PRIMARY KEY(ACCT_NUM)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ACCT_EXT
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT_EXT
/

CREATE TABLE TRV_ACCT_EXT
(
      ACCT_NUM VARCHAR(10)
        , ACCT_TYPE VARCHAR(100)
    
    , CONSTRAINT TRV_ACCT_EXTP1 PRIMARY KEY(ACCT_NUM,ACCT_TYPE)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ACCT_FO
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT_FO
/

CREATE TABLE TRV_ACCT_FO
(
      ACCT_FO_ID DECIMAL(14)
        , ACCT_FO_USER_NAME VARCHAR(50) NOT NULL
    
    , CONSTRAINT TRV_ACCT_FOP1 PRIMARY KEY(ACCT_FO_ID)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ACCT_TYPE
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT_TYPE
/

CREATE TABLE TRV_ACCT_TYPE
(
      ACCT_TYPE VARCHAR(10)
        , ACCT_TYPE_NAME VARCHAR(50)
    
    , CONSTRAINT TRV_ACCT_TYPEP1 PRIMARY KEY(ACCT_TYPE)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_DOC_2
# -----------------------------------------------------------------------
drop table if exists TRV_DOC_2
/

CREATE TABLE TRV_DOC_2
(
      FDOC_NBR VARCHAR(14)
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , FDOC_EXPLAIN_TXT VARCHAR(400)
        , REQUEST_TRAV VARCHAR(30) NOT NULL
        , TRAVELER VARCHAR(200)
        , ORG VARCHAR(60)
        , DEST VARCHAR(60)
    
    , CONSTRAINT TRV_DOC_2P1 PRIMARY KEY(FDOC_NBR)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_DOC_ACCT
# -----------------------------------------------------------------------
drop table if exists TRV_DOC_ACCT
/

CREATE TABLE TRV_DOC_ACCT
(
      DOC_HDR_ID DECIMAL(14)
        , ACCT_NUM VARCHAR(10)
    
    , CONSTRAINT TRV_DOC_ACCTP1 PRIMARY KEY(DOC_HDR_ID,ACCT_NUM)



) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/
# -----------------------------------------------------------------------
# TRV_FO_ID_S
# -----------------------------------------------------------------------
drop table if exists TRV_FO_ID_S
/

CREATE TABLE TRV_FO_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE TRV_FO_ID_S auto_increment = 1000
/
ALTER TABLE TRV_ACCT
    ADD CONSTRAINT TRV_ACCT_FK1
    FOREIGN KEY (ACCT_FO_ID)
    REFERENCES TRV_ACCT_FO (ACCT_FO_ID)
/