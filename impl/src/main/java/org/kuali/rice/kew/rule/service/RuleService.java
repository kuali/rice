/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.rule.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.identity.Id;
import org.kuali.rice.kew.rule.MyRules2;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kew.xml.XmlLoader;
import org.kuali.rice.kew.xml.export.XmlExporter;


/**
 * A service which provides data access and functions for the KEW Rules engine.
 *
 * @see RuleBaseValues
 * @see RuleResponsibility
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RuleService extends XmlLoader, XmlExporter {

    /**
     * Returns a Rule based on unique name.  Returns null if name is null.
     * @param name the rule name
     * @return the Rule if found, null if not found or null name
     */
    public RuleBaseValues getRuleByName(String name);

    //public Long route(MyRules myRules, WorkflowUser user, String annotation) throws Exception;
    public Long route2(Long routeHeaderId, MyRules2 myRules, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception;
    public Long routeRuleWithDelegate(Long routeHeaderId, RuleBaseValues parentRule, RuleBaseValues delegateRule, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception;
    //public void save(RuleBaseValues ruleBaseValues) throws Exception;
    public void save2(RuleBaseValues ruleBaseValues) throws Exception;
    public void validate2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, List errors) throws Exception;
    public void delete(Long ruleBaseValuesId);
    public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId);
    public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, String workgroupId, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective);
    public List search(String docTypeName, String ruleTemplateName, String ruleDescription, GroupId workgroupId, UserId userId, String roleName, Boolean workgroupMember, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection<String> actionRequestCodes) throws KEWUserNotFoundException;
    public RuleResponsibility findRuleResponsibility(Long responsibilityId);
    public void deleteRuleResponsibilityById(Long ruleResponsibilityId);
    public RuleResponsibility findByRuleResponsibilityId(Long ruleResponsibilityId);
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType);
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, boolean ignoreCache);
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, Timestamp effectiveDate);
    public List findByRouteHeaderId(Long routeHeaderId);
    public void makeCurrent(Long routeHeaderId) throws KEWUserNotFoundException;
    public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type);
    public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type);
    public Long isLockedForRouting(Long currentRuleBaseValuesId);
    public List fetchAllRules(boolean currentRules);
    public void saveDeactivationDate(RuleBaseValues rule);
    public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId);
    public void notifyCacheOfRuleChange(RuleBaseValues rule, DocumentType documentType);
    public RuleBaseValues getParentRule(Long ruleBaseValuesId);


    /**
     * Notifies the Rule system that the given DocumentType has been changed.  When a DocumentType changes this
     * could result in the change to the DocumentType hierarchy.  In these cases we want to ensure that all
     * Rules within that DocumentType hierarchy get flushed from the cache so they can be re-cached with the proper
     * DocumentType hierarchy in place.
     */
    public void notifyCacheOfDocumentTypeChange(DocumentType documentType);

    public void flushRuleCache();

    /**
     * Returns the name of the document type definition that should be used to route the given List of rules.  This method will never
     * return a null value, as it will default to the default Rule document type name if not custom document type is configured for
     * the given rules.
     */
    public String getRuleDocmentTypeName(List rules);

    /**
     * Replaces entities who have responsibilities on the given set of rules with the specified new entity.  In this case
     * the Id can be the id of either a Workgroup or a User.
     *
     * <p>This method should handle any versioning of the rules that is required.
     */
    public void replaceRuleInvolvement(Id entityToBeReplaced, Id newEntity, List<Long> ruleIds, Long documentId) throws WorkflowException;

    /**
     * Removes entities who have responsibilities on the given set of rules.  In the case that a targeted rule
     * contains only a single responsibility the rule will be inactivated instead of removing the responsibility.
     * The Id can be the id of either a Workgroup or a User.
     *
     * <p>This method should handle any versioning of the rules that is required.
     */
    public void removeRuleInvolvement(Id entityToBeRemoved, List<Long> ruleIds, Long documentId) throws WorkflowException;

}
