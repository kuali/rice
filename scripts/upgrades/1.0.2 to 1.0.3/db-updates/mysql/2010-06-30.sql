--
-- Copyright 2010 The Kuali Foundation
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
-- DO NOT add comments before the blank line below, or they will disappear.
insert into krim_perm_tmpl_t values ('51', '430ad531-89e4-11df-98b1-1300c9ee50c1', 1, 'KR-WKFLW', 'Add Message to Route Log', null, '3', 'Y')
/
insert into krim_perm_t values ('841', '65677409-89e4-11df-98b1-1300c9ee50c1', 1, '51', 'KUALI', 'Add Message to Route Log', null, 'Y')
/
insert into krim_perm_attr_data_t values ('881', '717e2c18-89e4-11df-98b1-1300c9ee50c1', 1, '841', '3', '13', 'KualiDocument')
/
insert into krim_role_perm_t values ('856', 'c3e39a4b-9e49-11df-8925-d2f1416e68bc', 1, '63', '841', 'Y')
/
