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
-- 
alter table KRIM_PND_DLGN_MT rename to KRIM_PND_DLGN_T
/
alter table KRIM_PND_ROLE_RSP_ACTN_MT add IGNORE_PREV_IND varchar2(1) NULL;
/
CREATE TABLE KRIM_PND_ROLE_PERM_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_PERM_ID	VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    PERM_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_PERM_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_ROLE_RSP_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_RSP_ID		VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    RSP_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_RSP_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_T
(
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    DLGN_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    DLGN_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_IND    	VARCHAR2(1)  DEFAULT 'Y' NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(DLGN_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T (
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    ACTV_IND    	VARCHAR2(1) DEFAULT 'Y' NULL,
    EDIT_FLAG    	VARCHAR2(1) DEFAULT 'N' NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_GRP_DOCUMENT_T
(
    FDOC_NBR          VARCHAR2(14) 		NOT NULL,
    GRP_ID            VARCHAR2(40) 		NOT NULL,
    OBJ_ID            VARCHAR2(36) 		DEFAULT SYS_GUID() NOT NULL,
    VER_NBR           NUMBER(8,0)  		DEFAULT 1 NOT NULL,
    KIM_TYP_ID 	  	  VARCHAR2(40) 	 	NOT NULL,
    GRP_NMSPC         VARCHAR2(100)  	NOT NULL,
    GRP_NM       	  VARCHAR2(400),
    GRP_DESC       	  VARCHAR2(400),
    ACTV_IND          VARCHAR2(1) 		DEFAULT 'Y',
    CONSTRAINT krim_grp_document_tp1 PRIMARY KEY ( FDOC_NBR )
)
/
CREATE TABLE KRIM_PND_GRP_MBR_T (
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    GRP_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    GRP_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(GRP_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_GRP_ATTR_DATA_T (
    FDOC_NBR          	VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
alter table KRIM_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/
alter table KRIM_PERM_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to PERM_ID
/
alter table KRIM_ROLE_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_PND_ROLE_MBR_ATTR_DATA_MT rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_RSP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to RSP_ID
/
alter table KRIM_PND_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_PND_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/
