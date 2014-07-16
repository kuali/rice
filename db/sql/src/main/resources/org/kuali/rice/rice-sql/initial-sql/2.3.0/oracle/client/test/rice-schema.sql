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
-- ACCT_DD_ATTR_DOC
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'ACCT_DD_ATTR_DOC';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE ACCT_DD_ATTR_DOC CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE ACCT_DD_ATTR_DOC
(
      DOC_HDR_ID VARCHAR2(14)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(14)
        , ACCT_NUM NUMBER(14) NOT NULL
        , ACCT_OWNR VARCHAR2(50) NOT NULL
        , ACCT_BAL NUMBER(16,2) NOT NULL
        , ACCT_OPN_DAT DATE NOT NULL
        , ACCT_STAT VARCHAR2(30) NOT NULL
        , ACCT_UPDATE_DT_TM DATE
        , ACCT_AWAKE VARCHAR2(1)


)
/

ALTER TABLE ACCT_DD_ATTR_DOC
    ADD CONSTRAINT ACCT_DD_ATTR_DOCP1
PRIMARY KEY (DOC_HDR_ID)
/







-----------------------------------------------------------------------------
-- KR_KIM_TEST_BO
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KR_KIM_TEST_BO';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KR_KIM_TEST_BO CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KR_KIM_TEST_BO
(
      PK VARCHAR2(40)
        , PRNCPL_ID VARCHAR2(40)
    

)
/







-----------------------------------------------------------------------------
-- TST_SEARCH_ATTR_INDX_TST_DOC_T
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TST_SEARCH_ATTR_INDX_TST_DOC_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TST_SEARCH_ATTR_INDX_TST_DOC_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE TST_SEARCH_ATTR_INDX_TST_DOC_T
(
      DOC_HDR_ID VARCHAR2(14)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR NUMBER(14)
        , RTE_LVL_CNT NUMBER(14)
        , CNSTNT_STR VARCHAR2(50)
        , RTD_STR VARCHAR2(50)
        , HLD_RTD_STR VARCHAR2(50)
        , RD_ACCS_CNT NUMBER(14)


)
/

ALTER TABLE TST_SEARCH_ATTR_INDX_TST_DOC_T
    ADD CONSTRAINT TST_SEARCH_ATTR_INDX_TST_DOP1
PRIMARY KEY (DOC_HDR_ID)
/