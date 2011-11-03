--
-- Copyright 2005-2011 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

INSERT INTO KRIM_GRP_T ( GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID )
    (select WRKGRP_ID, SYS_GUID(), WRKGRP_NM, 'KUALI', WRKGRP_DESC, WRKGRP_TYP_CD
    from EN_WRKGRP_T
    WHERE WRKGRP_ACTV_IND = '1'
      AND WRKGRP_CUR_IND = '1'
)
/
INSERT INTO KRIM_GRP_MBR_T ( GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD )
    SELECT KRIM_GRP_MBR_ID_S.NEXTVAL, SYS_GUID(), g.GRP_ID, g2.grp_id, 'W'
            --, g.GRP_NM, g2.grp_nm
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KRIM_GRP_T g
            , EN_WRKGRP_T w2, KRIM_GRP_T g2
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
INSERT INTO KRIM_GRP_MBR_T ( GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID,  MBR_TYP_CD)
    SELECT KRIM_GRP_MBR_ID_S.NEXTVAL, SYS_GUID(), g.GRP_ID, m.WRKGRP_MBR_PRSN_EN_ID, 'P'
        FROM EN_WRKGRP_MBR_T m, EN_WRKGRP_T w, KRIM_GRP_T g
        WHERE m.WRKGRP_MBR_TYP = 'U'
          AND w.WRKGRP_ACTV_IND = '1'
          AND w.WRKGRP_CUR_IND = '1'
          AND m.WRKGRP_ID = w.WRKGRP_ID
          AND m.WRKGRP_VER_NBR = w.WRKGRP_VER_NBR
          AND g.GRP_NM = w.WRKGRP_NM
/


