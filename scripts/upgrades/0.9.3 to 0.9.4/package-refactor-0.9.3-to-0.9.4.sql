select distinct(RULE_ATTRIB_CLS_NM) from EN_RULE_ATTRIB_T

edu.iu.uis.eden.docsearch.xml.DocumentSearchXMLResultProcessorImpl, org.kuali.rice.kew.docsearch.xml.DocumentSearchXMLResultProcessorImpl
edu.iu.uis.eden.routetemplate.InitiatorRoleAttribute, org.kuali.rice.kew.rule.InitiatorRoleAttribute
edu.iu.uis.eden.routetemplate.RemoveReplaceRuleRoutingAttribute, org.kuali.rice.kew.rule.RemoveReplaceRuleRoutingAttribute
edu.iu.uis.eden.routetemplate.RemoveReplaceWorkgroupTypeRoutingAttribute, org.kuali.rice.kew.rule.RemoveReplaceWorkgroupTypeRoutingAttribute
edu.iu.uis.eden.routetemplate.RoutedByUserRoleAttribute, org.kuali.rice.kew.rule.RoutedByUserRoleAttribute
edu.iu.uis.eden.routetemplate.RuleRoutingAttribute, org.kuali.rice.kew.rule.RuleRoutingAttribute
org.kuali.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl, org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl
org.kuali.workflow.attribute.KualiXmlRuleAttributeImpl, org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl
org.kuali.workflow.attribute.KualiXmlSearchableAttributeImpl, org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl
edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute, org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute
edu.iu.uis.eden.routetemplate.NetworkIdRoleAttribute, org.kuali.rice.kew.rule.NetworkIdRoleAttribute
edu.iu.uis.eden.routetemplate.UniversityIdRoleAttribute, org.kuali.rice.kew.rule.UniversityIdRoleAttribute
edu.iu.uis.eden.routetemplate.xmlrouting.StandardGenericXMLRuleAttribute, org.kuali.rice.kew.xmlrouting.StandardGenericXMLRuleAttribute
org.kuali.notification.kew.ChannelReviewerRoleAttribute, org.kuali.rice.ken.kew.ChannelReviewerRoleAttribute
org.kuali.notification.kew.NotificationCustomActionListAttribute, org.kuali.rice.ken.kew.NotificationCustomActionListAttribute
edu.iu.uis.eden.routetemplate.WorkgroupTypeRoutingAttribute, org.kuali.rice.kew.rule.WorkgroupTypeRoutingAttribute
edu.iu.uis.eden.mail.CustomEmailAttributeImpl, org.kuali.rice.kew.mail.CustomEmailAttributeImpl
edu.iu.uis.eden.notes.CustomNoteAttributeImpl, org.kuali.rice.kew.notes.CustomNoteAttributeImpl
edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator, org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator
edu.iu.uis.eden.docsearch.StandardDocumentSearchCriteriaProcessor, org.kuali.rice.kew.docsearch.StandardDocumentSearchCriteriaProcessor

select distinct(RULE_RSP_NM) from EN_RULE_RSP_T where RULE_RSP_NM like 'edu.%'

edu.iu.uis.eden.routetemplate.InitiatorRoleAttribute!INITIATOR, org.kuali.rice.kew.rule.InitiatorRoleAttribute!INITIATOR
edu.iu.uis.eden.routetemplate.RoutedByUserRoleAttribute!ROUTED_BY_USER, eorg.kuali.rice.kew.rule.RoutedByUserRoleAttribute!ROUTED_BY_USER
edu.iu.uis.eden.routetemplate.NetworkIdRoleAttribute!networkId, org.kuali.rice.kew.rule.NetworkIdRoleAttribute!networkId
edu.iu.uis.eden.routetemplate.UniversityIdRoleAttribute!universityId, org.kuali.rice.kew.rule.UniversityIdRoleAttribute!universityId
org.kuali.notification.kew.ChannelReviewerRoleAttribute!reviewers, org.kuali.rice.ken.kew.ChannelReviewerRoleAttribute!reviewers

select distinct(DOC_TYP_POST_PRCSR_NM) from EN_DOC_TYP_T

org.kuali.workflow.postprocessor.KualiPostProcessor, org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor
edu.iu.uis.eden.removereplace.RemoveReplacePostProcessor, org.kuali.rice.kew.removereplace.RemoveReplacePostProcessor
edu.iu.uis.eden.routetemplate.RulePostProcessor, org.kuali.rice.kew.rule.RulePostProcessor
edu.iu.uis.eden.postprocessor.DefaultPostProcessor, org.kuali.rice.kew.postprocessor.DefaultPostProcessor
edu.iu.uis.eden.edl.EDocLitePostProcessor, org.kuali.rice.kew.edl.EDocLitePostProcessor
edu.iu.uis.eden.workgroup.WorkgroupPostProcessor, org.kuali.rice.kew.workgroup.WorkgroupPostProcessor
edu.iu.uis.eden.edl.EDLDatabasePostProcessor, org.kuali.rice.kew.edl.EDLDatabasePostProcessor
org.kuali.notification.postprocessor.kew.NotificationPostProcessor, org.kuali.rice.ken.postprocessor.kew.NotificationPostProcessor
org.kuali.notification.postprocessor.kew.NotificationSenderFormPostProcessor, org.kuali.rice.ken.postprocessor.kew.NotificationSenderFormPostProcessor
