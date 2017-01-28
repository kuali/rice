--
-- Copyright 2005-2017 The Kuali Foundation
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

CREATE TABLE KRTST_TEST_ANOTHER_REF_OBJ_T  ( 
    STR_PROP      	VARCHAR2(10),
    DATE_PROP     	DATE,
    OTHER_STR_PROP	VARCHAR2(40),
    PRIMARY KEY(STR_PROP,DATE_PROP)
)
/
CREATE TABLE KRTST_TEST_COLL_T  ( 
    PK_PROP    	VARCHAR2(10),
    PK_PROP_TWO	VARCHAR2(10),
    STR_PROP   	VARCHAR2(40),
    PRIMARY KEY(PK_PROP,PK_PROP_TWO)
)
/
CREATE TABLE KRTST_TEST_COLL_TWO_T  ( 
    PK_PROP         	VARCHAR2(10),
    PK_COLL_KEY_PROP	VARCHAR2(10),
    STR_PROP        	VARCHAR2(40),
    PRIMARY KEY(PK_PROP,PK_COLL_KEY_PROP)
)
/
CREATE TABLE KRTST_TEST_INDIR_LINK_T  ( 
    PK_PROP     	VARCHAR2(10),
    COLL_PK_PROP	VARCHAR2(10),
    PRIMARY KEY(PK_PROP,COLL_PK_PROP)
)
/
CREATE TABLE KRTST_TEST_INDIRECT_COLL_T  ( 
    PK_COLL_PROP	VARCHAR2(10),
    STR_PROP    	VARCHAR2(40),
    PRIMARY KEY(PK_COLL_PROP)
)
/
CREATE TABLE KRTST_TEST_REF_OBJ_T  ( 
    STR_PROP      	VARCHAR2(10),
    OTHER_STR_PROP	VARCHAR2(40),
    PRIMARY KEY(STR_PROP)
)
/
CREATE TABLE KRTST_TEST_TABLE_2_T  ( 
    PK_PROP         	VARCHAR2(10),
    PK_COLL_KEY_PROP	VARCHAR2(10),
    STR_PROP        	VARCHAR2(40),
    PRIMARY KEY(PK_PROP,PK_COLL_KEY_PROP)
)
/
CREATE TABLE KRTST_TEST_TABLE_EXT_T  ( 
    PK_PROP 	VARCHAR2(10),
    STR_PROP	VARCHAR2(40),
    PRIMARY KEY(PK_PROP)
)
/
CREATE TABLE KRTST_TEST_TABLE_T  ( 
    PK_PROP      	VARCHAR2(10),
    STR_PROP     	VARCHAR2(40),
    LONG_STR_PROP	VARCHAR2(200),
    DATE_PROP    	DATE,
    CURR_PROP    	NUMBER(19,2),
    NON_STANDARD 	VARCHAR2(40),
    BOOL_PROP    	char(1) DEFAULT 'N',
    ENCR_PROP     VARCHAR2(255),
    RO_PROP       VARCHAR2(40),
    PRIMARY KEY(PK_PROP)
)
/
CREATE TABLE KRTST_TEST_YARDO_T  ( 
    ID            	VARCHAR2(10),
    OTHER_STR_PROP	VARCHAR2(40),
    PRIMARY KEY(ID)
)
/
CREATE TABLE KRTST_PARENT_WITH_MULTI_KEY_T  ( 
    FIN_COA_CD      	VARCHAR2(2),
    ACCOUNT_NBR     	VARCHAR2(7),
    ORG_CD		      	VARCHAR2(4),
    PRIMARY KEY(FIN_COA_CD,ACCOUNT_NBR)
)
/
CREATE TABLE KRTST_TWO_KEY_CHILD_T  ( 
    FIN_COA_CD      	VARCHAR2(2),
    ORG_CD		      	VARCHAR2(4),
    PRIMARY KEY(FIN_COA_CD,ORG_CD)
)
/
