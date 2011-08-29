/*
 * Copyright 2005-2008 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.framework.impex.xml.XmlLoader;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;



/**
 * A service which provides data access and functions for the KEW Rules engine.
 *
 * @see RuleBaseValues
 * @see RuleResponsibility
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RuleService extends XmlLoader, XmlExporter {

    /**
     * Returns a Rule based on unique name.  Returns null if name is null.
     * @param name the rule name
     * @return the Rule if found, null if not found or null name
     */
    public RuleBaseValues getRuleByName(String name);

    public String routeRuleWithDelegate(String documentId, RuleBaseValues parentRule, RuleBaseValues delegateRule, PrincipalContract principal, String annotation, boolean blanketApprove) throws Exception;
    //public void save(RuleBaseValues ruleBaseValues) throws Exception;
    public void save2(RuleBaseValues ruleBaseValues) throws Exception;
    public void validate2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, List errors) throws Exception;
    public void delete(String ruleBaseValuesId);
    public RuleBaseValues findRuleBaseValuesById(String ruleBaseValuesId);
    public List<RuleBaseValues> search(String docTypeName, String ruleId, String ruleTemplateId, String ruleDescription, String groupId, String principalId, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective);
    public List<RuleBaseValues> searchByTemplate(String docTypeName, String ruleTemplateName, String ruleDescription, String groupId, String principalId, Boolean workgroupMember, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection<String> actionRequestCodes);
    public RuleResponsibility findRuleResponsibility(String responsibilityId);
    public void deleteRuleResponsibilityById(String ruleResponsibilityId);
    public RuleResponsibility findByRuleResponsibilityId(String ruleResponsibilityId);
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType);
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, Timestamp effectiveDate);
    public List<RuleBaseValues> findByDocumentId(String documentId);
    public void makeCurrent(String documentId);
    public void makeCurrent(RuleBaseValues rule, boolean isRetroactiveUpdatePermitted);
    public void makeCurrent(RuleDelegation ruleDelegation, boolean isRetroactiveUpdatePermitted);
    public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type);
    public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type);
    public String isLockedForRouting(String currentRuleBaseValuesId);
    public List fetchAllRules(boolean currentRules);
    public RuleBaseValues findDefaultRuleByRuleTemplateId(String ruleTemplateId);
    public RuleBaseValues getParentRule(String ruleBaseValuesId);

    /**
     * Returns the name of the document type definition that should be used to route the given List of rules.  This method will never
     * return a null value, as it will default to the default Rule document type name if not custom document type is configured for
     * the given rules.
     */
    public String getRuleDocmentTypeName(List rules);

    /**
     * Checks if the Rule with the given value is a duplicate of an existing rule in the system.
     * 
     * @return the id of the duplicate rule if one exists, null otherwise
     */
    public String getDuplicateRuleId(RuleBaseValues rule);
        
    public RuleBaseValues saveRule(RuleBaseValues rule, boolean isRetroactiveUpdatePermitted);
    
    public List<RuleBaseValues> saveRules(List<RuleBaseValues> rulesToSave, boolean isRetroactiveUpdatePermitted);
    
    public RuleDelegation saveRuleDelegation(RuleDelegation ruleDelegation, boolean isRetroactiveUpdatePermitted);
    
    public List<RuleDelegation> saveRuleDelegations(List<RuleDelegation> ruleDelegationsToSave, boolean isRetroactiveUpdatePermitted);
    
    public String findResponsibilityIdForRule(String ruleName, String ruleResponsibilityName, String ruleResponsibilityType);
}
