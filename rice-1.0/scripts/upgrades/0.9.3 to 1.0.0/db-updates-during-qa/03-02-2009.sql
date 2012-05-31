-- 
-- Copyright 2009 The Kuali Foundation
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
-- need to remember to go back to "action-list-helpdesk-permissions.sql" and "doc-search-permissions.sql"
-- and reconcile related changes with these when we build the final upgrade scripts
-- (essentially, those old scripts can be removed)
--
-- UPDATE - 3/21/2009 - i've removed these scripts
-- so the deletions below won't need to be included in the finla upgrade script!!!

-- first of all, delete the original setup

DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List'
	)
)
/
DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search'
	)
)
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List')
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search')
/
DELETE FROM KRIM_PERM_TMPL_T where NM='View Other Action List'
/
DELETE FROM KRIM_PERM_TMPL_T where NM='Unrestricted Document Search'
/
commit
/

-- now, create as default permissions

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('298', sys_guid(), 1, '1', 'View Other Action List', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('564', sys_guid(), 1, '63', '298', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('299', sys_guid(), 1, '1', 'Unrestricted Document Search', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('566', sys_guid(), 1, '63', '299', 'Y')
/

commit
/
