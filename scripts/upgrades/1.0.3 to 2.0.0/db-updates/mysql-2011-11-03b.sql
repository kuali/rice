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

-- Invalid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('QQ8801', 'Invalid Rule', 'KRMS_TEST', 'Invalid Rule', 'Y', null, 1, 'If true, execute the action');

-- Valid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('QQ8802', 'Valid Rule', 'KRMS_TEST', 'Valid Rule', 'Y', null, 1, 'If false, execute the action');

-- General validation rule type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('T8', 'Validation Rule', 'KRMS_TEST', 'validationRuleTypeService', 'Y', 1);

-- General context rule type mapping
insert into krms_cntxt_vld_rule_t (cntxt_vld_rule_id, cntxt_id, rule_id, ver_nbr) values ('CVR8', 'CONTEXT1', 'T8', 1);

-- The General rule subtypes attributes
-- use same sequence number to prove that falling back to natural order when sequences are the same works.
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('T8I', 1, 'T8', 'QQ8801', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('T8V', 1, 'T8', 'QQ8802', 'Y', 1);

-- warning action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('QQ8804', 'Warning Action', 'KRMS_TEST', 'Warning Action', 'Y', null, 1, 'Warning');

-- error action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('QQ8805', 'Error Action', 'KRMS_TEST', 'Error Action', 'Y', null, 1, 'Error');

-- action message
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('QQ8806', 'Action Message', 'KRMS_TEST', 'Action Message', 'Y', null, 1, 'Message validation action returns');

-- General validation action type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('T9', 'Validation Action', 'KRMS_TEST', 'validationActionTypeService', 'Y', 1);

-- Context general validation acton type mapping
insert into krms_cntxt_vld_actn_t (cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr) values ('CONTEXT1T9', 'CONTEXT1', 'T9', 1);

-- The General action type attribute
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('T9W', 1, 'T9', 'QQ8804', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('T9E', 2, 'T9', 'QQ8805', 'Y', 1);
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('T9M', 3, 'T9', 'QQ8806', 'Y', 1);
    

