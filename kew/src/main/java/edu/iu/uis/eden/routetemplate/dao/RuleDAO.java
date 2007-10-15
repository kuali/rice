/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.routetemplate.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;

public interface RuleDAO {

    public void save(RuleBaseValues ruleBaseValues);
    public void saveDeactivationDate(RuleBaseValues ruleBaseValues);
    public void delete(Long ruleBaseValuesId);
    public RuleBaseValues findRuleBaseValuesByName(String name);
    public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId);
    public RuleResponsibility findRuleResponsibility(Long responsibilityId);
    public List fetchAllRules(boolean currentRules);
    public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes);
    public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes, Timestamp effectiveDate);
    public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, Long workgroupId, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective);
    public List search(String docTypeName, Long ruleTemplateId, String ruleDescription, Collection<String> workgroupIds, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection actionRequestCodes);
    public List findByRouteHeaderId(Long routeHeaderId);
    public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type);
    public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type);
    public List findResponsibilitiesByDelegationRuleId(Long delegationRuleId);
    public List findByPreviousVersionId(Long previousVersionId);
    public void clearCache();
    public void retrieveAllReferences(RuleBaseValues rule);
    public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId);
    public RuleBaseValues getParentRule(Long ruleBaseValuesId);
    public List findOldDelegations(RuleBaseValues oldRule, RuleBaseValues newRule);
}
