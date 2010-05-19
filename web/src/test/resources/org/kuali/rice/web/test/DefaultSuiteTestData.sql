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
insert into TRV_ACCT_TYPE values ('CAT', 'Clearing Account Type', '2661F83AD74943E688D5F4DB7173914C', 1)
;
insert into TRV_ACCT_TYPE values ('EAT', 'Expense Account Type', 'B0B067E2D30B48AEA01AA630B0E0C7C4', 1)
;
insert into TRV_ACCT_TYPE values ('IAT', 'Income Account Type', '37DA37109BCB421BAF78E6F8824921B6', 1)
;
insert into TRV_ACCT values ('a1', 'a1', 'CAT', 1, '7003409C8F824FDD836C799F87A2C04C', 1)
;
insert into TRV_ACCT values ('a2', 'a2', 'EAT', 2, 'DC6F5FE5003B483092F21D99AF4C701A', 1)
;
insert into TRV_ACCT values ('a3', 'a3', 'IAT', 3, 'C68B618C117A4234A33F0C3146E92439', 1)
;
insert into TRV_ACCT_EXT values ('a1', 'CAT', '8AE92DD8F6F248179F40BB9E4C095B9D', 1)
;
insert into TRV_ACCT_EXT values ('a2', 'EAT', '64BD0F58C50447A89AC1058940DCC6BD', 1)
;
insert into TRV_ACCT_EXT values ('a3', 'IAT', 'E4278BA8AA024A7D90F1F8173EA72082', 1)
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a1')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a2')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a3')
;
