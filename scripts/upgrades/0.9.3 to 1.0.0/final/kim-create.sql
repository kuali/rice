--
-- Copyright 2005-2011 The Kuali Foundation
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
-- KRIM_ADDR_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ADDR_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ADDR_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ADDR_TYP_T
(
      ADDR_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ADDR_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_ADDR_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_ADDR_TYP_T
    ADD CONSTRAINT KRIM_ADDR_TYP_TP1
PRIMARY KEY (ADDR_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_AFLTN_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_AFLTN_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_AFLTN_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_AFLTN_TYP_T
(
      AFLTN_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , EMP_AFLTN_TYP_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_AFLTN_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_AFLTN_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_AFLTN_TYP_T
    ADD CONSTRAINT KRIM_AFLTN_TYP_TP1
PRIMARY KEY (AFLTN_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_ATTR_DEFN_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ATTR_DEFN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ATTR_DEFN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ATTR_DEFN_T
(
      KIM_ATTR_DEFN_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(100)
        , LBL VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , NMSPC_CD VARCHAR2(40)
        , CMPNT_NM VARCHAR2(100)
        , APPL_URL VARCHAR2(2000)

    , CONSTRAINT KR_KIM_ATTRIBUTE_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ATTR_DEFN_T
    ADD CONSTRAINT KRIM_ATTR_DEFN_TP1
PRIMARY KEY (KIM_ATTR_DEFN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_CTZNSHP_STAT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_CTZNSHP_STAT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_CTZNSHP_STAT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_CTZNSHP_STAT_T
(
      CTZNSHP_STAT_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_CTZNSHP_STAT_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_CTZNSHP_STAT_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_CTZNSHP_STAT_T
    ADD CONSTRAINT KRIM_CTZNSHP_STAT_TP1
PRIMARY KEY (CTZNSHP_STAT_CD)
/







-----------------------------------------------------------------------------
-- KRIM_DLGN_MBR_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_DLGN_MBR_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_DLGN_MBR_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_DLGN_MBR_ATTR_DATA_T
(
      ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , DLGN_MBR_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)

    , CONSTRAINT KR_KIM_DELE_MBR_ATTR_DATA_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_DLGN_MBR_ATTR_DATA_T
    ADD CONSTRAINT KRIM_DLGN_MBR_ATTR_DATA_TP1
PRIMARY KEY (ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_DLGN_MBR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_DLGN_MBR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_DLGN_MBR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_DLGN_MBR_T
(
      DLGN_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , DLGN_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) default 'P'
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , LAST_UPDT_DT DATE default SYSDATE
        , ROLE_MBR_ID VARCHAR2(40)

    , CONSTRAINT KRIM_DLGN_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TP1
PRIMARY KEY (DLGN_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_DLGN_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_DLGN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_DLGN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_DLGN_T
(
      DLGN_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , ROLE_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , DLGN_TYP_CD VARCHAR2(1)

    , CONSTRAINT KR_KIM_DELE_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_DLGN_T
    ADD CONSTRAINT KRIM_DLGN_TP1
PRIMARY KEY (DLGN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_EMAIL_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_EMAIL_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_EMAIL_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_EMAIL_TYP_T
(
      EMAIL_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_EMAIL_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_EMAIL_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_EMAIL_TYP_T
    ADD CONSTRAINT KRIM_EMAIL_TYP_TP1
PRIMARY KEY (EMAIL_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_EMP_STAT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_EMP_STAT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_EMP_STAT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_EMP_STAT_T
(
      EMP_STAT_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_EMP_STAT_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_EMP_STAT_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_EMP_STAT_T
    ADD CONSTRAINT KRIM_EMP_STAT_TP1
PRIMARY KEY (EMP_STAT_CD)
/







-----------------------------------------------------------------------------
-- KRIM_EMP_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_EMP_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_EMP_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_EMP_TYP_T
(
      EMP_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_EMP_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_EMP_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_EMP_TYP_T
    ADD CONSTRAINT KRIM_EMP_TYP_TP1
PRIMARY KEY (EMP_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_ADDR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_ADDR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_ADDR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_ADDR_T
(
      ENTITY_ADDR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , ENT_TYP_CD VARCHAR2(40)
        , ADDR_TYP_CD VARCHAR2(40)
        , ADDR_LINE_1 VARCHAR2(45)
        , ADDR_LINE_2 VARCHAR2(45)
        , ADDR_LINE_3 VARCHAR2(45)
        , CITY_NM VARCHAR2(30)
        , POSTAL_STATE_CD VARCHAR2(2)
        , POSTAL_CD VARCHAR2(20)
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_ADDR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_ADDR_T
    ADD CONSTRAINT KRIM_ENTITY_ADDR_TP1
PRIMARY KEY (ENTITY_ADDR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_AFLTN_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_AFLTN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_AFLTN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_AFLTN_T
(
      ENTITY_AFLTN_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , AFLTN_TYP_CD VARCHAR2(40)
        , CAMPUS_CD VARCHAR2(2)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_AFLTN_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_AFLTN_T
    ADD CONSTRAINT KRIM_ENTITY_AFLTN_TP1
PRIMARY KEY (ENTITY_AFLTN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_BIO_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_BIO_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_BIO_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_BIO_T
(
      ENTITY_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ETHNCTY_CD VARCHAR2(40)
        , BIRTH_DT DATE
        , GNDR_CD VARCHAR2(1) NOT NULL
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_BIO_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_BIO_T
    ADD CONSTRAINT KRIM_ENTITY_BIO_TP1
PRIMARY KEY (ENTITY_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_CACHE_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_CACHE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_CACHE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_CACHE_T
(
      ENTITY_ID VARCHAR2(40)
        , PRNCPL_ID VARCHAR2(40) NOT NULL
        , PRNCPL_NM VARCHAR2(40)
        , ENTITY_TYP_CD VARCHAR2(40)
        , FIRST_NM VARCHAR2(40)
        , MIDDLE_NM VARCHAR2(40)
        , LAST_NM VARCHAR2(40)
        , PRSN_NM VARCHAR2(40)
        , CAMPUS_CD VARCHAR2(40)
        , PRMRY_DEPT_CD VARCHAR2(40)
        , EMP_ID VARCHAR2(40)
        , LAST_UPDT_TS DATE
        , OBJ_ID VARCHAR2(36) NOT NULL

    , CONSTRAINT KRIM_ENTITY_CACHE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KRIM_ENTITY_CACHE_TC1 UNIQUE (PRNCPL_ID)

)
/

ALTER TABLE KRIM_ENTITY_CACHE_T
    ADD CONSTRAINT KRIM_ENTITY_CACHE_TP1
PRIMARY KEY (ENTITY_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_CTZNSHP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_CTZNSHP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_CTZNSHP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_CTZNSHP_T
(
      ENTITY_CTZNSHP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , CTZNSHP_STAT_CD VARCHAR2(40)
        , STRT_DT DATE
        , END_DT DATE
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_CTZNSHP_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_CTZNSHP_T
    ADD CONSTRAINT KRIM_ENTITY_CTZNSHP_TP1
PRIMARY KEY (ENTITY_CTZNSHP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_EMAIL_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_EMAIL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_EMAIL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_EMAIL_T
(
      ENTITY_EMAIL_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , ENT_TYP_CD VARCHAR2(40)
        , EMAIL_TYP_CD VARCHAR2(40)
        , EMAIL_ADDR VARCHAR2(200)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_EMAIL_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_EMAIL_T
    ADD CONSTRAINT KRIM_ENTITY_EMAIL_TP1
PRIMARY KEY (ENTITY_EMAIL_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_EMP_INFO_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_EMP_INFO_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_EMP_INFO_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_EMP_INFO_T
(
      ENTITY_EMP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , ENTITY_AFLTN_ID VARCHAR2(40)
        , EMP_STAT_CD VARCHAR2(40)
        , EMP_TYP_CD VARCHAR2(40)
        , BASE_SLRY_AMT NUMBER(15,2)
        , PRMRY_IND VARCHAR2(1)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE
        , PRMRY_DEPT_CD VARCHAR2(40)
        , EMP_ID VARCHAR2(40)
        , EMP_REC_ID VARCHAR2(40)

    , CONSTRAINT KR_KIM_ENTITY_EMP_INFO_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_EMP_INFO_T
    ADD CONSTRAINT KRIM_ENTITY_EMP_INFO_TP1
PRIMARY KEY (ENTITY_EMP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_ENT_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_ENT_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_ENT_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_ENT_TYP_T
(
      ENT_TYP_CD VARCHAR2(40)
        , ENTITY_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KRIM_ENTITY_ENT_TYP_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_ENT_TYP_T
    ADD CONSTRAINT KRIM_ENTITY_ENT_TYP_TP1
PRIMARY KEY (ENT_TYP_CD,ENTITY_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_EXT_ID_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_EXT_ID_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_EXT_ID_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_EXT_ID_T
(
      ENTITY_EXT_ID_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , EXT_ID_TYP_CD VARCHAR2(40)
        , EXT_ID VARCHAR2(100)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_EXT_ID_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_EXT_ID_T
    ADD CONSTRAINT KRIM_ENTITY_EXT_ID_TP1
PRIMARY KEY (ENTITY_EXT_ID_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_NM_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_NM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_NM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_NM_T
(
      ENTITY_NM_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , NM_TYP_CD VARCHAR2(40)
        , FIRST_NM VARCHAR2(40)
        , MIDDLE_NM VARCHAR2(40)
        , LAST_NM VARCHAR2(80)
        , SUFFIX_NM VARCHAR2(20)
        , TITLE_NM VARCHAR2(20)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_NAME_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_NM_T
    ADD CONSTRAINT KRIM_ENTITY_NM_TP1
PRIMARY KEY (ENTITY_NM_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_PHONE_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_PHONE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_PHONE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_PHONE_T
(
      ENTITY_PHONE_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , ENT_TYP_CD VARCHAR2(40)
        , PHONE_TYP_CD VARCHAR2(40)
        , PHONE_NBR VARCHAR2(20)
        , PHONE_EXTN_NBR VARCHAR2(8)
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_PHONE_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_PHONE_T
    ADD CONSTRAINT KRIM_ENTITY_PHONE_TP1
PRIMARY KEY (ENTITY_PHONE_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_PRIV_PREF_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_PRIV_PREF_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_PRIV_PREF_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_PRIV_PREF_T
(
      ENTITY_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , SUPPRESS_NM_IND VARCHAR2(1) default 'N'
        , SUPPRESS_EMAIL_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_ADDR_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_PHONE_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_PRSNL_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_PRIV_PREF_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_PRIV_PREF_T
    ADD CONSTRAINT KRIM_ENTITY_PRIV_PREF_TP1
PRIMARY KEY (ENTITY_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENTITY_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENTITY_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENTITY_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENTITY_T
(
      ENTITY_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENTITY_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ENTITY_T
    ADD CONSTRAINT KRIM_ENTITY_TP1
PRIMARY KEY (ENTITY_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ENT_NM_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENT_NM_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENT_NM_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENT_NM_TYP_T
(
      ENT_NM_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ENT_NAME_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_ENT_NAME_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_ENT_NM_TYP_T
    ADD CONSTRAINT KRIM_ENT_NM_TYP_TP1
PRIMARY KEY (ENT_NM_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_ENT_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ENT_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ENT_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ENT_TYP_T
(
      ENT_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , DISPLAY_SORT_CD VARCHAR2(2)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_ENT_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_ENT_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_ENT_TYP_T
    ADD CONSTRAINT KRIM_ENT_TYP_TP1
PRIMARY KEY (ENT_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_EXT_ID_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_EXT_ID_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_EXT_ID_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_EXT_ID_TYP_T
(
      EXT_ID_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(40)
        , ENCR_REQ_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_EXT_ID_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_EXT_ID_TYPE_TC1 UNIQUE (NM)

)
/

ALTER TABLE KRIM_EXT_ID_TYP_T
    ADD CONSTRAINT KRIM_EXT_ID_TYP_TP1
PRIMARY KEY (EXT_ID_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_GRP_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_GRP_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_GRP_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_GRP_ATTR_DATA_T
(
      ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , GRP_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)

    , CONSTRAINT KR_KIM_GROUP_ATTR_DATA_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_GRP_ATTR_DATA_T
    ADD CONSTRAINT KRIM_GRP_ATTR_DATA_TP1
PRIMARY KEY (ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_GRP_DOCUMENT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_GRP_DOCUMENT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_GRP_DOCUMENT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_GRP_DOCUMENT_T
(
      FDOC_NBR VARCHAR2(14)
        , GRP_ID VARCHAR2(40) NOT NULL
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , GRP_NMSPC VARCHAR2(100) NOT NULL
        , GRP_NM VARCHAR2(400)
        , GRP_DESC VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_GRP_DOCUMENT_T
    ADD CONSTRAINT KRIM_GRP_DOCUMENT_TP1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_GRP_MBR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_GRP_MBR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_GRP_MBR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_GRP_MBR_T
(
      GRP_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , GRP_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) default 'P'
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KRIM_GRP_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_GRP_MBR_T
    ADD CONSTRAINT KRIM_GRP_MBR_TP1
PRIMARY KEY (GRP_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_GRP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_GRP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_GRP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_GRP_T
(
      GRP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , GRP_NM VARCHAR2(80) NOT NULL
        , NMSPC_CD VARCHAR2(40) NOT NULL
        , GRP_DESC VARCHAR2(4000)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_GROUP_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_GROUP_TC1 UNIQUE (GRP_NM, NMSPC_CD)

)
/

ALTER TABLE KRIM_GRP_T
    ADD CONSTRAINT KRIM_GRP_TP1
PRIMARY KEY (GRP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PERM_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PERM_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PERM_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PERM_ATTR_DATA_T
(
      ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PERM_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)

    , CONSTRAINT KR_KIM_PERM_ATTR_DATA_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_PERM_ATTR_DATA_T
    ADD CONSTRAINT KRIM_PERM_ATTR_DATA_TP1
PRIMARY KEY (ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PERM_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PERM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PERM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PERM_T
(
      PERM_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PERM_TMPL_ID VARCHAR2(40)
        , NMSPC_CD VARCHAR2(40)
        , NM VARCHAR2(100)
        , DESC_TXT VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_PERM_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_PERM_T
    ADD CONSTRAINT KRIM_PERM_TP1
PRIMARY KEY (PERM_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PERM_TMPL_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PERM_TMPL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PERM_TMPL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PERM_TMPL_T
(
      PERM_TMPL_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NMSPC_CD VARCHAR2(40)
        , NM VARCHAR2(100)
        , DESC_TXT VARCHAR2(400)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_PERM_TMPL_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_PERM_TMPL_T
    ADD CONSTRAINT KRIM_PERM_TMPL_TP1
PRIMARY KEY (PERM_TMPL_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PERSON_DOCUMENT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PERSON_DOCUMENT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PERSON_DOCUMENT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PERSON_DOCUMENT_T
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_ID VARCHAR2(40) NOT NULL
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PRNCPL_ID VARCHAR2(40) NOT NULL
        , PRNCPL_NM VARCHAR2(100) NOT NULL
        , PRNCPL_PSWD VARCHAR2(400)
        , TAX_ID VARCHAR2(100)
        , UNIV_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_PERSON_DOCUMENT_T
    ADD CONSTRAINT KRIM_PERSON_DOCUMENT_TP1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_PHONE_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PHONE_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PHONE_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PHONE_TYP_T
(
      PHONE_TYP_CD VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PHONE_TYP_NM VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , DISPLAY_SORT_CD VARCHAR2(2)
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_PHONE_TYPE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_PHONE_TYPE_TC1 UNIQUE (PHONE_TYP_NM)

)
/

ALTER TABLE KRIM_PHONE_TYP_T
    ADD CONSTRAINT KRIM_PHONE_TYP_TP1
PRIMARY KEY (PHONE_TYP_CD)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ADDR_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ADDR_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ADDR_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ADDR_MT
(
      FDOC_NBR VARCHAR2(14)
        , ADDR_TYP_CD VARCHAR2(40)
        , ADDR_LINE_1 VARCHAR2(50)
        , ADDR_LINE_2 VARCHAR2(50)
        , ADDR_LINE_3 VARCHAR2(50)
        , CITY_NM VARCHAR2(30)
        , POSTAL_STATE_CD VARCHAR2(2)
        , POSTAL_CD VARCHAR2(20)
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , DISPLAY_SORT_CD VARCHAR2(2)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , ENTITY_ADDR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_ADDR_MT
    ADD CONSTRAINT KRIM_PND_ADDR_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_ADDR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_AFLTN_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_AFLTN_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_AFLTN_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_AFLTN_MT
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_AFLTN_ID VARCHAR2(40)
        , AFLTN_TYP_CD VARCHAR2(40)
        , CAMPUS_CD VARCHAR2(2)
        , EDIT_FLAG VARCHAR2(1) default 'N'
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL


)
/

ALTER TABLE KRIM_PND_AFLTN_MT
    ADD CONSTRAINT KRIM_PND_AFLTN_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_AFLTN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_CTZNSHP_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_CTZNSHP_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_CTZNSHP_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_CTZNSHP_MT
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_CTZNSHP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , CTZNSHP_STAT_CD VARCHAR2(40)
        , STRT_DT DATE
        , END_DT DATE
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_CTZNSHP_MT
    ADD CONSTRAINT KRIM_PND_CTZNSHP_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_CTZNSHP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_DLGN_MBR_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_DLGN_MBR_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T
(
      FDOC_NBR VARCHAR2(14)
        , ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , DLGN_MBR_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40)
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T
    ADD CONSTRAINT KRIM_PND_DLGN_MBR_ATTR_DATAP1
PRIMARY KEY (FDOC_NBR,ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_DLGN_MBR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_DLGN_MBR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_DLGN_MBR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_DLGN_MBR_T
(
      FDOC_NBR VARCHAR2(14)
        , DLGN_MBR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , DLGN_ID VARCHAR2(40) NOT NULL
        , MBR_ID VARCHAR2(40)
        , MBR_NM VARCHAR2(40)
        , MBR_TYP_CD VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , ROLE_MBR_ID VARCHAR2(40)


)
/

ALTER TABLE KRIM_PND_DLGN_MBR_T
    ADD CONSTRAINT KRIM_PND_DLGN_MBR_TP1
PRIMARY KEY (FDOC_NBR,DLGN_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_DLGN_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_DLGN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_DLGN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_DLGN_T
(
      FDOC_NBR VARCHAR2(14)
        , DLGN_ID VARCHAR2(40)
        , ROLE_ID VARCHAR2(40) NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , KIM_TYP_ID VARCHAR2(40)
        , DLGN_TYP_CD VARCHAR2(100) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_PND_DLGN_T
    ADD CONSTRAINT KRIM_PND_DLGN_TP1
PRIMARY KEY (FDOC_NBR,DLGN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_EMAIL_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_EMAIL_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_EMAIL_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_EMAIL_MT
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_EMAIL_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENT_TYP_CD VARCHAR2(40)
        , EMAIL_TYP_CD VARCHAR2(40)
        , EMAIL_ADDR VARCHAR2(200)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_EMAIL_MT
    ADD CONSTRAINT KRIM_PND_EMAIL_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_EMAIL_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_EMP_INFO_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_EMP_INFO_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_EMP_INFO_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_EMP_INFO_MT
(
      FDOC_NBR VARCHAR2(14)
        , PRMRY_DEPT_CD VARCHAR2(40)
        , ENTITY_EMP_ID VARCHAR2(40)
        , EMP_ID VARCHAR2(40)
        , EMP_REC_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENTITY_AFLTN_ID VARCHAR2(40)
        , EMP_STAT_CD VARCHAR2(40)
        , EMP_TYP_CD VARCHAR2(40)
        , BASE_SLRY_AMT NUMBER(15,2)
        , PRMRY_IND VARCHAR2(1)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_EMP_INFO_MT
    ADD CONSTRAINT KRIM_PND_EMP_INFO_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_EMP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_GRP_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_GRP_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_GRP_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_GRP_ATTR_DATA_T
(
      FDOC_NBR VARCHAR2(14)
        , ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , GRP_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40)
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)


)
/

ALTER TABLE KRIM_PND_GRP_ATTR_DATA_T
    ADD CONSTRAINT KRIM_PND_GRP_ATTR_DATA_TP1
PRIMARY KEY (FDOC_NBR,ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_GRP_MBR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_GRP_MBR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_GRP_MBR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_GRP_MBR_T
(
      FDOC_NBR VARCHAR2(14)
        , GRP_MBR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , GRP_ID VARCHAR2(40) NOT NULL
        , MBR_ID VARCHAR2(40)
        , MBR_NM VARCHAR2(40)
        , MBR_TYP_CD VARCHAR2(40) NOT NULL
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE


)
/

ALTER TABLE KRIM_PND_GRP_MBR_T
    ADD CONSTRAINT KRIM_PND_GRP_MBR_TP1
PRIMARY KEY (FDOC_NBR,GRP_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_GRP_PRNCPL_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_GRP_PRNCPL_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_GRP_PRNCPL_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_GRP_PRNCPL_MT
(
      GRP_MBR_ID VARCHAR2(40)
        , FDOC_NBR VARCHAR2(14)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , GRP_ID VARCHAR2(40) NOT NULL
        , PRNCPL_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , GRP_NM VARCHAR2(80) NOT NULL
        , GRP_TYPE VARCHAR2(80)
        , KIM_TYP_ID VARCHAR2(40)
        , NMSPC_CD VARCHAR2(40)
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_GRP_PRNCPL_MT
    ADD CONSTRAINT KRIM_PND_GRP_PRNCPL_MTP1
PRIMARY KEY (GRP_MBR_ID,FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_PND_NM_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_NM_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_NM_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_NM_MT
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_NM_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM_TYP_CD VARCHAR2(40)
        , FIRST_NM VARCHAR2(40)
        , MIDDLE_NM VARCHAR2(40)
        , LAST_NM VARCHAR2(80)
        , SUFFIX_NM VARCHAR2(20)
        , TITLE_NM VARCHAR2(20)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_NM_MT
    ADD CONSTRAINT KRIM_PND_NM_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_NM_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_PHONE_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_PHONE_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_PHONE_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_PHONE_MT
(
      FDOC_NBR VARCHAR2(14)
        , ENTITY_PHONE_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ENT_TYP_CD VARCHAR2(40)
        , PHONE_TYP_CD VARCHAR2(40)
        , PHONE_NBR VARCHAR2(20)
        , PHONE_EXTN_NBR VARCHAR2(8)
        , POSTAL_CNTRY_CD VARCHAR2(2)
        , DFLT_IND VARCHAR2(1) default 'N'
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_PHONE_MT
    ADD CONSTRAINT KRIM_PND_PHONE_MTP1
PRIMARY KEY (FDOC_NBR,ENTITY_PHONE_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_PRIV_PREF_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_PRIV_PREF_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_PRIV_PREF_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_PRIV_PREF_MT
(
      FDOC_NBR VARCHAR2(14)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , SUPPRESS_NM_IND VARCHAR2(1) default 'N'
        , SUPPRESS_EMAIL_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_ADDR_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_PHONE_IND VARCHAR2(1) default 'Y'
        , SUPPRESS_PRSNL_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_PRIV_PREF_MT
    ADD CONSTRAINT KRIM_PND_PRIV_PREF_MTP1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_MBR_ATTR_DATA_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_MBR_ATTR_DATA_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_MBR_ATTR_DATA_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_MBR_ATTR_DATA_MT
(
      FDOC_NBR VARCHAR2(14)
        , ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_MBR_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40)
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_ROLE_MBR_ATTR_DATA_MT
    ADD CONSTRAINT KRIM_PND_ROLE_MBR_ATTR_DATAP1
PRIMARY KEY (FDOC_NBR,ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_MBR_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_MBR_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_MBR_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_MBR_MT
(
      FDOC_NBR VARCHAR2(14)
        , ROLE_MBR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_ID VARCHAR2(40) NOT NULL
        , MBR_ID VARCHAR2(40)
        , MBR_NM VARCHAR2(40)
        , MBR_TYP_CD VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_ROLE_MBR_MT
    ADD CONSTRAINT KRIM_PND_ROLE_MBR_MTP1
PRIMARY KEY (FDOC_NBR,ROLE_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_MT
(
      FDOC_NBR VARCHAR2(14)
        , ROLE_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_NM VARCHAR2(100) NOT NULL
        , KIM_TYP_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , NMSPC_CD VARCHAR2(40)
        , EDIT_FLAG VARCHAR2(1) default 'N'


)
/

ALTER TABLE KRIM_PND_ROLE_MT
    ADD CONSTRAINT KRIM_PND_ROLE_MTP1
PRIMARY KEY (FDOC_NBR,ROLE_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_PERM_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_PERM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_PERM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_PERM_T
(
      FDOC_NBR VARCHAR2(14)
        , ROLE_PERM_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_ID VARCHAR2(40) NOT NULL
        , PERM_ID VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_PND_ROLE_PERM_T
    ADD CONSTRAINT KRIM_PND_ROLE_PERM_TP1
PRIMARY KEY (FDOC_NBR,ROLE_PERM_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_RSP_ACTN_MT
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_RSP_ACTN_MT';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_RSP_ACTN_MT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_RSP_ACTN_MT
(
      ROLE_RSP_ACTN_ID VARCHAR2(40)
        , FDOC_NBR VARCHAR2(14)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ACTN_TYP_CD VARCHAR2(40)
        , PRIORITY_NBR NUMBER(3)
        , ACTN_PLCY_CD VARCHAR2(40)
        , ROLE_MBR_ID VARCHAR2(40)
        , ROLE_RSP_ID VARCHAR2(40)
        , EDIT_FLAG VARCHAR2(1) default 'N'
        , FRC_ACTN VARCHAR2(1)


)
/

ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT
    ADD CONSTRAINT KRIM_PND_ROLE_RSP_ACTN_MTP1
PRIMARY KEY (ROLE_RSP_ACTN_ID,FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_PND_ROLE_RSP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PND_ROLE_RSP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PND_ROLE_RSP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PND_ROLE_RSP_T
(
      FDOC_NBR VARCHAR2(14)
        , ROLE_RSP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_ID VARCHAR2(40) NOT NULL
        , RSP_ID VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_PND_ROLE_RSP_T
    ADD CONSTRAINT KRIM_PND_ROLE_RSP_TP1
PRIMARY KEY (FDOC_NBR,ROLE_RSP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_PRNCPL_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_PRNCPL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_PRNCPL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_PRNCPL_T
(
      PRNCPL_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , PRNCPL_NM VARCHAR2(100) NOT NULL
        , ENTITY_ID VARCHAR2(40)
        , PRNCPL_PSWD VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_PRINCIPAL_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_PRINCIPAL_TC1 UNIQUE (PRNCPL_NM)

)
/

ALTER TABLE KRIM_PRNCPL_T
    ADD CONSTRAINT KRIM_PRNCPL_TP1
PRIMARY KEY (PRNCPL_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_DOCUMENT_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_DOCUMENT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_DOCUMENT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_DOCUMENT_T
(
      FDOC_NBR VARCHAR2(14)
        , ROLE_ID VARCHAR2(40) NOT NULL
        , OBJ_ID VARCHAR2(36) default SYS_GUID() NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_TYP_ID VARCHAR2(40) NOT NULL
        , ROLE_NMSPC VARCHAR2(100) NOT NULL
        , ROLE_NM VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'


)
/

ALTER TABLE KRIM_ROLE_DOCUMENT_T
    ADD CONSTRAINT KRIM_ROLE_DOCUMENT_TP1
PRIMARY KEY (FDOC_NBR)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_MBR_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_MBR_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_MBR_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_MBR_ATTR_DATA_T
(
      ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_MBR_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)

    , CONSTRAINT KR_KIM_ROLE_MBR_ATTR_DATA_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_MBR_ATTR_DATA_T
    ADD CONSTRAINT KRIM_ROLE_MBR_ATTR_DATA_TP1
PRIMARY KEY (ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_MBR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_MBR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_MBR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_MBR_T
(
      ROLE_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , ROLE_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) default 'P'
        , ACTV_FRM_DT DATE
        , ACTV_TO_DT DATE
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KRIM_ROLE_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_MBR_T
    ADD CONSTRAINT KRIM_ROLE_MBR_TP1
PRIMARY KEY (ROLE_MBR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_PERM_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_PERM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_PERM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_PERM_T
(
      ROLE_PERM_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_ID VARCHAR2(40)
        , PERM_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_ROLE_PERM_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_PERM_T
    ADD CONSTRAINT KRIM_ROLE_PERM_TP1
PRIMARY KEY (ROLE_PERM_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_RSP_ACTN_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_RSP_ACTN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_RSP_ACTN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_RSP_ACTN_T
(
      ROLE_RSP_ACTN_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ACTN_TYP_CD VARCHAR2(40)
        , PRIORITY_NBR NUMBER(3)
        , ACTN_PLCY_CD VARCHAR2(40)
        , ROLE_MBR_ID VARCHAR2(40)
        , ROLE_RSP_ID VARCHAR2(40)
        , FRC_ACTN VARCHAR2(1) default 'N'

    , CONSTRAINT KRIM_ROLE_RSP_ACTN_TC1 UNIQUE (ROLE_RSP_ID, ROLE_MBR_ID)
    , CONSTRAINT KR_KIM_ROLE_RESP_ACTN_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_RSP_ACTN_T
    ADD CONSTRAINT KRIM_ROLE_RSP_ACTN_TP1
PRIMARY KEY (ROLE_RSP_ACTN_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_RSP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_RSP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_RSP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_RSP_T
(
      ROLE_RSP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_ID VARCHAR2(40)
        , RSP_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_ROLE_RESP_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_RSP_T
    ADD CONSTRAINT KRIM_ROLE_RSP_TP1
PRIMARY KEY (ROLE_RSP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_ROLE_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_ROLE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_ROLE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_ROLE_T
(
      ROLE_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , ROLE_NM VARCHAR2(80) NOT NULL
        , NMSPC_CD VARCHAR2(40) NOT NULL
        , DESC_TXT VARCHAR2(4000)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , ACTV_IND VARCHAR2(1) default 'Y'
        , LAST_UPDT_DT DATE default SYSDATE

    , CONSTRAINT KR_KIM_ROLE_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KR_KIM_ROLE_TC1 UNIQUE (ROLE_NM, NMSPC_CD)

)
/

ALTER TABLE KRIM_ROLE_T
    ADD CONSTRAINT KRIM_ROLE_TP1
PRIMARY KEY (ROLE_ID)
/







-----------------------------------------------------------------------------
-- KRIM_RSP_ATTR_DATA_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_RSP_ATTR_DATA_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_RSP_ATTR_DATA_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_RSP_ATTR_DATA_T
(
      ATTR_DATA_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , RSP_ID VARCHAR2(40)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ATTR_VAL VARCHAR2(400)

    , CONSTRAINT KR_KIM_RESP_ATTR_DATA_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_RSP_ATTR_DATA_T
    ADD CONSTRAINT KRIM_RSP_ATTR_DATA_TP1
PRIMARY KEY (ATTR_DATA_ID)
/







-----------------------------------------------------------------------------
-- KRIM_RSP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_RSP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_RSP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_RSP_T
(
      RSP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , RSP_TMPL_ID VARCHAR2(40)
        , NMSPC_CD VARCHAR2(40)
        , NM VARCHAR2(100)
        , DESC_TXT VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_RESP_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_RSP_T
    ADD CONSTRAINT KRIM_RSP_TP1
PRIMARY KEY (RSP_ID)
/







-----------------------------------------------------------------------------
-- KRIM_RSP_TMPL_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_RSP_TMPL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_RSP_TMPL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_RSP_TMPL_T
(
      RSP_TMPL_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NMSPC_CD VARCHAR2(40)
        , NM VARCHAR2(80)
        , KIM_TYP_ID VARCHAR2(100) NOT NULL
        , DESC_TXT VARCHAR2(400)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_RESP_TMPL_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_RSP_TMPL_T
    ADD CONSTRAINT KRIM_RSP_TMPL_TP1
PRIMARY KEY (RSP_TMPL_ID)
/







-----------------------------------------------------------------------------
-- KRIM_TYP_ATTR_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_TYP_ATTR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_TYP_ATTR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_TYP_ATTR_T
(
      KIM_TYP_ATTR_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , SORT_CD VARCHAR2(2)
        , KIM_TYP_ID VARCHAR2(40) NOT NULL
        , KIM_ATTR_DEFN_ID VARCHAR2(40)
        , ACTV_IND VARCHAR2(1) default 'Y'

    , CONSTRAINT KR_KIM_TYPE_ATTRIBUTE_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_TYP_ATTR_T
    ADD CONSTRAINT KRIM_TYP_ATTR_TP1
PRIMARY KEY (KIM_TYP_ATTR_ID)
/







-----------------------------------------------------------------------------
-- KRIM_TYP_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KRIM_TYP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KRIM_TYP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KRIM_TYP_T
(
      KIM_TYP_ID VARCHAR2(40)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 1 NOT NULL
        , NM VARCHAR2(100)
        , SRVC_NM VARCHAR2(200)
        , ACTV_IND VARCHAR2(1) default 'Y'
        , NMSPC_CD VARCHAR2(40)

    , CONSTRAINT KR_KIM_TYPE_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_TYP_T
    ADD CONSTRAINT KRIM_TYP_TP1
PRIMARY KEY (KIM_TYP_ID)
/

------------------------
-- Sequences
------------------------

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ATTR_DATA_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ATTR_DATA_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ATTR_DATA_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ATTR_DEFN_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ATTR_DEFN_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ATTR_DEFN_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_DLGN_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_DLGN_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_DLGN_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_DLGN_MBR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_DLGN_MBR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_DLGN_MBR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_ADDR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_ADDR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_ADDR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_AFLTN_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_AFLTN_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_AFLTN_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_CTZNSHP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_CTZNSHP_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_CTZNSHP_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_EMAIL_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_EMAIL_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_EMAIL_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_EMP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_EMP_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_EMP_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_EXT_ID_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_EXT_ID_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_EXT_ID_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_NM_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_NM_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_NM_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ENTITY_PHONE_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ENTITY_PHONE_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ENTITY_PHONE_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_GRP_ATTR_DATA_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_GRP_ATTR_DATA_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_GRP_ATTR_DATA_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_GRP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_GRP_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_GRP_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_GRP_MBR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_GRP_MBR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_GRP_MBR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_PERM_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_PERM_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_PERM_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_PERM_RQRD_ATTR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_PERM_RQRD_ATTR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_PERM_RQRD_ATTR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_PERM_TMPL_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_PERM_TMPL_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_PERM_TMPL_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_PRNCPL_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_PRNCPL_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_PRNCPL_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ROLE_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ROLE_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ROLE_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ROLE_MBR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ROLE_MBR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ROLE_MBR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ROLE_RSP_ACTN_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ROLE_RSP_ACTN_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ROLE_RSP_ACTN_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_ROLE_RSP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_ROLE_RSP_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_ROLE_RSP_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_RSP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_RSP_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_RSP_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_RSP_RQRD_ATTR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_RSP_RQRD_ATTR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_RSP_RQRD_ATTR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_RSP_TMPL_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_RSP_TMPL_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_RSP_TMPL_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_TYP_ATTR_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_TYP_ATTR_ID_S'; END IF;
END;
/

CREATE SEQUENCE KRIM_TYP_ATTR_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_sequences WHERE sequence_name = 'KRIM_TYP_ID_S';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE KRIM_TYP_ID_S'; END IF;
END;
/
