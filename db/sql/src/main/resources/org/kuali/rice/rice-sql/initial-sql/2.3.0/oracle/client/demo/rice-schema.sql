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


-----------------------------------------------------------------------------
-- BK_ADDRESS_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_ADDRESS_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_ADDRESS_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_ADDRESS_T
(
      ADDRESS_ID NUMBER(22)
        , AUTHOR_ID NUMBER(22)
        , ADDR_TYP VARCHAR2(40)
        , STREET1 VARCHAR2(50)
        , STREET2 VARCHAR2(50)
        , CITY VARCHAR2(50)
        , PROVIENCE VARCHAR2(50)
        , COUNTRY VARCHAR2(50)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_ADDRESS_T
    ADD CONSTRAINT BK_ADDRESS_TP1
PRIMARY KEY (ADDRESS_ID)
/







-----------------------------------------------------------------------------
-- BK_ADDRESS_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_ADDRESS_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_ADDRESS_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_ADDRESS_TYP_T
(
      ADDR_TYP VARCHAR2(40)
        , DESC_TXT VARCHAR2(255)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_ADDRESS_TYP_T
    ADD CONSTRAINT BK_ADDRESS_TYP_TP1
PRIMARY KEY (ADDR_TYP)
/







-----------------------------------------------------------------------------
-- BK_AUTHOR_ACCOUNT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_AUTHOR_ACCOUNT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_AUTHOR_ACCOUNT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_AUTHOR_ACCOUNT_T
(
      AUTHOR_ID NUMBER(22)
        , ACCOUNT_NUMBER VARCHAR2(50)
        , BANK_NAME VARCHAR2(100)
    

)
/

ALTER TABLE BK_AUTHOR_ACCOUNT_T
    ADD CONSTRAINT BK_AUTHOR_ACCOUNT_TP1
PRIMARY KEY (AUTHOR_ID)
/







-----------------------------------------------------------------------------
-- BK_AUTHOR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_AUTHOR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_AUTHOR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_AUTHOR_T
(
      AUTHOR_ID NUMBER(22)
        , NM VARCHAR2(100)
        , ADDRESS VARCHAR2(200)
        , EMAIL VARCHAR2(50)
        , PHONE_NBR VARCHAR2(20)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_AUTHOR_T
    ADD CONSTRAINT BK_AUTHOR_TP1
PRIMARY KEY (AUTHOR_ID)
/







-----------------------------------------------------------------------------
-- BK_BOOK_AUTHOR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_BOOK_AUTHOR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_BOOK_AUTHOR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_BOOK_AUTHOR_T
(
      BOOK_ID NUMBER(22)
        , AUTHOR_ID NUMBER(22)
    

)
/

ALTER TABLE BK_BOOK_AUTHOR_T
    ADD CONSTRAINT BK_BOOK_AUTHOR_TP1
PRIMARY KEY (BOOK_ID,AUTHOR_ID)
/







-----------------------------------------------------------------------------
-- BK_BOOK_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_BOOK_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_BOOK_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_BOOK_T
(
      BOOK_ID NUMBER(22)
        , TITLE VARCHAR2(100)
        , AUTHOR VARCHAR2(100)
        , TYP_CD VARCHAR2(40)
        , ISBN VARCHAR2(17)
        , PUBLISHER VARCHAR2(100)
        , PUB_DATE DATE
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PRICE NUMBER(15,2)
        , RATING NUMBER(22)
    

)
/

ALTER TABLE BK_BOOK_T
    ADD CONSTRAINT BK_BOOK_TP1
PRIMARY KEY (BOOK_ID)
/







-----------------------------------------------------------------------------
-- BK_BOOK_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_BOOK_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_BOOK_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_BOOK_TYP_T
(
      TYP_CD VARCHAR2(40)
        , NM VARCHAR2(100)
        , DESC_TXT VARCHAR2(255)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_BOOK_TYP_T
    ADD CONSTRAINT BK_BOOK_TYP_TP1
PRIMARY KEY (TYP_CD)
/







-----------------------------------------------------------------------------
-- BK_ORDER_DOC_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_ORDER_DOC_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_ORDER_DOC_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_ORDER_DOC_T
(
      DOC_HDR_ID NUMBER(22)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_ORDER_DOC_T
    ADD CONSTRAINT BK_ORDER_DOC_TP1
PRIMARY KEY (DOC_HDR_ID)
/







-----------------------------------------------------------------------------
-- BK_ORDER_ENTRY_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'BK_ORDER_ENTRY_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE BK_ORDER_ENTRY_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE BK_ORDER_ENTRY_T
(
      BK_ORDER_ENTRY_ID NUMBER(22)
        , DOC_HDR_ID VARCHAR2(14) NOT NULL
        , BOOK_ID NUMBER(22) NOT NULL
        , QUANTITY NUMBER(22) NOT NULL
        , UNIT_PRICE NUMBER(15,2)
        , DISCOUNT NUMBER(5,2)
        , TOTAL_PRICE NUMBER(15,2)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE BK_ORDER_ENTRY_T
    ADD CONSTRAINT BK_ORDER_ENTRY_TP1
PRIMARY KEY (BK_ORDER_ENTRY_ID)
/







-----------------------------------------------------------------------------
-- TRAV_DOC_2_ACCOUNTS
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRAV_DOC_2_ACCOUNTS';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRAV_DOC_2_ACCOUNTS CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRAV_DOC_2_ACCOUNTS
(
      FDOC_NBR VARCHAR2(14)
        , ACCT_NUM VARCHAR2(10)
    

)
/

ALTER TABLE TRAV_DOC_2_ACCOUNTS
    ADD CONSTRAINT TRAV_DOC_2_ACCOUNTSP1
PRIMARY KEY (FDOC_NBR,ACCT_NUM)
/







-----------------------------------------------------------------------------
-- TRVL_AUTH_DOC_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRVL_AUTH_DOC_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRVL_AUTH_DOC_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRVL_AUTH_DOC_T
(
      FDOC_NBR VARCHAR2(14)
        , TRVL_ID VARCHAR2(19)
        , TRAVELER_DTL_ID NUMBER(19)
        , TEM_PROFILE_ID NUMBER(19)
        , TRIP_TYP_CD VARCHAR2(3)
        , TRIP_BGN_DT DATE
        , TRIP_END_DT DATE
        , PRIMARY_DEST_ID NUMBER(19)
        , PRIMARY_DEST_NAME VARCHAR2(100)
        , PRIMARY_DEST_CNTRY_ST VARCHAR2(100)
        , PRIMARY_DEST_CNTY VARCHAR2(100)
        , EXP_LMT NUMBER(19,2) default 0
        , MEAL_WITHOUT_LODGING VARCHAR2(255)
        , TRIP_DESC VARCHAR2(255)
        , DELINQUENT_TR_EXCEPTION VARCHAR2(1)
        , PER_DIEM_ADJ NUMBER(19,2) default 0
        , AR_CUST_ID VARCHAR2(255)
        , AR_INV_DOC_NBR VARCHAR2(255)
        , CELL_PH_NUM VARCHAR2(20)
        , RGN_FAMIL VARCHAR2(255)
        , CTZN_CNTRY_CD VARCHAR2(255)
        , FDOC_NXT_EXP_NBR NUMBER(7)
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
    

)
/

ALTER TABLE TRVL_AUTH_DOC_T
    ADD CONSTRAINT TRVL_AUTH_DOC_TP1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- TRVL_PER_DIEM_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRVL_PER_DIEM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRVL_PER_DIEM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRVL_PER_DIEM_T
(
      ID NUMBER(19)
        , TRIP_TYP_CD VARCHAR2(3) NOT NULL
        , COUNTRY VARCHAR2(100)
        , COUNTRY_NM VARCHAR2(100)
        , COUNTY_CD VARCHAR2(100)
        , PRI_DEST VARCHAR2(100)
        , SSN_BGN_DT DATE
        , EFFECT_FROM_DT DATE
        , EFFECT_TO_DT DATE
        , LOAD_DT DATE
        , SSN_BGN_MONTH_DAY VARCHAR2(5)
        , BKFST NUMBER(19)
        , LUNCH NUMBER(19)
        , DIN NUMBER(19)
        , LODGING NUMBER(19,2) default 0
        , INC NUMBER(19,2) default 0
        , MEALS_INC NUMBER(19,2) default 0
        , ACTV_IND VARCHAR2(1) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
    

)
/

ALTER TABLE TRVL_PER_DIEM_T
    ADD CONSTRAINT TRVL_PER_DIEM_TP1
PRIMARY KEY (ID)
/







-----------------------------------------------------------------------------
-- TRVL_TRIP_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRVL_TRIP_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRVL_TRIP_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRVL_TRIP_TYP_T
(
      CODE VARCHAR2(3)
        , NM VARCHAR2(40) NOT NULL
        , GEN_ENC_IND VARCHAR2(1) NOT NULL
        , ENC_BAL_TYP VARCHAR2(2)
        , ENC_OBJ_CD VARCHAR2(4)
        , CONT_INFO_REQ_IND VARCHAR2(1) NOT NULL
        , BLANKET_IND VARCHAR2(1) NOT NULL
        , AUTO_TR_LIMIT NUMBER(19,2) NOT NULL
        , USE_PER_DIEM VARCHAR2(1) NOT NULL
        , TA_REQUIRED VARCHAR2(1) NOT NULL
        , PER_DIEM_CALC_METHOD VARCHAR2(1) NOT NULL
        , ACTV_IND VARCHAR2(1) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
    

)
/

ALTER TABLE TRVL_TRIP_TYP_T
    ADD CONSTRAINT TRVL_TRIP_TYP_TP1
PRIMARY KEY (CODE)
/







-----------------------------------------------------------------------------
-- TRV_ACCT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ACCT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ACCT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ACCT
(
      ACCT_NUM VARCHAR2(10) NOT NULL
        , ACCT_NAME VARCHAR2(50)
        , ACCT_TYPE VARCHAR2(100)
        , ACCT_FO_ID NUMBER(14)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(8) default 0
        , SUB_ACCT VARCHAR2(10)
        , SUB_ACCT_NAME VARCHAR2(50)
        , CREATE_DT DATE
        , SUBSIDIZED_PCT FLOAT
    

)
/








-----------------------------------------------------------------------------
-- TRV_ACCT_EXT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ACCT_EXT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ACCT_EXT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ACCT_EXT
(
      ACCT_NUM VARCHAR2(10)
        , ACCT_TYPE VARCHAR2(100)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(8) default 0
    

)
/

ALTER TABLE TRV_ACCT_EXT
    ADD CONSTRAINT TRV_ACCT_EXTP1
PRIMARY KEY (ACCT_NUM,ACCT_TYPE)
/







-----------------------------------------------------------------------------
-- TRV_ACCT_FO
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ACCT_FO';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ACCT_FO CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ACCT_FO
(
      ACCT_FO_ID NUMBER(14)
        , ACCT_FO_USER_NAME VARCHAR2(50) NOT NULL
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(8) default 0
    

)
/

ALTER TABLE TRV_ACCT_FO
    ADD CONSTRAINT TRV_ACCT_FOP1
PRIMARY KEY (ACCT_FO_ID)
/







-----------------------------------------------------------------------------
-- TRV_ACCT_TYPE
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ACCT_TYPE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ACCT_TYPE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ACCT_TYPE
(
      ACCT_TYPE VARCHAR2(10)
        , ACCT_TYPE_NAME VARCHAR2(50)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(8) default 0
    

)
/

ALTER TABLE TRV_ACCT_TYPE
    ADD CONSTRAINT TRV_ACCT_TYPEP1
PRIMARY KEY (ACCT_TYPE)
/







-----------------------------------------------------------------------------
-- TRV_ACCT_USE_RT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ACCT_USE_RT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ACCT_USE_RT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ACCT_USE_RT_T
(
      ID VARCHAR2(40)
        , ACCT_NUM VARCHAR2(10)
        , RATE NUMBER(8)
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
    

)
/

ALTER TABLE TRV_ACCT_USE_RT_T
    ADD CONSTRAINT TRV_ACCT_USE_RT_TP1
PRIMARY KEY (ID)
/







-----------------------------------------------------------------------------
-- TRV_ATT_SAMPLE
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_ATT_SAMPLE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_ATT_SAMPLE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_ATT_SAMPLE
(
      ATTACHMENT_ID VARCHAR2(30)
        , DESCRIPTION VARCHAR2(4000)
        , ATTACHMENT_FILENAME VARCHAR2(300)
        , ATTACHMENT_FILE_CONTENT_TYPE VARCHAR2(255)
        , ATTACHMENT_FILE BLOB
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 0 NOT NULL
    

)
/

ALTER TABLE TRV_ATT_SAMPLE
    ADD CONSTRAINT TRV_ATT_SAMPLEP1
PRIMARY KEY (ATTACHMENT_ID)
/







-----------------------------------------------------------------------------
-- TRV_DOC_2
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_DOC_2';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_DOC_2 CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_DOC_2
(
      FDOC_NBR VARCHAR2(14)
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , FDOC_EXPLAIN_TXT VARCHAR2(400)
        , REQUEST_TRAV VARCHAR2(30) NOT NULL
        , TRAVELER VARCHAR2(200)
        , ORG VARCHAR2(60)
        , DEST VARCHAR2(60)
    

)
/

ALTER TABLE TRV_DOC_2
    ADD CONSTRAINT TRV_DOC_2P1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- TRV_DOC_ACCT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_DOC_ACCT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_DOC_ACCT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_DOC_ACCT
(
      DOC_HDR_ID VARCHAR2(40)
        , ACCT_NUM VARCHAR2(10)
    

)
/

ALTER TABLE TRV_DOC_ACCT
    ADD CONSTRAINT TRV_DOC_ACCTP1
PRIMARY KEY (DOC_HDR_ID,ACCT_NUM)
/







-----------------------------------------------------------------------------
-- TRV_MULTI_ATT_SAMPLE
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TRV_MULTI_ATT_SAMPLE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TRV_MULTI_ATT_SAMPLE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TRV_MULTI_ATT_SAMPLE
(
      GEN_ID NUMBER(14)
        , ATTACHMENT_ID VARCHAR2(30)
        , DESCRIPTION VARCHAR2(4000)
        , ATTACHMENT_FILENAME VARCHAR2(300)
        , ATTACHMENT_FILE_CONTENT_TYPE VARCHAR2(255)
        , ATTACHMENT_FILE BLOB
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 0 NOT NULL
    

)
/

ALTER TABLE TRV_MULTI_ATT_SAMPLE
    ADD CONSTRAINT TRV_MULTI_ATT_SAMPLEP1
PRIMARY KEY (GEN_ID)
/







DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'BK_ADDRESS_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE BK_ADDRESS_ID_S'; END IF;
END;
/

CREATE SEQUENCE BK_ADDRESS_ID_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'BK_AUTHOR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE BK_AUTHOR_ID_S'; END IF;
END;
/

CREATE SEQUENCE BK_AUTHOR_ID_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'BK_BOOK_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE BK_BOOK_ID_S'; END IF;
END;
/

CREATE SEQUENCE BK_BOOK_ID_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'BK_ORDER_ENTRY_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE BK_ORDER_ENTRY_S'; END IF;
END;
/

CREATE SEQUENCE BK_ORDER_ENTRY_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'TRVL_ID_SEQ';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE TRVL_ID_SEQ'; END IF;
END;
/

CREATE SEQUENCE TRVL_ID_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'TRVL_PER_DIEM_ID_SEQ';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE TRVL_PER_DIEM_ID_SEQ'; END IF;
END;
/

CREATE SEQUENCE TRVL_PER_DIEM_ID_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'TRV_FO_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE TRV_FO_ID_S'; END IF;
END;
/

CREATE SEQUENCE TRV_FO_ID_S INCREMENT BY 1 START WITH 1000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/