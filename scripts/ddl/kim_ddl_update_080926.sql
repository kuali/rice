DROP table KR_KIM_GROUP_ATTRIBUTE_T cascade constraints
/

CREATE TABLE kr_kim_group_attribute_t
(
  grp_attrib_id      VARCHAR2(40) constraint kr_kim_group_attribute_tn1 NOT NULL,
  obj_id             VARCHAR2(36) CONSTRAINT kr_kim_group_attribute_tn2 NOT NULL,
  ver_nbr            NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_group_attribute_tn3 NOT NULL,
  grp_id             VARCHAR2(40),
  kim_type_attrib_id VARCHAR2(40),
  attrib_val         VARCHAR2(100),
  CONSTRAINT kr_kim_group_attribute_tp1 PRIMARY KEY ( grp_attrib_id )
)
/

ALTER TABLE kr_kim_group_attribute_t ADD CONSTRAINT kr_kim_group_attribute_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_responsibility_t
(
    role_resp_id          VARCHAR2(40) constraint kr_kim_role_responsibility_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_role_responsibility_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_responsibility_tn3 NOT NULL,
    role_id               VARCHAR2(40),
    resp_id               VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_role_responsibility_tp1 PRIMARY KEY ( role_resp_id )
)
/

ALTER TABLE kr_kim_role_responsibility_t ADD CONSTRAINT kr_kim_role_responsibility_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_role_resp_attrib_t
(
    role_resp_attrib_id   VARCHAR2(40) constraint kr_kim_role_resp_attrib_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_role_resp_attrib_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_resp_attrib_tn3 NOT NULL,
    role_resp_id          VARCHAR2(40),
    kim_type_attrib_id    VARCHAR2(40),
    attrib_val            VARCHAR2(40),
    CONSTRAINT kr_kim_role_resp_attrib_tp1 PRIMARY KEY ( role_resp_attrib_id )
)
/

ALTER TABLE kr_kim_role_resp_attrib_t ADD CONSTRAINT kr_kim_role_resp_attrib_tc0 UNIQUE (obj_id)
/

CREATE TABLE kr_kim_responsibility_t
(
    resp_id               VARCHAR2(40) constraint kr_kim_responsibility_tn1 NOT NULL,
    obj_id                VARCHAR2(36) CONSTRAINT kr_kim_responsibility_tn2 NOT NULL,
    ver_nbr               NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_responsibility_tn3 NOT NULL,
    nmspc_cd              VARCHAR2(40),
    resp_nm               VARCHAR2(40),
    kim_type_id           VARCHAR2(40),
    resp_desc             VARCHAR2(40),
    actv_ind              VARCHAR2(1) DEFAULT 'Y',
    CONSTRAINT kr_kim_responsibility_tp1 PRIMARY KEY ( resp_id  )
)
/

ALTER TABLE kr_kim_responsibility_t ADD CONSTRAINT kr_kim_responsibility_tc0 UNIQUE (obj_id)
/

-- drop constraints between KIM sub-modules
ALTER TABLE kr_kim_role_group_t DROP CONSTRAINT kr_kim_role_group_tr2
/
ALTER TABLE kr_kim_role_principal_t DROP CONSTRAINT kr_kim_role_principal_tr2
/
ALTER TABLE kr_kim_group_principal_t DROP CONSTRAINT kr_kim_group_principal_tr2
/
ALTER TABLE kr_kim_role_perm_attrib_t DROP CONSTRAINT kr_kim_role_perm_attrib_tr1
/
ALTER TABLE kr_kim_role_perm_attrib_t DROP CONSTRAINT kr_kim_role_perm_attrib_tr2
/
-- fix reference (was linked to wrong table)
ALTER TABLE kr_kim_role_perm_attrib_t ADD CONSTRAINT kr_kim_role_perm_attrib_tr1
    FOREIGN KEY (kim_type_attrib_id)
    REFERENCES kr_kim_type_attribute_t
    ON DELETE CASCADE
/
-- incorrect reference
ALTER TABLE kr_kim_role_member_attrib_t DROP CONSTRAINT kr_kim_role_member_attrib_tr1
/
ALTER TABLE kr_kim_role_perm_attrib_t ADD CONSTRAINT kr_kim_role_perm_attrib_tr2
    FOREIGN KEY (role_perm_id)
    REFERENCES kr_kim_role_permission_t
    ON DELETE CASCADE
/

ALTER TABLE kr_kim_role_resp_attrib_t ADD CONSTRAINT kr_kim_role_resp_attrib_tr1
    FOREIGN KEY (kim_type_attrib_id)
    REFERENCES kr_kim_type_attribute_t
    ON DELETE CASCADE
/
ALTER TABLE kr_kim_role_resp_attrib_t ADD CONSTRAINT kr_kim_role_resp_attrib_tr2
    FOREIGN KEY (role_resp_id)
    REFERENCES kr_kim_role_responsibility_t
    ON DELETE CASCADE
/

-- new sequences

CREATE SEQUENCE kr_kim_resp_id_seq INCREMENT BY 1
    START WITH 1000
    NOMAXVALUE NOMINVALUE NOCYCLE
    CACHE 100 ORDER
/
CREATE SEQUENCE kr_kim_role_resp_id_seq INCREMENT BY 1
    START WITH 1000
    NOMAXVALUE NOMINVALUE NOCYCLE
    CACHE 100 ORDER
/

CREATE SEQUENCE kr_kim_role_resp_attrib_id_seq INCREMENT BY 1
    START WITH 1000
    NOMAXVALUE NOMINVALUE NOCYCLE
    CACHE 100 ORDER
/
