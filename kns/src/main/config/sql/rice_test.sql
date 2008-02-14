create table trv_doc_2 (
        FDOC_NBR                       VARCHAR2(14) CONSTRAINT FP_INT_BILL_DOC_TN1 NOT NULL,
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT FP_INT_BILL_DOC_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT FP_INT_BILL_DOC_TN3 NOT NULL,
        FDOC_EXPLAIN_TXT               VARCHAR2(400),
	    request_trav varchar2(30) not null,
	    traveler          varchar2(200),
        org          varchar2(60),
        dest         varchar2(60),
	    CONSTRAINT trv_doc_2P1 PRIMARY KEY (FDOC_NBR)
)
/

create table trv_acct (
    acct_num  varchar2(10) not null,
    acct_name varchar2(50),
    acct_type varchar2(100),
    acct_fo_id number(14),
    constraint trv_acct_pk primary key(acct_num)
)
/

create table trv_doc_acct (
    doc_hdr_id  number(14) not null,
    acct_num    varchar2(10) not null,
    constraint trv_doc_acct_pk primary key(doc_hdr_id, acct_num)
)
/

create table trv_acct_fo (
	acct_fo_id  number(14) not null,
	acct_fo_user_name varchar2(50) not null,
	constraint trv_acct_fo_id_pk primary key(acct_fo_id)
)
/

create table TRAV_DOC_2_ACCOUNTS (
    FDOC_NBR VARCHAR2(14),
    ACCT_NUM varchar2(10),
    CONSTRAINT TRAV_DOC_2_ACCOUNTS_P1 PRIMARY KEY (FDOC_NBR, ACCT_NUM)
)
/

create table TRV_ACCT_TYPE (
    ACCT_TYPE VARCHAR2(10),
    ACCT_TYPE_NAME varchar2(50),
    CONSTRAINT TRV_ACCT_TYPE_PK PRIMARY KEY (ACCT_TYPE)
)
/

create table TRV_ACCT_EXT (
    ACCT_NUM VARCHAR2(10),
    ACCT_TYPE varchar2(100),
    CONSTRAINT TRV_ACCT_TYPE_P1 PRIMARY KEY (ACCT_NUM, ACCT_TYPE)
)
/

CREATE SEQUENCE SEQ_TRAVEL_DOC_ID INCREMENT BY 1 START WITH 1000
/
CREATE SEQUENCE SEQ_TRAVEL_FO_ID INCREMENT BY 1 START WITH 1000
/

create table EN_UNITTEST_T (
    COL VARCHAR2(1) NULL
)
/
