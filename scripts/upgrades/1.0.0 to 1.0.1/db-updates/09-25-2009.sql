--
-- Copyright 2005-2011 The Kuali Foundation
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

CREATE INDEX krim_entity_afltn_ti1 ON krim_entity_afltn_t (entity_id)
/
CREATE INDEX krim_entity_emp_info_ti1 ON krim_entity_emp_info_t (entity_id)
/
CREATE INDEX krim_entity_emp_info_ti2 ON krim_entity_emp_info_t (entity_afltn_id)
/
CREATE INDEX krim_entity_ent_typ_ti1 ON krim_entity_ent_typ_t (entity_id)
/
CREATE INDEX krim_entity_ext_id_ti1 ON krim_entity_ext_id_t (entity_id)
/
CREATE INDEX krim_entity_nm_ti1 ON krim_entity_nm_t (entity_id)
/
CREATE INDEX krim_perm_attr_data_ti1 ON krim_perm_attr_data_t (perm_id)
/
CREATE INDEX krim_role_perm_ti1 ON krim_role_perm_t (perm_id)
/
CREATE INDEX krim_role_rsp_ti1 ON krim_role_rsp_t (rsp_id)
/
CREATE INDEX krim_typ_attribute_ti1 ON krim_typ_attr_t (kim_typ_id) 
/
