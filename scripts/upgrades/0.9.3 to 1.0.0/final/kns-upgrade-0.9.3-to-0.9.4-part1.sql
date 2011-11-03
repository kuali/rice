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

CREATE TABLE KR_KNS_SESN_DOC_T (
        SESSION_ID                     VARCHAR2(40) CONSTRAINT KR_KNS_SESN_DOC_TN1 NOT NULL,
        FDOC_NBR                       VARCHAR2(14) CONSTRAINT KR_KNS_SESN_DOC_TN2 NOT NULL,
        SERIALIZED_DOC_FRM             BLOB,
        LST_UPDATE_DT                  DATE,
     CONSTRAINT KR_KNS_SESN_DOC_TP1 PRIMARY KEY (SESSION_ID, FDOC_NBR)
)
/

CREATE INDEX KR_KNS_SESN_DOC_TI1 ON KR_KNS_SESN_DOC_T ( LST_UPDATE_DT )
/

-- ALTER TABLE SH_PARM_T DROP COLUMN GRP_NM
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD,SH_PARM_DTL_TYP_CD,SH_PARM_NM,OBJ_ID,VER_NBR,SH_PARM_TYP_CD,SH_PARM_TXT,SH_PARM_DESC,SH_PARM_CONS_CD,WRKGRP_NM)
VALUES
('KR-NS','State','STATE', SYS_GUID(), 1,'HELP','default.htm?turl=WordDocuments%2Fstatemaintenancedocument.htm','Help URL for State document.','A','FP_OPERATIONS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD,SH_PARM_DTL_TYP_CD,SH_PARM_NM,OBJ_ID,VER_NBR,SH_PARM_TYP_CD,SH_PARM_TXT,SH_PARM_DESC,SH_PARM_CONS_CD,WRKGRP_NM)
VALUES
('KR-NS','Country','COUNTRY', SYS_GUID(), 1,'HELP','default.htm?turl=WordDocuments%2Fcountrymaintenancedocument.htm','Help URL for Chart Country.','A','FP_OPERATIONS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD,SH_PARM_DTL_TYP_CD,SH_PARM_NM,OBJ_ID,VER_NBR,SH_PARM_TYP_CD,SH_PARM_TXT,SH_PARM_DESC,SH_PARM_CONS_CD,WRKGRP_NM)
VALUES
('KR-NS','PostalCode','POSTAL_CODE', SYS_GUID(),1,'HELP','default.htm?turl=WordDocuments%2Fpostalcodemaintenancedocument.htm','Help URL for Postal Code document.','A','FP_OPERATIONS')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD,SH_PARM_DTL_TYP_CD,SH_PARM_NM,OBJ_ID,VER_NBR,SH_PARM_TYP_CD,SH_PARM_TXT,SH_PARM_DESC,SH_PARM_CONS_CD,WRKGRP_NM)
VALUES
('KR-NS','All','DEFAULT_COUNTRY',  SYS_GUID(),1,'CONFG','US','Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.','A','FP_OPERATIONS')
/
