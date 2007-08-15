CREATE TABLE SH_ATT_T(
        NTE_ID                         NUMBER(14) CONSTRAINT SH_ATT_TN1 NOT NULL,
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT SH_ATT_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT SH_ATT_TN3 NOT NULL,
        ATT_MIME_TYP_CD                VARCHAR2(40),
        ATT_FL_NM                      VARCHAR2(250),
        ATT_ID                         VARCHAR2(36),
        ATT_FL_SZ                      NUMBER(14),
        ATT_TYP_CD                     VARCHAR2(2),
     CONSTRAINT SH_ATT_TP1 PRIMARY KEY (NTE_ID),
     CONSTRAINT SH_ATT_TC0 UNIQUE (OBJ_ID)
)
/