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
# ACCT_DD_ATTR_DOC
# -----------------------------------------------------------------------
drop table if exists ACCT_DD_ATTR_DOC
/

CREATE TABLE ACCT_DD_ATTR_DOC
(
      DOC_HDR_ID VARCHAR(14)
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(14)
        , ACCT_NUM DECIMAL(14) NOT NULL
        , ACCT_OWNR VARCHAR(50) NOT NULL
        , ACCT_BAL DECIMAL(16,2) NOT NULL
        , ACCT_OPN_DAT DATETIME NOT NULL
        , ACCT_STAT VARCHAR(30) NOT NULL
        , ACCT_UPDATE_DT_TM DATETIME
        , ACCT_AWAKE VARCHAR(1)

    , CONSTRAINT ACCT_DD_ATTR_DOCP1 PRIMARY KEY(DOC_HDR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# KR_KIM_TEST_BO
# -----------------------------------------------------------------------
drop table if exists KR_KIM_TEST_BO
/

CREATE TABLE KR_KIM_TEST_BO
(
      PK VARCHAR(40)
        , PRNCPL_ID VARCHAR(40)
    






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/


# -----------------------------------------------------------------------
# TST_SEARCH_ATTR_INDX_TST_DOC_T
# -----------------------------------------------------------------------
drop table if exists TST_SEARCH_ATTR_INDX_TST_DOC_T
/

CREATE TABLE TST_SEARCH_ATTR_INDX_TST_DOC_T
(
      DOC_HDR_ID VARCHAR(14)
        , OBJ_ID VARCHAR(36)
        , VER_NBR DECIMAL(14)
        , RTE_LVL_CNT DECIMAL(14)
        , CNSTNT_STR VARCHAR(50)
        , RTD_STR VARCHAR(50)
        , HLD_RTD_STR VARCHAR(50)
        , RD_ACCS_CNT DECIMAL(14)

    , CONSTRAINT TST_SEARCH_ATTR_INDX_TST_DOP1 PRIMARY KEY(DOC_HDR_ID)






) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin
/