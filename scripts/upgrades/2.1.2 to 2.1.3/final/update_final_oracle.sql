--
-- Copyright 2005-2017 The Kuali Foundation
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




-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2012-10-23.sql
-- 


--
--    KULRICE-8302 - Backdoor Restriction via Permission
--

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
VALUES('KR1001', SYS_GUID(), 1, 'Backdoor Restriction', 'backdoorRestrictionPermissionTypeService','Y','KR-SYS')
/

INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, ACTV_IND, NMSPC_CD, CMPNT_NM)
VALUES('KR1000', SYS_GUID(), 1,'appCode','Application Code', 'Y', 'KR-SYS', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
VALUES('KR1003', SYS_GUID(),1,'A',
    (select KIM_TYP_ID from KRIM_TYP_T where nm = 'Backdoor Restriction'),
    (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where nm = 'appCode'),'Y')
/

INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, VER_NBR, NMSPC_CD, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND)
VALUES('KR1003', SYS_GUID(), 1, 'KR-SYS', 'Backdoor Restriction', 'Backdoor Restriction',
      (select KIM_TYP_ID from KRIM_TYP_T where nm = 'Backdoor Restriction'),'Y')
/



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2012-11-15.sql
-- 


--
--    KULRICE-8415 - Large roles cannot be opened or edited in KIM
--

alter table KRIM_TYP_ATTR_T ADD constraint KRIM_TYP_ATTR_TC1 unique (SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
/


--
--    KULRICE-4559 - Add Type as a qualifying value for Assign Group permissions
--


INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, ACTV_IND, NMSPC_CD, CMPNT_NM)
  VALUES('KR1001', SYS_GUID(), 1, 'kimTypeName', 'Kim Type Name', 'Y', 'KR-IDM', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('KR1004',  SYS_GUID(), 1, 'b',
  (SELECT KIM_TYP_ID from KRIM_TYP_T where NM = 'Group' and SRVC_NM = 'groupPermissionTypeService'),
  (SELECT KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'kimTypeName' and NMSPC_CD = 'KR-IDM'), 'Y')
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2012-11-28.sql
-- 


--
--    KULRICE-7842 - Ad Hoc Route for Completion recipient does not have the Approve action available in the
--                   Action Requested drop down field
--

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
  VALUES('KR1000', SYS_GUID(), 1,
    (Select PERM_TMPL_ID from KRIM_PERM_TMPL_T where NM = 'Take Requested Action'),
    'KUALI', 'Take Requested Complete Action',
    'Authorizes users to take the Complete action on documents routed to them.', 'Y')
/

INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('KR1000', SYS_GUID(), 1, 'KR1000',
  (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Action Request Type'),
  (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'actionRequestCd'), 'C')
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT)
  VALUES('KR1001', SYS_GUID(), 1, 'Complete Request Recipient', 'KR-WKFLW',
    'This role derives its members from users with an complete action request in the route log of a given document.',
    (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Derived Role: Action Request'), 'Y', NULL)
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('KR1000', SYS_GUID(), 1,
    (Select ROLE_ID from KRIM_ROLE_T where ROLE_NM = 'Complete Request Recipient'),
    (Select PERM_ID from KRIM_PERM_T where NM = 'Take Requested Complete Action'), 'Y')
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('KR1001', SYS_GUID(), 1,
    (Select ROLE_ID from KRIM_ROLE_T where ROLE_NM = 'Complete Request Recipient'),
    (Select PERM_ID from KRIM_PERM_T where NM = 'Edit Kuali ENROUTE Document Route Status Code R'), 'Y')
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2012-12-12.sql
-- 


--
-- KULRICE-8573: Add session id to locks and delete these locks when session is destroyed.
--

ALTER TABLE KRNS_PESSIMISTIC_LOCK_T ADD SESN_ID VARCHAR2(40) DEFAULT '' NOT NULL
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2013-01-09.sql
-- 




-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
--
-- NOTE: Please do *not* apply this to Rice's master database, this is redundant to a statement
--       that has been added to bootstrap-server-dataset-cleanup.sql
--
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

-- delete the assignment of the recall from routing permission for KULRICE-7687
delete from krim_role_perm_t where
  role_id = (select role_id from krim_role_t where nmspc_cd = 'KR-WKFLW' and role_nm = 'Initiator') and
  perm_id = (select PERM_ID from krim_perm_t where nmspc_cd = 'KR-WKFLW' and nm = 'Recall Document')
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2013-01-14.sql
-- 


-- Add column for Document Type Authorizer
ALTER TABLE KREW_DOC_TYP_T ADD AUTHORIZER VARCHAR(255) DEFAULT NULL
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2013-01-16.sql
-- 



-- KULRICE-8177: CONTRIB: Identity Mgmt Section listed as "Undefined"
update KREW_DOC_TYP_T set LBL = 'Identity Management Document' where
  DOC_TYP_NM = 'IdentityManagementDocument' and LBL = 'Undefined'
/


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- 2013-02-19.sql
--

--
-- KULRICE-8985: Add Index to prevent deadlocks during deletion of KSB entries
--

CREATE INDEX KRSB_SVC_DEF_TI4 ON KRSB_SVC_DEF_T(SVC_DSCRPTR_ID)
/

