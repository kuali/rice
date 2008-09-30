INSERT INTO KR_KIM_GROUP_T ( GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, TYP_ID )
    (select WRKGRP_ID, SYS_GUID(), WRKGRP_NM, 'KFS', WRKGRP_DESC, WRKGRP_TYP_CD
    from EN_WRKGRP_T
    WHERE WRKGRP_ACTV_IND = '1'
      AND WRKGRP_CUR_IND = '1'
)
/
INSERT INTO KR_KIM_GROUP_GROUP_T ( GRP_MEMBER_ID, OBJ_ID, GRP_ID, MEMBER_GRP_ID )
    SELECT kr_kim_group_member_id_seq.NEXTVAL, SYS_GUID(), g.GRP_ID, g2.grp_id
            --, g.GRP_NM, g2.grp_nm
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KR_KIM_GROUP_T g
            , EN_WRKGRP_T w2, KR_KIM_GROUP_T g2
        WHERE m.WRKGRP_MBR_TYP = 'W'
          AND w.WRKGRP_ACTV_IND = '1'
          AND w.WRKGRP_CUR_IND = '1'
          AND m.WRKGRP_ID = w.WRKGRP_ID
          AND m.WRKGRP_VER_NBR = w.WRKGRP_VER_NBR
          AND g.GRP_NM = w.WRKGRP_NM
          AND w2.WRKGRP_ID = m.WRKGRP_MBR_PRSN_EN_ID
          AND g2.GRP_NM = w2.WRKGRP_NM
          AND w2.WRKGRP_ACTV_IND = '1'
          AND w2.WRKGRP_CUR_IND = '1'
/
INSERT INTO KR_KIM_GROUP_PRINCIPAL_T ( GRP_MEMBER_ID, OBJ_ID, GRP_ID, PRNCPL_ID )
    SELECT kr_kim_group_member_id_seq.NEXTVAL, SYS_GUID(), g.GRP_ID, m.WRKGRP_MBR_PRSN_EN_ID
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KR_KIM_GROUP_T g
        WHERE m.WRKGRP_MBR_TYP = 'U'
          AND w.WRKGRP_ACTV_IND = '1'
          AND w.WRKGRP_CUR_IND = '1'
          AND m.WRKGRP_ID = w.WRKGRP_ID
          AND m.WRKGRP_VER_NBR = w.WRKGRP_VER_NBR
          AND g.GRP_NM = w.WRKGRP_NM
/
COMMIT
/

drop sequence kr_kim_group_id_seq
/
create sequence kr_kim_group_id_seq increment by 1 start with 1000000
/
