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

create table EN_UNITTEST_T (
    COL VARCHAR(1) NULL
);

CREATE TABLE KR_KIM_TEST_BO  ( 
	PK       	varchar(40),
	PRNCPL_ID	varchar(40)
);

CREATE TABLE TST_SEARCH_ATTR_INDX_TST_DOC_T  ( 
	DOC_HDR_ID 	varchar(14),
	OBJ_ID     	varchar(36) NOT NULL,
	VER_NBR    	decimal(8,0) DEFAULT 1 NOT NULL,
	RTE_LVL_CNT	decimal(14,0),
	CNSTNT_STR 	varchar(50),
	RTD_STR    	varchar(50),
	HLD_RTD_STR	varchar(50),
	RD_ACCS_CNT	decimal(14,0),
	PRIMARY KEY(DOC_HDR_ID)
);

CREATE TABLE TRV_ACCT_USE_RT_T  ( 
    ID         	varchar(40),
    ACCT_NUM   	varchar(10),
    RATE       	decimal(8,0),
    ACTV_FRM_DT	datetime,
    ACTV_TO_DT 	datetime,
    PRIMARY KEY(ID)
);

CREATE TABLE ACCT_DD_ATTR_DOC  ( 
	DOC_HDR_ID       	varchar(14),
	OBJ_ID           	varchar(36) NOT NULL,
	VER_NBR          	decimal(8,0) DEFAULT 1 NOT NULL,
	ACCT_NUM         	decimal(14,0),
	ACCT_OWNR        	varchar(50),
	ACCT_BAL         	decimal(16,2),
	ACCT_OPN_DAT     	datetime,
	ACCT_STAT        	varchar(30),
	ACCT_UPDATE_DT_TM	datetime,
	ACCT_AWAKE       	varchar(1),
	PRIMARY KEY(DOC_HDR_ID)
);

CREATE TABLE KRTST_TEST_ANOTHER_REF_OBJ_T  ( 
    STR_PROP      	varchar(10),
    DATE_PROP     	datetime,
    OTHER_STR_PROP	varchar(40),
    PRIMARY KEY(STR_PROP,DATE_PROP)
);
CREATE TABLE KRTST_TEST_COLL_T  ( 
    PK_PROP    	varchar(10),
    PK_PROP_TWO	varchar(10),
    STR_PROP   	varchar(40),
    PRIMARY KEY(PK_PROP,PK_PROP_TWO)
);
CREATE TABLE KRTST_TEST_COLL_TWO_T  ( 
    PK_PROP         	varchar(10),
    PK_COLL_KEY_PROP	varchar(10),
    STR_PROP        	varchar(40),
    PRIMARY KEY(PK_PROP,PK_COLL_KEY_PROP)
);
CREATE TABLE KRTST_TEST_INDIR_LINK_T  ( 
    PK_PROP     	varchar(10),
    COLL_PK_PROP	varchar(10),
    PRIMARY KEY(PK_PROP,COLL_PK_PROP)
);
CREATE TABLE KRTST_TEST_INDIRECT_COLL_T  ( 
    PK_COLL_PROP	varchar(10),
    STR_PROP    	varchar(40),
    PRIMARY KEY(PK_COLL_PROP)
);
CREATE TABLE KRTST_TEST_REF_OBJ_T  ( 
    STR_PROP      	varchar(10),
    OTHER_STR_PROP	varchar(40),
    PRIMARY KEY(STR_PROP)
);
CREATE TABLE KRTST_TEST_TABLE_2_T  ( 
    PK_PROP         	varchar(10),
    PK_COLL_KEY_PROP	varchar(10),
    STR_PROP        	varchar(40),
    PRIMARY KEY(PK_PROP,PK_COLL_KEY_PROP)
);
CREATE TABLE KRTST_TEST_TABLE_EXT_T  ( 
    PK_PROP 	varchar(10),
    STR_PROP	varchar(40),
    PRIMARY KEY(PK_PROP)
);
CREATE TABLE KRTST_TEST_TABLE_T  ( 
    PK_PROP      	varchar(10),
    STR_PROP     	varchar(40),
    LONG_STR_PROP	varchar(200),
    DATE_PROP    	datetime,
    CURR_PROP    	decimal(19,2),
    NON_STANDARD 	varchar(40),
    BOOL_PROP    	char(1) DEFAULT 'N',
    PRIMARY KEY(PK_PROP)
);
CREATE TABLE KRTST_TEST_YARDO_T  ( 
    ID            	varchar(10),
    OTHER_STR_PROP	varchar(40),
    PRIMARY KEY(ID)
);

CREATE TABLE TRV_DOC_2  ( 
	FDOC_NBR        	varchar(14) ,
	OBJ_ID          	varchar(36) NOT NULL,
	VER_NBR         	decimal(8,0) NOT NULL DEFAULT 1,
	FDOC_EXPLAIN_TXT	varchar(400),
	REQUEST_TRAV    	varchar(30),
	TRAVELER        	varchar(200),
	ORG             	varchar(60),
	DEST            	varchar(60),
	PRIMARY KEY(FDOC_NBR)
);

CREATE TABLE TRAV_DOC_2_ACCOUNTS  ( 
	FDOC_NBR	varchar(14),
	ACCT_NUM	varchar(10),
	PRIMARY KEY(FDOC_NBR,ACCT_NUM)
);

CREATE TABLE TRVL_ID_SEQ  ( 
	id	bigint(19) AUTO_INCREMENT NOT NULL,
	PRIMARY KEY(id)
)
ENGINE = MyISAM
AUTO_INCREMENT = 1;

CREATE TABLE KRTST_PARENT_WITH_MULTI_KEY_T  ( 
    FIN_COA_CD      	VARCHAR(2),
    ACCOUNT_NBR     	VARCHAR(7),
    ORG_CD		      	VARCHAR(4),
    PRIMARY KEY(FIN_COA_CD,ACCOUNT_NBR)
);

CREATE TABLE KRTST_TWO_KEY_CHILD_T  ( 
    FIN_COA_CD      	VARCHAR(2),
    ORG_CD		      	VARCHAR(4),
    PRIMARY KEY(FIN_COA_CD,ORG_CD)
);
