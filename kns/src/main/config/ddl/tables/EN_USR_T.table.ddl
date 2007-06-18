create table EN_USR_T (
	    PRSN_EN_ID                       VARCHAR2(30) NOT NULL,
	    PRSN_UNIV_ID                     VARCHAR2(11) NOT NULL,
        PRSN_NTWRK_ID                    VARCHAR2(30),
        PRSN_UNVL_USR_ID                 VARCHAR2(10),
        PRSN_EMAIL_ADDR                  VARCHAR2(255),
        PRSN_NM                          VARCHAR2(255),
        PRSN_GVN_NM						 VARCHAR2(255),
        PRSN_LST_NM                      VARCHAR2(255),
        USR_CRTE_DT                      DATE,
        USR_LST_UPDT_DT                  DATE,
        PRSN_ID_MSNG_IND                 NUMBER(1) DEFAULT 0,
	    DB_LOCK_VER_NBR	                 NUMBER(8) DEFAULT 0,
  CONSTRAINT EN_USR_T_PK PRIMARY KEY (PRSN_EN_ID) USING INDEX
)
/