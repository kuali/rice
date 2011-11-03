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

-- This script is part of the effort to clean up the Rice database prior to packaging for public release

-- IMPORTANT: execute the following before running the demo-server-dataset-cleanup.sql

delete from krew_rule_tmpl_attr_t where rule_tmpl_attr_id=1080
/
update krew_rule_tmpl_attr_t set actv_ind=1 where rule_tmpl_attr_id=1027
/

-- IMPORTANT: at this point, run the following:
--
--      1) demo-server-dataset-cleanup.sql
--      2) demo-client-dataset-cleanup.sql
--
-- DO NOT RUN THESE SCRIPTS BEFORE THIS POINT!!!  The previous statements in this file need to be
-- executed first.

-- Disable constraints for the duration of this script
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status = 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' DISABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/

-- the mysql way to disable constraints
-- SET foreign_key_checks = 0


-- ##############
-- # KEW Tables #
-- ##############

-- Document Types

delete from krew_doc_typ_t where doc_typ_nm like 'EDENSERVICE-DOCS%'
/
delete from krew_doc_typ_t where doc_typ_nm='RemoveReplaceUserDocument'
/
delete from krew_doc_typ_t where doc_typ_nm like 'KIM%'
/
delete from krew_doc_typ_t where doc_typ_nm like 'KualiOrganization%'
/
delete from krew_doc_typ_t where doc_typ_nm='RiceUserMaintenanceDocument'
/
delete from krew_doc_typ_t where doc_typ_nm='TravelTripReimbursement'
/

delete from krew_doc_typ_attr_t where DOC_TYP_ID not in (select doc_typ_id from KREW_DOC_TYP_T)
/
delete from krew_doc_typ_plcy_reln_t where DOC_TYP_ID not in (select doc_typ_id from KREW_DOC_TYP_T)
/
delete from krew_doc_typ_proc_t where DOC_TYP_ID not in (select doc_typ_id from KREW_DOC_TYP_T)
/

delete from krew_rte_node_cfg_parm_t where rte_node_id in (select rte_node_id from krew_rte_node_t where doc_typ_id not in (select doc_typ_id from krew_doc_typ_t))
/
delete from krew_rte_node_t where DOC_TYP_ID not in (select doc_typ_id from KREW_DOC_TYP_T)
/
delete from krew_rte_node_lnk_t where from_rte_node_id not in (select rte_node_id from krew_rte_node_t)
/
delete from krew_rte_brch_proto_t
/

-- Rules

delete from krew_rule_t where rule_id=1034
/
delete from krew_rule_t where rule_id=1035
/
delete from krew_rule_t where doc_typ_nm like 'KIM%'
/
update krew_rule_t set NM='SendNotificationRequest.Reviewers', RULE_BASE_VAL_DESC='Notification Request Reviewers' where rule_id=1044
/
update krew_rule_t set NM='TravelRequest.Destination.LasVegas', RULE_BASE_VAL_DESC='Destination - Las Vegas' where rule_id=1046
/
update krew_rule_t set NM='TravelRequest.Traveler', RULE_BASE_VAL_DESC='Travler Routing' where rule_id=1049
/
update krew_rule_t set NM='TravelRequest.Supervisor', RULE_BASE_VAL_DESC='Supervisor Routing' where rule_id=1049
/
update krew_rule_t set NM='TravelRequest.DeanDirector', RULE_BASE_VAL_DESC='Dean/Director Routing' where rule_id=1050
/
update krew_rule_t set NM='TravelRequest.FiscalOfficer', RULE_BASE_VAL_DESC='Fiscal Officer Routing' where rule_id=1051
/
update krew_rule_t set NM='eDoc.Example1Doctype.IUB' where rule_id=1103
/
update krew_rule_t set NM='eDoc.Example1Doctype.IUPUI' where rule_id=1106
/
delete from krew_rule_t where rule_id=1637
/
delete from krew_rule_t where rule_id=1640
/

-- update rule for Recipe Masters so that it points to a valid group id
update krew_rule_rsp_t set NM='9997' where rule_rsp_id='2064'
/

delete from krew_dlgn_rsp_t
/

delete from krew_rule_ext_t where rule_id not in (select rule_id from krew_rule_t)
/
delete from krew_rule_ext_val_t where rule_ext_id not in (select rule_ext_id from krew_rule_ext_t)
/
delete from krew_rule_rsp_t where rule_id not in (select rule_id from krew_rule_t)
/

-- Rule Attributes

update krew_rule_attr_t set lbl='Rule Routing Attribute', desc_txt='Rule Routing Attribute' where nm='RuleRoutingAttribute'
/
delete from krew_rule_attr_t where nm like 'RemoveReplace%'
/
delete from krew_rule_attr_t where cls_nm like 'edu.iu%'
/
delete from krew_rule_attr_t where nm like 'SIS%'
/
delete from krew_rule_attr_t where nm like 'Travel%' and nm != 'TravelAccountDocumentAccountNumberAttribute'
/
delete from krew_rule_attr_t where nm like 'Timesheet%'
/
delete from krew_rule_attr_t where nm like 'EPIC%'
/
delete from krew_rule_attr_t where nm like 'Hrms%'
/
delete from krew_rule_attr_t where nm like 'Iuf%'
/
delete from krew_rule_attr_t where nm like 'UGS%'
/
delete from krew_rule_attr_t where nm='DepartmentAttribute'
/
delete from krew_rule_attr_t where nm='DepartmentSearchAttribute'
/
delete from krew_rule_attr_t where nm='DepartmentSearchAttribute'
/
delete from krew_rule_attr_t where nm='SchoolAttribute'
/
delete from krew_rule_attr_t where nm='SchoolSearchAttribute'
/
delete from krew_rule_attr_t where nm='proposedDirectorAttribute'
/
delete from krew_rule_attr_t where nm='DirectorSearchAttribute'
/
delete from krew_rule_attr_t where nm='TeacherAttribute'
/                                                                                 
delete from krew_rule_attr_t where nm='TeacherSearchAttribute'
/
delete from krew_rule_attr_t where nm='facultyAdvisorAttribute'
/
delete from krew_rule_attr_t where nm='AdvisorSearchAttribute'
/                                                                          
delete from krew_rule_attr_t where nm='StudentProgramCodeAttribute'
/
delete from krew_rule_attr_t where nm='StudentProgramCodeSearchAttribute'
/
delete from krew_rule_attr_t where nm='CourseSubjectCodeAttribute'
/
delete from krew_rule_attr_t where nm='CourseSubjectCodeSearchAttribute'
/
delete from krew_rule_attr_t where nm='EmplidSearchAttribute'
/
delete from krew_rule_attr_t where nm='CashTransferAmountAttribute'
/

-- Rule Templates

delete from krew_rule_tmpl_t where NM='RemoveReplaceWorkgroupTemplate'
/
delete from krew_rule_tmpl_t where NM='RemoveReplaceRuleTemplate'
/
delete from krew_rule_tmpl_t where NM='SubAccount'
/
delete from krew_rule_tmpl_t where NM like 'HRMS%'
/
delete from krew_rule_tmpl_t where NM='Fiscal & University Personnel Action Approval'
/
delete from krew_rule_tmpl_t where NM like 'EPIC%'
/
delete from krew_rule_tmpl_t where NM like 'UniversityGraduate%'
/
delete from krew_rule_tmpl_t where NM='KSBDuplicating-Route'
/
delete from krew_rule_tmpl_t where NM like 'E596%'
/
delete from krew_rule_tmpl_t where NM='SchoolofMusic-TeacherRouting'
/
delete from krew_rule_tmpl_t where NM='InternshipContract-advisorRouting'
/
delete from krew_rule_tmpl_t where NM='RegistrarsOffice-ProgramRouting'
/
delete from krew_rule_tmpl_t where NM='RegistrarsOffice-Routing'
/
delete from krew_rule_tmpl_t where NM like 'Timesheet%'
/
delete from krew_rule_tmpl_t where NM='SupervisorRuleTemplate'
/
delete from krew_rule_tmpl_t where NM like 'SIS%'
/
delete from krew_rule_tmpl_t where NM='KualiSimpleMaintenanceDocumentTemplate'
/
delete from krew_rule_tmpl_t where NM like 'Iuf%'
/
delete from krew_rule_tmpl_t where NM='TravelAccountDelegationTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelContentTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelSubAccountTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelAccountTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelChartOrgDollarRangeTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelSeparationTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelInternalTRMSTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelTravelerTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelSubFundTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelBudgetTemplate'
/
delete from krew_rule_tmpl_t where NM like 'SAV%'
/
delete from krew_rule_tmpl_t where NM='Fiscal and University Personnel Action Approval v2'
/
delete from krew_rule_tmpl_t where NM='AccountTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelArrangerAuthorizationTemplate'
/
delete from krew_rule_tmpl_t where NM='TravelContractsAndGrantsTemplate'
/
delete from krew_rule_tmpl_t where NM='ChartOrgTemplate'
/
delete from krew_rule_tmpl_t where NM='RegistrarsOffice-SubjectRouting'
/
delete from krew_rule_tmpl_t where NM='PayrollProcessorRuleTemplate'
/

delete from krew_rule_tmpl_attr_t where rule_attr_id not in (select rule_attr_id from krew_rule_attr_t)
/
delete from krew_rule_tmpl_attr_t where rule_tmpl_id not in (select rule_tmpl_id from krew_rule_tmpl_t)
/

-- Rule Template Options

delete from krew_rule_tmpl_optn_t
/

-- ##############
-- # KNS Tables #
-- ##############

delete from krns_campus_t where campus_cd='IX'
/

update krns_nmspc_t set appl_nmspc_cd='RICE' where nmspc_cd like 'KR%'
/

delete from krns_parm_t where parm_nm='nate'
/

-- ##############
-- # KEN Tables #
-- ##############

-- No KEN data needs to be fixed

-- ##############
-- # KIM Tables #
-- ##############

-- add missing attribute definition

INSERT INTO krim_attr_defn_t(KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, ACTV_IND, NMSPC_CD, CMPNT_NM, APPL_URL)
  VALUES('46', '69FA55ACC2EE2598E0404F8189D86880', 1, 'qualifierResolverProvidedIdentifier', NULL, 'Y', 'KR-IDM', 'org.kuali.rice.kim.bo.impl.KimAttributes', '${application.url}')
/

-- remove a bad employee status code

delete from krim_emp_stat_t where NM='Retired 2'
/

-- get rid of all the weird email addresses

update krim_entity_email_t set email_addr='test@email.edu'
/

-- delete 'TestRouteAGroup123'

delete from krim_grp_mbr_t where grp_id='2036'
/
delete from krim_grp_t where grp_id='2036'
/

-- delete 'TestAGroupRoutenum1'

delete from krim_grp_mbr_t where grp_id='2060'
/
delete from krim_grp_t where grp_id='2060'
/

-- delete 'CreatinAGroup4'

delete from krim_grp_mbr_t where grp_id='2122'
/
delete from krim_grp_t where grp_id='2122'
/

-- delete 'Testify!'

delete from krim_grp_mbr_t where grp_id='2180'
/
delete from krim_grp_t where grp_id='2180'
/

-- delete 'TestAGroup123'

delete from krim_grp_mbr_t where grp_id='2260'
/
delete from krim_grp_t where grp_id='2260'
/

-- update all of the names on our permissions

update krim_perm_t p set p.nm=(select pt.nm from krim_perm_tmpl_t pt where p.perm_tmpl_id=pt.perm_tmpl_id) where nm is null
/

-- Fix id of perm 169 (Save Document) and update it's namespace code from KUALI to KR-SYS, make it's id match the identical permission in the KFS db

update krim_perm_t set perm_id='290', nmspc_cd='KR-SYS', desc_txt='Users who can save RICE documents' where perm_id='169'
/
update krim_perm_attr_data_t set perm_id='290' where perm_id='169'
/
update krim_role_perm_t set perm_id='290' where perm_id='169'
/

-- setup "Modify Entity" permission, assign to KR-SYS Technical Administrators

INSERT INTO krim_perm_t(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
  VALUES('307', '638DD46953F9BCD5E0404F8189D86240', 1, '1', 'KR-IDM', 'Modify Entity', 'Users who can modify entity records in Kuali Identity Management.', 'Y')
/
INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('850', '70086A2DF17C62E4E0404F8189D863CD', 1, '63', '307', 'Y')
/

-- setup "Full Unmask Field" permission

INSERT INTO krim_perm_t(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
  VALUES('306', '6314CC58CF58B7B5E0404F8189D84439', 1, '27', 'KR-SYS', 'Full Unmask Field', 'Authorizes users to view the entire Tax Identification Number on the Person document and inquiry.', 'Y')
/
INSERT INTO krim_perm_attr_data_t(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('431', '6314CC58CF59B7B5E0404F8189D84439', 1, '306', '11', '5', 'IdentityManagementPersonDocument')
/
INSERT INTO krim_perm_attr_data_t(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('432', '6314CC58CF5AB7B5E0404F8189D84439', 1, '306', '11', '6', 'taxId')
/
-- assign to KR-SYS Technical Administrator
INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('578', '6314CC58CF5BB7B5E0404F8189D84439', 1, '63', '306', 'Y')
/

-- modify perm 827 ("Use Screen" for the Ingester) so that it has the same id as the corresponding perm in the kfs database (265)

update krim_perm_t set perm_id='265' where perm_id='827'
/
update krim_perm_attr_data_t set perm_id='265' where perm_id='827'
/
update krim_role_perm_t set perm_id='265' where perm_id='827'
/

-- remove extraneous permission attributes from "Use Screen" permissions

delete from krim_perm_attr_data_t where kim_attr_defn_id='4' and perm_id in ('140','141','142','143','144','145')
/

-- remove an extraneous permission for delete note/attachment

delete from krim_perm_t where perm_id='262'
/
delete from krim_perm_attr_data_t where perm_id='262'
/
delete from krim_role_perm_t where perm_id='262'
/

-- update descriptions on various permissions

update krim_perm_t set desc_txt='Authorizes users to login to the Kuali portal.' where perm_id='174'
/
update krim_perm_t set desc_txt='Administer Pessimistic Locking' where perm_id='289'
/
update krim_perm_t set desc_txt='Authorizes users to access other users action lists via the Help Desk Action List Login.' where perm_id='298'
/	
update krim_perm_t set desc_txt='Users who can perform a document search with no criteria or result limits.' where perm_id='299'
/	
update krim_perm_t set desc_txt='Allows a user to override entity privacy preferences' where perm_id='378'
/
update krim_perm_t set desc_txt='Authorizes the initiation of RICE Documents.' where perm_id='149'
/
update krim_perm_t set desc_txt='Authorizes users to cancel a document prior to it being submitted for routing.' where perm_id='167'
/
update krim_perm_t set desc_txt='Allows users to edit Kuali documents that are in ENROUTE status.' where perm_id='180'
/
update krim_perm_t set desc_txt='Allows users to edit Kuali documents that are in ENROUTE status.' where perm_id='181'
/
update krim_perm_t set desc_txt='Authorizes users to copy RICE Documents.' where perm_id='156'
/
update krim_perm_t set desc_txt='Allow users to access Kuali RICE lookups.' where perm_id='162'
/	
update krim_perm_t set desc_txt='Allows users to access Kuali RICE inquiries.' where perm_id='161'
/
update krim_perm_t set desc_txt='Authorizes users to view the entire Tax Identification Number on the Payee ACH document and Inquiry.' where perm_id='183'
/	
update krim_perm_t set desc_txt='Allows users to access the Document Operation screen.' where perm_id='140'
/	
update krim_perm_t set desc_txt='Allows users to access the Java Security Management screen.' where perm_id='141'
/
update krim_perm_t set desc_txt='Allows users to access the Message Queue screen.' where perm_id='142'
/
update krim_perm_t set desc_txt='Allows users to access the Service Registry screen.' where perm_id='143'
/
update krim_perm_t set desc_txt='Allows users to access the Thread Pool screen.' where perm_id='144'
/
update krim_perm_t set desc_txt='Allows users to access the Quartz Queue screen.' where perm_id='145'
/
update krim_perm_t set desc_txt='Allows users to access all RICE screens.' where perm_id='166'
/	
update krim_perm_t set desc_txt='Allows users to open RICE Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).' where perm_id='147'
/
update krim_perm_t set desc_txt='Allows users to access and run Batch Jobs associated with KR modules via the Schedule link.' where perm_id='164'
/
update krim_perm_t set desc_txt='Authorizes to initiate and edit the Parameter document for parameters with a module code beginning with KR.' where perm_id='163'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Assignees Tab of the Role Document and the Roles section of the Membership Tab on the Person Document for Roles with a Module Code beginning with KR.' where perm_id='150'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Permissions tab of the Role Document for roles with a module code beginning with KR.' where perm_id='151'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Responsibility tab of the Role Document for roles with a Module Code that begins with KR.' where perm_id='152'
/	
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Assignees Tab of the Group Document and the Group section of the Membership Tab on the Person Document for groups with namespaces beginning with KR.' where perm_id='155'
/	
update krim_perm_t set desc_txt='Allows access to the Blanket Approval button on RICE Documents.' where perm_id='148'
/	
update krim_perm_t set desc_txt='Authorizes users to open RICE Documents.' where perm_id='165'
/	
update krim_perm_t set desc_txt='Users who can add notes and attachments to any document answering to the Kuali Document parent document type.' where perm_id='259'
/	
update krim_perm_t set desc_txt='Authorizes users to view notes and attachments on documents answering to the KualiDocument parent document type.' where perm_id='261'
/	
update krim_perm_t set desc_txt='Authorizes users to delete notes and attachments created by any user on documents answering to the RICE Document parent document type.' where perm_id='264'
/	
update krim_perm_t set desc_txt='Authorizes users to send FYI ad hoc requests for Kuali Documents' where perm_id='332'
/
update krim_perm_t set desc_txt='Authorizes users to send Acknowledge ad hoc requests for Kuali Documents' where perm_id='333'
/
update krim_perm_t set desc_txt='Authorizes users to send Approve ad hoc requests for Kuali Documents' where perm_id='334'
/
update krim_perm_t set desc_txt='Authorizes users to submit a document for routing.' where perm_id='168'
/
update krim_perm_t set desc_txt='Authorizes users to take the Approve action on documents routed to them.' where perm_id='170'
/	
update krim_perm_t set desc_txt='Authorizes users to take the FYI action on documents routed to them.' where perm_id='172'
/	
update krim_perm_t set desc_txt='Authorizes users to take the Acknowledge action on documents routed to them.' where perm_id='173'
/	
update krim_perm_t set desc_txt='Allows a user to receive ad hoc requests for RICE Documents.' where perm_id='146'
/

update krim_perm_t set desc_txt='Allow users to access the Rule Template lookup.' where perm_id='701'
/
update krim_perm_t set desc_txt='Allow users to access the Stylesheet lookup.' where perm_id='702'
/
update krim_perm_t set desc_txt='Allow users to access the eDocLite lookup.' where perm_id='703'
/
update krim_perm_t set desc_txt='Allow users to access the Rule Attribute lookup.' where perm_id='707'
/
update krim_perm_t set desc_txt='Allow users to access the Pessimistic Lock lookup.' where perm_id='714'
/
update krim_perm_t set desc_txt='Allow users to access the Parameter Component lookup.' where perm_id='719'
/
update krim_perm_t set desc_txt='Allow users to access the Namespace lookup.' where perm_id='720'
/
update krim_perm_t set desc_txt='Allow users to access the Parameter Type lookup.' where perm_id='721'
/

update krim_perm_t set desc_txt='Allow users to access the Rule Template inquiry.' where perm_id='801'
/
update krim_perm_t set desc_txt='Allow users to access the Stylesheet inquiry.' where perm_id='802'
/
update krim_perm_t set desc_txt='Allow users to access the eDocLite inquiry.' where perm_id='803'
/
update krim_perm_t set desc_txt='Allow users to access the Rule Attribute inquiry.' where perm_id='807'
/
update krim_perm_t set desc_txt='Allow users to access the Pessimistic Lock inquiry.' where perm_id='814'
/
update krim_perm_t set desc_txt='Allow users to access the Parameter Component inquiry.' where perm_id='819'
/
update krim_perm_t set desc_txt='Allow users to access the Namespace inquiry.' where perm_id='820'
/
update krim_perm_t set desc_txt='Allow users to access the Parameter Type inquiry.' where perm_id='821'
/

update krim_perm_t set desc_txt='Authorizes users to modify the information on the Assignees Tab of the Group Document and the Group section of the Membership Tab on the Person Document for groups with the KUALI namespace.' where perm_id='833'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Assignees Tab of the Role Document and the Roles section of the Membership Tab on the Person Document for Roles with the KUALI namespace.' where perm_id='834'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Permissions tab of the Role Document for roles with the KUALI namespace.' where perm_id='835'
/
update krim_perm_t set desc_txt='Authorizes users to modify the information on the Responsibility tab of the Role Document for roles with the KUALI namespace.' where perm_id='836'
/

-- Role Data

-- update role descriptions

update krim_role_t
set desc_txt = 'This role derives its members from users with the Edit Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Editor'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with the Open Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Opener'
/
update krim_role_t
set desc_txt = 'This role can take superuser actions and blanket approve RICE documents as well as being able to modify and assign permissions, responsibilities and roles belonging to the KR namespaces.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Technical Administrator'
/
update krim_role_t
set desc_txt = 'This role represents the KR System User, that is the user ID RICE uses when it takes programmed actions.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'System User'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with the Initiate Document permission for a given document type.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Document Initiator'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with that have received an action request for a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Approve Request Recipient'
/
update krim_role_t
set desc_txt = 'This role derives its members from the initiator listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator'
/
update krim_role_t
set desc_txt = 'This role derives its members from the initiator and action request recipients listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator or Reviewer'
/
update krim_role_t
set desc_txt = 'This role derives its members from the user who took the Complete action on a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Router'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with an acknowledge action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Acknowledge Request Recipient'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with an FYI action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'FYI Request Recipient'
/
update krim_role_t
set desc_txt = 'This role derives its members from users with an Approval action request (that was not generated via the ad-hoc recipients tab) in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Non-Ad Hoc Approve Request Recipient'
/
update krim_role_t
set desc_txt = 'This role derives its members from the users in the Principal table. This role gives users high-level permissions to interact with RICE documents and to login to KUALI.'
where nmspc_cd = 'KUALI'
and role_nm = 'User'
/

-- remove the "Rice" role, we will create a new "System User" role later in the script

delete from krim_role_t where role_id='62'
/
delete from krim_role_mbr_t where role_id='62'
/
delete from krim_role_perm_t where role_id='62'
/

-- create missing "System User" role

INSERT INTO krim_role_t(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT)
  VALUES('90', '61815E6C62D0B647E0404F8189D873B3', 1, 'System User', 'KR-SYS', 'This role represents the KR System User, that is the user ID RICE uses when it takes programmed actions.', '1', 'Y', NULL)
/
INSERT INTO krim_role_mbr_t(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT)
  VALUES('1282', 1, '5B4B421E43857717E0404F8189D821F7', '90', '1', 'P', NULL, NULL, NULL)
/
INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('552', '61815E6C62D3B647E0404F8189D873B3', 1, '90', '290', 'Y')
/

-- create missing "Document Initiator" role

INSERT INTO krim_role_t(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT)
  VALUES('95', '67F145466E8B9160E0404F8189D86771', 1, 'Document Initiator', 'KR-SYS', 'This role derives its members from users with the Initiate Document permission for a given document type.', '66', 'Y', NULL)
/
INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('250', '70086A2DF17D62E4E0404F8189D863CD', 1, '95', '156', 'Y')
/

-- create missing "Non-Ad Hoc Approve Request Recipient" role, no permissions assigned here

INSERT INTO krim_role_t(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT)
  VALUES('97', '67F145466EB09160E0404F8189D86771', 1, 'Non-Ad Hoc Approve Request Recipient', 'KR-WKFLW', 'This role derives its members from users with an Approval action request (that was not generated via the ad-hoc recipients tab) in the route log of a given document.', '42', 'Y', NULL)
/
INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('251', '70086A2DF17E62E4E0404F8189D863CD', 1, '97', '181', 'Y')
/


-- Kim Types

-- update missing service names

update krim_typ_t set srvc_nm='permissionPermissionTypeService' where kim_typ_id='19'
/
update krim_typ_t set srvc_nm='responsibilityPermissionTypeService' where kim_typ_id='20'
/

-- add missing Rice kim types

INSERT INTO krim_typ_t(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('66', '67F145466E8A9160E0404F8189D86771', 1, 'Derived Role: Permission (Initiate Document)', 'documentInitiatorRoleTypeService', 'Y', 'KR-SYS')
/
INSERT INTO krim_typ_t(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('67', '67F145466E8F9160E0404F8189D86771', 1, 'Namespace', 'namespacePermissionTypeService', 'Y', 'KR-NS')
/

-- fix missing kim type attributes

INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('111', '67F145466E909160E0404F8189D86771', 1, 'a', '67', '4', 'Y')
/
INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('112', '67F145466E959160E0404F8189D86771', 1, 'b', '14', '13', 'Y')
/
INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('28', '5ADF18B6D4F87954E0404F8189D85002', 1, 'a', '17', '12', 'Y')
/
INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('95', '5C997D14EAC3FE40E0404F8189D87DC5', 1, 'a', '52', '13', 'Y')
/
INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('96', '5C997D14EAC4FE40E0404F8189D87DC5', 1, 'b', '52', '16', 'Y')
/
INSERT INTO krim_typ_attr_t(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('97', '5C997D14EAC5FE40E0404F8189D87DC5', 1, 'c', '52', '6', 'Y')
/

-- Re-enable constraints
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status <> 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' ENABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/

-- the mysql way to re-enable constraints
-- SET foreign_key_checks = 1
