DECLARE
    CURSOR constraint_cursor IS
        SELECT table_name, constraint_name
            FROM user_constraints
            WHERE constraint_type = 'R'
              AND table_name LIKE 'KR\_KIM\_%' ESCAPE '\';
    CURSOR tbls IS 
        SELECT table_name FROM user_tables
            WHERE table_name LIKE 'KR\_KIM\_%' ESCAPE '\';
BEGIN
    FOR r IN constraint_cursor LOOP
        dbms_output.put_line( 'Dropping Constraint: '||r.constraint_name );
        execute immediate 'ALTER TABLE '||r.table_name||' DROP CONSTRAINT '||r.constraint_name;
    END LOOP;

    FOR r IN tbls LOOP
        dbms_output.put_line( 'Dropping Table: '||r.table_name );
        EXECUTE IMMEDIATE 'DROP TABLE '||r.table_name;
    END LOOP;
END;
/

CREATE TABLE kr_kim_entity_t
(
    entity_id      VARCHAR2(40),
    obj_id         VARCHAR2(36) CONSTRAINT kr_kim_entity_tn1 NOT NULL,
    ver_nbr        NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_tn2 NOT NULL,
    row_actv_ind   CHAR(1) DEFAULT 'Y',
    last_updt_dt   DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_tp1 PRIMARY KEY ( entity_id )
)
/
ALTER TABLE kr_kim_entity_t ADD CONSTRAINT kr_kim_entity_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_ent_type_t
(
    ent_type_cd     VARCHAR2(10),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_ent_type_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ent_type_tn2 NOT NULL,
    ent_type_nm     VARCHAR2(40),
    display_sort_cd VARCHAR2(2),
    row_actv_ind    CHAR(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ent_type_tp1 PRIMARY KEY ( ent_type_cd )
)
/
ALTER TABLE kr_kim_ent_type_t ADD CONSTRAINT kr_kim_ent_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_ent_type_t
(
    entity_id      VARCHAR2(40),
    ent_type_cd    VARCHAR2(10),
    obj_id         VARCHAR2(36) CONSTRAINT kr_kim_entity_ent_type_tn1 NOT NULL,
    ver_nbr        NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_ent_type_tn2 NOT NULL,
    row_actv_ind   CHAR(1) DEFAULT 'Y',
    last_updt_dt   DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_ent_type_tp1 PRIMARY KEY ( entity_id, ent_type_cd )
)
/

ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_ent_name_type_t
(
    ent_name_type_cd    VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_ent_name_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ent_name_type_tn2 NOT NULL,
    ent_name_type_nm    VARCHAR2(40),
    display_sort_cd     VARCHAR2(2),
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ent_name_type_tp1 PRIMARY KEY ( ent_name_type_cd )
)
/
ALTER TABLE kr_kim_ent_name_type_t ADD CONSTRAINT kr_kim_ent_name_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_name_t
(
    entity_id         VARCHAR2(40),
    ent_name_type_cd  VARCHAR2(10),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_name_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_name_tn2 NOT NULL,
    first_nm          VARCHAR2(80),
    last_nm           VARCHAR2(80),
    middle_nm         VARCHAR2(80),
    name_suffix       VARCHAR2(80),
    name_prefix       VARCHAR2(80),
    row_actv_ind      CHAR(1) DEFAULT 'Y',
    last_updt_dt      DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_name_tp1 PRIMARY KEY ( entity_id, ent_name_type_cd )
)
/
ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_bio_t
(
    entity_id         VARCHAR2(40),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_bio_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_bio_tn2 NOT NULL,
    ethnicity         VARCHAR2(40),
    birth_dt          DATE,
    gender_cd         CHAR(1) CONSTRAINT kr_kim_entity_bio_tn3 NOT NULL,
    row_actv_ind      CHAR(1) DEFAULT 'Y',
    last_updt_dt      DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_bio_tp1 PRIMARY KEY ( entity_id )
)
/
ALTER TABLE kr_kim_entity_bio_t ADD CONSTRAINT kr_kim_entity_bio_tc0 UNIQUE (obj_id)
/


CREATE TABLE kr_kim_ext_key_type_t
(
    ext_key_type_cd     VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_ext_key_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ext_key_type_tn2 NOT NULL,
    ext_key_type_nm     VARCHAR2(40),
    display_sort_cd     VARCHAR2(2),
    encr_req_ind        CHAR(1) DEFAULT 'N',
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ext_key_type_tp1 PRIMARY KEY ( ext_key_type_cd )
)
/
ALTER TABLE kr_kim_ext_key_type_t ADD CONSTRAINT kr_kim_ext_key_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_email_type_t
(
    email_type_cd       VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_email_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_email_type_tn2 NOT NULL,
    email_type_nm       VARCHAR2(40),
    display_sort_cd     VARCHAR2(2),
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_email_type_tp1 PRIMARY KEY ( email_type_cd )
)
/
ALTER TABLE kr_kim_email_type_t ADD CONSTRAINT kr_kim_email_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_phone_type_t
(
    phone_type_cd       VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_phone_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_phone_type_tn2 NOT NULL,
    phone_type_nm       VARCHAR2(40),
    display_sort_cd     VARCHAR2(2),
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_phone_type_tp1 PRIMARY KEY ( phone_type_cd )
)
/
ALTER TABLE kr_kim_phone_type_t ADD CONSTRAINT kr_kim_phone_type_tc0 UNIQUE (obj_id)
/


CREATE TABLE kr_kim_addr_type_t
(
    addr_type_cd       VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_addr_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_addr_type_tn2 NOT NULL,
    addr_type_nm       VARCHAR2(40),
    display_sort_cd     VARCHAR2(2),
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_addr_type_tp1 PRIMARY KEY ( addr_type_cd )
)
/
ALTER TABLE kr_kim_addr_type_t ADD CONSTRAINT kr_kim_addr_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_afltn_type_t
(
    afltn_type_cd       VARCHAR2(10),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_afltn_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_afltn_type_tn2 NOT NULL,
    afltn_type_nm       VARCHAR2(40),
    emp_afltn_type_ind  CHAR(1) DEFAULT 'N',
    display_sort_cd     VARCHAR2(2),
    row_actv_ind        CHAR(1) DEFAULT 'Y',
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_afltn_type_tp1 PRIMARY KEY ( afltn_type_cd )
)
/
ALTER TABLE kr_kim_afltn_type_t ADD CONSTRAINT kr_kim_afltn_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_ext_key_t
(
    entity_id       VARCHAR2(40),
    ext_key_type_cd VARCHAR2(10),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_ext_key_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_ext_key_tn2 NOT NULL,
    ext_key_val     VARCHAR2(128),
    row_actv_ind    CHAR(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_ext_key_tp1 PRIMARY KEY ( entity_id, ext_key_type_cd )
)
/

ALTER TABLE kr_kim_entity_ext_key_t ADD CONSTRAINT kr_kim_entity_ext_key_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_addr_t
(
    entity_id       VARCHAR2(40),
    addr_type_cd    VARCHAR2(10),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_addr_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_addr_tn2 NOT NULL,
    addr_line_1     VARCHAR2(80),
    addr_line_2     VARCHAR2(80),
    addr_line_3     VARCHAR2(80),
    city_name       VARCHAR2(60),
    postal_state_cd VARCHAR2(2),
    postal_zip_code VARCHAR2(11),
    postal_cntry_cd VARCHAR2(2),
    row_actv_ind    CHAR(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_addr_tp1 PRIMARY KEY ( entity_id, addr_type_cd )
)
/

ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_email_t
(
    entity_id       VARCHAR2(40),
    email_type_cd   VARCHAR2(10),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_email_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_email_tn2 NOT NULL,
    email_addr      VARCHAR2(128),
    row_actv_ind    CHAR(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_email_tp1 PRIMARY KEY ( entity_id, email_type_cd )
)
/

ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_phone_t
(
    entity_id       VARCHAR2(40),
    phone_type_cd   VARCHAR2(10),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_phone_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_phone_tn2 NOT NULL,
    area_code_nbr   VARCHAR2(3),
    phone_nbr       VARCHAR2(20),
    phone_ext_nbr   VARCHAR2(8),
    postal_cntry_cd VARCHAR2(2),
    row_actv_ind    CHAR(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_phone_tp1 PRIMARY KEY ( entity_id, phone_type_cd )
)
/

ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tc0 UNIQUE (obj_id)
/
