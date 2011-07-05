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
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, obj_id, ver_nbr) values (1, 'fred', '2405B8D44B8347ACA107E59099ADFE8E', 1)
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, obj_id, ver_nbr) values (2, 'fran', '4C756C7C96514B28AD1C381F05374B7E', 1)
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, obj_id, ver_nbr) values (3, 'frank', 'EBDDAA1CF0904AACB9B2D0C53CB55B94', 1)
;
insert into TRV_ACCT_TYPE values ('CAT', 'Clearing Account Type', '2661F83AD74943E688D5F4DB7173914C', 1)
;
insert into TRV_ACCT_TYPE values ('EAT', 'Expense Account Type', 'B0B067E2D30B48AEA01AA630B0E0C7C4', 1)
;
insert into TRV_ACCT_TYPE values ('IAT', 'Income Account Type', '37DA37109BCB421BAF78E6F8824921B6', 1)
;
insert into TRV_ACCT values ('a1', 'a1', 'CAT', 1, '7003409C8F824FDD836C799F87A2C04C', 1, null, null, null, null)
;
insert into TRV_ACCT values ('a2', 'a2', 'EAT', 2, 'DC6F5FE5003B483092F21D99AF4C701A', 1, null, null, null, null)
;
insert into TRV_ACCT values ('a3', 'a3', 'IAT', 3, 'C68B618C117A4234A33F0C3146E92439', 1, null, null, null, null)
;
insert into TRV_ACCT_EXT values ('a1', 'CAT', '8AE92DD8F6F248179F40BB9E4C095B9D', 1)
;
insert into TRV_ACCT_EXT values ('a2', 'EAT', '64BD0F58C50447A89AC1058940DCC6BD', 1)
;
insert into TRV_ACCT_EXT values ('a3', 'IAT', 'E4278BA8AA024A7D90F1F8173EA72082', 1)
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values ('1', 'a1')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values ('1', 'a2')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values ('1', 'a3')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y', '13369942-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('DH', 'DOCUMENT HEADER', 'Y', '201993e4-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND)
  VALUES('93', '5B4F0974284DEF33ED404F8189D44F24', '2', '93RSPNAME', null, 'KR-SYS', 'Y')
;
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
  VALUES('1080', '5DF45238F5528846E0404F8189D840B8', 1, '63', '93', 'Y')
;
-- assign to 'WorkflowAdmin'
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD)
VALUES('2000', 1, '62311426-7dfb-102c-97b6-ed716fdaf540', '63', '1', 'G')
;
insert into krim_perm_t (perm_id, obj_id, ver_nbr, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind)
values ('1652','4348B3EDA0204A9A82D11801A0B5BF89',1,'4', 'KR-NS', 'Blanket Approve Document', 'Allow blanket approvals of documents', 'Y')
;
insert into krim_perm_attr_data_t (attr_data_id, obj_id, ver_nbr, perm_id, kim_typ_id, kim_attr_defn_id, attr_val)
values ('1000', '9A6B69E11DA1477FB0FD899A3C746A17', 1, '1652', '3', '13', 'RiceDocument')
;
insert into krim_role_perm_t (role_perm_id, obj_id, ver_nbr, role_id, perm_id, actv_ind)
values ('2112', '7FE9BCE73E2748FEB56DB358F0FFA84F', 1, '63', '1652', 'Y')
;
