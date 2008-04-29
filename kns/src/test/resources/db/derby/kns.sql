CREATE TABLE FP_DOC_STATUS_T(
        FDOC_STATUS_CD                 VARCHAR(2) CONSTRAINT FP_DOC_STATUS_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_DOC_STATUS_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_DOC_STATUS_TN3 NOT NULL,
        FDOC_STATUS_NM                 VARCHAR(10),
     CONSTRAINT FP_DOC_STATUS_TP1 PRIMARY KEY (FDOC_STATUS_CD),
     CONSTRAINT FP_DOC_STATUS_TC0 UNIQUE (OBJ_ID)
)
;

CREATE TABLE SH_NTE_TYP_T(
        NTE_TYP_CD                     VARCHAR(4) CONSTRAINT SH_NTE_TYP_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT SH_NTE_TYP_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT SH_NTE_TYP_TN3 NOT NULL,
        NTE_TYP_DESC                   VARCHAR(100),
        NTE_TYP_ACTV_IND               VARCHAR(1),
     CONSTRAINT SH_NTE_TYP_TP1 PRIMARY KEY (NTE_TYP_CD),
     CONSTRAINT SH_NTE_TYP_TC0 UNIQUE (OBJ_ID)
)
;

CREATE TABLE SH_NTE_T(
        NTE_ID                         BIGINT CONSTRAINT SH_NTE_TN1 NOT NULL, 
        OBJ_ID                         VARCHAR(36)   CONSTRAINT SH_NTE_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT SH_NTE_TN3 NOT NULL,
        RMT_OBJ_ID                     VARCHAR(36) CONSTRAINT SH_NTE_TN4 NOT NULL,
        NTE_AUTH_ID                    VARCHAR(30) CONSTRAINT SH_NTE_TN5 NOT NULL,
        NTE_POST_TS                    DATE CONSTRAINT SH_NTE_TN6 NOT NULL,
        NTE_TYP_CD                     VARCHAR(4) CONSTRAINT SH_NTE_TN7 NOT NULL,
        NTE_TXT                        VARCHAR(800),
        NTE_PRG_CD                     VARCHAR(1),
        NTE_TPC_TXT                    VARCHAR(40),
     CONSTRAINT SH_NTE_TP1 PRIMARY KEY (NTE_ID),
     CONSTRAINT SH_NTE_TC0 UNIQUE (OBJ_ID),
     CONSTRAINT SH_NTE_TC1 UNIQUE (RMT_OBJ_ID, NTE_AUTH_ID, NTE_POST_TS, NTE_TYP_CD)
)
;

ALTER TABLE SH_NTE_T ADD CONSTRAINT SH_NTE_TR1 FOREIGN KEY (NTE_TYP_CD )
  REFERENCES SH_NTE_TYP_T (NTE_TYP_CD )
;

CREATE TABLE SH_USR_PROP_T(
        PERSON_UNVL_ID                 VARCHAR(10) CONSTRAINT SH_USR_PROP_TN1 NOT NULL,
        APPL_MOD_ID                    VARCHAR(20) CONSTRAINT SH_USR_PROP_TN2 NOT NULL,
        USR_PROP_NM                    VARCHAR(40) CONSTRAINT SH_USR_PROP_TN3 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT SH_USR_PROP_TN4 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT SH_USR_PROP_TN5 NOT NULL,
        USR_PROP_VAL                   VARCHAR(4000),
     CONSTRAINT SH_USR_PROP_TP1 PRIMARY KEY (
        PERSON_UNVL_ID,
        APPL_MOD_ID,
        USR_PROP_NM),
     CONSTRAINT SH_USR_PROP_TC0 UNIQUE (OBJ_ID)
)

;

CREATE TABLE FS_UNIVERSAL_USR_T(
        PERSON_UNVL_ID                 VARCHAR(10) CONSTRAINT FS_UNIVERSAL_USR_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_UNIVERSAL_USR_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_UNIVERSAL_USR_TN3 NOT NULL,
        PERSON_USER_ID                 VARCHAR(8) CONSTRAINT FS_UNIVERSAL_USR_TN4 NOT NULL,
        EMPLID                         VARCHAR(11) CONSTRAINT FS_UNIVERSAL_USR_TN5 NOT NULL,
        PERSON_SSN_ID                  VARCHAR(4000),
        FS_ENCRPTD_PWD_TXT             VARCHAR(4000),
        PERSON_NM                      VARCHAR(30),
        PRSN_1ST_NM                    VARCHAR(20),
        PRSN_LST_NM                    VARCHAR(20),
        PRSN_MID_NM					   VARCHAR(50),
        PRSN_EMAIL_ADDR                VARCHAR(100),
        CAMPUS_CD                      VARCHAR(2),
        PRSN_TAX_ID_TYP_CD			   VARCHAR(25),
        DEPTID                         VARCHAR(10),
        PRSN_CMP_ADDR                  VARCHAR(55),
        PRSN_TAX_ID					   VARCHAR(55),
        PRSN_LOC_PHN_NBR               VARCHAR(10),
        EMP_STAT_CD                    VARCHAR(1),
        PRSN_AFLT_IND				   VARCHAR(1),
        PRSN_FAC_IND				   VARCHAR(1),
        PRSN_STAFF_IND				   VARCHAR(1),
        PRSN_STU_IND				   VARCHAR(1),
        EMP_TYPE_CD                    VARCHAR(1),
        EMP_PRM_DEPT_CD				   VARCHAR(25),
        PRSN_PYRL_ID				   VARCHAR(55),
        PRSN_BASE_SLRY_AMT             DOUBLE,
     CONSTRAINT FS_UNIVERSAL_USR_TP1 PRIMARY KEY (
        PERSON_UNVL_ID)  ,
     CONSTRAINT FS_UNIVERSAL_USR_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE INDEX FS_UNIVERSAL_USR_TI1 ON FS_UNIVERSAL_USR_T(
        PRSN_LST_NM, PRSN_1ST_NM )
 
;
CREATE INDEX FS_UNIVERSAL_USR_TI2 ON FS_UNIVERSAL_USR_T(
        PERSON_USER_ID )
 
;
create unique index FS_UNIVERSAL_USR_TC3 on FS_UNIVERSAL_USR_T
( EMPLID )
 
;

CREATE TABLE FP_MAINTENANCE_DOCUMENT_T(
        FDOC_NBR                       VARCHAR(14) CONSTRAINT FP_MAINTENANCE_DOCUMENT_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_MAINTENANCE_DOCUMENT_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_MAINTENANCE_DOCUMENT_TN3 NOT NULL,
        FDOC_EXPLAIN_TXT               VARCHAR(400),
        LOCK_REPRESENTATION_TXT        VARCHAR(4000),
        LOCKED_IND                     VARCHAR(1) DEFAULT 'N' CONSTRAINT FP_MAINTENANCE_DOCUMENT_TN4 NOT NULL,
        DOCUMENT_CONTENTS              CLOB,
     CONSTRAINT FP_MAINTENANCE_DOCUMENT_TP1 PRIMARY KEY (
        FDOC_NBR)  ,
     CONSTRAINT FP_MAINTENANCE_DOCUMENT_TC0 UNIQUE (OBJ_ID)  
)
 
;	 

CREATE TABLE FP_DOC_HEADER_T(
        FDOC_NBR                       VARCHAR(14) CONSTRAINT FP_DOC_HEADER_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_DOC_HEADER_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_DOC_HEADER_TN3 NOT NULL,
        FDOC_STATUS_CD                 VARCHAR(2),
        FDOC_DESC                      VARCHAR(40),
        FDOC_TOTAL_AMT                 DOUBLE,
        ORG_DOC_NBR                    VARCHAR(10),
        FDOC_IN_ERR_NBR                VARCHAR(14),
        FDOC_TMPL_NBR                  VARCHAR(14),
        TEMP_DOC_FNL_DT                DATE, 
        FDOC_EXPLAIN_TXT               VARCHAR(400),
     CONSTRAINT FP_DOC_HEADER_TP1 PRIMARY KEY (
        FDOC_NBR)  ,
     CONSTRAINT FP_DOC_HEADER_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE INDEX FP_DOC_HEADER_TI3 ON FP_DOC_HEADER_T(
        ORG_DOC_NBR )
 
;
CREATE INDEX FP_DOC_HEADER_TI4 ON FP_DOC_HEADER_T(
        FDOC_STATUS_CD )
 
;

CREATE TABLE FP_DOC_TYPE_T(
        FDOC_TYP_CD                    VARCHAR(4) CONSTRAINT FP_DOC_TYPE_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_DOC_TYPE_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_DOC_TYPE_TN3 NOT NULL,
        FDOC_GRP_CD                    VARCHAR(2),
        FDOC_NM                        VARCHAR(40),
        FIN_ELIM_ELGBL_CD              VARCHAR(1),
        FDOC_TYP_ACTIVE_CD             VARCHAR(1),
        FDOC_RTNG_RULE_CD              VARCHAR(1),
        FDOC_AUTOAPRV_DAYS             BIGINT,
        FDOC_BALANCED_CD               VARCHAR(1),
        TRN_SCRBBR_OFST_GEN_IND        CHAR(1),
     CONSTRAINT FP_DOC_TYPE_TP1 PRIMARY KEY (
        FDOC_TYP_CD)  ,
     CONSTRAINT FP_DOC_TYPE_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE INDEX FP_DOC_TYPE_TI2 ON FP_DOC_TYPE_T(
        FDOC_GRP_CD )
 
;

CREATE TABLE FS_PARM_SEC_T(
        FS_SCR_NM                      VARCHAR(255) CONSTRAINT FS_PARM_SEC_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_PARM_SEC_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_PARM_SEC_TN3 NOT NULL,
        WRKGRP_NM                      VARCHAR(70) CONSTRAINT FS_PARM_SEC_TN4 NOT NULL,
        FS_SCR_DESC                    VARCHAR(2000),
     CONSTRAINT FS_PARM_SEC_TP1 PRIMARY KEY (
        FS_SCR_NM)  ,
     CONSTRAINT FS_PARM_SEC_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE TABLE FS_PARM_T(
        FS_SCR_NM                      VARCHAR(255) CONSTRAINT FS_PARM_TN1 NOT NULL,
        FS_PARM_NM                     VARCHAR(255) CONSTRAINT FS_PARM_TN2 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_PARM_TN3 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_PARM_TN4 NOT NULL,
        FS_PARM_TXT                    VARCHAR(4000),
        FS_PARM_DESC                   VARCHAR(2000),
        FS_MULT_VAL_IND                VARCHAR(1) CONSTRAINT FS_PARM_TN5 NOT NULL,
     CONSTRAINT FS_PARM_TP1 PRIMARY KEY (
        FS_SCR_NM,
        FS_PARM_NM)  ,
     CONSTRAINT FS_PARM_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE TABLE FS_BSNS_RULE_SEC_T(
        FS_RULE_GRP_NM                 VARCHAR(255) CONSTRAINT FS_BSNS_RULE_SEC_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_BSNS_RULE_SEC_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_BSNS_RULE_SEC_TN3 NOT NULL,
        WRKGRP_NM                      VARCHAR(70) CONSTRAINT FS_BSNS_RULE_SEC_TN4 NOT NULL,
        FS_RULE_GRP_DESC               VARCHAR(2000),
     CONSTRAINT FS_BSNS_RULE_SEC_TP1 PRIMARY KEY (
        FS_RULE_GRP_NM)  ,
     CONSTRAINT FS_BSNS_RULE_SEC_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE TABLE FS_BSNS_RULE_T(
        FS_RULE_GRP_NM                 VARCHAR(255) CONSTRAINT FS_BSNS_RULE_TN1 NOT NULL,
        FS_RULE_NM                     VARCHAR(255) CONSTRAINT FS_BSNS_RULE_TN2 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_BSNS_RULE_TN3 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_BSNS_RULE_TN4 NOT NULL,
        FS_RULE_TXT                    VARCHAR(4000),
        FS_RULE_DESC                   VARCHAR(2000),
        FS_RULE_OPR_CD                 VARCHAR(1) CONSTRAINT FS_BSNS_RULE_TN5 NOT NULL,
        FS_MULT_VAL_IND                VARCHAR(1) CONSTRAINT FS_BSNS_RULE_TN6 NOT NULL,
        FS_ACTIVE_IND                  VARCHAR(1) CONSTRAINT FS_BSNS_RULE_TN7 NOT NULL,
     CONSTRAINT FS_BSNS_RULE_TP1 PRIMARY KEY (
        FS_RULE_GRP_NM,
        FS_RULE_NM)  ,
     CONSTRAINT FS_BSNS_RULE_TC0 UNIQUE (OBJ_ID)  
)
 
;

CREATE TABLE FS_ADHOC_RTE_ACTN_RECP_T(
	    ACTN_RQST_RECP_TYP_CD          BIGINT CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN1 NOT NULL,
	    ACTN_RQST_CD                   VARCHAR(30) CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN2 NOT NULL,
        ACTN_RQST_RECP_ID              VARCHAR(70) CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN3 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN4 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN5 NOT NULL,
        FDOC_NBR                       VARCHAR(14) CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TN6 NOT NULL,
     CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TP1 PRIMARY KEY (
	    ACTN_RQST_RECP_TYP_CD,
	    ACTN_RQST_CD,
        ACTN_RQST_RECP_ID)  ,
     CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TC0 UNIQUE (OBJ_ID)   
)
 
;

CREATE TABLE FP_DOC_GROUP_T(
        FDOC_GRP_CD                    VARCHAR(2) CONSTRAINT FP_DOC_GROUP_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_DOC_GROUP_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_DOC_GROUP_TN3 NOT NULL,
        FDOC_GRP_NM                    VARCHAR(40),
        FDOC_CLASS_CD                  VARCHAR(2),
     CONSTRAINT FP_DOC_GROUP_TP1 PRIMARY KEY (
        FDOC_GRP_CD) ,
     CONSTRAINT FP_DOC_GROUP_TC0 UNIQUE (OBJ_ID) 
)

;

CREATE TABLE FP_MAINT_LOCK_T(
        LOCK_REPRESENTATION_TXT        VARCHAR(255) CONSTRAINT FP_MAINT_LOCK_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36)   CONSTRAINT FP_MAINT_LOCK_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_MAINT_LOCK_TN3 NOT NULL,
        FDOC_NBR                       VARCHAR(14) CONSTRAINT FP_MAINT_LOCK_TN4 NOT NULL,
     CONSTRAINT FP_MAINT_LOCK_TP1 PRIMARY KEY (LOCK_REPRESENTATION_TXT),
     CONSTRAINT FP_MAINT_LOCK_TC0 UNIQUE (OBJ_ID) 
)

;

CREATE INDEX FP_MAINT_LOCK_TI2 ON FP_MAINT_LOCK_T(
        FDOC_NBR)

;

CREATE INDEX FS_ADHOC_RTE_ACTN_RECP_TI2 ON FS_ADHOC_RTE_ACTN_RECP_T(
        FDOC_NBR )

;

create table EN_UNITTEST_T (
    COL VARCHAR2(1) NULL
)

;

CREATE TABLE EN_MESSAGE_QUE_T (
   MESSAGE_QUE_ID                         BIGINT NOT NULL,
   MESSAGE_QUE_DT             DATE NOT NULL,
   MESSAGE_EXP_DT                         DATE,
   MESSAGE_QUE_PRIO_NBR       BIGINT NOT NULL,
   MESSAGE_QUE_STAT_CD        CHAR(1) NOT NULL,
   MESSAGE_QUE_RTRY_CNT       BIGINT NOT NULL,
   MESSAGE_QUE_IP_NBR         VARCHAR(2000) NOT NULL,
   MESSAGE_PAYLOAD                    CLOB NOT NULL,
   MESSAGE_SERVICE_NM             VARCHAR(255),
   MESSAGE_ENTITY_NM              VARCHAR(10) NOT NULL,
   SERVICE_METHOD_NM              VARCHAR(2000) ,
   DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
   CONSTRAINT EN_MESSAGE_QUE_T_PK PRIMARY KEY (MESSAGE_QUE_ID)
)
;

CREATE TABLE EN_BAM_T (
        BAM_ID                          BIGINT NOT NULL,
        SERVICE_NM                      VARCHAR(255) NOT NULL,
        SERVICE_URL                     VARCHAR(500) NOT NULL,
        METHOD_NM                       VARCHAR(2000) NOT NULL,
        THREAD_NM                       VARCHAR(500) NOT NULL,
        CALL_DT                         DATE NOT NULL,
        TARGET_TO_STRING        VARCHAR(2000) NOT NULL,
        SRVR_IND_IND            SMALLINT NOT NULL,
        EXCEPTION_TO_STRING     VARCHAR(2000),
        EXCEPTION_MSG           CLOB,
        CONSTRAINT EN_BAM_T_PK PRIMARY KEY (BAM_ID)
)
;

CREATE TABLE EN_BAM_PARAM_T (
        BAM_PARAM_ID            BIGINT NOT NULL,
        BAM_ID                          BIGINT NOT NULL,
        PARAM                           CLOB NOT NULL,
        CONSTRAINT EN_BAM_PARAM_T_PK PRIMARY KEY (BAM_PARAM_ID)
)
 ;

 CREATE TABLE EN_SERVICE_DEF_DUEX_T (
        SERVICE_DEF_ID                             BIGINT NOT NULL,
        SERVICE_NM                                         VARCHAR(255)
NOT NULL,
    SERVICE_URL                    VARCHAR(500) NOT NULL,
        SERVER_IP                                          VARCHAR(40)
NOT NULL,
        MESSAGE_ENTITY_NM                          VARCHAR(10) NOT NULL,
        SERVICE_ALIVE                              SMALLINT NOT NULL,
        SERVICE_DEFINITION                     CLOB NOT NULL,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_SERVICE_DEF_DUEX_T_PK PRIMARY KEY (SERVICE_DEF_ID)
)

;

 
 CREATE TABLE EN_ACTN_ITM_T (
        ACTN_ITM_ID                 BIGINT NOT NULL,
        ACTN_ITM_PRSN_EN_ID     VARCHAR(30) NOT NULL,
        ACTN_ITM_ASND_DT        DATE NOT NULL,
        ACTN_ITM_RQST_CD        CHAR(1) NOT NULL,
        ACTN_RQST_ID            BIGINT NOT NULL,
        DOC_HDR_ID              BIGINT NOT NULL,
        WRKGRP_ID               BIGINT,
        ROLE_NM                                 VARCHAR(2000) ,
        ACTN_ITM_DLGN_PRSN_EN_ID VARCHAR(30) ,
    ACTN_ITM_DLGN_WRKGRP_ID BIGINT,
        DOC_TTL                         VARCHAR(255) ,
        DOC_TYP_LBL_TXT         VARCHAR(255) NOT NULL,
        DOC_TYP_HDLR_URL_ADDR   VARCHAR(255) NOT NULL,
        DOC_TYP_NM                      VARCHAR(255) NOT NULL,
        ACTN_ITM_RESP_ID        BIGINT NOT NULL,
        DLGN_TYP                                VARCHAR(1) ,
        DB_LOCK_VER_NBR         BIGINT DEFAULT 0,
        CONSTRAINT EN_ACTN_ITM_T_PK PRIMARY KEY (ACTN_ITM_ID) 
)

;


CREATE TABLE EN_ACTN_RQST_T (
       ACTN_RQST_ID                   BIGINT NOT NULL,
       ACTN_RQST_PARNT_ID                         BIGINT ,
       ACTN_RQST_CD                   CHAR(1) NOT NULL,
       DOC_HDR_ID                     BIGINT NOT NULL,
       RULE_BASE_VALUES_ID            BIGINT ,
       ACTN_RQST_STAT_CD              CHAR(1) NOT NULL,
       ACTN_RQST_RESP_ID              BIGINT NOT NULL,
       ACTN_RQST_PRSN_EN_ID           VARCHAR(30) ,
       WRKGRP_ID                      BIGINT,
       ROLE_NM                                            VARCHAR(2000),
       QUAL_ROLE_NM                               VARCHAR(2000) ,
       QUAL_ROLE_NM_LBL_TXT                       VARCHAR(2000) ,
       ACTN_RQST_RECP_TYP_CD          CHAR(1) ,
       ACTN_RQST_PRIO_NBR             BIGINT NOT NULL,
       ACTN_RQST_RTE_TYP_NM           VARCHAR(255) ,
       ACTN_RQST_RTE_LVL_NBR          BIGINT NOT NULL,
       ACTN_RQST_RTE_NODE_INSTN_ID        BIGINT,
       ACTN_TKN_ID                        BIGINT,
       DOC_VER_NBR                    BIGINT NOT NULL,
       ACTN_RQST_CRTE_DT              DATE NOT NULL,
       ACTN_RQST_RESP_DESC            VARCHAR(200) ,
       ACTN_RQST_IGN_PREV_ACTN_IND    SMALLINT DEFAULT 0,
       ACTN_RQST_ANNOTN_TXT               VARCHAR(2000) ,
           DLGN_TYP
CHAR(1) ,
       ACTN_RQST_APPR_PLCY            CHAR(1) ,
       ACTN_RQST_CUR_IND              SMALLINT DEFAULT 1,
       DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
       CONSTRAINT EN_ACTN_RQST_TEMP_T_PK PRIMARY KEY (ACTN_RQST_ID)
)
;



CREATE TABLE EN_ACTN_TKN_T (
       ACTN_TKN_ID                   BIGINT NOT NULL,
       DOC_HDR_ID                    BIGINT NOT NULL,
       ACTN_TKN_PRSN_EN_ID           VARCHAR(30) NOT NULL,
       ACTN_TKN_DLGTR_PRSN_EN_ID     VARCHAR(30) ,
       ACTN_TKN_DLGTR_WRKGRP_ID          BIGINT,
       ACTN_TKN_CD                   CHAR(1) NOT NULL,
       ACTN_TKN_DT                   DATE NOT NULL,
       DOC_VER_NBR                   BIGINT NOT NULL,
       ACTN_TKN_ANNOTN_TXT           VARCHAR(2000) ,
       ACTN_TKN_CUR_IND              SMALLINT DEFAULT 1,
       DB_LOCK_VER_NBR               BIGINT DEFAULT 0,
       CONSTRAINT EN_ACTN_TKN_T_PK PRIMARY KEY (ACTN_TKN_ID)
)
;


CREATE TABLE EN_DOC_HDR_T (
        DOC_HDR_ID                          BIGINT NOT NULL,
        DOC_TYP_ID                          BIGINT ,
        DOC_RTE_STAT_CD                 CHAR(1) NOT NULL,
        DOC_RTE_LVL_NBR                 BIGINT NOT NULL,
        DOC_STAT_MDFN_DT                DATE NOT NULL,
        DOC_CRTE_DT                         DATE NOT NULL,
        DOC_APRV_DT                         DATE ,
        DOC_FNL_DT                          DATE ,
        DOC_RTE_STAT_MDFN_DT    DATE ,
        DOC_RTE_LVL_MDFN_DT             DATE ,
        DOC_TTL                             VARCHAR(255) ,
        DOC_APPL_DOC_ID                 VARCHAR(20) ,
        DOC_VER_NBR                         BIGINT NOT NULL,
        DOC_INITR_PRSN_EN_ID    VARCHAR(30) NOT NULL,
        DOC_OVRD_IND                    SMALLINT DEFAULT 0,
        DOC_LOCK_CD                             CHAR(1) ,
        DB_LOCK_VER_NBR                 BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_HDR_T_PK PRIMARY KEY (DOC_HDR_ID)
)
;

CREATE TABLE EN_DOC_HDR_CNTNT_T (  
        DOC_HDR_ID                          INT NOT NULL,
        DOC_CNTNT_TXT                   CLOB,
        CONSTRAINT EN_DOC_HDR_T_CNTNT_PK PRIMARY KEY (DOC_HDR_ID)
)
;


CREATE TABLE EN_DOC_HDR_EXT_T (
        DOC_HDR_EXT_ID          BIGINT NOT NULL,
        DOC_HDR_ID                          BIGINT NOT NULL,
        DOC_HDR_EXT_VAL_KEY             VARCHAR(256) NOT NULL,
        DOC_HDR_EXT_VAL                 VARCHAR(2000) NOT NULL,
        CONSTRAINT EN_DOC_HDR_EXT_T_PK PRIMARY KEY (DOC_HDR_EXT_ID)
)
 ;



CREATE TABLE EN_DOC_TYP_T (
        DOC_TYP_ID                        BIGINT NOT NULL,
        DOC_TYP_PARNT_ID                  BIGINT,
        DOC_TYP_NM                        VARCHAR(255),
        DOC_TYP_VER_NBR                   BIGINT DEFAULT 0,
        DOC_TYP_ACTV_IND                  SMALLINT,
        DOC_TYP_CUR_IND                   SMALLINT,
        DOC_TYP_LBL_TXT                   VARCHAR(255),
        DOC_TYP_PREV_VER                          BIGINT,
        DOC_HDR_ID                        BIGINT,
        DOC_TYP_DESC                      VARCHAR(255),
        DOC_TYP_HDLR_URL_ADDR         VARCHAR(255),
        DOC_TYP_POST_PRCSR_NM         VARCHAR(255),
        DOC_TYP_JNDI_URL_ADDR         VARCHAR(255),
    WRKGRP_ID                     BIGINT,
    BLNKT_APPR_WRKGRP_ID          BIGINT,
    BLNKT_APPR_PLCY               VARCHAR(10),
        ADV_DOC_SRCH_URL_ADDR         VARCHAR(255),
    CSTM_ACTN_LIST_ATTRIB_CLS_NM  VARCHAR(255),
    CSTM_ACTN_EMAIL_ATTRIB_CLS_NM VARCHAR(255),
    CSTM_DOC_NTE_ATTRIB_CLS_NM    VARCHAR(255),
    DOC_TYP_RTE_VER_NBR                   VARCHAR(2) DEFAULT '1' NOT
NULL,
    DOC_TYP_NOTIFY_ADDR                   VARCHAR(255),
    DOC_TYP_EMAIL_XSL                     VARCHAR(255),
    MESSAGE_ENTITY_NM                     VARCHAR(10),
        DB_LOCK_VER_NBR               BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_TYP_T_PK PRIMARY KEY (DOC_TYP_ID)
)
 ;


CREATE TABLE EN_DOC_TYP_ATTRIB_T (
        DOC_TYP_ATTRIB_ID               BIGINT NOT NULL,
        DOC_TYP_ID                      BIGINT NOT NULL,
        RULE_ATTRIB_ID                          BIGINT NOT NULL,
        CONSTRAINT EN_DOC_TYP_ATTRIB_T_PK PRIMARY KEY
(DOC_TYP_ATTRIB_ID)
)
;


CREATE TABLE EN_DOC_TYP_RTE_LVL_T (
        DOC_RTE_LVL_ID                 BIGINT NOT NULL,
        DOC_TYP_ID                     BIGINT,
        DOC_RTE_LVL_NM                 VARCHAR(255),
        DOC_RTE_LVL_PRIO_NBR           BIGINT,
        DOC_RTE_MTHD_NM                VARCHAR(255) NOT NULL,
        DOC_FNL_APRVR_IND              SMALLINT,
        DOC_MNDTRY_RTE_IND             SMALLINT,
        WRKGRP_ID                      BIGINT,
        DOC_RTE_MTHD_CD                VARCHAR(2),
    DOC_ACTVN_TYP_TXT              VARCHAR(250),
    DOC_RTE_LVL_IGN_PREV_ACTN_IND  SMALLINT DEFAULT 0,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_TYP_RTE_LVL_T_PK PRIMARY KEY (DOC_RTE_LVL_ID)
)
 ;


CREATE TABLE EN_DOC_TYP_PLCY_RELN_T (
        DOC_TYP_ID            BIGINT NOT NULL,
        DOC_PLCY_NM           VARCHAR(255) NOT NULL ,
        DOC_PLCY_VAL          SMALLINT NOT NULL ,
        DB_LOCK_VER_NBR       BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_TYP_PLCY_RELN_T_PK PRIMARY KEY (DOC_TYP_ID,
DOC_PLCY_NM)
)
;



CREATE TABLE EN_ORG_RESP_ID_T (
   ORG_CD                      VARCHAR(4) NOT NULL,
   FIN_COA_CD                  VARCHAR(2) NOT NULL,
   ORG_RESP_ID                 BIGINT NOT NULL,
   ORG_RESP_ID_APRVR_TYP_CD    CHAR(1) NOT NULL,
   DB_LOCK_VER_NBR                 BIGINT DEFAULT 0,
   CONSTRAINT EN_ORG_RESP_ID_T_PK PRIMARY KEY (org_cd, fin_coa_cd,
ORG_RESP_ID_APRVR_TYP_CD)
)
;


CREATE TABLE EN_DOC_RTE_TYP_T (
   DOC_RTE_TYP_NM                 VARCHAR(255) NOT NULL,
   DOC_RTE_TYP_LBL_TXT            VARCHAR(250),
   DOC_RTE_MOD_NM                 VARCHAR(250),
   DOC_RTE_TYP_DESC               VARCHAR(250),
   DOC_RTE_TYP_ACTV_IND           SMALLINT DEFAULT 0 NOT NULL,
   DOC_RTE_MOD_JNDI_FTRY_CLS_NM   VARCHAR(200),
   DOC_RTE_MOD_JNDI_URL_ADDR      VARCHAR(200),
   DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
   CONSTRAINT EN_DOC_RTE_TYP_TEMP_T_PK PRIMARY KEY (DOC_RTE_TYP_NM)
)
 ;


CREATE TABLE EN_WRKGRP_T (
   WRKGRP_ID            BIGINT NOT NULL,
   WRKGRP_VER_NBR       BIGINT DEFAULT 0,
   WRKGRP_NM            VARCHAR(70) NOT NULL,
   WRKGRP_ACTV_IND      SMALLINT NOT NULL,
   WRKGRP_TYP_CD        CHAR(1) NOT NULL,
   WRKGRP_DESC          VARCHAR(2000) ,
   WRKGRP_CUR_IND       SMALLINT DEFAULT 0,
   DOC_HDR_ID           BIGINT,
   DB_LOCK_VER_NBR      BIGINT DEFAULT 0,
   CONSTRAINT EN_WRKGRP_T_PK PRIMARY KEY (WRKGRP_ID, WRKGRP_VER_NBR)
)
;



CREATE TABLE EN_WRKGRP_MBR_T (
    WRKGRP_MBR_PRSN_EN_ID     VARCHAR(30) NOT NULL,
    WRKGRP_ID                 BIGINT NOT NULL,
    WRKGRP_VER_NBR            BIGINT DEFAULT 0,
    DB_LOCK_VER_NBR           BIGINT DEFAULT 0,
    CONSTRAINT EN_WRKGRP_MBR_T_PK PRIMARY KEY (WRKGRP_MBR_PRSN_EN_ID,
WRKGRP_ID, WRKGRP_VER_NBR)
)
;



CREATE TABLE EN_USR_OPTN_T (
   PRSN_EN_ID       VARCHAR(30) ,
   PRSN_OPTN_ID     VARCHAR(200) NOT NULL,
   PRSN_OPTN_VAL    VARCHAR(2000) ,
   DB_LOCK_VER_NBR  BIGINT DEFAULT 0,
   CONSTRAINT EN_USR_OPTN_T_PK PRIMARY KEY (PRSN_EN_ID, PRSN_OPTN_ID)
)
;



CREATE TABLE EN_APPL_CNST_T (
   APPL_CNST_NM          VARCHAR(100) NOT NULL,
   APPL_CNST_VAL_TXT     VARCHAR(2000) ,
   DB_LOCK_VER_NBR       NUMERIC(8) DEFAULT 0,
   CONSTRAINT EN_APPL_CNST_T_PK PRIMARY KEY (APPL_CNST_NM)
)
 ;


create table EN_USR_T (
            PRSN_EN_ID                       VARCHAR(30) NOT NULL,
            PRSN_UNIV_ID                     VARCHAR(11) NOT NULL,
        PRSN_NTWRK_ID                    VARCHAR(30),
        PRSN_UNVL_USR_ID                 VARCHAR(10),
        PRSN_EMAIL_ADDR                  VARCHAR(255),
        PRSN_NM                          VARCHAR(255),
        PRSN_GVN_NM
VARCHAR(255),
        PRSN_LST_NM                      VARCHAR(255),
        USR_CRTE_DT                      DATE,
        USR_LST_UPDT_DT                  DATE,
        PRSN_ID_MSNG_IND                 SMALLINT DEFAULT 0,
            DB_LOCK_VER_NBR                      BIGINT DEFAULT 0,
  CONSTRAINT EN_USR_T_PK PRIMARY KEY (PRSN_EN_ID)
)
 ;

CREATE TABLE EN_RULE_TMPL_T (
        RULE_TMPL_ID BIGINT NOT NULL,
        RULE_TMPL_NM VARCHAR(250) NOT NULL,
        RULE_TMPL_DESC VARCHAR(2000) ,
        DLGN_RULE_TMPL_ID BIGINT ,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_TMPL_T_PK PRIMARY KEY (RULE_TMPL_ID)
)
 ;

CREATE TABLE EN_RULE_TMPL_OPTN_T (
        RULE_TMPL_OPTN_ID BIGINT NOT NULL,
        RULE_TMPL_ID BIGINT ,
        RULE_TMPL_OPTN_KEY VARCHAR(250) ,
        RULE_TMPL_OPTN_VAL VARCHAR(2000) ,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_TMPL_OPTN_T_PK PRIMARY KEY
(RULE_TMPL_OPTN_ID)
)
 ;

CREATE TABLE EN_RULE_ATTRIB_T (
        RULE_ATTRIB_ID BIGINT NOT NULL,
        RULE_ATTRIB_NM VARCHAR(255) NOT NULL,
        RULE_ATTRIB_LBL_TXT VARCHAR(2000) NOT NULL,
        RULE_ATTRIB_TYP VARCHAR(2000) NOT NULL,
        RULE_ATTRIB_DESC VARCHAR(2000) ,
        RULE_ATTRIB_CLS_NM VARCHAR(2000) ,
        RULE_ATTRIB_XML_RTE_TXT CLOB ,
        MESSAGE_ENTITY_NM VARCHAR(10) ,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_ATTRIB_PK PRIMARY KEY (RULE_ATTRIB_ID)
)
 ;

CREATE TABLE EN_RULE_TMPL_ATTRIB_T (
        RULE_TMPL_ATTRIB_ID BIGINT NOT NULL,
        RULE_TMPL_ID BIGINT NOT NULL,
        RULE_ATTRIB_ID BIGINT NOT NULL,
        REQ_IND SMALLINT NOT NULL,
        DSPL_ORD BIGINT NOT NULL,
        DFLT_VAL VARCHAR(2000),
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_TMPL_ATTRIB_PK PRIMARY KEY
(RULE_TMPL_ATTRIB_ID)
)
;

CREATE TABLE EN_RULE_ATTRIB_VLD_VAL_T (
        RULE_ATTRIB_VLD_VAL_ID BIGINT NOT NULL,
        RULE_ATTRIB_VLD_VAL_NM VARCHAR(2000) NOT NULL,
        RULE_ATTRIB_VLD_VAL_LBL_TXT VARCHAR(2000) NOT NULL,
        RULE_ATTRIB_ID BIGINT NOT NULL,
        RULE_ATTRIB_VLD_VAL_CUR_IND SMALLINT DEFAULT 0,
    RULE_ATTRIB_VLD_VAL_VER_NBR BIGINT DEFAULT 0,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_ATTRIB_VLD_VAL_PK PRIMARY KEY
(RULE_ATTRIB_VLD_VAL_ID)
)
 ;

CREATE TABLE EN_RULE_ATTRIB_KEY_VAL_T (
        RULE_ATTRIB_KEY_VAL_ID BIGINT NOT NULL,
        RULE_ATTRIB_ID BIGINT NOT NULL,
        RULE_ATTRIB_KEY VARCHAR(2000) NOT NULL,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_ATTRIB_KEY_VAL_T PRIMARY KEY
(RULE_ATTRIB_KEY_VAL_ID)
)
 ;

CREATE TABLE EN_RULE_BASE_VAL_T (
        RULE_BASE_VAL_ID BIGINT NOT NULL,
        RULE_TMPL_ID BIGINT NOT NULL,
        RULE_BASE_VAL_ACTV_IND SMALLINT NOT NULL,
        RULE_BASE_VAL_DESC VARCHAR(2000) ,
        RULE_BASE_VAL_IGNR_PRVS SMALLINT NOT NULL,
        DOC_TYP_NM VARCHAR(2000) NOT NULL,
        DOC_HDR_ID BIGINT,
        TMPL_RULE_IND SMALLINT,
        RULE_BASE_VAL_FRM_DT   DATE NOT NULL,
  RULE_BASE_VAL_TO_DT    DATE NOT NULL,
  RULE_BASE_VAL_DACTVN_DT DATE,
  RULE_BASE_VAL_CUR_IND SMALLINT DEFAULT 0,
  RULE_BASE_VAL_VER_NBR BIGINT DEFAULT 0,
  RULE_BASE_VAL_DLGN_IND SMALLINT,
  RULE_BASE_VAL_PREV_VER BIGINT,
  RULE_BASE_VAL_ACTVN_DT DATE,
  DB_LOCK_VER_NBR       BIGINT DEFAULT 0,
  CONSTRAINT EN_RULE_BASE_VAL_PK PRIMARY KEY (RULE_BASE_VAL_ID)
)
 ;

CREATE TABLE EN_DLGN_RSP_T (
        DLGN_RULE_ID BIGINT NOT NULL,
        RULE_RSP_ID BIGINT NOT NULL,
        DLGN_RULE_BASE_VAL_ID BIGINT NOT NULL,
        DLGN_TYP VARCHAR(20) NOT NULL,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_DLGN_RSP_PK PRIMARY KEY (DLGN_RULE_ID)
)
 ;

CREATE TABLE EN_RULE_RSP_T (
        RULE_RSP_ID BIGINT NOT NULL,
        RSP_ID BIGINT NOT NULL,
        RULE_BASE_VAL_ID BIGINT NOT NULL,
        RULE_RSP_PRIO_NBR BIGINT ,
        ACTION_RQST_CD VARCHAR(2000) ,
        RULE_RSP_NM VARCHAR(200) ,
        RULE_RSP_TYP VARCHAR(1) ,
    RULE_RSP_APPR_PLCY CHAR(1) ,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_RSP_PK PRIMARY KEY (RULE_RSP_ID)
)
 ;

CREATE TABLE EN_RULE_EXT_T (
        RULE_EXT_ID BIGINT NOT NULL,
        RULE_TMPL_ATTRIB_ID BIGINT NOT NULL,
        RULE_BASE_VAL_ID BIGINT NOT NULL,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_EXT_PK PRIMARY KEY (RULE_EXT_ID)
)
 ;

CREATE TABLE EN_RULE_EXT_VAL_T (
        RULE_EXT_VAL_ID BIGINT NOT NULL,
        RULE_EXT_ID BIGINT NOT NULL,
        RULE_EXT_VAL VARCHAR(2000) NOT NULL,
        RULE_EXT_VAL_KEY VARCHAR(2000) NOT NULL,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_RULE_EXT_VAL_PK PRIMARY KEY (RULE_EXT_VAL_ID) 
)
 ;

CREATE TABLE EN_TRANSACTION_TST_PRSN_T (
        PRSN_ID BIGINT NOT NULL,
        FRST_NM VARCHAR(2000) NOT NULL,
        LST_NM VARCHAR(2000) NOT NULL,
        EMAIL VARCHAR(2000),
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_TRANSACTION_TST_PRSN_T_PK PRIMARY KEY (PRSN_ID) 
)
 ;

CREATE TABLE EN_TRANSACTION_TST_ADDRS_T (
        ADDRS_ID BIGINT NOT NULL,
        STREET VARCHAR(2000),
        CITY VARCHAR(2000),
        STATE VARCHAR(2),
        ZIP BIGINT,
        DB_LOCK_VER_NBR BIGINT DEFAULT 0,
        CONSTRAINT EN_TRANSACTION_TST_ADDRS_T_PK PRIMARY KEY (ADDRS_ID)

)
;

CREATE TABLE EN_DOC_NTE_T (
        DOC_NTE_ID                BIGINT NOT NULL,
        DOC_HDR_ID                BIGINT NOT NULL,
        DOC_NTE_AUTH_PRSN_EN_ID   VARCHAR(30) NOT NULL,
        DOC_NTE_CRT_DT            DATE NOT NULL,
    DOC_NTE_TXT               VARCHAR(4000),
        DB_LOCK_VER_NBR           BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_NTE_T_PK PRIMARY KEY (DOC_NTE_ID) 
)
 ;

CREATE TABLE EN_HLP_T (
        EN_HLP_ID                       BIGINT NOT NULL,
        EN_HLP_NM               VARCHAR(500) NOT NULL,
        EN_HLP_KY                       VARCHAR(500) NOT NULL,
        EN_HLP_TXT              VARCHAR(4000) NOT NULL,
        DB_LOCK_VER_NBR     BIGINT DEFAULT 0,
        CONSTRAINT EN_HLP_T_PK PRIMARY KEY (EN_HLP_ID) 
)
 ;

CREATE TABLE EN_DOC_TYP_PROC_T (
        DOC_TYP_PROC_ID                            BIGINT NOT NULL,
        DOC_TYP_ID                         BIGINT NOT NULL,
        INIT_RTE_NODE_ID               BIGINT NOT NULL,
        PROC_NM                                            VARCHAR(255)
NOT NULL,
        INIT_IND                                           SMALLINT
DEFAULT 0 NOT NULL,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_DOC_TYP_PROC_T_PK PRIMARY KEY (DOC_TYP_PROC_ID)
)
;

CREATE TABLE EN_RTE_NODE_T (
        RTE_NODE_ID                    BIGINT NOT NULL,
        DOC_TYP_ID                     BIGINT,
        RTE_NODE_NM                    VARCHAR(255) NOT NULL,
        RTE_NODE_TYP                               VARCHAR(255) NOT
NULL,
        DOC_RTE_MTHD_NM                VARCHAR(255),
        DOC_RTE_MTHD_CD                VARCHAR(2),
        DOC_FNL_APRVR_IND              SMALLINT,
        DOC_MNDTRY_RTE_IND             SMALLINT,
        WRKGRP_ID                      BIGINT,
    DOC_ACTVN_TYP_TXT              VARCHAR(250),
    BRCH_PROTO_ID                                  BIGINT,
    CONTENT_FRAGMENT               VARCHAR(4000),
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_NODE_T_PK PRIMARY KEY (RTE_NODE_ID)
)
;

CREATE TABLE EN_RTE_NODE_LNK_T (
        FROM_RTE_NODE_ID                   BIGINT NOT NULL,
        TO_RTE_NODE_ID                     BIGINT NOT NULL,
        CONSTRAINT EN_RTE_NODE_LNK_T_PK PRIMARY KEY (FROM_RTE_NODE_ID,
TO_RTE_NODE_ID)
)
 ;

CREATE TABLE EN_RTE_BRCH_PROTO_T (
    RTE_BRCH_PROTO_ID                      BIGINT NOT NULL,
    RTE_BRCH_PROTO_NM                      VARCHAR(255) NOT NULL,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_BRCH_PROTO_T_PK PRIMARY KEY
(RTE_BRCH_PROTO_ID)
)
;

CREATE TABLE EN_INIT_RTE_NODE_INSTN_T (
        DOC_HDR_ID                              BIGINT NOT NULL,
        RTE_NODE_INSTN_ID               BIGINT NOT NULL,
        CONSTRAINT EN_INIT_RTE_NODE_INSTN_T_PK PRIMARY KEY (DOC_HDR_ID,
RTE_NODE_INSTN_ID)
)
;


CREATE TABLE EN_RTE_NODE_INSTN_T (
        RTE_NODE_INSTN_ID              BIGINT NOT NULL,
        DOC_ID                                             BIGINT NOT
NULL,
        RTE_NODE_ID                    BIGINT NOT NULL,
        BRCH_ID                                            BIGINT,
        PROC_RTE_NODE_INSTN_ID             BIGINT,
        ACTV_IND                                           SMALLINT
DEFAULT 0 NOT NULL,
    CMPLT_IND                                      SMALLINT DEFAULT 0
NOT NULL,
    INIT_IND                                       SMALLINT DEFAULT 0
NOT NULL,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_NODE_INSTN_T_PK PRIMARY KEY
(RTE_NODE_INSTN_ID)
)
 ;

CREATE TABLE EN_RTE_NODE_INSTN_LNK_T (
        FROM_RTE_NODE_INSTN_ID             BIGINT NOT NULL,
        TO_RTE_NODE_INSTN_ID               BIGINT NOT NULL,
        CONSTRAINT EN_RTE_NODE_INSTN_LNK_T_PK PRIMARY KEY
(FROM_RTE_NODE_INSTN_ID, TO_RTE_NODE_INSTN_ID)
)
  ;

CREATE TABLE EN_RTE_BRCH_T (
    RTE_BRCH_ID                                    BIGINT NOT NULL,
    BRCH_NM                                                VARCHAR(255)
NOT NULL,
    PARNT_RTE_BRCH_ID                      BIGINT,
    INIT_RTE_NODE_INSTN_ID                 BIGINT,
    SPLT_RTE_NODE_INSTN_ID                 BIGINT,
    JOIN_RTE_NODE_INSTN_ID                 BIGINT,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_BRCH_T_PK PRIMARY KEY (RTE_BRCH_ID)
)
 ;

CREATE TABLE EN_RTE_BRCH_ST_T (
        RTE_BRCH_ST_ID                             BIGINT NOT NULL,
    RTE_BRCH_ID                                    BIGINT NOT NULL,
        ST_KEY                                             VARCHAR(255)
NOT NULL,
        ST_VAL_TXT
VARCHAR(2000),
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_BRCH_ST_T_PK PRIMARY KEY (RTE_BRCH_ST_ID)
)
  ;

CREATE TABLE EN_RTE_NODE_INSTN_ST_T (
        RTE_NODE_INSTN_ST_ID               BIGINT NOT NULL,
    RTE_NODE_INSTN_ID                      BIGINT NOT NULL,
        ST_KEY                                             VARCHAR(255)
NOT NULL,
        ST_VAL_TXT
VARCHAR(2000),
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_RTE_NODE_INSTN_ST_T_PK PRIMARY KEY
(RTE_NODE_INSTN_ST_ID)
)
  ;

create table en_edoclt_style_t (
  edoclt_style_id BIGINT not null,
  edoclt_style_nm varchar(200) not null,
  edoclt_style_xml clob not null,
  edoclt_style_actv_ind SMALLINT not null,
  db_lock_ver_nbr BIGINT default 0,
  CONSTRAINT en_edoclt_style_t PRIMARY KEY (edoclt_style_id)
)
  ;

create table en_edoclt_def_t (
  edoclt_def_id BIGINT not null,
  edoclt_def_nm varchar(200) not null,
  edoclt_def_xml clob not null,
  edoclt_def_actv_ind SMALLINT not null,
  db_lock_ver_nbr BIGINT default 0,
  CONSTRAINT en_edoclt_def_t PRIMARY KEY (edoclt_def_id)
)
  ;

create table en_edoclt_assoc_t (
  edoclt_assoc_id BIGINT not null,
  edoclt_assoc_doctype_nm varchar(200) not null,
  edoclt_assoc_def_nm varchar(200),
  edoclt_assoc_style_nm varchar(200),
  edoclt_assoc_actv_ind SMALLINT not null,
  db_lock_ver_nbr BIGINT default 0,
  CONSTRAINT en_edoclt_assoc_t PRIMARY KEY (edoclt_assoc_id)
)
  ;

CREATE TABLE EN_ATTACHMENT_T (
        ATTACHMENT_ID                              BIGINT NOT NULL,
        NTE_ID                             BIGINT NOT NULL,
        FILE_NM                                VARCHAR(255) NOT NULL,
        FILE_LOC                                           VARCHAR(255) NOT NULL,
        MIME_TYP                                           VARCHAR(255) NOT NULL,
        DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
        CONSTRAINT EN_ATTACHMENT_T_PK PRIMARY KEY (ATTACHMENT_ID)
)
 ;

create table trv_doc_2 (
        FDOC_NBR                       VARCHAR(14) CONSTRAINT FP_INT_BILL_DOC_TN1 NOT NULL,
        OBJ_ID                         VARCHAR(36) CONSTRAINT FP_INT_BILL_DOC_TN2 NOT NULL,
        VER_NBR                        BIGINT DEFAULT 1 CONSTRAINT FP_INT_BILL_DOC_TN3 NOT NULL,
        FDOC_EXPLAIN_TXT               VARCHAR(400),
	    request_trav varchar(30) not null,
	    traveler          varchar(200),
        org          varchar(60),
        dest         varchar(60),
	    CONSTRAINT trv_doc_2P1 PRIMARY KEY (FDOC_NBR) 
)

;

create table trv_acct (
    acct_num  varchar(10) not null,
    acct_name varchar(50),
    acct_type varchar(100),
    acct_fo_id BIGINT,
    constraint trv_acct_pk primary key(acct_num)
) 

;

create table trv_acct_ext (
    acct_num  varchar2(10) not null,
    acct_type varchar2(100),
    constraint trv_acct_ext_pk primary key(acct_num)
) 

;

create table trv_acct_type (
    acct_type  varchar2(3) not null,
    acct_type_name varchar2(50),
    constraint trv_acct_type_pk primary key(acct_type)
) 

;

create table trv_doc_acct (
    doc_hdr_id  BIGINT not null,
    acct_num    varchar(10) not null,
    constraint trv_doc_acct_pk primary key(doc_hdr_id, acct_num)
) 

;

create table trv_acct_fo (
	acct_fo_id  BIGINT not null,
	acct_fo_user_name varchar(50) not null,
	constraint trv_acct_fo_id_pk primary key(acct_fo_id)
)
 
;

create table TRAV_DOC_2_ACCOUNTS (
    FDOC_NBR VARCHAR(14),
    ACCT_NUM varchar(10),
    CONSTRAINT TRAV_DOC_2_ACCOUNTS_P1 PRIMARY KEY (FDOC_NBR, ACCT_NUM)
)


;

 
