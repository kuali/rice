--
-- Copyright 2005-2014 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--


# -----------------------------------------------------------------------
# BK_ADDRESS_T
# -----------------------------------------------------------------------
drop table if exists BK_ADDRESS_T
/

CREATE TABLE BK_ADDRESS_T
(
      ADDRESS_ID DECIMAL(22)
        , AUTHOR_ID DECIMAL(22)
        , ADDR_TYP VARCHAR(40)
        , STREET1 VARCHAR(50)
        , STREET2 VARCHAR(50)
        , CITY VARCHAR(50)
        , PROVIENCE VARCHAR(50)
        , COUNTRY VARCHAR(50)
        , ACTV_IND VARCHAR(1) default 'Y'
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_ADDRESS_TP1 PRIMARY KEY(ADDRESS_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_ADDRESS_TYP_T
# -----------------------------------------------------------------------
drop table if exists BK_ADDRESS_TYP_T
/

CREATE TABLE BK_ADDRESS_TYP_T
(
      ADDR_TYP VARCHAR(40)
        , DESC_TXT VARCHAR(255)
        , ACTV_IND VARCHAR(1) default 'Y'
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_ADDRESS_TYP_TP1 PRIMARY KEY(ADDR_TYP)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_AUTHOR_ACCOUNT_T
# -----------------------------------------------------------------------
drop table if exists BK_AUTHOR_ACCOUNT_T
/

CREATE TABLE BK_AUTHOR_ACCOUNT_T
(
      AUTHOR_ID DECIMAL(22)
        , ACCOUNT_NUMBER VARCHAR(50)
        , BANK_NAME VARCHAR(100)
    
    , CONSTRAINT BK_AUTHOR_ACCOUNT_TP1 PRIMARY KEY(AUTHOR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_AUTHOR_T
# -----------------------------------------------------------------------
drop table if exists BK_AUTHOR_T
/

CREATE TABLE BK_AUTHOR_T
(
      AUTHOR_ID DECIMAL(22)
        , NM VARCHAR(100)
        , ADDRESS VARCHAR(200)
        , EMAIL VARCHAR(50)
        , PHONE_NBR VARCHAR(20)
        , ACTV_IND VARCHAR(1) default 'Y'
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_AUTHOR_TP1 PRIMARY KEY(AUTHOR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_BOOK_AUTHOR_T
# -----------------------------------------------------------------------
drop table if exists BK_BOOK_AUTHOR_T
/

CREATE TABLE BK_BOOK_AUTHOR_T
(
      BOOK_ID DECIMAL(22)
        , AUTHOR_ID DECIMAL(22)
    
    , CONSTRAINT BK_BOOK_AUTHOR_TP1 PRIMARY KEY(BOOK_ID,AUTHOR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_BOOK_T
# -----------------------------------------------------------------------
drop table if exists BK_BOOK_T
/

CREATE TABLE BK_BOOK_T
(
      BOOK_ID DECIMAL(22)
        , TITLE VARCHAR(100)
        , AUTHOR VARCHAR(100)
        , TYP_CD VARCHAR(40)
        , ISBN VARCHAR(17)
        , PUBLISHER VARCHAR(100)
        , PUB_DATE DATETIME
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , PRICE DECIMAL(15,2)
        , RATING DECIMAL(22)
    
    , CONSTRAINT BK_BOOK_TP1 PRIMARY KEY(BOOK_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_BOOK_TYP_T
# -----------------------------------------------------------------------
drop table if exists BK_BOOK_TYP_T
/

CREATE TABLE BK_BOOK_TYP_T
(
      TYP_CD VARCHAR(40)
        , NM VARCHAR(100)
        , DESC_TXT VARCHAR(255)
        , ACTV_IND VARCHAR(1) default 'Y'
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_BOOK_TYP_TP1 PRIMARY KEY(TYP_CD)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_ORDER_DOC_T
# -----------------------------------------------------------------------
drop table if exists BK_ORDER_DOC_T
/

CREATE TABLE BK_ORDER_DOC_T
(
      DOC_HDR_ID DECIMAL(22)
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_ORDER_DOC_TP1 PRIMARY KEY(DOC_HDR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_ORDER_ENTRY_T
# -----------------------------------------------------------------------
drop table if exists BK_ORDER_ENTRY_T
/

CREATE TABLE BK_ORDER_ENTRY_T
(
      BK_ORDER_ENTRY_ID DECIMAL(22)
        , DOC_HDR_ID VARCHAR(14) NOT NULL
        , BOOK_ID DECIMAL(22) NOT NULL
        , QUANTITY DECIMAL(22) NOT NULL
        , UNIT_PRICE DECIMAL(15,2)
        , DISCOUNT DECIMAL(5,2)
        , TOTAL_PRICE DECIMAL(15,2)
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT BK_ORDER_ENTRY_TP1 PRIMARY KEY(BK_ORDER_ENTRY_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRAV_DOC_2_ACCOUNTS
# -----------------------------------------------------------------------
drop table if exists TRAV_DOC_2_ACCOUNTS
/

CREATE TABLE TRAV_DOC_2_ACCOUNTS
(
      FDOC_NBR VARCHAR(14)
        , ACCT_NUM VARCHAR(10)
    
    , CONSTRAINT TRAV_DOC_2_ACCOUNTSP1 PRIMARY KEY(FDOC_NBR,ACCT_NUM)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRVL_AUTH_DOC_T
# -----------------------------------------------------------------------
drop table if exists TRVL_AUTH_DOC_T
/

CREATE TABLE TRVL_AUTH_DOC_T
(
      FDOC_NBR VARCHAR(14)
        , TRVL_ID VARCHAR(19)
        , TRAVELER_DTL_ID DECIMAL(19)
        , TEM_PROFILE_ID DECIMAL(19)
        , TRIP_TYP_CD VARCHAR(3)
        , TRIP_BGN_DT DATETIME
        , TRIP_END_DT DATETIME
        , PRIMARY_DEST_ID DECIMAL(19)
        , PRIMARY_DEST_NAME VARCHAR(100)
        , PRIMARY_DEST_CNTRY_ST VARCHAR(100)
        , PRIMARY_DEST_CNTY VARCHAR(100)
        , EXP_LMT DECIMAL(19,2) default 0
        , MEAL_WITHOUT_LODGING VARCHAR(255)
        , TRIP_DESC VARCHAR(255)
        , DELINQUENT_TR_EXCEPTION VARCHAR(1)
        , PER_DIEM_ADJ DECIMAL(19,2) default 0
        , AR_CUST_ID VARCHAR(255)
        , AR_INV_DOC_NBR VARCHAR(255)
        , CELL_PH_NUM VARCHAR(20)
        , RGN_FAMIL VARCHAR(255)
        , CTZN_CNTRY_CD VARCHAR(255)
        , FDOC_NXT_EXP_NBR DECIMAL(7)
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , OBJ_ID VARCHAR(36) NOT NULL
    
    , CONSTRAINT TRVL_AUTH_DOC_TP1 PRIMARY KEY(FDOC_NBR)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRVL_PER_DIEM_T
# -----------------------------------------------------------------------
drop table if exists TRVL_PER_DIEM_T
/

CREATE TABLE TRVL_PER_DIEM_T
(
      ID DECIMAL(19)
        , TRIP_TYP_CD VARCHAR(3) NOT NULL
        , COUNTRY VARCHAR(100)
        , COUNTRY_NM VARCHAR(100)
        , COUNTY_CD VARCHAR(100)
        , PRI_DEST VARCHAR(100)
        , SSN_BGN_DT DATETIME
        , EFFECT_FROM_DT DATETIME
        , EFFECT_TO_DT DATETIME
        , LOAD_DT DATETIME
        , SSN_BGN_MONTH_DAY VARCHAR(5)
        , BKFST DECIMAL(19)
        , LUNCH DECIMAL(19)
        , DIN DECIMAL(19)
        , LODGING DECIMAL(19,2) default 0
        , INC DECIMAL(19,2) default 0
        , MEALS_INC DECIMAL(19,2) default 0
        , ACTV_IND VARCHAR(1) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , OBJ_ID VARCHAR(36) NOT NULL
    
    , CONSTRAINT TRVL_PER_DIEM_TP1 PRIMARY KEY(ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRVL_TRIP_TYP_T
# -----------------------------------------------------------------------
drop table if exists TRVL_TRIP_TYP_T
/

CREATE TABLE TRVL_TRIP_TYP_T
(
      CODE VARCHAR(3)
        , NM VARCHAR(40) NOT NULL
        , GEN_ENC_IND VARCHAR(1) NOT NULL
        , ENC_BAL_TYP VARCHAR(2)
        , ENC_OBJ_CD VARCHAR(4)
        , CONT_INFO_REQ_IND VARCHAR(1) NOT NULL
        , BLANKET_IND VARCHAR(1) NOT NULL
        , AUTO_TR_LIMIT DECIMAL(19,2) NOT NULL
        , USE_PER_DIEM VARCHAR(1) NOT NULL
        , TA_REQUIRED VARCHAR(1) NOT NULL
        , PER_DIEM_CALC_METHOD VARCHAR(1) NOT NULL
        , ACTV_IND VARCHAR(1) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , OBJ_ID VARCHAR(36) NOT NULL
    
    , CONSTRAINT TRVL_TRIP_TYP_TP1 PRIMARY KEY(CODE)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ACCT
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT
/

CREATE TABLE TRV_ACCT
(
      ACCT_NUM VARCHAR(10) NOT NULL
        , ACCT_NAME VARCHAR(50)
        , ACCT_TYPE VARCHAR(100)
        , ACCT_FO_ID DECIMAL(14)
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(8) default 0
        , SUB_ACCT VARCHAR(10)
        , SUB_ACCT_NAME VARCHAR(50)
        , CREATE_DT DATETIME
        , SUBSIDIZED_PCT FLOAT
    






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
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(8) default 0
    
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
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(8) default 0
    
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
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(8) default 0
    
    , CONSTRAINT TRV_ACCT_TYPEP1 PRIMARY KEY(ACCT_TYPE)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ACCT_USE_RT_T
# -----------------------------------------------------------------------
drop table if exists TRV_ACCT_USE_RT_T
/

CREATE TABLE TRV_ACCT_USE_RT_T
(
      ID VARCHAR(40)
        , ACCT_NUM VARCHAR(10)
        , RATE DECIMAL(8)
        , ACTV_FRM_DT DATETIME
        , ACTV_TO_DT DATETIME
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    
    , CONSTRAINT TRV_ACCT_USE_RT_TP1 PRIMARY KEY(ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_ATT_SAMPLE
# -----------------------------------------------------------------------
drop table if exists TRV_ATT_SAMPLE
/

CREATE TABLE TRV_ATT_SAMPLE
(
      ATTACHMENT_ID VARCHAR(30)
        , DESCRIPTION VARCHAR(4000)
        , ATTACHMENT_FILENAME VARCHAR(300)
        , ATTACHMENT_FILE_CONTENT_TYPE VARCHAR(255)
        , ATTACHMENT_FILE LONGBLOB
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 0 NOT NULL
    
    , CONSTRAINT TRV_ATT_SAMPLEP1 PRIMARY KEY(ATTACHMENT_ID)






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
      DOC_HDR_ID VARCHAR(40)
        , ACCT_NUM VARCHAR(10)
    
    , CONSTRAINT TRV_DOC_ACCTP1 PRIMARY KEY(DOC_HDR_ID,ACCT_NUM)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TRV_MULTI_ATT_SAMPLE
# -----------------------------------------------------------------------
drop table if exists TRV_MULTI_ATT_SAMPLE
/

CREATE TABLE TRV_MULTI_ATT_SAMPLE
(
      GEN_ID DECIMAL(14)
        , ATTACHMENT_ID VARCHAR(30)
        , DESCRIPTION VARCHAR(4000)
        , ATTACHMENT_FILENAME VARCHAR(300)
        , ATTACHMENT_FILE_CONTENT_TYPE VARCHAR(255)
        , ATTACHMENT_FILE LONGBLOB
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 0 NOT NULL
    
    , CONSTRAINT TRV_MULTI_ATT_SAMPLEP1 PRIMARY KEY(GEN_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# BK_ADDRESS_ID_S
# -----------------------------------------------------------------------
drop table if exists BK_ADDRESS_ID_S
/

CREATE TABLE BK_ADDRESS_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE BK_ADDRESS_ID_S auto_increment = 1
/

# -----------------------------------------------------------------------
# BK_AUTHOR_ID_S
# -----------------------------------------------------------------------
drop table if exists BK_AUTHOR_ID_S
/

CREATE TABLE BK_AUTHOR_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE BK_AUTHOR_ID_S auto_increment = 1
/

# -----------------------------------------------------------------------
# BK_BOOK_ID_S
# -----------------------------------------------------------------------
drop table if exists BK_BOOK_ID_S
/

CREATE TABLE BK_BOOK_ID_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE BK_BOOK_ID_S auto_increment = 1
/

# -----------------------------------------------------------------------
# BK_ORDER_ENTRY_S
# -----------------------------------------------------------------------
drop table if exists BK_ORDER_ENTRY_S
/

CREATE TABLE BK_ORDER_ENTRY_S
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE BK_ORDER_ENTRY_S auto_increment = 1
/

# -----------------------------------------------------------------------
# TRVL_ID_SEQ
# -----------------------------------------------------------------------
drop table if exists TRVL_ID_SEQ
/

CREATE TABLE TRVL_ID_SEQ
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE TRVL_ID_SEQ auto_increment = 1
/

# -----------------------------------------------------------------------
# TRVL_PER_DIEM_ID_SEQ
# -----------------------------------------------------------------------
drop table if exists TRVL_PER_DIEM_ID_SEQ
/

CREATE TABLE TRVL_PER_DIEM_ID_SEQ
(
	id bigint(19) not null auto_increment, primary key (id) 
) ENGINE MyISAM
/
ALTER TABLE TRVL_PER_DIEM_ID_SEQ auto_increment = 1
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