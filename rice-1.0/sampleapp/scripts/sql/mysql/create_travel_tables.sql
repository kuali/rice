--
-- Copyright 2010 The Kuali Foundation
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
-- DO NOT add comments before the blank line below, or they will disappear.
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
# -----------------------------------------------------------------------------
# -- TRAV_DOC_2_ACCOUNTS
# -----------------------------------------------------------------------------
drop table if exists TRAV_DOC_2_ACCOUNTS
/
CREATE TABLE TRAV_DOC_2_ACCOUNTS
(
      FDOC_NBR VARCHAR(14)
        , ACCT_NUM VARCHAR(10)
        , CONSTRAINT TRAV_DOC_2_ACCOUNTSP1 PRIMARY KEY(FDOC_NBR,ACCT_NUM)
)
/

# -----------------------------------------------------------------------------
# -- TRV_ATTACH_SAMPLE_T
# -----------------------------------------------------------------------------
drop table if exists trv_attach_sample_t
/
create table trv_attach_sample_t (attachment_id varchar(30),
                              description varchar(4000),
                              attachment_filename varchar(300),
                              attachment_file_content_type varchar(255),
                              attachment_file longblob,
                              obj_id varchar(36) not null,
                              ver_nbr decimal(8) default 1 not null,
                              primary key (attachment_id))
/     
# -----------------------------------------------------------------------------
# -- TRV_MULTI_ATTACH_SAMPLE_T
# -----------------------------------------------------------------------------
drop table if exists trv_multi_attach_sample_t
/
create table trv_multi_attach_sample_t (gen_id decimal(14,0) not null,
                              attachment_id varchar(30),
                              description varchar(4000),
                              attachment_filename varchar(300),
                              attachment_file_content_type varchar(255),
                              attachment_file longblob,
                              obj_id varchar(36) not null,
                              ver_nbr decimal(8) default 1 not null,
                              primary key (gen_id),
                              foreign key (attachment_id) references attachment_sample(attachment_id))
/
