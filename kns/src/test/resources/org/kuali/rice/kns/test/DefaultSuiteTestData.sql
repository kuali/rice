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
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (1, 'fred')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (2, 'fran')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (3, 'frank')
;
insert into TRV_ACCT_TYPE values ('CAT', 'Clearing Account Type')
;
insert into TRV_ACCT_TYPE values ('EAT', 'Expense Account Type')
;
insert into TRV_ACCT_TYPE values ('IAT', 'Income Account Type')
;
insert into TRV_ACCT values ('a1', 'a1', 'CAT', 1)
;
insert into TRV_ACCT values ('a2', 'a2', 'EAT', 2)
;
insert into TRV_ACCT values ('a3', 'a3', 'IAT', 3)
;
insert into TRV_ACCT_EXT values ('a1', 'CAT')
;
insert into TRV_ACCT_EXT values ('a2', 'EAT')
;
insert into TRV_ACCT_EXT values ('a3', 'IAT')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a1')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a2')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a3')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y', '13369942-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('DH', 'DOCUMENT HEADER', 'Y', '201993e4-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND)
  VALUES('93', '5B4F0974284DEF33ED404F8189D44F24', '2', null, null, 'KR-SYS', 'Y')
;
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
  VALUES('1080', '5DF45238F5528846E0404F8189D840B8', 1, '63', '93', 'Y')
;
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('1651', '5BAF0974495DEF33E0404F8189D84F24', 1, '1', 'Administer Pessimistic Locking', null, 'Y', 'KR-NS')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('2111', '5C27A26EJFD787417E0404F8189D830A', 1, '63', '1651', 'Y')
;
-- assign to 'WorkflowAdmin'
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD)
VALUES('2000', 1, '62311426-7dfb-102c-97b6-ed716fdaf540', '63', '1', 'G')
;
