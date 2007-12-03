--Update and insert statements--;
update en_doc_typ_t set doc_typ_hdlr_url_addr = '&1.en/Workgroup.do?methodToCall=docHandler' where doc_typ_nm = 'EDENSERVICE-DOCS.WKGRPREQ';

UPDATE EN_DOC_TYP_T SET DOC_TYP_HDLR_URL_ADDR = '&1.DocumentType.do?methodToCall=docHandler' WHERE DOC_TYP_NM = 'EDENSERVICE-DOCS.DocumentType';

update en_doc_rte_typ_t set doc_rte_mod_nm = 'edu.iu.uis.eden.routemodule.ExceptionRouteModule' where doc_rte_typ_nm = 'Exception';

update en_doc_rte_typ_t set doc_rte_mod_nm = 'edu.iu.uis.eden.routemodule.AdHocRouteModule' where doc_rte_typ_nm = 'AdHoc';

update en_doc_rte_typ_t set doc_rte_mod_nm = 'edu.iu.uis.hr.hrerm.FinalFYIRouteModule' where doc_rte_typ_nm = 'HREFinalAck';

update en_doc_rte_typ_t set doc_rte_mod_nm = 'edu.iu.uis.hr.hrerm.FinalApprovalRouteModule' where doc_rte_typ_nm = 'HREFinalApp';

update en_doc_typ_t set doc_typ_post_prcsr_nm = 'edu.iu.uis.eden.postprocessor.DefaultPostProcessor' where doc_typ_post_prcsr_nm = 'en/DummyProcessor' or doc_typ_post_prcsr_nm = 'en/DummyPostProcessor';

update en_doc_typ_t set doc_typ_post_prcsr_nm = 'edu.iu.uis.hr.hrerm.PostProcessor' where doc_typ_post_prcsr_nm = 'hrerm/PostProcessor';

UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM = 'edu.iu.uis.eden.workgroup.WorkgroupPostProcessor' WHERE DOC_TYP_POST_PRCSR_NM = 'en/WorkGroupPostProcessor';

UPDATE EN_DOC_TYP_T SET DOC_TYP_POST_PRCSR_NM = 'edu.iu.uis.eden.doctype.DocumentTypePostProcessor' WHERE DOC_TYP_POST_PRCSR_NM = 'en/DocumentTypePostProcessor';

UPDATE EN_DOC_TYP_RTE_LVL_T A SET A.DOC_RTE_MTHD_NM = (SELECT B.DOC_RTE_MOD_NM FROM EN_DOC_RTE_TYP_T B WHERE A.DOC_RTE_MTHD_NM = B.DOC_RTE_TYP_NM AND B.DOC_RTE_MOD_NM IS NOT NULL) where A.DOC_RTE_MTHD_NM IN ('Exception', 'AdHoc', 'HREFinalAck', 'HREFinalApp');

update en_doc_typ_rte_lvl_t set doc_rte_mthd_nm = 'edu.iu.uis.hr.hrerm.FinalFYIRouteModule' where doc_rte_mthd_nm = 'hrerm/FinalFYIRouteModule';
 
update en_doc_typ_rte_lvl_t set doc_rte_mthd_nm = 'edu.iu.uis.hr.hrerm.FinalApprovalRouteModule' where doc_rte_mthd_nm = 'hrerm/FinalApprovalRouteModule'; 

UPDATE EN_ACTN_RQST_T A SET A.ACTN_RQST_RTE_TYP_NM = (SELECT B.DOC_RTE_MOD_NM FROM EN_DOC_RTE_TYP_T B WHERE A.ACTN_RQST_RTE_TYP_NM = B.DOC_RTE_TYP_NM AND B.DOC_RTE_MOD_NM IS NOT NULL) where A.ACTN_RQST_RTE_TYP_NM IN ('Exception', 'AdHoc', 'HREFinalAck', 'HREFinalApp'); 

update EN_ACTN_RQST_T set ACTN_RQST_RTE_TYP_NM = 'edu.iu.uis.hr.hrerm.FinalFYIRouteModule' where ACTN_RQST_RTE_TYP_NM = 'hrerm/FinalFYIRouteModule';
 
update EN_ACTN_RQST_T set ACTN_RQST_RTE_TYP_NM = 'edu.iu.uis.hr.hrerm.FinalApprovalRouteModule' where ACTN_RQST_RTE_TYP_NM = 'hrerm/FinalApprovalRouteModule'; 

update EN_RULE_BASE_VAL_T set TMPL_RULE_IND = 0;

update en_rule_tmpl_attrib_t set rule_attrib_id = 200 where rule_tmpl_id = (select rule_tmpl_id from en_rule_tmpl_t where rule_tmpl_nm = 'HRMS Review Hierarchy Approval');

update en_rule_tmpl_attrib_t set rule_attrib_id = 201 where rule_tmpl_id = (select rule_tmpl_id from en_rule_tmpl_t where rule_tmpl_nm = 'HRMS Review Hierarchy Acknowledgement');

update EN_RULE_ATTRIB_T set RULE_ATTRIB_TYP = 'RuleAttribute';

insert into en_rule_attrib_t values (200, 'HRMSApproveOrgReviewAttribute', 'HRMSApproveOrgReviewAttribute', 'RuleAttribute', 'HRMS Approve Hierarchy Attribute', 'edu.iu.uis.eden.routetemplate.attribute.HRMSApproveOrgReviewAttribute', 0);

insert into en_rule_attrib_t values (201, 'HRMSAcknowledgeOrgReviewAttribute', 'HRMSAcknowledgeOrgReviewAttribute', 'RuleAttribute', 'HRMS Acknowledge Hierarchy Attribute', 'edu.iu.uis.eden.routetemplate.attribute.HRMSAcknowledgeHierarchyAttribute', 0);

DELETE FROM EN_DOC_TYP_PLCY_RELN_T WHERE DOC_PLCY_NM IN ('SU_APPROVE', 'SU_DISAPPROVE', 'SU_CANCEL', 'SU_ROUTE_LEVEL_APPROVE', 'SU_ACTION_REQUEST_APPROVE');

update en_actn_itm_t set dlgn_typ = 'S' where actn_itm_dlgn_prsn_en_id is not null and actn_rqst_id in (select actn_rqst_id from en_actn_rqst_t where dlgn_typ = 'S');
update en_actn_itm_t set dlgn_typ = 'P' where actn_itm_dlgn_prsn_en_id is not null and actn_rqst_id in (select actn_rqst_id from en_actn_rqst_t where dlgn_typ = 'P');

--Application Constants--;
insert into EN_APPL_CNST_T values ('User.CreateNew.Instruction', 'Create or modify user information.', 0);
insert into EN_APPL_CNST_T values ('Workflow.AdminUrls', '/RouteType.do /RouteManagerDriver.do /ApplicationConstants.do /RuleAttribute.do /RuleTemplate.do /WorkflowUser.do', 0);
insert into EN_APPL_CNST_T values ('Note.CreateNew.Instruction', 'Create or modify note information.', 0);
insert into EN_APPL_CNST_T values ('RouteQueue.CreateNew.Instruction', 'Create or modify a Route Queue entry.', 0);
insert into EN_APPL_CNST_T values ('appConstant1', 'value 1', 0);
insert into EN_APPL_CNST_T values ('appConstant2', 'value 2', 0);

commit