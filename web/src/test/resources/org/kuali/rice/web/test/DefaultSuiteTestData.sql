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
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, OBJ_ID) values (1, 'fred', '5EA45238F5528846E0404F8189D840B8')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, OBJ_ID) values (2, 'fran', '5EA45238F5528846E0404F8189D840B8')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name, OBJ_ID) values (3, 'frank', '5EA45238F5528846E0404F8189D840B8')
;
insert into TRV_ACCT_TYPE values ('CAT', 'Clearing Account Type', '5EA45238F5528846E0404F8189D840B8', 1)
;
insert into TRV_ACCT_TYPE values ('EAT', 'Expense Account Type', '5EA45238F5528846E0404F8189D840B8', 1)
;
insert into TRV_ACCT_TYPE values ('IAT', 'Income Account Type', '5EA45238F5528846E0404F8189D840B8', 1)
;
insert into TRV_ACCT values ('a1', 'a1', 'CAT', 1, '5EF45238F5528846E0404F8189D840B8', 1)
;
insert into TRV_ACCT values ('a2', 'a2', 'EAT', 2, '5EF45238F5528846E0404F8189D840B9', 1)
;
insert into TRV_ACCT values ('a3', 'a3', 'IAT', 3, '5EF45238F5528846E0404F8189D840BA', 1)
;
insert into TRV_ACCT_EXT values ('a1', 'CAT', '5EF45238F5528846E0404F8189D840B8', 1)
;
insert into TRV_ACCT_EXT values ('a2', 'EAT', '5EF45238F5528846E0404F8189D840B9', 1)
;
insert into TRV_ACCT_EXT values ('a3', 'IAT', '5EF45238F5528846E0404F8189D840BA', 1)
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a1')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a2')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a3')
;
