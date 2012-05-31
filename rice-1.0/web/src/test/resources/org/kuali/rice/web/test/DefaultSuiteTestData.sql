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
insert into TRV_ACCT_USE_RT_T (ID, ACCT_NUM, RATE, ACTV_FRM_DT, ACTV_TO_DT) values ('1', 'a1', 1.5, {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'})
;                                                                          
insert into TRV_ACCT_USE_RT_T (ID, ACCT_NUM, RATE, ACTV_FRM_DT, ACTV_TO_DT) values ('2', 'a2', 1.5, {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'})
;                                                                          
insert into TRV_ACCT_USE_RT_T (ID, ACCT_NUM, RATE, ACTV_FRM_DT, ACTV_TO_DT) values ('3', 'a2', 1.5, {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'})
;                                                                          
insert into TRV_ACCT_USE_RT_T (ID, ACCT_NUM, RATE, ACTV_FRM_DT, ACTV_TO_DT) values ('4', 'a2', 1.5, {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'})
;                                                                          
insert into TRV_ACCT_USE_RT_T (ID, ACCT_NUM, RATE, ACTV_FRM_DT, ACTV_TO_DT) values ('5', 'a3', 1.5, {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'})
;
