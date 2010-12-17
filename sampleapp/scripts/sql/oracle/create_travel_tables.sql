
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
      ACCT_NUM VARCHAR2(10)
        , ACCT_NAME VARCHAR2(50)
        , ACCT_TYPE VARCHAR2(100)
        , ACCT_FO_ID NUMBER(14)
    

)
/

ALTER TABLE TRV_ACCT
    ADD CONSTRAINT TRV_ACCTP1
PRIMARY KEY (ACCT_NUM)
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
    

)
/

ALTER TABLE TRV_ACCT_TYPE
    ADD CONSTRAINT TRV_ACCT_TYPEP1
PRIMARY KEY (ACCT_TYPE)
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
      DOC_HDR_ID NUMBER(14)
        , ACCT_NUM VARCHAR2(10)
    

)
/

ALTER TABLE TRV_DOC_ACCT
    ADD CONSTRAINT TRV_DOC_ACCTP1
PRIMARY KEY (DOC_HDR_ID,ACCT_NUM)
/


