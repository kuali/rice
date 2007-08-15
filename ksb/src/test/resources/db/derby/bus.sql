CREATE TABLE EN_MSG_QUE_T (
   MESSAGE_QUE_ID                         BIGINT NOT NULL,
   MESSAGE_QUE_DT             DATE NOT NULL,
   MESSAGE_EXP_DT                         DATE,
   MESSAGE_QUE_PRIO_NBR       BIGINT NOT NULL,
   MESSAGE_QUE_STAT_CD        CHAR(1) NOT NULL,
   MESSAGE_QUE_RTRY_CNT       BIGINT NOT NULL,
   MESSAGE_QUE_IP_NBR         VARCHAR(2000) NOT NULL,
   MESSAGE_SERVICE_NM             VARCHAR(255),
   MESSAGE_ENTITY_NM              VARCHAR(10) NOT NULL,
   SERVICE_METHOD_NM              VARCHAR(2000) ,
   VAL_ONE					  VARCHAR(2000),
   VAL_TWO					  VARCHAR(2000) ,
   DB_LOCK_VER_NBR                BIGINT DEFAULT 0,
   CONSTRAINT EN_MSG_QUE_T_PK PRIMARY KEY (MESSAGE_QUE_ID)
)
;

CREATE TABLE EN_MSG_PAYLOAD_T (
	   MESSAGE_QUE_ID			  BIGINT NOT NULL,
	   MESSAGE_PAYLOAD 		      CLOB NOT NULL,
	   CONSTRAINT EN_MSG_PAYLOAD_T_PK PRIMARY KEY (MESSAGE_QUE_ID)
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

CREATE TABLE EN_UNITTEST_T (
        ID              BIGINT
)
 ;
 