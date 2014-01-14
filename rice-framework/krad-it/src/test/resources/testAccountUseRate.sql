--
-- Copyright 2005-2014 The Kuali Foundation
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

insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('1', 'a1', '1.5', {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'}, '45468924-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('2', 'a2', '1.5', {ts '2010-01-01 00:00:00'}, {ts '2011-01-01 00:00:00'}, '54e594c4-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('3', 'a2', '1.5', {ts '2010-03-01 00:00:00'}, {ts '2011-01-01 00:00:00'}, '54e594ce-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('4', 'a2', '1.5', {ts '2012-01-01 00:00:00'}, {ts '2013-01-01 00:00:00'}, '54e594d8-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('5', 'a3', '1.5', {ts '2010-01-01 00:00:00'}, {ts '2010-06-01 00:00:00'}, '54e594d9-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('6', 'a4', '1.5', null, {ts '2011-01-01 00:00:00'}, '54e594da-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('7', 'a5', '1.5', {ts '2010-01-01 00:00:00'}, null, '54e594db-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('8', 'a6', '1.5', null, null, '874a3bd6-0b6d-11e3-86d0-44a3671740fd');
insert into trv_acct_use_rt_t (id, acct_num, rate, actv_frm_dt, actv_to_dt, obj_id) values ('9', 'b1', '3', {ts '2010-01-01 12:30:00'}, {ts '2010-06-01 15:30:00'}, '87d076b0-0b6d-11e3-86d0-44a3671740fd');