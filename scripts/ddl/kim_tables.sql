DECLARE
    CURSOR constraint_cursor IS
        SELECT table_name, constraint_name
            FROM user_constraints
            WHERE constraint_type = 'R'
              AND table_name LIKE 'KR\_KIM\_%' ESCAPE '\'; --'
    CURSOR tbls IS 
        SELECT table_name FROM user_tables
            WHERE table_name LIKE 'KR\_KIM\_%' ESCAPE '\'; --'
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
-- REFERENCE TABLES

CREATE TABLE kr_kim_ent_type_t
(
    ent_typ_cd      VARCHAR2(40),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_ent_type_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ent_type_tn2 NOT NULL,
    ent_typ_nm      VARCHAR2(40),
    display_sort_cd VARCHAR2(2),
    actv_ind        VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_ent_type_tp1 PRIMARY KEY ( ent_typ_cd )
)
/
ALTER TABLE kr_kim_ent_type_t ADD CONSTRAINT kr_kim_ent_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_ent_type_t ADD CONSTRAINT kr_kim_ent_type_tc1 UNIQUE (ent_typ_nm)
/

CREATE TABLE kr_kim_afltn_type_t
(
    afltn_typ_cd        VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_afltn_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_afltn_type_tn2 NOT NULL,
    afltn_typ_nm        VARCHAR2(40),
    emp_afltn_typ_ind   VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_afltn_type_tp1 PRIMARY KEY ( afltn_typ_cd )
)
/
ALTER TABLE kr_kim_afltn_type_t ADD CONSTRAINT kr_kim_afltn_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_afltn_type_t ADD CONSTRAINT kr_kim_afltn_type_tc1 UNIQUE (afltn_typ_nm)
/

CREATE TABLE kr_kim_ext_id_type_t
(
    ext_id_typ_cd       VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_ext_id_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ext_id_type_tn2 NOT NULL,
    ext_id_typ_nm       VARCHAR2(40),
    encr_req_ind       VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ext_id_type_tp1 PRIMARY KEY ( ext_id_typ_cd )
)
/
ALTER TABLE kr_kim_ext_id_type_t ADD CONSTRAINT kr_kim_ext_id_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_ext_id_type_t ADD CONSTRAINT kr_kim_ext_id_type_tc1 UNIQUE (ext_id_typ_nm)
/

CREATE TABLE kr_kim_ent_name_type_t
(
    ent_name_typ_cd     VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_ent_name_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ent_name_type_tn2 NOT NULL,
    ent_name_typ_nm     VARCHAR2(40),
    dflt_ind            VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ent_name_type_tp1 PRIMARY KEY ( ent_name_typ_cd )
)
/
ALTER TABLE kr_kim_ent_name_type_t ADD CONSTRAINT kr_kim_ent_name_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_ent_name_type_t ADD CONSTRAINT kr_kim_ent_name_type_tc1 UNIQUE (ent_name_typ_nm)
/

CREATE TABLE kr_kim_addr_type_t
(
    addr_typ_cd         VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_addr_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_addr_type_tn2 NOT NULL,
    addr_typ_nm         VARCHAR2(40),
    dflt_ind            VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_addr_type_tp1 PRIMARY KEY ( addr_typ_cd )
)
/
ALTER TABLE kr_kim_addr_type_t ADD CONSTRAINT kr_kim_addr_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_addr_type_t ADD CONSTRAINT kr_kim_addr_type_tc1 UNIQUE (addr_typ_nm)
/

CREATE TABLE kr_kim_phone_type_t
(
    phone_typ_cd        VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_phone_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_phone_type_tn2 NOT NULL,
    phone_typ_nm        VARCHAR2(40),
    dflt_ind            VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_phone_type_tp1 PRIMARY KEY ( phone_typ_cd )
)
/
ALTER TABLE kr_kim_phone_type_t ADD CONSTRAINT kr_kim_phone_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_phone_type_t ADD CONSTRAINT kr_kim_phone_type_tc1 UNIQUE (phone_typ_nm)
/

CREATE TABLE kr_kim_email_type_t
(
    email_typ_cd        VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_email_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_email_type_tn2 NOT NULL,
    email_typ_nm        VARCHAR2(40),
    dflt_ind            VARCHAR2(1) DEFAULT 'N',
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_email_type_tp1 PRIMARY KEY ( email_typ_cd )
)
/
ALTER TABLE kr_kim_email_type_t ADD CONSTRAINT kr_kim_email_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_email_type_t ADD CONSTRAINT kr_kim_email_type_tc1 UNIQUE (email_typ_nm)
/

CREATE TABLE kr_kim_emp_type_t
(
    emp_typ_cd          VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_emp_type_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_emp_type_tn2 NOT NULL,
    emp_typ_nm          VARCHAR2(40),
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_emp_type_tp1 PRIMARY KEY ( emp_typ_cd )
)
/
ALTER TABLE kr_kim_emp_type_t ADD CONSTRAINT kr_kim_emp_type_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_emp_type_t ADD CONSTRAINT kr_kim_emp_type_tc1 UNIQUE (emp_typ_nm)
/

CREATE TABLE kr_kim_emp_stat_t
(
    emp_stat_cd         VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_emp_stat_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_emp_stat_tn2 NOT NULL,
    emp_stat_nm         VARCHAR2(40),
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_emp_stat_tp1 PRIMARY KEY ( emp_stat_cd )
)
/
ALTER TABLE kr_kim_emp_stat_t ADD CONSTRAINT kr_kim_emp_stat_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_emp_stat_t ADD CONSTRAINT kr_kim_emp_stat_tc1 UNIQUE (emp_stat_nm)
/

CREATE TABLE kr_kim_ctznshp_stat_t
(
    ctznshp_stat_cd     VARCHAR2(40),
    obj_id              VARCHAR2(36) CONSTRAINT kr_kim_ctznshp_stat_tn1 NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_ctznshp_stat_tn2 NOT NULL,
    ctznshp_stat_nm     VARCHAR2(40),
    actv_ind            VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd     VARCHAR2(2),
    last_updt_dt        DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_ctznshp_stat_tp1 PRIMARY KEY ( ctznshp_stat_cd )
)
/
ALTER TABLE kr_kim_ctznshp_stat_t ADD CONSTRAINT kr_kim_ctznshp_stat_tc0 UNIQUE (obj_id)
/
ALTER TABLE kr_kim_ctznshp_stat_t ADD CONSTRAINT kr_kim_ctznshp_stat_tc1 UNIQUE (ctznshp_stat_nm)
/


-- ENTITY TABLES

CREATE TABLE kr_kim_entity_t
(
    entity_id       VARCHAR2(40),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_tn2 NOT NULL,
    actv_ind        VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_tp1 PRIMARY KEY ( entity_id )
)
/
ALTER TABLE kr_kim_entity_t ADD CONSTRAINT kr_kim_entity_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_ent_type_t
(
    entity_ent_type_id VARCHAR2(40), 
    obj_id             VARCHAR2(36) CONSTRAINT kr_kim_entity_ent_type_tn1 NOT NULL,
    ver_nbr            NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_ent_type_tn2 NOT NULL,
    ent_typ_cd         VARCHAR2(40),
    entity_id          VARCHAR2(40),
    actv_ind           VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt       DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_ent_type_tp1 PRIMARY KEY ( entity_ent_type_id )
)
/
ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tc0 UNIQUE (obj_id)
/
 
CREATE TABLE kr_kim_entity_ext_id_t
(
    entity_ext_id_id   VARCHAR2(40), 
    obj_id             VARCHAR2(36) CONSTRAINT kr_kim_entity_ext_id_tn1 NOT NULL,
    ver_nbr            NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_ext_id_tn2 NOT NULL,
    entity_id          VARCHAR2(40),
    ext_id_typ_cd      VARCHAR2(40),
    ext_id             VARCHAR2(100), 
    actv_ind           VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt       DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_ext_id_tp1 PRIMARY KEY ( entity_ext_id_id )
)
/
ALTER TABLE kr_kim_entity_ext_id_t ADD CONSTRAINT kr_kim_entity_ext_id_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_afltn_t
(
    entity_afltn_id    VARCHAR2(40), 
    obj_id             VARCHAR2(36) CONSTRAINT kr_kim_entity_afltn_tn1 NOT NULL,
    ver_nbr            NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_afltn_tn2 NOT NULL,
    entity_id          VARCHAR2(40),
    afltn_typ_cd       VARCHAR2(40),
    campus_cd          VARCHAR2(2), 
    dflt_ind           VARCHAR2(1) DEFAULT 'N',
    actv_ind           VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt       DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_afltn_tp1 PRIMARY KEY ( entity_afltn_id )
)
/
ALTER TABLE kr_kim_entity_afltn_t ADD CONSTRAINT kr_kim_entity_afltn_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_name_t
(
    entity_name_id    VARCHAR2(40),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_name_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_name_tn2 NOT NULL,
    entity_id         VARCHAR2(40),
    name_typ_cd       VARCHAR2(40),
    first_nm          VARCHAR2(40),
    middle_nm         VARCHAR2(40),
    last_nm           VARCHAR2(80),
    suffix_nm         VARCHAR2(20),
    title_nm          VARCHAR2(20),
    dflt_ind          VARCHAR2(1) DEFAULT 'N',
    actv_ind          VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt      DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_name_tp1 PRIMARY KEY ( entity_name_id )
)
/
ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_addr_t
(
    entity_addr_id  VARCHAR2(40),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_addr_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_addr_tn2 NOT NULL,
    entity_id       VARCHAR2(40),
    ent_typ_cd      VARCHAR2(40),
    addr_typ_cd    VARCHAR2(40),
    addr_line_1     VARCHAR2(50),
    addr_line_2     VARCHAR2(50),
    addr_line_3     VARCHAR2(50),
    city_nm         VARCHAR2(30),
    postal_state_cd VARCHAR2(2),
    postal_cd       VARCHAR2(20),
    postal_cntry_cd VARCHAR2(2),
    display_sort_cd VARCHAR2(2),
    dflt_ind        VARCHAR2(1) DEFAULT 'N',
    actv_ind        VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_addr_tp1 PRIMARY KEY ( entity_addr_id )
)
/

ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_phone_t
(
    entity_phone_id VARCHAR2(40),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_phone_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_phone_tn2 NOT NULL,
    entity_id       VARCHAR2(40),
    ent_typ_cd      VARCHAR2(40),
    phone_typ_cd    VARCHAR2(40),
    phone_nbr       VARCHAR2(20),
    phone_extn_nbr  VARCHAR2(8),
    postal_cntry_cd VARCHAR2(2),
    dflt_ind        VARCHAR2(1) DEFAULT 'N',
    actv_ind        VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_phone_tp1 PRIMARY KEY ( entity_phone_id )
)
/

ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_email_t
(
    entity_email_id VARCHAR2(40),
    obj_id          VARCHAR2(36) CONSTRAINT kr_kim_entity_email_tn1 NOT NULL,
    ver_nbr         NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_email_tn2 NOT NULL,
    entity_id       VARCHAR2(40),
    ent_typ_cd      VARCHAR2(40),
    email_typ_cd    VARCHAR2(40),
    email_addr      VARCHAR2(200),
    dflt_ind        VARCHAR2(1) DEFAULT 'N',
    actv_ind        VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_email_tp1 PRIMARY KEY ( entity_email_id )
)
/

ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_priv_pref_t
(
    entity_id           VARCHAR2(40),
    obj_id              VARCHAR2(36) NOT NULL,
    ver_nbr             NUMBER(8,0) DEFAULT 1 NOT NULL,
    suppress_name_ind   VARCHAR2(1) DEFAULT 'N',
    suppress_email_ind  VARCHAR2(1) DEFAULT 'Y',
    suppress_addr_ind   VARCHAR2(1) DEFAULT 'Y',
    suppress_phone_ind  VARCHAR2(1) DEFAULT 'Y',
    suppress_prsnl_ind  VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_priv_pref_tp1 PRIMARY KEY ( entity_id )
)
/

ALTER TABLE kr_kim_entity_priv_pref_t ADD CONSTRAINT kr_kim_entity_priv_pref_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_bio_t
(
    entity_id         VARCHAR2(40),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_bio_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_bio_tn2 NOT NULL,
    ethncty_cd        VARCHAR2(40),
    birth_dt          DATE,
    gndr_cd           VARCHAR2(1) CONSTRAINT kr_kim_entity_bio_tn3 NOT NULL,
    last_updt_dt      DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_bio_tp1 PRIMARY KEY ( entity_id )
)
/
ALTER TABLE kr_kim_entity_bio_t ADD CONSTRAINT kr_kim_entity_bio_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_emp_info_t
(
    entity_emp_id     VARCHAR2(40),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_emp_info_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_emp_info_tn2 NOT NULL,
    entity_id         VARCHAR2(40),
    entity_afltn_id   VARCHAR2(40),
    emp_stat_cd       VARCHAR2(40),
    emp_typ_cd        VARCHAR2(40),
    base_slry_amt     NUMBER(15,2),
    prmry_ind         VARCHAR2(1),
    actv_ind          VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_emp_info_tp1 PRIMARY KEY ( entity_emp_id )
)
/
ALTER TABLE kr_kim_entity_emp_info_t ADD CONSTRAINT kr_kim_entity_emp_info_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_entity_ctznshp_t
(
    entity_ctznshp_id VARCHAR2(40),
    obj_id            VARCHAR2(36) CONSTRAINT kr_kim_entity_ctznshp_tn1 NOT NULL,
    ver_nbr           NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_entity_ctznshp_tn2 NOT NULL,
    entity_id         VARCHAR2(40),
    postal_cntry_cd   VARCHAR2(2),
    ctznshp_stat_cd   VARCHAR2(40),
    strt_dt           DATE,
    end_dt            DATE,
    actv_ind          VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_entity_ctznshp_tp1 PRIMARY KEY ( entity_ctznshp_id )
)
/
ALTER TABLE kr_kim_entity_ctznshp_t ADD CONSTRAINT kr_kim_entity_ctznshp_tc0 UNIQUE (obj_id)
/

-- kr_kim_entity_principal_t (Been added to DEV and DBA)

CREATE TABLE kr_kim_principal_t
(
    prncpl_id VARCHAR2(40) constraint kr_kim_principal_tn1 NOT NULL,
    obj_id VARCHAR2(36) CONSTRAINT kr_kim_principal_tn2 NOT NULL,
    ver_nbr NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_principal_tn3 NOT NULL,
    prncpl_nm VARCHAR2(100) CONSTRAINT kr_kim_principal_tn4 NOT NULL,
    entity_id VARCHAR2(40),
    prncpl_pswd VARCHAR2(400),
    actv_ind VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_principal_tp1 PRIMARY KEY ( prncpl_id )
)
/

ALTER TABLE kr_kim_principal_t ADD CONSTRAINT kr_kim_principal_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_principal_t ADD CONSTRAINT kr_kim_principal_tc1 UNIQUE (prncpl_nm)
/


-- ************************************************************************
-- Group tables
-- ************************************************************************


CREATE TABLE kr_kim_group_t
(
    grp_id    VARCHAR2(40) constraint kr_kim_group_tn1 NOT NULL,
    obj_id    VARCHAR2(36) CONSTRAINT kr_kim_group_tn2 NOT NULL,
    ver_nbr   NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_group_tn3 NOT NULL,
    grp_nm    VARCHAR2(80) CONSTRAINT kr_kim_group_tn4 NOT NULL,
    nmspc_cd  VARCHAR2(40) CONSTRAINT kr_kim_group_tn5 NOT NULL,
    grp_desc  VARCHAR2(4000),
    typ_id    VARCHAR2(40),
    actv_ind  VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_group_tp1 PRIMARY KEY ( grp_id )
)
/

ALTER TABLE kr_kim_group_t ADD CONSTRAINT kr_kim_group_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_group_t ADD CONSTRAINT kr_kim_group_tc1 UNIQUE (grp_nm,nmspc_cd)
/

CREATE TABLE kr_kim_group_group_t
(
    grp_member_id    VARCHAR2(40) constraint kr_kim_group_group_tn1 NOT NULL,
    obj_id           VARCHAR2(36) CONSTRAINT kr_kim_group_group_tn2 NOT NULL,
    ver_nbr          NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_group_group_tn3 NOT NULL,
    grp_id           VARCHAR2(40) constraint kr_kim_group_group_tn4 NOT NULL,
    member_grp_id    VARCHAR2(40) CONSTRAINT kr_kim_group_group_tn5 NOT NULL,
    last_updt_dt     DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_group_group_tp1 PRIMARY KEY ( grp_member_id )
)
/

ALTER TABLE kr_kim_group_group_t ADD CONSTRAINT kr_kim_group_group_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_group_group_t ADD CONSTRAINT kr_kim_group_group_tc1 UNIQUE (grp_id,member_grp_id)
/

CREATE TABLE kr_kim_group_principal_t
(
    grp_member_id    VARCHAR2(40) constraint kr_kim_group_principal_tn1 NOT NULL,
    obj_id           VARCHAR2(36) CONSTRAINT kr_kim_group_principal_tn2 NOT NULL,
    ver_nbr          NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_group_principal_tn3 NOT NULL,
    grp_id           VARCHAR2(40) constraint kr_kim_group_principal_tn4 NOT NULL,
    prncpl_id    VARCHAR2(40) CONSTRAINT kr_kim_group_principal_tn5 NOT NULL,
    last_updt_dt     DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_group_principal_tp1 PRIMARY KEY ( grp_member_id )
)
/

ALTER TABLE kr_kim_group_principal_t ADD CONSTRAINT kr_kim_group_principal_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_group_principal_t ADD CONSTRAINT kr_kim_group_principal_tc1 UNIQUE (grp_id,prncpl_id)
/

-- ************************************************************************
-- role tables
-- ************************************************************************


CREATE TABLE kr_kim_role_t
(
    role_id    VARCHAR2(40) constraint kr_kim_role_tn1 NOT NULL,
    obj_id    VARCHAR2(36) CONSTRAINT kr_kim_role_tn2 NOT NULL,
    ver_nbr   NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_tn3 NOT NULL,
    role_nm    VARCHAR2(80) CONSTRAINT kr_kim_role_tn4 NOT NULL,
    nmspc_cd  VARCHAR2(40) CONSTRAINT kr_kim_role_tn5 NOT NULL,
    role_desc  VARCHAR2(4000),
    typ_id    VARCHAR2(40),
    actv_ind  VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt    DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_role_tp1 PRIMARY KEY ( role_id )
)
/

ALTER TABLE kr_kim_role_t ADD CONSTRAINT kr_kim_role_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_role_t ADD CONSTRAINT kr_kim_role_tc1 UNIQUE (role_nm,nmspc_cd)
/

CREATE TABLE kr_kim_role_principal_t
(
    role_member_id    VARCHAR2(40) constraint kr_kim_role_principal_tn1 NOT NULL,
    obj_id           VARCHAR2(36) CONSTRAINT kr_kim_role_principal_tn2 NOT NULL,
    ver_nbr          NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_principal_tn3 NOT NULL,
    role_id           VARCHAR2(40) constraint kr_kim_role_principal_tn4 NOT NULL,
    prncpl_id    VARCHAR2(40) CONSTRAINT kr_kim_role_principal_tn5 NOT NULL,
    last_updt_dt     DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_role_principal_tp1 PRIMARY KEY ( role_member_id )
)
/

ALTER TABLE kr_kim_role_principal_t ADD CONSTRAINT kr_kim_role_principal_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_role_principal_t ADD CONSTRAINT kr_kim_role_principal_tc1 UNIQUE (role_id,prncpl_id)
/




-- ************************************************************************
-- new authz tables
-- ************************************************************************

CREATE TABLE kr_kim_type_t
(
    kim_type_id           VARCHAR2(40) constraint kr_kim_type_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_type_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_type_tn3 NOT NULL,
    type_nm               VARCHAR2(40),
    srvc_nm               VARCHAR2(200),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_type_tp1 PRIMARY KEY ( kim_type_id )
)
/

ALTER TABLE kr_kim_type_t ADD CONSTRAINT kr_kim_type_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_attribute_t
(
    kim_attrib_id         VARCHAR2(40) constraint kr_kim_attribute_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_attribute_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_attribute_tn3 NOT NULL,
    attrib_nm             VARCHAR2(40),
    srvc_nm               VARCHAR2(200),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_attribute_tp1 PRIMARY KEY ( kim_attrib_id  )
)
/

ALTER TABLE kr_kim_attribute_t ADD CONSTRAINT kr_kim_attribute_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_type_attribute_t
(
    kim_type_attrib_id    VARCHAR2(40) constraint kr_kim_type_attribute_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_type_attribute_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_type_attribute_tn3 NOT NULL,
    kim_type_id           VARCHAR2(40),
    kim_attrib_id         VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_type_attribute_tp1 PRIMARY KEY ( kim_type_attrib_id )
)
/

ALTER TABLE kr_kim_type_attribute_t ADD CONSTRAINT kr_kim_type_attribute_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_group_t
(
    role_member_id        VARCHAR2(40) constraint kr_kim_role_group_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_role_group_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_group_tn3 NOT NULL,
    role_id               VARCHAR2(40),
    grp_id                VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_group_tp1 PRIMARY KEY ( role_member_id  )
)
/

ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_rel_t
(
    role_rel_id           VARCHAR2(40) constraint kr_kim_role_rel_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_role_rel_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_rel_tn3 NOT NULL,
    role_id               VARCHAR2(40),
    contained_role_id     VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_rel_tp1 PRIMARY KEY ( role_rel_id )
)
/

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_group_attr_data_t
(
    attrib_data_id   VARCHAR2(40) NOT NULL,
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key    VARCHAR2(40),
	kim_type_id           VARCHAR2(40),
	kim_attrib_id         VARCHAR2(40),
    attrib_val            VARCHAR2(40),
    CONSTRAINT kr_kim_group_attr_data_tp1 PRIMARY KEY ( attrib_data_id )
)
/

ALTER TABLE kr_kim_group_attr_data_t ADD CONSTRAINT kr_kim_group_attr_data_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_mbr_attr_data_t
(
    attrib_data_id   VARCHAR2(40) NOT NULL,
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key    VARCHAR2(40),
	kim_type_id           VARCHAR2(40),
	kim_attrib_id         VARCHAR2(40),
    attrib_val            VARCHAR2(40),
    CONSTRAINT kr_kim_role_mbr_attr_data_tp1 PRIMARY KEY ( attrib_data_id )
)
/

ALTER TABLE kr_kim_role_mbr_attr_data_t ADD CONSTRAINT kr_kim_role_mbr_attr_data_tc0 UNIQUE (obj_id)
/




-- NEW Tables

CREATE TABLE kr_kim_resp_tmpl_t
(
    resp_tmpl_id         VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    resp_nm               VARCHAR2(40),
    kim_type_id           VARCHAR2(40),
    resp_desc             VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_resp_tmpl_tp1 PRIMARY KEY ( resp_tmpl_id  )
)
/

ALTER TABLE kr_kim_resp_tmpl_t ADD CONSTRAINT kr_kim_resp_tmpl_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_resp_t
(
    resp_id               VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    resp_tmpl_id         VARCHAR2(40),
    resp_nm               VARCHAR2(40),
    resp_desc             VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_resp_tp1 PRIMARY KEY ( resp_id )
)
/

ALTER TABLE kr_kim_resp_t ADD CONSTRAINT kr_kim_resp_tc0 UNIQUE (obj_id)
/


ALTER TABLE kr_kim_resp_t ADD CONSTRAINT kr_kim_resp_tr1
	FOREIGN KEY (resp_tmpl_id)
	REFERENCES kr_kim_resp_tmpl_t
/

CREATE TABLE kr_kim_resp_attr_data_t
(
    attrib_data_id   VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key    VARCHAR2(40),
	kim_type_id           VARCHAR2(40),
	kim_attrib_id         VARCHAR2(40),
    attrib_val            VARCHAR2(40),
    CONSTRAINT kr_kim_resp_attr_data_tp1 PRIMARY KEY ( attrib_data_id )
)
/

ALTER TABLE kr_kim_resp_attr_data_t ADD CONSTRAINT kr_kim_resp_attr_data_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_resp_t
(
    role_resp_id          VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    role_id               VARCHAR2(40),
    resp_id               VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_resp_tp1 PRIMARY KEY ( role_resp_id )
)
/

ALTER TABLE kr_kim_role_resp_t ADD CONSTRAINT kr_kim_role_resp_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_resp_resol_t
(
    role_resp_resol_id    VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    resp_id               VARCHAR2(40),
    role_id               VARCHAR2(40),
    prncpl_id             VARCHAR2(40),
    actn_typ_id           VARCHAR2(40),
    priority_nbr          NUMBER(3),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_resp_resol_tp1 PRIMARY KEY ( role_resp_resol_id )
)
/

ALTER TABLE kr_kim_role_resp_resol_t ADD CONSTRAINT kr_kim_role_resp_resol_tc0 UNIQUE (obj_id)
/



CREATE TABLE kr_kim_perm_tmpl_t
(
    perm_tmpl_id         VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    name                  VARCHAR2(40),
    description           VARCHAR2(40),
    kim_type_id           VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_perm_tmpl_tp1 PRIMARY KEY ( perm_tmpl_id  )
)
/

ALTER TABLE kr_kim_perm_tmpl_t ADD CONSTRAINT kr_kim_perm_tmpl_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_perm_t
(
    perm_id               VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    perm_tmpl_id          VARCHAR2(40),
    name                  VARCHAR2(40),
    description           VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_perm_tp1 PRIMARY KEY ( perm_id  )
)
/

ALTER TABLE kr_kim_perm_t ADD CONSTRAINT kr_kim_perm_tc0 UNIQUE (obj_id)
/


CREATE TABLE kr_kim_role_perm_t
(
    role_perm_id          VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    role_id               VARCHAR2(40),
    perm_id               VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_perm_tp1 PRIMARY KEY ( role_perm_id )
)
/
ALTER TABLE kr_kim_role_perm_t ADD CONSTRAINT kr_kim_role_perm_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_perm_attr_data_t
(
    attrib_data_id        VARCHAR2(40),
    obj_id                VARCHAR2(36) NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key    VARCHAR2(40),
	kim_type_id           VARCHAR2(40),
	kim_attrib_id         VARCHAR2(40),
    attrib_val            VARCHAR2(40),
    CONSTRAINT kr_kim_perm_attr_data_tp1 PRIMARY KEY ( attrib_data_id )
)
/

ALTER TABLE kr_kim_perm_attr_data_t ADD CONSTRAINT kr_kim_perm_attr_data_tc0 UNIQUE (obj_id)
/
