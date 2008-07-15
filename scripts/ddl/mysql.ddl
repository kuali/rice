CREATE TABLE OJB_NEXTVAL_SEQ
(
    SEQ_NAME    VARCHAR(150) NOT NULL,
    MAX_KEY     BIGINT,
    PRIMARY KEY(SEQ_NAME)
)
;
CREATE TABLE FP_DOC_TYPE_ATTR_ID_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE LOCK_ID_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_attribute_types_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_entity_attribs_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_entity_types_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_entitys_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_group_attributes_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_group_qlfd_roles_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_group_types_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_groups_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_grp_typ_dflt_attr_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_namespaces_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_nmspce_dflt_attr_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_permissions_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_principals_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_prncpl_qlfd_roles_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_role_attributes_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_kim_roles_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_searchable_attribute_value (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_acct_fo_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_route_queue (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_route_template (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_document_route_header (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_rte_node (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_rte_node_cfg_parm (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_en_rte_node_lnk (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_responsibility_id (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_init_rte_node_instn (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_action_taken (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_action_request (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE SEQ_ACTION_LIST_OPTN (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_actn_itm (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_en_out_box_itm (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE nte_id_seq (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_out_box_itm (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE KCB_MESSAGES_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE KCB_MSG_DELIVS_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE KCB_RECIP_PREFS_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE KCB_RECIP_DELIVS_SEQ (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_help_entry (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;
CREATE TABLE seq_document_type_attribute (
  a INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (a)
) AUTO_INCREMENT=1000, ENGINE=MyISAM
;

CREATE TABLE KR_QRTZ_TEXT_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    TEXT_DATA mediumtext NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP)
)
;
CREATE TABLE KR_QRTZ_CALENDARS (
    CALENDAR_NAME  VARCHAR(80) NOT NULL,
    CALENDAR mediumtext NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
)
;
CREATE TABLE KR_QRTZ_CRON_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    CRON_EXPRESSION VARCHAR(80) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP)
)
;
CREATE TABLE KR_QRTZ_FIRED_TRIGGERS (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    PRIORITY BIGINT NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(80) NULL,
    JOB_GROUP VARCHAR(80) NULL,
    IS_STATEFUL VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (ENTRY_ID)
)
;
CREATE TABLE KR_QRTZ_JOB_DETAILS (
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    JOB_CLASS_NAME   VARCHAR(128) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    IS_STATEFUL VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA mediumtext NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
)
;
CREATE TABLE KR_QRTZ_JOB_LISTENERS (
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    JOB_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER)
)
;
CREATE TABLE KR_QRTZ_LOCKS (
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (LOCK_NAME)
)
;
CREATE TABLE KR_QRTZ_PAUSED_TRIGGERS_GRPS (
    TRIGGER_GROUP  VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_GROUP)
)
;
CREATE TABLE KR_QRTZ_SCHEDULER_STATE (
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
)
;
CREATE TABLE KR_QRTZ_SIMPLE_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP)
)
;
CREATE TABLE KR_QRTZ_TRIGGER_LISTENERS (
    TRIGGER_NAME  VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    TRIGGER_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER)
)
;
CREATE TABLE KR_QRTZ_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY BIGINT NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(80) NULL,
    MISFIRE_INSTR BIGINT NULL,
    JOB_DATA mediumtext NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP)
)
;
CREATE TABLE EN_OUT_BOX_ITM_T (
    ACTN_ITM_ID             int(14) NOT NULL,
    ACTN_ITM_PRSN_EN_ID     VARCHAR(30) NOT NULL,
    ACTN_ITM_ASND_DT        DATE NOT NULL,
    ACTN_ITM_RQST_CD        CHAR(1) NOT NULL,
    ACTN_RQST_ID            int(14) NOT NULL,
    DOC_HDR_ID              int(14) NOT NULL,
    WRKGRP_ID               int(14) NULL,
    ROLE_NM                 VARCHAR(2000) NULL,
    ACTN_ITM_DLGN_PRSN_EN_ID VARCHAR(30) NULL,
    ACTN_ITM_DLGN_WRKGRP_ID int(14) NULL,
    DOC_TTL                 VARCHAR(255) NULL,
    DOC_TYP_LBL_TXT         VARCHAR(255) NOT NULL,
    DOC_TYP_HDLR_URL_ADDR   VARCHAR(255) NOT NULL,
    DOC_TYP_NM              VARCHAR(255) NOT NULL,
    ACTN_ITM_RESP_ID        int(14) NOT NULL,
    DLGN_TYP                VARCHAR(1) NULL,
    DB_LOCK_VER_NBR         int(8) null,
    PRIMARY KEY (ACTN_ITM_ID)
)
;
create table EN_ACTN_ITM_T (DTYPE varchar(31), ACTN_ITM_ID bigint not null, ACTN_ITM_RQST_CD varchar(255), ACTN_RQST_ID bigint not null, ACTN_ITM_ASND_DT datetime, DLGN_TYP varchar(255), ACTN_ITM_DLGN_PRSN_EN_ID varchar(255), ACTN_ITM_DLGN_WRKGRP_ID bigint, DOC_TYP_HDLR_URL_ADDR varchar(255), DOC_TYP_LBL_TXT varchar(255), DOC_TYP_NM varchar(255), DOC_TTL varchar(255), DB_LOCK_VER_NBR integer, ACTN_ITM_RESP_ID bigint, ROLE_NM varchar(255), DOC_HDR_ID bigint, ACTN_ITM_PRSN_EN_ID varchar(255), WRKGRP_ID bigint, primary key (ACTN_ITM_ID)) ;
create table EN_ACTN_RQST_T (ACTN_RQST_ID bigint not null, ACTN_RQST_CD varchar(255), ACTN_TKN_ID bigint, ACTN_RQST_ANNOTN_TXT varchar(255), ACTN_RQST_APPR_PLCY varchar(255), ACTN_RQST_CRTE_DT datetime, ACTN_RQST_CUR_IND char(1), DLGN_TYP varchar(255), DOC_VER_NBR integer, ACTN_RQST_IGN_PREV_ACTN_IND char(1), DB_LOCK_VER_NBR integer, ACTN_RQST_PARNT_ID bigint, ACTN_RQST_PRIO_NBR integer, QUAL_ROLE_NM varchar(255), QUAL_ROLE_NM_LBL_TXT varchar(255), ACTN_RQST_RECP_TYP_CD varchar(255), ACTN_RQST_RESP_DESC varchar(255), ACTN_RQST_RESP_ID bigint, ROLE_NM varchar(255), DOC_HDR_ID bigint, ACTN_RQST_RTE_LVL_NBR integer, RULE_BASE_VALUES_ID bigint, ACTN_RQST_STAT_CD varchar(255), ACTN_RQST_PRSN_EN_ID varchar(255), WRKGRP_ID bigint, ACTN_RQST_RTE_NODE_INSTN_ID bigint, primary key (ACTN_RQST_ID)) ;
create table EN_ACTN_TKN_T (ACTN_TKN_ID bigint not null, ACTN_TKN_DT datetime, ACTN_TKN_CD varchar(255), ACTN_TKN_ANNOTN_TXT varchar(255), ACTN_TKN_CUR_IND char(1), ACTN_TKN_DLGTR_PRSN_EN_ID varchar(255), ACTN_TKN_DLGTR_WRKGRP_ID bigint, DOC_VER_NBR integer, DB_LOCK_VER_NBR integer, DOC_HDR_ID bigint, ACTN_TKN_PRSN_EN_ID varchar(255), primary key (ACTN_TKN_ID)) ;
create table EN_APPL_CNST_T (APPL_CNST_NM varchar(255) not null, APPL_CNST_VAL_TXT varchar(255), DB_LOCK_VER_NBR integer, primary key (APPL_CNST_NM)) ;
create table EN_ATTACHMENT_T (ATTACHMENT_ID bigint not null, FILE_LOC varchar(255), FILE_NM varchar(255), DB_LOCK_VER_NBR integer, MIME_TYP varchar(255), NTE_ID bigint, primary key (ATTACHMENT_ID)) ;
create table EN_BAM_PARAM_T (BAM_PARAM_ID bigint not null, PARAM varchar(255), BAM_ID bigint, primary key (BAM_PARAM_ID)) ;
create table EN_BAM_T (BAM_ID bigint not null, CALL_DT datetime, callback tinyblob, EXCEPTION_MSG mediumtext, EXCEPTION_TO_STRING varchar(255), METHOD_NM varchar(255), SRVR_IND_IND char(1), SERVICE_NM varchar(255), SERVICE_URL varchar(255), TARGET_TO_STRING varchar(255), THREAD_NM varchar(255), primary key (BAM_ID)) ;
create table EN_DLGN_RSP_T (DLGN_RULE_ID bigint not null, DLGN_RULE_BASE_VAL_ID bigint, DLGN_TYP varchar(255), DB_LOCK_VER_NBR integer, RULE_RSP_ID bigint, primary key (DLGN_RULE_ID)) ;
create table EN_DOC_HDR_CNTNT_T (DOC_HDR_ID bigint not null, DOC_CNTNT_TXT mediumtext, primary key (DOC_HDR_ID)) ;
create table EN_DOC_HDR_EXT_DT_T (DOC_HDR_EXT_ID bigint not null, DOC_HDR_ID bigint, DOC_HDR_EXT_VAL_KEY varchar(255), DOC_HDR_EXT_VAL datetime, primary key (DOC_HDR_EXT_ID)) ;
create table EN_DOC_HDR_EXT_FLT_T (DOC_HDR_EXT_ID bigint not null, DOC_HDR_ID bigint, DOC_HDR_EXT_VAL_KEY varchar(255), DOC_HDR_EXT_VAL decimal(19,2), primary key (DOC_HDR_EXT_ID)) ;
create table EN_DOC_HDR_EXT_LONG_T (DOC_HDR_EXT_ID bigint not null, DOC_HDR_ID bigint, DOC_HDR_EXT_VAL_KEY varchar(255), DOC_HDR_EXT_VAL bigint, primary key (DOC_HDR_EXT_ID)) ;
create table EN_DOC_HDR_EXT_T (DOC_HDR_EXT_ID bigint not null, DOC_HDR_ID bigint, DOC_HDR_EXT_VAL_KEY varchar(255), DOC_HDR_EXT_VAL varchar(255), primary key (DOC_HDR_EXT_ID)) ;
create table EN_DOC_HDR_T (DTYPE varchar(31), DOC_HDR_ID bigint not null, DOC_APPL_DOC_ID varchar(255), DOC_APRV_DT datetime, DOC_CRTE_DT datetime, DOC_RTE_LVL_NBR integer, DOC_RTE_STAT_CD varchar(255), DOC_TTL varchar(255), DOC_VER_NBR integer, DOC_TYP_ID bigint, DOC_FNL_DT datetime, DOC_INITR_PRSN_EN_ID varchar(255), DB_LOCK_VER_NBR integer, DOC_RTE_LVL_MDFN_DT datetime, DOC_RTE_STAT_MDFN_DT datetime, DOC_RTE_USR_PRSN_EN_ID varchar(255), DOC_STAT_MDFN_DT datetime, primary key (DOC_HDR_ID)) ;
create table EN_DOC_NTE_T (DOC_NTE_ID bigint not null, DB_LOCK_VER_NBR integer, DOC_NTE_AUTH_PRSN_EN_ID varchar(255), DOC_NTE_CRT_DT datetime, DOC_NTE_TXT varchar(255), DOC_HDR_ID bigint, primary key (DOC_NTE_ID)) ;
create table EN_DOC_TYP_ATTRIB_T (DOC_TYP_ATTRIB_ID bigint not null, DOC_TYP_ID bigint, ORD_INDX integer, RULE_ATTRIB_ID bigint, primary key (DOC_TYP_ATTRIB_ID)) ;
create table EN_DOC_TYP_PLCY_RELN_T (DOC_TYP_ID bigint not null, DOC_PLCY_NM varchar(255) not null, DB_LOCK_VER_NBR integer, DOC_PLCY_VAL char(1), primary key (DOC_TYP_ID, DOC_PLCY_NM)) ;
create table EN_DOC_TYP_PROC_T (DOC_TYP_PROC_ID bigint not null, INIT_IND char(1), DB_LOCK_VER_NBR integer, PROC_NM varchar(255), INIT_RTE_NODE_ID bigint, DOC_TYP_ID bigint, primary key (DOC_TYP_PROC_ID)) ;
create table EN_DOC_TYP_T (DOC_TYP_ID bigint not null, DOC_TYP_ACTV_IND char(1), BLNKT_APPR_PLCY varchar(255), BLNKT_APPR_WRKGRP_ID bigint, DOC_TYP_CUR_IND char(1), DOC_TYP_EMAIL_XSL varchar(255), DOC_TYP_DESC varchar(255), DOC_TYP_HDLR_URL_ADDR varchar(255), DOC_TYP_PARNT_ID bigint, DOC_TYP_SECURITY_XML mediumtext, DOC_TYP_LBL_TXT varchar(255), DB_LOCK_VER_NBR integer, MESSAGE_ENTITY_NM varchar(255), DOC_TYP_NM varchar(255), DOC_TYP_NOTIFY_ADDR varchar(255), DOC_TYP_POST_PRCSR_NM varchar(255), DOC_TYP_PREV_VER bigint, DOC_HDR_ID bigint, DOC_TYP_RTE_VER_NBR varchar(255), DOC_TYP_VER_NBR integer, WRKGRP_ID bigint, RPT_WRKGRP_ID bigint, primary key (DOC_TYP_ID)) ;create table EN_EDL_DMP_T (DOC_HDR_ID bigint not null, DOC_CRTE_DT datetime, DOC_CRNT_NODE_NM varchar(255), DOC_TTL varchar(255), DOC_INITR_ID varchar(255), DOC_MDFN_DT datetime, DOC_RTE_STAT_CD varchar(255), DOC_TYP_NM varchar(255), DB_LOCK_VER_NBR integer, primary key (DOC_HDR_ID)) ;
create table EN_EDL_FIELD_DMP_T (EDL_FIELD_DMP_ID bigint not null, DOC_HDR_ID bigint, FLD_NM varchar(255), FLD_VAL varchar(255), DB_LOCK_VER_NBR integer, primary key (EDL_FIELD_DMP_ID)) ;
create table EN_EDOCLT_ASSOC_T (edoclt_assoc_id bigint not null, edoclt_assoc_actv_ind char(1), edoclt_assoc_def_nm varchar(255), edoclt_assoc_doctype_nm varchar(255), db_lock_ver_nbr integer, edoclt_assoc_style_nm varchar(255), primary key (edoclt_assoc_id)) ;
create table EN_EDOCLT_DEF_T (edoclt_def_id bigint not null, edoclt_def_actv_ind char(1), db_lock_ver_nbr integer, edoclt_def_nm varchar(255), edoclt_def_xml mediumtext, primary key (edoclt_def_id)) ;
create table EN_EDOCLT_STYLE_T (edoclt_style_id bigint not null, edoclt_style_actv_ind char(1), db_lock_ver_nbr integer, edoclt_style_nm varchar(255), edoclt_style_xml mediumtext, primary key (edoclt_style_id)) ;
create table EN_HLP_T (EN_HLP_ID bigint not null, EN_HLP_KY varchar(255), EN_HLP_NM varchar(255), EN_HLP_TXT varchar(255), DB_LOCK_VER_NBR integer, primary key (EN_HLP_ID)) ;
create table EN_INIT_RTE_NODE_INSTN_T (DOC_HDR_ID bigint not null, RTE_NODE_INSTN_ID bigint not null) ;
create table EN_MSG_PAYLOAD_T (MESSAGE_QUE_ID bigint not null, MESSAGE_PAYLOAD mediumtext, primary key (MESSAGE_QUE_ID)) ;
create table EN_MSG_QUE_T (MESSAGE_QUE_ID bigint not null, MESSAGE_EXP_DT datetime, MESSAGE_QUE_IP_NBR varchar(255), DB_LOCK_VER_NBR integer, MESSAGE_ENTITY_NM varchar(255), SERVICE_METHOD_NM varchar(255), MESSAGE_QUE_DT datetime, MESSAGE_QUE_PRIO_NBR integer, MESSAGE_QUE_STAT_CD varchar(255), MESSAGE_QUE_RTRY_CNT integer, MESSAGE_SERVICE_NM varchar(255), VAL_ONE varchar(255), VAL_TWO varchar(255), primary key (MESSAGE_QUE_ID)) ;
create table EN_QUAL_ROLE_EXT_T (QUAL_ROLE_EXT_ID bigint not null, DB_LOCK_VER_NBR integer, QUAL_ROLE_ID bigint, ROLE_ATTRIB_ID bigint, primary key (QUAL_ROLE_EXT_ID)) ;
create table EN_QUAL_ROLE_EXT_VAL_T (QUAL_ROLE_EXT_VAL_ID bigint not null, EXT_KEY varchar(255), DB_LOCK_VER_NBR integer, QUAL_ROLE_EXT_ID bigint, EXT_VAL varchar(255), primary key (QUAL_ROLE_EXT_VAL_ID)) ;
create table EN_QUAL_ROLE_MBR_T (QUAL_ROLE_MBR_ID bigint not null, DB_LOCK_VER_NBR integer, MBR_ID varchar(255), MBR_TYP integer, QUAL_ROLE_ID bigint, RSP_ID bigint, primary key (QUAL_ROLE_MBR_ID)) ;
create table EN_QUAL_ROLE_T (QUAL_ROLE_ID bigint not null, ACTVN_DT datetime, ACTV_IND char(1), CUR_IND char(1), DACTVN_DT datetime, QUAL_ROLE_DESC varchar(255), DOC_HDR_ID bigint, FRM_DT datetime, DB_LOCK_VER_NBR integer, PREV_VER_ID bigint, ROLE_ID bigint, TO_DT datetime, VER_NBR integer, primary key (QUAL_ROLE_ID)) ;
create table EN_RMV_RPLC_DOC_T (DOC_HDR_ID bigint not null, DB_LOCK_VER_NBR integer, OPRN varchar(255), RPLC_PRSN_EN_ID varchar(255), PRSN_EN_ID varchar(255), primary key (DOC_HDR_ID)) ;
create table EN_RMV_RPLC_RULE_T (DOC_HDR_ID bigint not null, RULE_ID bigint not null, primary key (DOC_HDR_ID, RULE_ID)) ;
create table EN_RMV_RPLC_WRKGRP_T (DOC_HDR_ID bigint not null, WRKGRP_ID bigint not null, primary key (DOC_HDR_ID, WRKGRP_ID)) ;
create table EN_ROLE_ATTRIB_T (ROLE_ATTRIB_ID bigint not null, DB_LOCK_VER_NBR integer, ROLE_ID bigint, RULE_ATTRIB_ID bigint, primary key (ROLE_ATTRIB_ID)) ;
create table EN_ROLE_T (ROLE_ID bigint not null, ROLE_DESC varchar(255), DB_LOCK_VER_NBR integer, ROLE_NM varchar(255), primary key (ROLE_ID)) ;
create table EN_RTE_BRCH_PROTO_T (RTE_BRCH_PROTO_ID bigint not null, DB_LOCK_VER_NBR integer, RTE_BRCH_PROTO_NM varchar(255), primary key (RTE_BRCH_PROTO_ID)) ;
create table EN_RTE_BRCH_ST_T (RTE_BRCH_ST_ID bigint not null, ST_KEY varchar(255), ST_VAL_TXT varchar(255), DB_LOCK_VER_NBR integer, RTE_BRCH_ID bigint, primary key (RTE_BRCH_ST_ID)) ;
create table EN_RTE_BRCH_T (RTE_BRCH_ID bigint not null, INIT_RTE_NODE_INSTN_ID bigint, DB_LOCK_VER_NBR integer, BRCH_NM varchar(255), PARNT_RTE_BRCH_ID bigint, SPLT_RTE_NODE_INSTN_ID bigint, JOIN_RTE_NODE_INSTN_ID bigint, primary key (RTE_BRCH_ID)) ;
create table EN_RTE_NODE_CFG_PARM_T (RTE_NODE_CFG_PARM_ID bigint not null, RTE_NODE_CFG_PARM_KEY varchar(255), RTE_NODE_CFG_PARM_VAL varchar(255), RTE_NODE_CFG_PARM_ND bigint, primary key (RTE_NODE_CFG_PARM_ID)) ;
create table EN_RTE_NODE_INSTN_LNK_T (FROM_RTE_NODE_INSTN_ID bigint not null, TO_RTE_NODE_INSTN_ID bigint not null) ;
create table EN_RTE_NODE_INSTN_ST_T (RTE_NODE_INSTN_ST_ID bigint not null, ST_KEY varchar(255), ST_VAL_TXT varchar(255), DB_LOCK_VER_NBR integer, RTE_NODE_INSTN_ID bigint, primary key (RTE_NODE_INSTN_ST_ID)) ;
create table EN_RTE_NODE_INSTN_T (RTE_NODE_INSTN_ID bigint not null, ACTV_IND char(1), CMPLT_IND char(1), DOC_ID bigint, INIT_IND char(1), DB_LOCK_VER_NBR integer, PROC_RTE_NODE_INSTN_ID bigint, RTE_NODE_ID bigint, BRCH_ID bigint, primary key (RTE_NODE_INSTN_ID)) ;
create table EN_RTE_NODE_LNK_T (TO_RTE_NODE_ID bigint not null, FROM_RTE_NODE_ID bigint not null) ;
create table EN_RTE_NODE_T (RTE_NODE_ID bigint not null, DOC_ACTVN_TYP_TXT varchar(255), DOC_TYP_ID bigint, WRKGRP_ID bigint, DOC_FNL_APRVR_IND char(1), DB_LOCK_VER_NBR integer, DOC_MNDTRY_RTE_IND char(1), RTE_NODE_TYP varchar(255), DOC_RTE_MTHD_CD varchar(255), DOC_RTE_MTHD_NM varchar(255), RTE_NODE_NM varchar(255), BRCH_PROTO_ID bigint, primary key (RTE_NODE_ID)) ;
create table EN_RULE_ATTRIB_T (RULE_ATTRIB_ID bigint not null, RULE_ATTRIB_CLS_NM varchar(255), RULE_ATTRIB_DESC varchar(255), RULE_ATTRIB_LBL_TXT varchar(255), DB_LOCK_VER_NBR integer, MESSAGE_ENTITY_NM varchar(255), RULE_ATTRIB_NM varchar(255), RULE_ATTRIB_TYP varchar(255), RULE_ATTRIB_XML_RTE_TXT mediumtext, primary key (RULE_ATTRIB_ID)) ;
create table EN_RULE_BASE_VAL_T (RULE_BASE_VAL_ID bigint not null, RULE_BASE_VAL_ACTVN_DT datetime, RULE_BASE_VAL_ACTV_IND char(1), RULE_BASE_VAL_CUR_IND char(1), RULE_BASE_VAL_DACTVN_DT datetime, RULE_BASE_VAL_DLGN_IND char(1), RULE_BASE_VAL_DESC varchar(255), DOC_TYP_NM varchar(255), RULE_BASE_VAL_FRM_DT datetime, RULE_BASE_VAL_IGNR_PRVS char(1), DB_LOCK_VER_NBR integer, RULE_NM varchar(255), RULE_BASE_VAL_PREV_VER bigint, DOC_HDR_ID bigint, RULE_TMPL_ID bigint, TMPL_RULE_IND char(1), RULE_BASE_VAL_TO_DT datetime not null, RULE_BASE_VAL_VER_NBR integer, RULE_EXPR_ID bigint, primary key (RULE_BASE_VAL_ID)) ;
create table EN_RULE_EXPR_T (RULE_EXPR_ID bigint not null, RULE_EXPR varchar(255), RULE_EXPR_TYP varchar(255), primary key (RULE_EXPR_ID)) ;
create table EN_RULE_EXT_T (RULE_EXT_ID bigint not null, DB_LOCK_VER_NBR integer, RULE_BASE_VAL_ID bigint, RULE_TMPL_ATTRIB_ID bigint, primary key (RULE_EXT_ID)) ;
create table EN_RULE_EXT_VAL_T (RULE_EXT_VAL_ID bigint not null, RULE_EXT_VAL_KEY varchar(255), DB_LOCK_VER_NBR integer, RULE_EXT_ID bigint, RULE_EXT_VAL varchar(255), primary key (RULE_EXT_VAL_ID)) ;
create table EN_RULE_RSP_T (RULE_RSP_ID bigint not null, ACTION_RQST_CD varchar(255), RULE_RSP_APPR_PLCY varchar(255), DB_LOCK_VER_NBR integer, RULE_RSP_PRIO_NBR integer, RSP_ID bigint, RULE_BASE_VAL_ID bigint, RULE_RSP_NM varchar(255), RULE_RSP_TYP varchar(255), primary key (RULE_RSP_ID)) ;
create table EN_RULE_TMPL_ATTRIB_T (RULE_TMPL_ATTRIB_ID bigint not null, ACTV_IND char(1), DFLT_VAL varchar(255), DSPL_ORD integer, DB_LOCK_VER_NBR integer, REQ_IND char(1), RULE_ATTRIB_ID bigint, RULE_TMPL_ID bigint, primary key (RULE_TMPL_ATTRIB_ID)) ;
create table EN_RULE_TMPL_OPTN_T (RULE_TMPL_OPTN_ID bigint not null, RULE_TMPL_OPTN_KEY varchar(255), DB_LOCK_VER_NBR integer, RULE_TMPL_ID bigint, RULE_TMPL_OPTN_VAL varchar(255), primary key (RULE_TMPL_OPTN_ID)) ;
create table EN_RULE_TMPL_T (RULE_TMPL_ID bigint not null, DLGN_RULE_TMPL_ID bigint, RULE_TMPL_DESC varchar(255), DB_LOCK_VER_NBR integer, RULE_TMPL_NM varchar(255), primary key (RULE_TMPL_ID)) ;
create table EN_SERVICE_DEF_DUEX_T (SERVICE_DEF_ID bigint not null, SERVICE_ALIVE char(1), SERVICE_URL varchar(255), DB_LOCK_VER_NBR integer, MESSAGE_ENTITY_NM varchar(255), SERVICE_DEFINITION mediumtext, SERVER_IP varchar(255), SERVICE_NM varchar(255), primary key (SERVICE_DEF_ID)) ;
create table EN_USR_OPTN_T (PRSN_OPTN_ID varchar(255) not null, PRSN_EN_ID varchar(255) not null, DB_LOCK_VER_NBR integer, PRSN_OPTN_VAL varchar(255), primary key (PRSN_OPTN_ID, PRSN_EN_ID)) ;
create table EN_USR_T (DTYPE varchar(31), PRSN_EN_ID varchar(255) not null, PRSN_NTWRK_ID varchar(255), USR_CRTE_DT datetime, PRSN_NM varchar(255), PRSN_EMAIL_ADDR varchar(255), PRSN_UNIV_ID varchar(255), PRSN_GVN_NM varchar(255), PRSN_LST_NM varchar(255), USR_LST_UPDT_DT datetime, DB_LOCK_VER_NBR integer, PRSN_UNVL_USR_ID varchar(255), OBJ_ID varchar(255), VER_NBR bigint, primary key (PRSN_EN_ID)) ;
create table EN_WRKGRP_EXT_DTA_T (WRKGRP_EXT_DTA_ID bigint not null, EXT_KEY varchar(255), DB_LOCK_VER_NBR integer, EXT_VAL varchar(255), WRKGRP_EXT_ID bigint, primary key (WRKGRP_EXT_DTA_ID)) ;
create table EN_WRKGRP_EXT_T (WRKGRP_EXT_ID bigint not null, DB_LOCK_VER_NBR integer, WRKGRP_ID integer, WRKGRP_VER_NBR bigint, WRKGRP_TYP_ATTRIB_ID bigint, primary key (WRKGRP_EXT_ID)) ;
create table EN_WRKGRP_MBR_T (WRKGRP_MBR_PRSN_EN_ID varchar(255) not null, WRKGRP_ID bigint not null, WRKGRP_VER_NBR integer not null, DB_LOCK_VER_NBR integer, WRKGRP_MBR_TYP varchar(255), primary key (WRKGRP_MBR_PRSN_EN_ID, WRKGRP_ID, WRKGRP_VER_NBR)) ;
create table EN_WRKGRP_T (WRKGRP_VER_NBR integer not null, WRKGRP_ID bigint not null, WRKGRP_ACTV_IND char(1), WRKGRP_CUR_IND char(1), WRKGRP_DESC varchar(255), DOC_HDR_ID bigint, DB_LOCK_VER_NBR integer, WRKGRP_NM varchar(255), WRKGRP_TYP_CD varchar(255), primary key (WRKGRP_VER_NBR, WRKGRP_ID)) ;
create table EN_WRKGRP_TYP_ATTRIB_T (WRKGRP_TYP_ATTRIB_ID bigint not null, DB_LOCK_VER_NBR integer, ORD_INDX integer, WRKGRP_TYP_ID bigint, ATTRIB_ID bigint, primary key (WRKGRP_TYP_ATTRIB_ID)) ;
create table EN_WRKGRP_TYP_T (WRKGRP_TYP_ID bigint not null, ACTV_IND char(1), WRKGRP_TYP_DESC varchar(255), DOC_TYP_NM varchar(255), WRKGRP_TYP_LBL varchar(255), DB_LOCK_VER_NBR integer, WRKGRP_TYP_NM varchar(255), primary key (WRKGRP_TYP_ID)) ;
create table FP_DOC_HEADER_T (FDOC_NBR varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, FDOC_EXPLAIN_TXT varchar(255), FDOC_DESC varchar(255), FDOC_TMPL_NBR varchar(255), ORG_DOC_NBR varchar(255), primary key (FDOC_NBR)) ;
create table FP_DOC_TYPE_T (FDOC_TYP_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, FDOC_TYP_ACTIVE_CD char(1), FDOC_NM varchar(255), primary key (FDOC_TYP_CD)) ;
create table FP_DOC_TYPE_ATTR_T (ID bigint not null, OBJ_ID varchar(255) not null, VER_NBR bigint not null, ACTIVE_IND char(1) not null, DOC_TYP_ATTR_CD varchar(100) not null, DOC_TYP_ATTR_VAL varchar(400), DOC_TYP_ATTR_LBL varchar(400), FDOC_TYP_CD varchar(255) not null, primary key (ID)) ;
create table FP_MAINTENANCE_DOCUMENT_T (FDOC_NBR varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, DOCUMENT_CONTENTS mediumtext, primary key (FDOC_NBR)) ;
create table FP_MAINT_LOCK_T (LOCK_REPRESENTATION_TXT varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, FDOC_NBR varchar(255), primary key (LOCK_REPRESENTATION_TXT)) ;
create table FS_ADHOC_RTE_ACTN_RECP_T (ACTN_RQST_RECP_ID varchar(255) not null, ACTN_RQST_CD varchar(255) not null, ACTN_RQST_RECP_TYP_CD integer not null, OBJ_ID varchar(255), VER_NBR bigint, FDOC_NBR varchar(255), primary key (ACTN_RQST_RECP_ID, ACTN_RQST_CD, ACTN_RQST_RECP_TYP_CD)) ;
create table FS_LOOKUP_RESULTS_MT (LOOKUP_RESULT_SEQUENCE_NBR varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, PERSON_UNVL_ID varchar(255), SERIALIZED_LOOKUP_RESULTS mediumtext, primary key (LOOKUP_RESULT_SEQUENCE_NBR)) ;
create table FS_LOOKUP_SELECTIONS_MT (LOOKUP_RESULT_SEQUENCE_NBR varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, PERSON_UNVL_ID varchar(255), SELECTED_OBJ_IDS mediumtext, primary key (LOOKUP_RESULT_SEQUENCE_NBR)) ;
create table FS_UNIVERSAL_USR_T (PERSON_UNVL_ID varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, PRSN_AFLT_IND char(1), CAMPUS_CD varchar(255), EMP_STAT_CD varchar(255), EMP_TYPE_CD varchar(255), PRSN_FAC_IND char(1), FS_ENCRPTD_PWD_TXT varchar(255), PRSN_BASE_SLRY_AMT tinyblob, PRSN_CMP_ADDR varchar(255), PRSN_EMAIL_ADDR varchar(255), PRSN_1ST_NM varchar(255), PRSN_LST_NM varchar(255), PRSN_LOC_PHN_NBR varchar(255), PRSN_MID_NM varchar(255), PERSON_NM varchar(255), PRSN_PYRL_ID varchar(255), PRSN_TAX_ID varchar(255), PRSN_TAX_ID_TYP_CD varchar(255), PERSON_USER_ID varchar(255), EMP_PRM_DEPT_CD varchar(255), PRSN_STAFF_IND char(1), PRSN_STU_IND char(1), primary key (PERSON_UNVL_ID)) ;
create table KCB_MESSAGES (ID bigint not null, CHANNEL varchar(255) not null, CONTENT mediumtext not null, CONTENT_TYPE varchar(255), CREATED_DATETIME datetime not null, DELIVERY_TYPE varchar(255) not null, DB_LOCK_VER_NBR integer, ORIGIN_ID varchar(255) not null, PRODUCER varchar(255), USER_RECIPIENT_ID varchar(255) not null, TITLE varchar(255), URL varchar(255), primary key (ID)) ;
create table KCB_MSG_DELIVS (ID bigint not null, LOCKED_DATE datetime, DELIVERER_SYSTEM_ID varchar(255), DELIVERER_TYPE_NAME varchar(255) not null, DELIVERY_STATUS varchar(255), DB_LOCK_VER_NBR integer, PROCESS_COUNT integer, MESSAGE_ID bigint, primary key (ID)) ;
create table KCB_RECIP_DELIVS (ID bigint not null, CHANNEL varchar(255) not null, DELIVERER_NAME varchar(255) not null, DB_LOCK_VER_NBR integer, RECIPIENT_ID varchar(255) not null, primary key (ID)) ;
create table KCB_RECIP_PREFS (ID bigint not null, DB_LOCK_VER_NBR integer, PROPERTY varchar(255) not null, RECIPIENT_ID varchar(255) not null, VALUE varchar(255), primary key (ID)) ;
create table KIM_ATTRIBUTE_TYPES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, NAME varchar(255), DESCRIPTION varchar(255), primary key (ID)) ;
create table KIM_ENTITYS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ENTITY_TYPE_ID bigint, primary key (ID)) ;
create table KIM_ENTITY_ATTRIBUTES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATTRIBUTE_NAME varchar(255), ATTRIBUTE_TYPE_ID bigint, ATTRIBUTE_VALUE varchar(255), ATTRIBUTE_VALUES mediumtext, ENTITY_ID bigint, SPONSOR_NAMESPACE_ID bigint, primary key (ID)) ;
create table KIM_ENTITY_TYPES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), primary key (ID)) ;
create table KIM_GROUPS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), GROUP_TYPE_ID bigint, NAME varchar(255), primary key (ID)) ;
create table KIM_GROUP_ATTRIBUTES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATTRIBUTE_NAME varchar(255), ATTRIBUTE_TYPE_ID bigint, ATTRIBUTE_VALUE varchar(255), ATTRIBUTE_VALUES mediumtext, GROUP_ID bigint, primary key (ID)) ;
create table KIM_GROUP_QLFD_ROLES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATTRIBUTE_NAME varchar(255), ATTRIBUTE_VALUE varchar(255), ROLE_ID bigint, GROUP_ID bigint, primary key (ID)) ;
create table KIM_GROUP_TYPES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), WORKFLOW_DOCUMENT_TYPE varchar(255), primary key (ID)) ;
create table KIM_GRP_TYP_DFLT_ATTRIBS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE char(1), ATTRIBUTE_NAME varchar(255), ATTRIBUTE_TYPE_ID bigint, DESCRIPTION varchar(255), GROUP_TYPE_ID bigint, REQUIRED char(1), primary key (ID)) ;
create table KIM_NAMESPACES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), primary key (ID)) ;
create table KIM_NAMESPACE_DFLT_ATTRIBS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE char(1), ATTRIBUTE_NAME varchar(255), ATTRIBUTE_TYPE_ID bigint, DESCRIPTION varchar(255), NAMESPACE_ID bigint, REQUIRED char(1), primary key (ID)) ;
create table KIM_PERMISSIONS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), NAMESPACE_ID bigint, primary key (ID)) ;
create table KIM_PRINCIPALS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ENTITY_TYPE_ID bigint, ENTITY_ID bigint, NAME varchar(255), roleQualificationsForPrincipal tinyblob, primary key (ID)) ;
create table KIM_PRNCPL_QLFD_ROLES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATTRIBUTE_NAME varchar(255), ATTRIBUTE_VALUE varchar(255), ROLE_ID bigint, PRINCIPAL_ID bigint, primary key (ID)) ;
create table KIM_ROLES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), primary key (ID)) ;
create table KIM_ROLE_ATTRIBUTES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATTRIBUTE_NAME varchar(255), ATTRIBUTE_TYPE_ID bigint, ATTRIBUTE_VALUE varchar(255), ROLE_ID bigint, primary key (ID)) ;
CREATE TABLE KIM_GROUPS_PRINCIPALS_T (
        GROUP_ID bigint NOT NULL,
        PRINCIPAL_ID bigint NOT NULL,
        OBJ_ID VARCHAR(36),
        VER_NBR bigint,
        PRIMARY KEY (GROUP_ID, PRINCIPAL_ID)
)
;
CREATE TABLE KIM_ROLES_PRINCIPALS_T (
        ROLE_ID bigint NOT NULL,
        PRINCIPAL_ID bigint NOT NULL,
        OBJ_ID VARCHAR(36), 
        VER_NBR bigint, 
        PRIMARY KEY (ROLE_ID, PRINCIPAL_ID)
)
;
CREATE TABLE KIM_ROLES_PERMISSIONS_T (
        ROLE_ID bigint NOT NULL,
        PERMISSION_ID bigint NOT NULL,
        OBJ_ID VARCHAR(36), 
        VER_NBR bigint, 
        PRIMARY KEY (ROLE_ID, PERMISSION_ID)
)
;
CREATE TABLE KIM_ROLES_GROUPS_T (
        ROLE_ID bigint NOT NULL,
        GROUP_ID bigint NOT NULL, 
        OBJ_ID VARCHAR(36), 
        VER_NBR bigint, 
        PRIMARY KEY (ROLE_ID, GROUP_ID)
)
;
CREATE TABLE KIM_GROUPS_GROUPS_T (
        PARENT_GROUP_ID bigint NOT NULL,
        MEMBER_GROUP_ID bigint NOT NULL, 
        OBJ_ID VARCHAR(36), 
        VER_NBR bigint, 
        PRIMARY KEY (PARENT_GROUP_ID, MEMBER_GROUP_ID)
)
;
create table KOM_ORGANIZATIONS_CONTEXTS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE varchar(255), CONTEXT_ID bigint, ORGANIZATION_ID bigint, primary key (ID)) ;
create table KOM_ORGANIZATIONS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE varchar(255), CATEGORY_ID bigint, NAME varchar(255), PARENT_ORGANIZATION_ID bigint, SHORT_NAME varchar(255), primary key (ID)) ;
create table KOM_ORGANIZATION_CATEGORIES_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, NAME varchar(255), primary key (ID)) ;
create table KOM_ORGANIZATION_CONTEXTS_T (ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, DESCRIPTION varchar(255), NAME varchar(255), organizations tinyblob, primary key (ID)) ;
create table NOTIFICATIONS (ID bigint not null, AUTO_REMOVE_DATETIME datetime, CONTENT mediumtext not null, CREATED_DATETIME datetime not null, DELIVERY_TYPE varchar(255) not null, DB_LOCK_VER_NBR integer, LOCKED_DATE datetime, PROCESSING_FLAG varchar(255) not null, SEND_DATETIME datetime, TITLE varchar(255), NOTIFICATION_CHANNEL_ID bigint, PRIORITY_ID bigint, CONTENT_TYPE_ID bigint, PRODUCER_ID bigint, primary key (ID)) ;
create table NOTIFICATION_CHANNELS (ID bigint not null, DESCRIPTION varchar(255) not null, NAME varchar(255) not null, SUBSCRIBABLE char(1) not null, primary key (ID)) ;
create table NOTIFICATION_CHANNEL_PRODUCERS (PRODUCER_ID bigint not null, CHANNEL_ID bigint not null) ;
create table NOTIFICATION_CONTENT_TYPES (ID bigint not null, DESCRIPTION varchar(255) not null, NAME varchar(255) not null, NAMESPACE varchar(255) not null, XSD mediumtext not null, XSL mediumtext not null, primary key (ID)) ;
create table NOTIFICATION_MSG_DELIVS (ID bigint not null, DELIVERY_SYSTEM_ID varchar(255), DB_LOCK_VER_NBR integer, LOCKED_DATE datetime, MESSAGE_DELIVERY_STATUS varchar(255) not null, USER_RECIPIENT_ID varchar(255) not null, NOTIFICATION_ID bigint, primary key (ID)) ;
create table NOTIFICATION_PRIORITIES (ID bigint not null, DESCRIPTION varchar(255) not null, NAME varchar(255) not null, PRIORITY_ORDER integer not null, primary key (ID)) ;
create table NOTIFICATION_PRODUCERS (ID bigint not null, CONTACT_INFO varchar(255) not null, DESCRIPTION varchar(255) not null, NAME varchar(255) not null, primary key (ID)) ;
create table NOTIFICATION_RECIPIENTS (ID bigint not null, NOTIFICATION_ID bigint not null, RECIPIENT_ID varchar(255) not null, RECIPIENT_TYPE varchar(255) not null, primary key (ID)) ;
create table NOTIFICATION_RECIPIENTS_LISTS (ID bigint not null, RECIPIENT_ID varchar(255) not null, RECIPIENT_TYPE varchar(255) not null, CHANNEL_ID bigint, primary key (ID)) ;
create table NOTIFICATION_REVIEWERS (ID bigint not null, REVIEWER_ID varchar(255) not null, REVIEWER_TYPE varchar(255) not null, CHANNEL_ID bigint, primary key (ID)) ;
create table NOTIFICATION_SENDERS (ID bigint not null, NOTIFICATION_ID bigint not null, NAME varchar(255) not null, primary key (ID)) ;
create table SH_ATT_T (NTE_ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, ATT_FL_NM varchar(255), ATT_FL_SZ bigint, ATT_ID varchar(255), ATT_MIME_TYP_CD varchar(255), ATT_TYP_CD varchar(255), primary key (NTE_ID)) ;
create table SH_CAMPUS_T (CAMPUS_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, CAMPUS_NM varchar(255), CAMPUS_SHRT_NM varchar(255), CAMPUS_TYP_CD varchar(255), primary key (CAMPUS_CD)) ;
create table SH_CMP_TYP_T (CAMPUS_TYP_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, CMP_TYP_NM varchar(255), DOBJ_MAINT_CD_ACTV_IND char(1), primary key (CAMPUS_TYP_CD)) ;
create table SH_EMP_STAT_T (code varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE_IND char(1), name varchar(255), ROW_ACTV_IND char(1), primary key (code)) ;
create table SH_EMP_TYP_T (code varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE_IND char(1), name varchar(255), ROW_ACTV_IND char(1), primary key (code)) ;
create table SH_NTE_T (NTE_ID bigint not null, OBJ_ID varchar(255), VER_NBR bigint, authorUniversal tinyblob, NTE_AUTH_ID varchar(255), NTE_POST_TS datetime, NTE_PRG_CD varchar(255), NTE_TXT varchar(255), NTE_TPC_TXT varchar(255), NTE_TYP_CD varchar(255), RMT_OBJ_ID varchar(255), primary key (NTE_ID)) ;
create table SH_NTE_TYP_T (NTE_TYP_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, NTE_TYP_ACTV_IND char(1), NTE_TYP_DESC varchar(255), primary key (NTE_TYP_CD)) ;
create table SH_PARM_DTL_TYP_T (SH_PARM_DTL_TYP_CD varchar(255) not null, SH_PARM_NMSPC_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE_IND char(1), SH_PARM_DTL_TYP_NM varchar(255), primary key (SH_PARM_DTL_TYP_CD, SH_PARM_NMSPC_CD)) ;
create table SH_PARM_NMSPC_T (SH_PARM_NMSPC_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE_IND char(1), SH_PARM_NMSPC_NM varchar(255), primary key (SH_PARM_NMSPC_CD)) ;
create table SH_PARM_T (SH_PARM_DTL_TYP_CD varchar(255) not null, SH_PARM_NM varchar(255) not null, SH_PARM_NMSPC_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, SH_PARM_CONS_CD varchar(255), SH_PARM_DESC mediumtext, SH_PARM_TYP_CD varchar(255), SH_PARM_TXT varchar(255), WRKGRP_NM varchar(255), primary key (SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_NMSPC_CD)) ;
create table SH_PARM_TYP_T (SH_PARM_TYP_CD varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, ACTIVE_IND char(1), SH_PARM_TYP_NM varchar(255), primary key (SH_PARM_TYP_CD)) ;
create table SH_USR_PROP_T (APPL_MOD_ID varchar(255) not null, USR_PROP_NM varchar(255) not null, PERSON_UNVL_ID varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, USR_PROP_VAL varchar(255), primary key (APPL_MOD_ID, USR_PROP_NM, PERSON_UNVL_ID)) ;
create table TRAV_DOC_2_ACCOUNTS (fdoc_nbr varchar(255) not null, acct_num varchar(255) not null) ;
create table TRV_ACCT (acct_num varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, acct_fo_id bigint, acct_name varchar(255), primary key (acct_num)) ;
create table TRV_ACCT_EXT (acct_num varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, acct_type varchar(255), primary key (acct_num)) ;
create table TRV_ACCT_FO (acct_fo_id bigint not null, OBJ_ID varchar(255), VER_NBR bigint, acct_fo_user_name varchar(255), primary key (acct_fo_id)) ;
create table TRV_ACCT_TYPE (acct_type varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, acct_type_name varchar(255), primary key (acct_type)) ;
create table TRV_DOC_2 (FDOC_NBR varchar(255) not null, OBJ_ID varchar(255), VER_NBR bigint, dest varchar(255), org varchar(255), request_trav varchar(255), traveler varchar(255), primary key (FDOC_NBR)) ;
create table USER_CHANNEL_SUBSCRIPTIONS (ID bigint not null, USER_ID varchar(255) not null, CHANNEL_ID bigint, primary key (ID)) ;
CREATE TABLE KNS_PESSIMISTIC_LOCK_T (
        LOCK_ID                        bigint NOT NULL, 
        OBJ_ID                         VARCHAR(36) NOT NULL,
        VER_NBR                        bigint NOT NULL,
        LOCK_DESCRIPTOR                VARCHAR(4000),
        FDOC_NBR                       VARCHAR(14) NOT NULL,             
        LOCK_GENERATED_TS              datetime NOT NULL,
        PERSON_UNVL_ID                 VARCHAR(10) NOT NULL,
        PRIMARY KEY (LOCK_ID)
)
;
CREATE TABLE FP_MAINT_DOC_ATTACHMENT_T (
        FDOC_NBR                       VARCHAR(14) NOT NULL,
        ATTACHMENT                     BLOB NOT NULL,
        FILE_NAME                      VARCHAR(150),
        CONTENT_TYPE                   VARCHAR(50),
        OBJ_ID                         VARCHAR(36) NOT NULL,
        VER_NBR                        bigint NOT NULL, 
        PRIMARY KEY (FDOC_NBR)
)
;

-- Bootstrap Data

INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', sys_guid(), 1, 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS') 
;
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', sys_guid(), 1, 'AUTH', 'KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS') 
;
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PTYP', '1A6FEB2501C7607EE043814FD111607E', 1, 'Parameter Type','Y')
;
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PDTP', '1A6FEB2501C7607EE043814FD112607E', 1, 'Parameter Detailed Type', 'Y')
;
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PNMS', '1A6FEB2501C7607EE043814FD113607E', 1, 'Parameter Namespace', 'Y')
;

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('CONFG', 'Config', 'Y')
;

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('VALID', 'Document Validation', 'Y')
;

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('AUTH', 'Authorization', 'Y')
;

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('HELP', 'Help', 'Y')
;

insert into SH_PARM_NMSPC_T
(SH_PARM_NMSPC_CD, SH_PARM_NMSPC_NM, ACTIVE_IND)
values ('KR-NS', 'Kuali Nervous System', 'Y')
;

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'All', 'All')
;

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS','Batch', 'Batch')
;

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Document', 'Document')
;

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Lookup', 'Lookup')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND', 'CONFG', 'Y', 'Flag for enabling/disabling (Y/N) the demonstration encryption check.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_FIELD_LEVEL_HELP_IND', 'CONFG', 'Y', 'Indicates whether field level help links are enabled on lookup pages and documents.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'MAX_FILE_SIZE_DEFAULT_UPLOAD', 'CONFG', '5M', 'Maximum file upload size for the application. Used by PojoFormBase. Must be an integer, optionally followed by "K", "M", or "G". Only used if no other upload limits are in effect.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND', 'CONFG', 'N', 'If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'EXCEPTION_GROUP', 'AUTH', 'WorkflowAdmin', 'The workgroup to which a user must be assigned to perform actions on documents in exception routing status.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'MAX_FILE_SIZE_ATTACHMENT', 'CONFG', '5M', 'Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by "K", "M", or "G".', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES
('KR-NS', 'Document', 'SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS', 'CONFG', 'K', 'Some documents provide the functionality to send notes to another user using a workflow FYI or acknowledge functionality. This parameter specifies the default action that will be used when sending notes. This parameter should be one of the following 2 values: "K" for acknowledge or "F" for fyi. Depending on the notes and workflow service implementation, other values may be possible.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'SUPERVISOR_GROUP', 'AUTH', 'WorkflowAdmin', 'Workgroup which can perform almost any function within Kuali.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS', 'CONFG', '86400', 'Lookup results may continue to be persisted in the DB long after they are needed. This parameter represents the maximum amount of time, in seconds, that the results will be allowed to persist in the DB before they are deleted from the DB.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_PER_PAGE', 'CONFG', '100', 'Maximum number of rows that will be displayed on a look-up results screen.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_DEFAULT_MAX_COLUMN_LENGTH', 'CONFG', '70', 'If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.', 'A', 'WorkflowAdmin')
;

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_LIMIT', 'CONFG', '200', 'Maximum number of results returned in a look-up query.', 'A', 'WorkflowAdmin')
;

insert into SH_NTE_TYP_T (NTE_TYP_CD, NTE_TYP_DESC, NTE_TYP_ACTV_IND) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y')
;
insert into SH_NTE_TYP_T (NTE_TYP_CD, NTE_TYP_DESC, NTE_TYP_ACTV_IND) values ('DH', 'DOCUMENT HEADER', 'Y')
;

insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Feature.CheckRouteLogAuthentication.CheckFuture', 'true', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('RouteQueue.maxRetryAttempts', '0', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('BAM', 'true', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Security.HttpInvoker.SignMessages', 'true', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Workflow.AdminWorkgroup', 'WorkflowAdmin', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Routing.ImmediateExceptionRouting', 'true', 1)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Workgroup.IsRouteLogPopup', 'false', 0)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentType.IsRouteLogPopup', 'false', 0)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentSearch.IsRouteLogPopup', 'true', 0)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentSearch.IsDocumentPopup', 'true', 0)
;
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Config.Backdoor.TargetFrameName', 'iframe_51148', 0)
;

insert into en_usr_t (PRSN_EN_ID, PRSN_UNIV_ID, PRSN_NTWRK_ID, PRSN_UNVL_USR_ID, PRSN_EMAIL_ADDR, PRSN_NM, PRSN_GVN_NM, PRSN_LST_NM, USR_CRTE_DT, USR_LST_UPDT_DT, DB_LOCK_VER_NBR) values ('admin','admin','admin','admin','admin@localhost','admin','admin','admin',now(),now(),0)
;
insert into en_wrkgrp_t (WRKGRP_ID, WRKGRP_VER_NBR, WRKGRP_NM, WRKGRP_ACTV_IND, WRKGRP_TYP_CD, WRKGRP_DESC, WRKGRP_CUR_IND, DOC_HDR_ID, DB_LOCK_VER_NBR) values (1,1,'WorkflowAdmin',1,'W','Workflow Administrator Workgroup',1,null,0)
;
insert into EN_WRKGRP_MBR_T (WRKGRP_MBR_PRSN_EN_ID, WRKGRP_ID, WRKGRP_MBR_TYP, WRKGRP_VER_NBR, DB_LOCK_VER_NBR) values ('admin', 1, 'U', 1, 0)
;

INSERT INTO kr_qrtz_locks values('TRIGGER_ACCESS')
;
INSERT INTO kr_qrtz_locks values('JOB_ACCESS')
;
INSERT INTO kr_qrtz_locks values('CALENDAR_ACCESS')
;
INSERT INTO kr_qrtz_locks values('STATE_ACCESS')
;
INSERT INTO kr_qrtz_locks values('MISFIRE_ACCESS')
;

DELETE FROM NOTIFICATION_PRIORITIES 
;
DELETE FROM NOTIFICATION_CONTENT_TYPES 
;
DELETE FROM NOTIFICATION_PRODUCERS 
;

INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(1, 'Notification System', 'This producer represents messages sent from the general message sending forms.', 'kuali-ken-testing@cornell.edu')
;

INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(1, 'Normal', 'Normal priority', 2)
;

INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(2, 'Low', 'A low priority', 3)
;

INSERT INTO NOTIFICATION_PRIORITIES
(ID, NAME, DESCRIPTION, PRIORITY_ORDER)
VALUES
(3, 'High', 'A high priority', 1)
;

INSERT INTO NOTIFICATION_CONTENT_TYPES
(ID, NAME, DESCRIPTION, NAMESPACE, XSD, XSL)
VALUES
(1, 'Simple', 'Simple content type', 'notification/ContentTypeSimple',
'<?xml version="1.0" encoding="UTF-8"?>
<!-- This schema describes a simple notification.  It only contains a content
element which is a String...about as simple as one can get -->
<schema xmlns="http://www.w3.org/2001/XMLSchema"
  xmlns:c="ns:notification/common"
  xmlns:cs="ns:notification/ContentTypeSimple"
  targetNamespace="ns:notification/ContentTypeSimple"
  attributeFormDefault="unqualified" 
    elementFormDefault="qualified">
  <annotation>
    <documentation xml:lang="en">
      Simple Content Schema
    </documentation>
  </annotation>
  <import namespace="ns:notification/common" schemaLocation="resource:notification/notification-common" />
  <!--  The content element is just a String -->
  <element name="content">
    <complexType>
      <sequence>
        <element name="message" type="c:LongStringType"/>
      </sequence>
    </complexType>
  </element>
</schema>',
'<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
   version="1.0" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:n="ns:notification/ContentTypeSimple" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xsi:schemaLocation="ns:notification/ContentTypeSimple resource:notification/ContentTypeSimple" 
   exclude-result-prefixes="n xsi">
   <xsl:output method="html" omit-xml-declaration="yes" />
   <xsl:template match="/n:content/n:message">
      <strong>
          <xsl:value-of select="." disable-output-escaping="yes"/>
      </strong>
   </xsl:template>
</xsl:stylesheet>')
;

INSERT INTO NOTIFICATION_CONTENT_TYPES
(ID, NAME, DESCRIPTION, NAMESPACE, XSD, XSL)
VALUES
(2, 'Event', 'Event content type', 'notification/ContentTypeEvent',
'<?xml version="1.0" encoding="UTF-8"?>
<!-- This schema defines an generic event notification type in order for it
to be accepted into the system. -->
<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:c="ns:notification/common" xmlns:ce="ns:notification/ContentTypeEvent" targetNamespace="ns:notification/ContentTypeEvent" attributeFormDefault="unqualified" elementFormDefault="qualified">
  <annotation>
    <documentation xml:lang="en">Content Event Schema</documentation>
  </annotation>
  <import namespace="ns:notification/common" schemaLocation="resource:notification/notification-common" />
  <!-- The content element describes the content of the notification.  It
  contains a message (a simple String) and a message element -->
  <element name="content">
    <complexType>
      <sequence>
        <element name="message" type="c:LongStringType"/>
        <element ref="ce:event"/>
      </sequence>
    </complexType>
  </element>
  <!-- This is the event element.  It describes a simple event type containing a
  summary, description, location, and start/stop times -->
  <element name="event">
    <complexType>
      <sequence>
        <element name="summary" type="c:NonEmptyShortStringType" />
        <element name="description" type="c:NonEmptyShortStringType" />
        <element name="location" type="c:NonEmptyShortStringType" />
        <element name="startDateTime" type="dateTime" />
        <element name="stopDateTime" type="dateTime" />
      </sequence>
    </complexType>
  </element>
</schema>', 
'<?xml version="1.0" encoding="UTF-8"?>
<!-- style sheet declaration: be very careful editing the following, the
     default namespace must be used otherwise elements will not match -->
<xsl:stylesheet
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:n="ns:notification/ContentTypeEvent" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="ns:notification/ContentTypeEvent resource:notification/ContentTypeEvent" 
    exclude-result-prefixes="n xsi">
    <!-- output an html fragment -->
    <xsl:output method="html" indent="yes" />
    <!-- match everything -->
    <xsl:template match="/n:content" >
        <table class="bord-all">
            <xsl:apply-templates />
        </table>
    </xsl:template>
    <!--  match message element in the default namespace and render as strong -->
    <xsl:template match="n:message" >
        <caption>
            <strong><xsl:value-of select="." disable-output-escaping="yes"/></strong>
        </caption>
    </xsl:template>
    <!-- match on event in the default namespace and display all children -->
    <xsl:template match="n:event">
        <tr>
            <td class="thnormal"><strong>Summary: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:summary" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Description: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:description" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Location: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:location" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>Start Time: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:startDateTime" /></td>
        </tr>
        <tr>
            <td class="thnormal"><strong>End Time: </strong></td>
            <td class="thnormal"><xsl:value-of select="n:stopDateTime" /></td>
        </tr>
    </xsl:template> 
</xsl:stylesheet>')
;


insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (1, 'fred', 0)
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (2, 'fran', 0)
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, ver_nbr) values (3, 'frank', 0)
;

insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a1', 'a1', 1, 0)
;
insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a2', 'a2', 2, 0)
;
insert into TRV_ACCT (acct_num, acct_name, acct_fo_id, ver_nbr) values ('a3', 'a3', 3, 0)
;


insert into en_usr_t (PRSN_EN_ID, PRSN_UNIV_ID, PRSN_NTWRK_ID, PRSN_UNVL_USR_ID, PRSN_EMAIL_ADDR, PRSN_NM, PRSN_GVN_NM, PRSN_LST_NM, USR_CRTE_DT, USR_LST_UPDT_DT, DB_LOCK_VER_NBR) values ('quickstart','quickstart','quickstart','quickstart','quickstart@localhost','quickstart','quickstart','quickstart',now(),now(),0)
;
insert into EN_WRKGRP_MBR_T (WRKGRP_MBR_PRSN_EN_ID, WRKGRP_ID, WRKGRP_MBR_TYP, WRKGRP_VER_NBR, DB_LOCK_VER_NBR) values ('quickstart', 1, 'U', 1, 0)
;

insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('TRAV', '1A6FEB2501C7607EE043814FD881607E', 1, 'TRAV ACCNT', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('TRFO', '1A6FEB250342607EE043814FD881607E', 1, 'TRAV FO', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('TRD2', '1A6FEB250342607EE043814FD889607E', 1, 'TRAV D2', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('RUSR', '1A6FEB253342607EE043814FD889607E', 1, 'RICE USR', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('PARM', '1A6FRB253342607EE043814FD889607E', 1, 'System Parms', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('BR', '1A6FRB253343337EE043814FD889607E', 1, 'Biz Rules', 'Y')
;
insert into FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) values ('TRVA', '1A5FEB250342607EE043814FD889607E', 1,  'TRAV MAINT', 'Y')
;
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a1', 'IAT')
;
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a2', 'EAT')
;
insert into TRV_ACCT_EXT (ACCT_NUM, ACCT_TYPE) values ('a3', 'IAT')
;
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('CAT', 'Clearing Account Type')
;
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('EAT', 'Expense Account Type')
;
insert into TRV_ACCT_TYPE (ACCT_TYPE, ACCT_TYPE_NAME) values ('IAT', ' Income Account Type')
;

INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(2, 'University Library System', 'This producer represents messages sent from the University Library system.', 'kuali-ken-testing@cornell.edu')
;

INSERT INTO NOTIFICATION_PRODUCERS
(ID, NAME, DESCRIPTION, CONTACT_INFO)
VALUES
(3, 'University Events Office', 'This producer represents messages sent from the University Events system.', 'kuali-ken-testing@cornell.edu')
;

DELETE FROM NOTIFICATION_CHANNELS
;

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(1, 'Kuali Rice Channel', 'This channel is used for sending out information about the Kuali Rice effort.', 'Y')
;

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(2, 'Library Events Channel', 'This channel is used for sending out information about Library Events.', 'Y')
;

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(3, 'Overdue Library Books', 'This channel is used for sending out information about your overdue books.', 'N')
;

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(4, 'Concerts Coming to Campus', 'This channel broadcasts any concerts coming to campus.', 'Y')
;

INSERT INTO NOTIFICATION_CHANNELS
(ID, NAME, DESCRIPTION, SUBSCRIBABLE)
VALUES
(5, 'University Alerts', 'This channel broadcasts general announcements for the university.', 'N')
;

INSERT INTO USER_CHANNEL_SUBSCRIPTIONS
(ID, CHANNEL_ID, USER_ID)
VALUES
(1, 1, 'TestUser4')
;

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(1, 4, 'USER', 'TestUser1')
;

INSERT INTO NOTIFICATION_RECIPIENTS_LISTS
(ID, CHANNEL_ID, RECIPIENT_TYPE, RECIPIENT_ID)
values
(2, 4, 'USER', 'TestUser3')
;

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(1, 1, 'GROUP', 'RiceTeam')
;

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(2, 5, 'USER', 'TestUser3')
;

INSERT INTO NOTIFICATION_REVIEWERS
(ID, CHANNEL_ID, REVIEWER_TYPE, REVIEWER_ID)
VALUES
(3, 5, 'GROUP', 'TestGroup1')
;

DELETE FROM NOTIFICATION_CHANNEL_PRODUCERS
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(1, 1)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(2, 1)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(3, 1)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(4, 1)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(5, 1)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(2, 2)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(3, 2)
;

INSERT INTO NOTIFICATION_CHANNEL_PRODUCERS
(CHANNEL_ID, PRODUCER_ID)
VALUES
(4, 3)
;


-- Required By KNS for Maint. Docs - these can go away once the 0.9.3 KNS extraction tasks are finished --
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KNSD', 'KIM NAMESPACE', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KPMD', 'KIM PRINCIPAL', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KGMD', 'KIM GROUP', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KRMD', 'KIM ROLE', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KATD', 'KIM ATTRIBUTE TYPE', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KETM', 'KIM ENTITY TYPE', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KEMD', 'ENTITY', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KPRD', 'KIM PERMISSION', 'Y');
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KGTM', 'KIM GROUP TYPE', 'Y');




INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.');
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (2, 'Global', 'This record represents the global shared namespace that should house entity attributes and permissions global across all namespaces and not specific to any one namespace.');
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES(100, 'KRA', 'Kuali Research');
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES(101, 'KFS', 'Kuali Finance');

INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) VALUES(110, 'Text', 'Alphanumeric');
INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) VALUES(111, 'PhoneNumber', 'Phone number');
INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) VALUES(112, 'Email', 'Email address');

INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) VALUES(1, 1, 'FirstName', 110, 'First Name', 'Y', 'Y' );
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) VALUES(120, 100, 'FirstName', 110, 'First Name', 'Y', 'Y' );
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) VALUES(121, 100, 'WorkNumber', 111, 'Work Number', 'Y', 'Y' );
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) VALUES(122, 100, 'Email', 112, 'Test case', 'Y', 'Y' );

INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (1, 'Person', 'This entity type represents a person.');
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (131, 'System', 'This entity type represents another system.');
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (132, 'Service', 'This entity type represents a service.');
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (133, 'Process', 'This entity type represents a process.');

INSERT INTO KIM_ENTITYS_T (ID, ENTITY_TYPE_ID) VALUES (140, 1);
INSERT INTO KIM_ENTITYS_T (ID, ENTITY_TYPE_ID) VALUES (141, 1);
INSERT INTO KIM_ENTITYS_T (ID, ENTITY_TYPE_ID) VALUES (142, 131);

INSERT INTO KIM_ENTITY_ATTRIBUTES_T (ID, ENTITY_ID, SPONSOR_NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES(150, 140, 1,'EmailAddress', 112,'kuali-rice@googlegroups.com');
INSERT INTO KIM_ENTITY_ATTRIBUTES_T (ID, ENTITY_ID, SPONSOR_NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES(153, 140, 1,'Country', 112,'USA');
INSERT INTO KIM_ENTITY_ATTRIBUTES_T (ID, ENTITY_ID, SPONSOR_NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES(151, 141, 100,'GroupPhoneNumber', 111,'555-5555');
INSERT INTO KIM_ENTITY_ATTRIBUTES_T (ID, ENTITY_ID, SPONSOR_NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES(152, 142, 101,'Supervisor', 110,'Aaron Godert');
INSERT INTO KIM_ENTITY_ATTRIBUTES_T (ID, ENTITY_ID, SPONSOR_NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES(154, 141, 1,'EmailAddress', 112,'kuali-rice@googlegroups.com');

INSERT INTO KIM_PRINCIPALS_T (ID, NAME, ENTITY_ID) VALUES(160, 'jschmoe', 140);
INSERT INTO KIM_PRINCIPALS_T (ID, NAME, ENTITY_ID) VALUES(161, 'jdoe', 141);
INSERT INTO KIM_PRINCIPALS_T (ID, NAME, ENTITY_ID) VALUES(162, 'HAL', 142);

INSERT INTO KIM_GROUP_TYPES_T (ID, NAME, DESCRIPTION, WORKFLOW_DOCUMENT_TYPE) values (1, 'Default', 'This is the standard group type that most groups default to.', 'KIMGroupMaintenanceDocument');
INSERT INTO KIM_GROUP_TYPES_T (ID, NAME, DESCRIPTION, WORKFLOW_DOCUMENT_TYPE) VALUES (310, 'GroupType', 'Group Type', 'GroupTypeDocType');

INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(300, 'Group1', 'Test group1',310);
INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(301, 'Group2', 'Test group2',310);
INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(302, 'ParentGroup1', 'Parent Test group1',310);
INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(303, 'ParentGroup2', 'Parent Test group2',310);
INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(304, 'SiblingGroup1', 'Sibling Test group1',310);
INSERT INTO KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(305, 'SiblingGroup2', 'Sibling Test group2',310);

INSERT INTO KIM_GROUPS_GROUPS_T (PARENT_GROUP_ID, MEMBER_GROUP_ID) VALUES (301, 302);
INSERT INTO KIM_GROUPS_GROUPS_T (PARENT_GROUP_ID, MEMBER_GROUP_ID) VALUES (302,300);
INSERT INTO KIM_GROUPS_GROUPS_T (PARENT_GROUP_ID, MEMBER_GROUP_ID) VALUES (300,304);
INSERT INTO KIM_GROUPS_GROUPS_T (PARENT_GROUP_ID, MEMBER_GROUP_ID) VALUES (302,301);

INSERT INTO KIM_GROUP_ATTRIBUTES_T (ID, GROUP_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES (170, 300, 'GroupPhoneNumber', 111, '555-5555');
INSERT INTO KIM_GROUP_ATTRIBUTES_T (ID, GROUP_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES (171, 300, 'GroupEmail', 112, 'kuali-rice@googlegroups.com');
INSERT INTO KIM_GROUP_ATTRIBUTES_T (ID, GROUP_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUES) VALUES (172, 302, 'GroupEmail', 112, 'kualidev-rice@googlegroups.com');

INSERT INTO KIM_PERMISSIONS_T (ID, NAME, DESCRIPTION, NAMESPACE_ID) VALUES(1, 'canSave', 'Can save', 1);
INSERT INTO KIM_PERMISSIONS_T (ID, NAME, DESCRIPTION, NAMESPACE_ID) VALUES(180, 'canSave', 'Can save', 100);
INSERT INTO KIM_PERMISSIONS_T (ID, NAME, DESCRIPTION, NAMESPACE_ID) VALUES(181, 'canView', 'Can view', 100);
INSERT INTO KIM_PERMISSIONS_T (ID, NAME, DESCRIPTION, NAMESPACE_ID) VALUES(182, 'canEdit', 'Can edit', 100);


INSERT INTO KIM_ROLES_T (ID, NAME, DESCRIPTION) VALUES (190, 'Dean', 'Dean');
INSERT INTO KIM_ROLES_T (ID, NAME, DESCRIPTION) VALUES (191, 'Director of IT', 'Director of IT');

INSERT INTO KIM_ROLE_ATTRIBUTES_T (ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUE) VALUES (200, 190, 'Account Number', 110, '12345');
INSERT INTO KIM_ROLE_ATTRIBUTES_T (ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, ATTRIBUTE_VALUE) VALUES (201, 191, 'Account Number', 110, '12346');

INSERT INTO KIM_GROUP_QLFD_ROLES_T (ID, GROUP_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES (210, 300, 190, 'Department', 'Finance');
INSERT INTO KIM_GROUP_QLFD_ROLES_T (ID, GROUP_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES (211, 300, 190, 'College', 'Arts and Science');
INSERT INTO KIM_GROUP_QLFD_ROLES_T (ID, GROUP_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES (212, 301, 190, 'College', 'Arts and Science');

INSERT INTO KIM_PRNCPL_QLFD_ROLES_T (ID, PRINCIPAL_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES(220, 160,191,'QualifiedRole', 'Some role');
INSERT INTO KIM_PRNCPL_QLFD_ROLES_T (ID, PRINCIPAL_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES(221, 160,190,'QualifiedRole2', 'Some role2');
INSERT INTO KIM_PRNCPL_QLFD_ROLES_T (ID, PRINCIPAL_ID, ROLE_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE) VALUES(222, 161,190,'QualifiedRole2', 'Some role2');

INSERT INTO KIM_GROUPS_PRINCIPALS_T (GROUP_ID, PRINCIPAL_ID) VALUES (300, 160);
INSERT INTO KIM_GROUPS_PRINCIPALS_T (GROUP_ID, PRINCIPAL_ID) VALUES (302, 160);
INSERT INTO KIM_GROUPS_PRINCIPALS_T (GROUP_ID, PRINCIPAL_ID) VALUES (302, 161);
INSERT INTO KIM_GROUPS_PRINCIPALS_T (GROUP_ID, PRINCIPAL_ID) VALUES (303, 162);

INSERT INTO KIM_ROLES_PRINCIPALS_T (ROLE_ID, PRINCIPAL_ID) VALUES(190,160);
INSERT INTO KIM_ROLES_PRINCIPALS_T (ROLE_ID, PRINCIPAL_ID) VALUES(190,161);

INSERT INTO KIM_ROLES_PERMISSIONS_T (ROLE_ID, PERMISSION_ID) VALUES (190,181);
INSERT INTO KIM_ROLES_PERMISSIONS_T (ROLE_ID, PERMISSION_ID) VALUES (190,180);
INSERT INTO KIM_ROLES_PERMISSIONS_T (ROLE_ID, PERMISSION_ID) VALUES (191,180);
INSERT INTO KIM_ROLES_PERMISSIONS_T (ROLE_ID, PERMISSION_ID) VALUES (191,181);

INSERT INTO KIM_ROLES_GROUPS_T (ROLE_ID, GROUP_ID) VALUES (190, 302);
INSERT INTO KIM_ROLES_GROUPS_T (ROLE_ID, GROUP_ID) VALUES (190, 303);
INSERT INTO KIM_ROLES_GROUPS_T (ROLE_ID, GROUP_ID) VALUES (191, 300);
INSERT INTO KIM_ROLES_GROUPS_T (ROLE_ID, GROUP_ID) VALUES (191, 302);






commit
;


