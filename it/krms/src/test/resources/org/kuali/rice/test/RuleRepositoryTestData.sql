-- 
-- Copyright 2008-2009 The Kuali Foundation
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

insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('Q44001', 'Context1Qualifier', 'KRMS_TEST', 'Context 1 Qualifier', 'Y', 1)
;

insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('Q33001', 'Event', 'KRMS_TEST', 'Event Name', 'Y', 1)
;
insert into krms_typ_t 
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T2', 'CAMPUS', 'KRMS_TEST', 'myCampusService', 'Y', 1)
;

insert into krms_typ_t 
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T3', 'KrmsActionResolverType', 'KRMS_TEST', 'testActionTypeService', 'Y', 1)
;

insert into krms_typ_t 
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T4', 'CONTEXT', 'KRMS_TEST',  'Y', 1)
;

insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('T4A', 1, 'T4', 'Q44001', 'Y', 1)
;

insert into krms_typ_t 
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T5', 'AGENDA', 'KRMS_TEST',  'Y', 1)
;

insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('T5A', 1, 'T5', 'Q33001', 'Y', 1)
;

insert into krms_cntxt_t
(cntxt_id, nmspc_cd, nm, typ_id, actv, ver_nbr)
values ('CONTEXT1','KRMS_TEST', 'Context1', 'T4', 'Y', 1)
;

insert into krms_cntxt_attr_t
(cntxt_attr_id, cntxt_id, attr_val, attr_defn_id, ver_nbr)
values('C1ATTR1', 'CONTEXT1', 'BLAH', 'Q44001', 1)
;


insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('R201', 'KRMS_TEST', 'Rule1', 'T2', null, 'Y', 1, 'Bloomington Campus Code Rule')
;

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('P101', 'is campus bloomington', null, 'S','R201',1)
;

update krms_rule_t
set prop_id = 'P101' where rule_id = 'R201'
;

insert into krms_term_spec_t
(term_spec_id, cntxt_id, nm, typ, actv, ver_nbr)
values ('TERMSPEC_001', 'CONTEXT1', 'campusCodeTermSpec', 'java.lang.String', 'Y', 1);

insert into krms_term_t
(term_id, term_spec_id, ver_nbr)
values ('TERM_001', 'TERMSPEC_001', 1);

insert into krms_prop_parm_t 
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('101A', 'P101', 'TERM_001', 'T', 1, 1)
;

insert into krms_prop_parm_t 
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('101C', 'P101', 'BL', 'C', 2, 1)
;

insert into krms_prop_parm_t 
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('101B', 'P101', '=', 'O', 3, 1)
;

insert into krms_actn_t
(actn_id, nm, nmspc_cd, desc_txt, typ_id, rule_id, seq_no, ver_nbr)
values ( 'action2001', 'testAction', 'KRMS_TEST', 'Action Stub for Testing', 'T3', 'R201', 1, 1)
;

insert into krms_agenda_t
(agenda_id, nmspc_cd, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ( 'AGENDA301', 'KRMS_TEST', 'My Fabulous Agenda', 'CONTEXT1', null, 'T2', 'Y', 1)
;

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('AGENDA301ITEM1', 'R201', 'AGENDA301', 1)
;

update krms_agenda_t set INIT_AGENDA_ITM_ID = 'AGENDA301ITEM1' where agenda_id = 'AGENDA301'
;

insert into krms_agenda_attr_t
(agenda_attr_id, agenda_id, attr_val, attr_defn_id, ver_nbr)
values('AGENDA_ATTR1', 'AGENDA301', 'EARTHQUAKE', 'Q33001', 1)
;

insert into krms_term_spec_t
(term_spec_id, cntxt_id, nm, typ, actv, ver_nbr)
values ('TERM001', 'CONTEXT1', 'campusCode', 'T2', 'Y', 1)
;
