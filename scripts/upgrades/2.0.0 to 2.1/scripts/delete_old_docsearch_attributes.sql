-- Script to identify and delete obsolete doc search attributes
select * from KREW_DOC_TYP_ATTR_T where RULE_ATTR_ID in (select RULE_ATTR_ID from KREW_RULE_ATTR_T where RULE_ATTR_TYP_CD="DocumentSearchCriteriaProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchGeneratorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchResultProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchXMLResultProcessorAttribute")
/
select * from KREW_RULE_ATTR_T where RULE_ATTR_TYP_CD="DocumentSearchCriteriaProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchGeneratorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchResultProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchXMLResultProcessorAttribute"
/
-- delete from KREW_DOC_TYP_ATTR_T where RULE_ATTR_ID in (select RULE_ATTR_ID from KREW_RULE_ATTR_T where RULE_ATTR_TYP_CD="DocumentSearchCriteriaProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchGeneratorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchResultProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchXMLResultProcessorAttribute")
-- /
-- delete * from KREW_RULE_ATTR_T where RULE_ATTR_TYP_CD="DocumentSearchCriteriaProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchGeneratorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchResultProcessorAttribute" or RULE_ATTR_TYP_CD="DocumentSearchXMLResultProcessorAttribute"
-- /
