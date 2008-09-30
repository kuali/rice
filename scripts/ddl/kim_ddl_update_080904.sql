DROP TABLE kr_kim_role_role_t
/
CREATE TABLE kr_kim_role_rel_t
(
    role_rel_id    VARCHAR2(40) constraint kr_kim_role_rel_tn1 NOT NULL,
    obj_id           VARCHAR2(36) CONSTRAINT kr_kim_role_rel_tn2 NOT NULL,
    ver_nbr          NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_rel_tn3 NOT NULL,
    role_id           VARCHAR2(40) constraint kr_kim_role_rel_tn4 NOT NULL,
    contained_role_id    VARCHAR2(40) CONSTRAINT kr_kim_role_rel_tn5 NOT NULL,
    last_updt_dt     DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_role_rel_tp1 PRIMARY KEY ( role_rel_id )
)
/

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tc1 UNIQUE (role_id,contained_role_id)
/


ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tr1
	FOREIGN KEY (role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tr2
	FOREIGN KEY (contained_role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/


CREATE TABLE kr_kim_role_group_t
(
    role_member_id    VARCHAR2(40) constraint kr_kim_role_group_tn1 NOT NULL,
    obj_id           VARCHAR2(36) CONSTRAINT kr_kim_role_group_tn2 NOT NULL,
    ver_nbr          NUMBER(8,0) DEFAULT 1 CONSTRAINT kr_kim_role_group_tn3 NOT NULL,
    role_id           VARCHAR2(40) constraint kr_kim_role_group_tn4 NOT NULL,
    grp_id    VARCHAR2(40) CONSTRAINT kr_kim_role_group_tn5 NOT NULL,
    last_updt_dt     DATE DEFAULT SYSDATE,
    CONSTRAINT kr_kim_role_group_tp1 PRIMARY KEY ( role_member_id )
)
/

ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tc0 UNIQUE (obj_id)
/

ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tc1 UNIQUE (role_id,grp_id)
/


ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tr1
	FOREIGN KEY (role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tr2
	FOREIGN KEY (grp_id)
	REFERENCES kr_kim_group_t
	ON DELETE CASCADE
/

CREATE SEQUENCE kr_kim_role_rel_id_seq INCREMENT BY 1
	START WITH 1000
    NOMAXVALUE NOMINVALUE NOCYCLE
    CACHE 100 ORDER
/
INSERT INTO KR_KIM_role_rel_T ( role_rel_id, OBJ_ID, role_ID, contained_role_ID )
    SELECT kr_kim_role_rel_id_seq.NEXTVAL, SYS_GUID(), g.role_ID, g2.role_id
            --, g.role_NM, g2.role_nm
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KR_KIM_role_T g
            , EN_WRKGRP_T w2, KR_KIM_role_T g2
        WHERE m.WRKGRP_MBR_TYP = 'W'
          AND w.WRKGRP_ACTV_IND = '1'
          AND w.WRKGRP_CUR_IND = '1'
          AND m.WRKGRP_ID = w.WRKGRP_ID
          AND m.WRKGRP_VER_NBR = w.WRKGRP_VER_NBR
          AND g.role_NM = w.WRKGRP_NM
          AND w2.WRKGRP_ID = m.WRKGRP_MBR_PRSN_EN_ID
          AND g2.role_NM = w2.WRKGRP_NM
          AND w2.WRKGRP_ACTV_IND = '1'
          AND w2.WRKGRP_CUR_IND = '1'
/
COMMIT
/

