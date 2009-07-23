/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
------------------ KEW UPGRADE -----------------------
-- A few additions for JPA inheritance --
alter table EN_USR_T add (DTYPE VARCHAR2(50))
/
alter table EN_DOC_HDR_T add (DTYPE VARCHAR2(50))
/
alter table EN_ACTN_ITM_T add (DTYPE VARCHAR2(50))
/
CREATE INDEX EN_ACTN_ITM_TI5 ON EN_ACTN_ITM_T (ACTN_ITM_PRSN_EN_ID, DLGN_TYP, DOC_HDR_ID)
/

-- Reporting Workgroup --
ALTER TABLE EN_DOC_TYP_T ADD RPT_WRKGRP_ID NUMBER(14) NULL
/
CREATE INDEX EN_DOC_TYP_TI7 ON EN_DOC_TYP_T (RPT_WRKGRP_ID)
/

-- EDL Indexes
CREATE INDEX EN_DOC_DMP_TI1 ON EN_EDL_DMP_T (DOC_TYP_NM, DOC_HDR_ID)
/
CREATE INDEX EN_EDL_FIELD_DMP_TI1 ON EN_EDL_FIELD_DMP_T (DOC_HDR_ID, FLD_NM, FLD_VAL)
/
CREATE INDEX EN_EDL_FIELD_DMP_TI2 ON EN_EDL_FIELD_DMP_T (FLD_NM, FLD_VAL)
/
