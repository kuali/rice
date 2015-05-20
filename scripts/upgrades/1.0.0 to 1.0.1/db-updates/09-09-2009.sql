--
-- Copyright 2009 The Kuali Foundation
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

-- create ethnicity table
CREATE TABLE KRIM_ENTITY_ETHNIC_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      ETHNCTY_CD VARCHAR2(40),
      SUB_ETHNCTY_CD VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_ETHNIC_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_ETHNIC_T
    ADD CONSTRAINT KRIM_ENTITY_ETHNIC_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_ETHNIC_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- create residency table
CREATE TABLE KRIM_ENTITY_RESIDENCY_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      DETERMINATION_METHOD VARCHAR2(40),
      IN_STATE VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_RESIDENCY_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_RESIDENCY_T
    ADD CONSTRAINT KRIM_ENTITY_RESIDENCY_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_RESIDENCY_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- add visa table
CREATE TABLE KRIM_ENTITY_VISA_T
(
      ID VARCHAR2(40),
      ENTITY_ID VARCHAR2(40),
      VISA_TYPE_KEY VARCHAR2(40),
      VISA_ENTRY VARCHAR2(40),
      VISA_ID VARCHAR2(40),
      VER_NBR NUMBER(8) default 1 NOT NULL,
      OBJ_ID VARCHAR2(36) NOT NULL,

      CONSTRAINT KRIM_ENTITY_VISA_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KRIM_ENTITY_VISA_T
    ADD CONSTRAINT KRIM_ENTITY_VISA_TP1
PRIMARY KEY (ID)
/
CREATE SEQUENCE KRIM_ENTITY_VISA_ID_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- insert ethnicity values into new ethinicity table from bio table (copy ethnicityCode and entityId values... double check sequence usage)
INSERT INTO KRIM_ENTITY_ETHNIC_T ( ID, OBJ_ID, ENTITY_ID, ETHNCTY_CD )
    SELECT KRIM_ENTITY_ETHNIC_ID_S.NEXTVAL, SYS_GUID(), bio.ENTITY_ID, bio.ETHNCTY_CD
        FROM KRIM_ENTITY_BIO_T bio
/

-- alter bio table to add new fields
ALTER TABLE KRIM_ENTITY_BIO_T ADD DECEASED_DT DATE
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD MARITAL_STATUS VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD PRIM_LANG_CD VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD SEC_LANG_CD VARCHAR2(40)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CNTRY_CD VARCHAR2(2)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_STATE_CD VARCHAR2(2)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD BIRTH_CITY VARCHAR2(30)
/
ALTER TABLE KRIM_ENTITY_BIO_T ADD GEO_ORIGIN VARCHAR2(100)
/

-- drop ethnicity from bio table
ALTER TABLE KRIM_ENTITY_BIO_T DROP COLUMN ETHNCTY_CD
/