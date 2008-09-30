INSERT INTO KR_KIM_role_T ( role_ID, OBJ_ID, role_NM, NMSPC_CD, role_DESC, TYP_ID )
    (select kr_kim_role_id_seq.NEXTVAL, SYS_GUID(), WRKGRP_NM, 'KFS', WRKGRP_DESC, WRKGRP_TYP_CD
    from EN_WRKGRP_T
    WHERE WRKGRP_ACTV_IND = '1'
      AND WRKGRP_CUR_IND = '1'
)
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
INSERT INTO KR_KIM_role_PRINCIPAL_T ( role_MEMBER_ID, OBJ_ID, role_ID, PRNCPL_ID )
    SELECT kr_kim_role_member_id_seq.NEXTVAL, SYS_GUID(), g.role_ID, m.WRKGRP_MBR_PRSN_EN_ID
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KR_KIM_role_T g
        WHERE m.WRKGRP_MBR_TYP = 'U'
          AND w.WRKGRP_ACTV_IND = '1'
          AND w.WRKGRP_CUR_IND = '1'
          AND m.WRKGRP_ID = w.WRKGRP_ID
          AND m.WRKGRP_VER_NBR = w.WRKGRP_VER_NBR
          AND g.role_NM = w.WRKGRP_NM
/
COMMIT
/
