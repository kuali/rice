update krew_doc_typ_t set post_prcsr = 'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor' where post_prcsr = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.krad.workflow.attribute.KualiXmlSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl'
/