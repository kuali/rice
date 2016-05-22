--
-- Copyright 2005-2016 The Kuali Foundation
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




-- ===================================================================================
-- 2014-05-19--KULRICE-10653.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-10653
-- ===================================================================================


ALTER TABLE KRMS_NL_TMPL_T ADD ACTV VARCHAR(1) DEFAULT 'Y' NOT NULL
/


-- ===================================================================================
-- 2014-05-22--KULRICE-6281.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-6281
-- ===================================================================================


UPDATE KRCR_PARM_T SET VAL = 'http://site.kuali.org/rice/${rice.version}/reference/html/Help.html#lookup'
WHERE APPL_ID = 'KUALI' AND NMSPC_CD = 'KR-KRAD' AND CMPNT_CD = 'Lookup' AND PARM_NM = 'DEFAULT_HELP_URL'
/


-- ===================================================================================
-- 2014-06-02--KULRICE-12767.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-12767
-- ===================================================================================


INSERT INTO KRCR_PARM_T (APPL_ID, NMSPC_CD, CMPNT_CD, PARM_NM, VAL, PARM_DESC_TXT, PARM_TYP_CD, EVAL_OPRTR_CD, OBJ_ID, VER_NBR)
    SELECT 'KUALI', 'KR-NS', 'All', 'DEFAULT_COUNTRY', 'US', 'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.', 'CONFG', 'A', UUID(), 1 FROM dual
    WHERE NOT EXISTS (SELECT 1 FROM KRCR_PARM_T WHERE NMSPC_CD = 'KR-NS' AND CMPNT_CD = 'All' AND PARM_NM = 'DEFAULT_COUNTRY')
/


-- ===================================================================================
-- 2014-06-27--KULRICE-12893.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-12893
-- ===================================================================================


DELETE FROM KRIM_ROLE_PERM_T
WHERE ROLE_ID = (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD = 'KR-RULE' AND ROLE_NM = 'Kuali Rules Management System Administrator')
AND PERM_ID = (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KR-RULE-TEST' AND NM = 'Maintain KRMS Agenda')
/

DELETE FROM KRIM_PERM_T WHERE NMSPC_CD = 'KR-RULE-TEST' AND NM = 'Maintain KRMS Agenda'
/


-- ===================================================================================
-- 2014-08-22--KULRICE-12691.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-12691
-- ===================================================================================


alter table KREW_PPL_FLW_MBR_T add column FRC_ACTN DECIMAL(1) default 1 not null
/


-- ===================================================================================
-- 2014-08-26--KULRICE-9109.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-9109
-- ===================================================================================


INSERT INTO KRCR_PARM_T (APPL_ID, NMSPC_CD, CMPNT_CD, PARM_NM, VAL, PARM_DESC_TXT, PARM_TYP_CD, EVAL_OPRTR_CD, OBJ_ID, VER_NBR)
    VALUES ('KUALI', 'KR-KRAD', 'All', 'AUTO_TRUNCATE_COLUMNS', 'N', 'Automatically truncate text that does not fit into table columns.  A tooltip with the non-trucated text on hover over.', 'CONFG', 'A', UUID(), 1)
/
INSERT INTO KRCR_PARM_T (APPL_ID, NMSPC_CD, CMPNT_CD, PARM_NM, VAL, PARM_DESC_TXT, PARM_TYP_CD, EVAL_OPRTR_CD, OBJ_ID, VER_NBR)
    VALUES ('KUALI', 'KR-KRAD', 'Lookup', 'AUTO_TRUNCATE_COLUMNS', 'N', 'Automatically truncate text that does not fit into lookup result columns.  A tooltip with the non-trucated text on hover over.', 'CONFG', 'A', UUID(), 1)
/